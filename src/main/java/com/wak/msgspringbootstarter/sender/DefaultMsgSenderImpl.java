package com.wak.msgspringbootstarter.sender;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.wak.msgspringbootstarter.delay.DelayQueueTaskProcessor;
import com.wak.msgspringbootstarter.dto.MessageEnvelope;
import com.wak.msgspringbootstarter.entities.MailMessage;
import com.wak.msgspringbootstarter.entities.MsgPO;
import com.wak.msgspringbootstarter.entities.MsgSendRetryPO;
import com.wak.msgspringbootstarter.enums.MsgStatusEnum;
import com.wak.msgspringbootstarter.lock.LocalLock;
import com.wak.msgspringbootstarter.mail.IMailService;
import com.wak.msgspringbootstarter.service.IMsgService;
import com.wak.msgspringbootstarter.service.ISequentialMsgNumberGeneratorService;
import com.wak.msgspringbootstarter.utils.*;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author wuankang
 * @date 2024/10/9 15:10
 * @Description 消息发送实现
 * @Version 1.0
 */
public class DefaultMsgSenderImpl implements IMsgSender {
    private static final Logger log = LoggerFactory.getLogger(DefaultMsgSenderImpl.class);
    /**
     * 消息服务
     */
    private final IMsgService msgService;
    /**
     * 邮件服务
     */
    private final IMailService mailService;
    /**
     * rabbitmq
     */
    private final RabbitTemplate rabbitTemplate;
    /**
     * 多线程任务执行器
     */
    private final ThreadPoolTaskExecutor taskExecutor;
    /**
     * 延迟队列任务处理器
     */
    private final DelayQueueTaskProcessor delayMsgProcessor;
    /**
     * 延迟消息等待集合，存放待投递的延迟消息
     */
    private final Map<String, MsgPO> delayMsgWaitingMap = new ConcurrentHashMap<>();
    /**
     * 延迟发送重试处理器
     */
    private final DelayQueueTaskProcessor delaySendRetryProcessor;
    /**
     * 延迟发送重试等待集合，存放待重复投递的延迟消息
     */
    private final Map<String, MsgPO> delaySendRetryWaitingMap = new ConcurrentHashMap<>();
    /**
     * 单机锁，实现顺序发送，可以使用redisson分布式锁
     */
    private final LocalLock localLock;
    /**
     * 事务操作模板
     */
    private final TransactionTemplate transactionTemplate;
    /**
     * 顺序消费序号生成服务
     */
    private final ISequentialMsgNumberGeneratorService msgNumberGeneratorService;

    public DefaultMsgSenderImpl(IMsgService msgService, IMailService mailService, RabbitTemplate rabbitTemplate, ThreadPoolTaskExecutor taskExecutor, DelayQueueTaskProcessor delayMsgProcessor, DelayQueueTaskProcessor delaySendRetryProcessor, LocalLock localLock, TransactionTemplate transactionTemplate, ISequentialMsgNumberGeneratorService msgNumberGeneratorService) {
        this.msgService = msgService;
        this.mailService = mailService;
        this.rabbitTemplate = rabbitTemplate;
        this.taskExecutor = taskExecutor;
        this.delayMsgProcessor = delayMsgProcessor;
        this.delaySendRetryProcessor = delaySendRetryProcessor;
        this.localLock = localLock;
        this.transactionTemplate = transactionTemplate;
        this.msgNumberGeneratorService = msgNumberGeneratorService;
    }

    @Override
    public void send(String exchange, String routingKey, List<MessageEnvelope<?>> msgList) {
        this.send(exchange, routingKey, 0, TimeUnit.MILLISECONDS, msgList);
    }

    /**
     * 统一消息发送处理逻辑
     *
     * @param exchange   交换
     * @param routingKey 路由键
     * @param delayTime  延迟时间
     * @param timeUnit   时间单位
     * @param msgList    消息列表
     */
    @Override
    public void send(String exchange, String routingKey, long delayTime, TimeUnit timeUnit, List<MessageEnvelope<?>> msgList) {
        //将object转换成json
        List<String> msgBodyList = CollUtils.convertList(msgList, JSONUtil::toJsonStr);
        //期望发送时间单位：秒
        LocalDateTime expectSendTime = LocalDateTime.now().plusSeconds(TimeUnit.SECONDS.convert(delayTime, timeUnit));
        //判断是否存入数据库中
        boolean needStoreToDb = NeedStoreToDb(expectSendTime);
        if (needStoreToDb) {
            //插入数据
            List<MsgPO> msgPOList = this.msgService.batchInsert(exchange, routingKey, expectSendTime, msgBodyList);
            if (!hasTransaction()) {
                taskExecutor.execute(() -> this.deliverMsg(msgPOList));
            } else {
                //当事务结束后再进行投递
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        String id = msgPOList.get(0).getId();
                        MsgPO msgPO = msgService.getById(id);
                        if (Objects.isNull(msgPO)) {
                            log.info("msg插入失败，不进行任何msg投递");
                            throw new RuntimeException("msg插入失败，不进行任何msg投递");
                        }
                        //为了提升性能：事务消息的投递消息这里异步去执行，即使失败了，会有补偿JOB进行重试
                        taskExecutor.execute(() -> deliverMsg(msgPOList));
                    }
                });
            }
        } else {
            //不需要入库直接投递给MQ
            for (String msgJson : msgBodyList) {
                this.deliverMsgToMq(exchange, routingKey, msgJson);
            }
        }
    }

    @Override
    public void sendRetry(MsgPO msgPO) {
        this.deliverMsg(msgPO);
    }

    /**
     * 需要存储到数据库
     * 存在事务，获取是延迟任务需要先入库
     *
     * @param expectSendTime 预计发送时间
     * @return boolean
     */
    private boolean NeedStoreToDb(LocalDateTime expectSendTime) {
        return hasTransaction() || expectSendTime.isAfter(LocalDateTime.now());
    }

    /**
     * 是否存在事务
     *
     * @return boolean
     */
    private boolean hasTransaction() {
        return TransactionSynchronizationManager.isSynchronizationActive() && TransactionSynchronizationManager.isActualTransactionActive();
    }

    /**
     * 批量投递消息
     *
     * @param msgPOList 消息对象集合
     */
    private void deliverMsg(List<MsgPO> msgPOList) {
        for (MsgPO msgPO : msgPOList) {
            this.deliverMsg(msgPO);
        }
    }

    /**
     * 投递单条消息
     *
     * @param msgPO 消息对象
     */
    private void deliverMsg(MsgPO msgPO) {
        //是否是延迟消息
        boolean delayMsg = msgPO.getExpectSendTime().isAfter(LocalDateTime.now());
        if (delayMsg) {
            //延迟投递
            this.deliverDelayMsg(msgPO);
        } else {
            //立即投递
            this.deliverImmediateMsg(msgPO.getId());
        }
    }

    /**
     * 递送延迟消息
     *
     * @param msgPO 消息对象
     */
    private void deliverDelayMsg(MsgPO msgPO) {
        //此处只处理2分钟内的消息（首次发送不限时长），超过该时间不处理
        if (timeInRangeTowMinutes(msgPO)) {
            //防止消息重复排队，map中没有时，才放入队列中进行排队
            this.delayMsgWaitingMap.computeIfAbsent(msgPO.getId(), po -> {
                log.info("添加延迟任务到延迟队列中...");
                //获取延时任务执行时间
                long delaySendTimeMs = getDelaySendTimeMs(msgPO);
                boolean putSuccess = this.delayMsgProcessor.put(delaySendTimeMs, () -> {
                    //同步MDC值
                    String mdcVal = MDCUtil.get(msgPO.getId());
                    TraceContext.setTraceId(mdcVal);
                    log.info("延迟任务开始执行...");
                    //立即投递消息
                    this.deliverImmediateMsg(msgPO.getId());
                    //执行时删除map中的消息
                    delayMsgWaitingMap.remove(msgPO.getId());
                });
                //消息投递失败
                if (!putSuccess) {
                    log.error("本地延迟队列已满：{}", this.delayMsgProcessor);
                    throw new RuntimeException("本地延迟队列已满,延迟消息排队失败");
                }
                //延迟任务添加成功，存入key:msgId, value:traceId
                MDCUtil.put(msgPO.getId());
                return msgPO;
            });
            return;
        }
        log.info("任务:[msgId:{}, 延迟任务时间：{}]，延时时间超过两分钟，此处不执行...", msgPO.getId(), msgPO.getExpectSendTime());
    }

    /**
     * 递送即时消息
     *
     * @param msgId 消息ID
     */
    private void deliverImmediateMsg(String msgId) {
        //对消息添加分布式锁，防止服务集群部署时，避免重复投递，此处暂时使用单机锁
        localLock.accept("deliverImmediateMsg:" + msgId, lockKey -> {
            log.info("投递消息, msgId:{}", msgId);
            //加锁成功后，重新从db中获取消息
            MsgPO msg = msgService.getById(msgId);
            Objects.requireNonNull(msg);
            Integer status = msg.getStatus();
            Integer sendRetry = msg.getSendRetry();
            //投递成功或投递失败且无需重试的不处理
            if (MsgStatusEnum.SUCCESS.getStatus().equals(status) || (MsgStatusEnum.FAIL.getStatus().equals(status) && sendRetry.equals(0))) {
                log.info("msgId:{}, 已成功或失败但无需重试，无需投递.", msgId);
                return;
            }
            try {
                //投递消息
                this.deliverMsgToMq(msg.getExchange(), msg.getRoutingKey(), msg.getBodyJson());
                //跟新消息状态
                this.msgService.updateMsgStatusSuccess(msg);
                //删除MDC值
                MDCUtil.remove(msgId);
            } catch (Exception e) {
                this.handleSendException(msg, e);
            }
        });
    }

    /**
     * 延迟投递重试，对于投递失败的且需要投递重试的，调用此方法进行排队重试
     *
     * @param msgPO 消息对象
     */
    private void delaySendRetry(MsgPO msgPO) {
        log.info("消息投递失败，进行延迟重试，msgId:{}", msgPO.getId());
        Objects.requireNonNull(msgPO);
        //此处只处理延迟时长2分钟内的消息（此处都是失败重试的，不可能是首次发送），超过该时间不处理
        if (timeInRangeTowMinutes(msgPO)) {
            delaySendRetryWaitingMap.computeIfAbsent(msgPO.getId(), k -> {
                //下次执行时间
                long delaySendTimeMs = getDelaySendTimeMs(msgPO);
                boolean putSuccess = delaySendRetryProcessor.put(delaySendTimeMs, () -> {
                    //设置MDC值
                    String mdcVal = MDCUtil.get(msgPO.getId());
                    TraceContext.setTraceId(mdcVal);
                    //移除元素
                    delaySendRetryWaitingMap.remove(msgPO.getId());
                    //投递消息
                    this.deliverMsg(msgPO);
                });
                if (!putSuccess) {
                    log.error("本地延迟投递重试队列已满：{}", this.delaySendRetryProcessor);
                    throw new RuntimeException("本地延迟投递重试队列已满，延迟投递重试排队失败");
                }
                MDCUtil.put(msgPO.getId());
                return msgPO;
            });
        }
    }

    /**
     * 递送消息到MQ
     *
     * @param exchange    交换机
     * @param routingKey  路由键
     * @param msgBodyJson 消息身体json
     */
    private void deliverMsgToMq(String exchange, String routingKey, String msgBodyJson) {
        log.debug("开始投递消息到MQ，消息内容：{}", msgBodyJson);
        this.rabbitTemplate.convertAndSend(exchange, routingKey, msgBodyJson);
        log.info("**************消息已投递到MQ,exchange:{},routingKey{}", exchange, routingKey);
    }


    /**
     * 获取延迟任务执行时间，单位毫秒
     *
     * @param msgPO 消息对象
     * @return long
     */
    private long getDelaySendTimeMs(MsgPO msgPO) {
        long delaySendTime;
        if (msgPO.getFailCount() == 0) {
            delaySendTime = LocalDateTimeUtil.toEpochMilli(msgPO.getExpectSendTime());
        } else {
            delaySendTime = LocalDateTimeUtil.toEpochMilli(msgPO.getNextRetryTime());
        }
        return delaySendTime;
    }

    /**
     * 时间范围内是否在2分钟内
     *
     * @param msg 消息对象
     * @return boolean
     */
    private boolean timeInRangeTowMinutes(MsgPO msg) {
        if (MsgStatusEnum.INIT.getStatus().equals(msg.getStatus())) {
            return true;
        }
        //获取延迟时间
        long costTime = LocalDateTimeUtil.between(LocalDateTime.now(), msg.getNextRetryTime()).toMillis();
        //两分钟毫秒时间
        long thresholdTime = TimeUnit.MINUTES.toMillis(2);
        return costTime < thresholdTime;
    }

    /**
     * 处理发送异常
     *
     * @param msg       消息
     * @param exception 异常
     */
    private void handleSendException(MsgPO msg, Exception exception) {
        //1.获取重试结果，获取延迟时间和是否重试
        MsgSendRetryPO msgSendRetry = MsgSendRetryUtil.getMsgSendRetry(msg.getFailCount());
        //2.获取错误信息
        String failMsg = ExceptionUtil.stacktraceToString(exception);
        //3.更新消息数据
        this.msgService.updateMsgStatusFailure(msg, msgSendRetry.getSendRetry(), msgSendRetry.getNextSendRetryTime(), failMsg);
        //4.如果sendRetry=0，则代表到达上限，进行人工处理
        if (msgSendRetry.getSendRetry() == 1) {
            log.info("投递消息失败，msgId：{}，当前重试次数：{}", msg.getId(), msg.getFailCount());
            MsgPO msgPO = this.msgService.getById(msg.getId());
            //重发
            this.delaySendRetry(msgPO);
        } else {
            log.info("msgId:{}, 重试次数已达最大次数，进行人工干预...", msg.getId());
            MailMessage mailMessage = MailMessageUtil.assembleMailMessage(msg, failMsg);
            this.mailService.sendThymeleaf(mailMessage);
            //删除MDC值
            MDCUtil.remove(msg.getId());
        }
    }

    /**
     * 发送顺序消息
     *
     * @param busId      业务ID
     * @param exchange   交换
     * @param routingKey 路由键
     * @param msg        消息列表
     */
    @Override
    public void sendSequentialMsg(String busId, String exchange, String routingKey, MessageEnvelope<?> msg) {
        //同一个groupId的顺序消费
        String groupId = String.format("%s-%s-%s", busId, StrUtil.emptyIfNull(exchange), StrUtil.emptyIfNull(routingKey));
        localLock.accept(groupId, lock -> {
            this.transactionTemplate.executeWithoutResult(action -> {
                long numbering = this.msgNumberGeneratorService.get(groupId);
                msg.setSequentialMsgGroupId(groupId);
                msg.setSequentialMsgNumbering(numbering);
                this.send(exchange, routingKey, msg);
            });
        });
    }
}
