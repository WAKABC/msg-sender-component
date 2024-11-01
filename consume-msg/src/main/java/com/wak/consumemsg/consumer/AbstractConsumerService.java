package com.wak.consumemsg.consumer;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.wak.consumemsg.entities.MsgConsume;
import com.wak.consumemsg.enums.MsgConsumerStatusEnum;
import com.wak.consumemsg.retry.MsgConsumeRetryResult;
import com.wak.consumemsg.service.IMsgConsumerService;
import com.wak.consumemsg.uitls.MsgConsumeRetryResultUtil;
import com.wak.msgspringbootstarter.dto.MessageEnvelope;
import com.wak.msgspringbootstarter.entities.MailMessage;
import com.wak.msgspringbootstarter.mail.IMailService;
import com.wak.msgspringbootstarter.sender.IMsgSender;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author wuankang
 * @Date 2024/10/15 21:37
 * @Description TODO 基础消费服务抽象类
 * @Version 1.0
 */
@Slf4j
public abstract class AbstractConsumerService<B, M extends MessageEnvelope<B>> {
    @Resource
    private IMsgConsumerService msgConsumerService;

    @Resource
    private IMsgSender msgSender;

    @Resource
    private IMailService mailService;
    /**
     * B 类型
     */
    private Type clsType;

    /**
     * 通用处理逻辑，提供消费记录创建入库、失败重试、成功修改状态的逻辑
     *
     * @param message 信息
     */
    public void dispose(Message message) {
        M msg = getBodyObj(message);
        log.info("从RabbitMQ收到订单消息：{}", msg);
        //创建消费记录
        MsgConsume msgConsume = this.msgConsumerService.getAndCreate(msg, this.getClass().getName(), message.getMessageProperties().getConsumerQueue());
        //是否需要消费
        if (!needConsume(msgConsume)) {
            log.info("该消息无需消费，不处理...");
            return;
        }
        try {
            //消费逻辑，子类实现
            this.consume(message, msg, msgConsume);
            //消费成功更改状态
            this.msgConsumerService.updateStatusSuccess(msgConsume.getId(), MsgConsumerStatusEnum.SUCCESS.getStatus());
        } catch (Exception exception) {
            log.error("消费记录：{}", msgConsume);
            log.error("消息消费异常:{}", exception.getMessage(), exception);
            //失败重试流程
            this.handleFailure(message, msg, msgConsume, exception);
        }

    }

    /**
     * 子类业务逻辑
     *
     * @param message    信息
     * @param msg        消息
     * @param msgConsume 消费记录
     */
    protected abstract void consume(Message message, M msg, MsgConsume msgConsume);

    /**
     * 获取消息体对象
     *
     * @param message mq消息
     * @return {@code MessageEnvelope<B> }
     */
    protected M getBodyObj(Message message) {
        String jsonBody = StrUtil.str(message.getBody(), StandardCharsets.UTF_8);
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type type = parameterizedType.getActualTypeArguments()[1];
        //设置消息体类型
        this.clsType = type;
        return JSONUtil.toBean(jsonBody, type, true);
    }

    /**
     * 消息json转换为对象
     *
     * @param msgJson 消息JSON
     * @return {@code M }
     */
    protected M msgJson2Obj(String msgJson) {
        return JSONUtil.toBean(msgJson, clsType, true);
    }

    /**
     * 是否需要消费
     *
     * @param msgConsume 消息消费记录
     * @return boolean
     */
    private boolean needConsume(@NonNull MsgConsume msgConsume) {
        //初始状态
        if (msgConsume.getStatus().equals(MsgConsumerStatusEnum.INIT.getStatus())) {
            return true;
        } else {
            return msgConsume.getStatus().equals(MsgConsumerStatusEnum.FAIL.getStatus()) && msgConsume.getConsumeRetry() == 1;
        }
    }

    /**
     * 处理失败逻辑
     *
     * @param message         信息
     * @param messageEnvelope 消息信封
     * @param msgConsume      消息消耗
     * @param exception       异常
     */
    private void handleFailure(Message message, MessageEnvelope<B> messageEnvelope, MsgConsume msgConsume, Exception exception) {
        //获取重试结果对象
        MsgConsumeRetryResult retryResult = MsgConsumeRetryResultUtil.getRetryResult(msgConsume);
        //异常信息
        String failMsg = ExceptionUtil.stacktraceToString(exception);
        //更新状态
        this.msgConsumerService.updateStatusFail(msgConsume.getId(), MsgConsumerStatusEnum.FAIL.getStatus(), failMsg, retryResult.isRetry() ? 1 : 0, retryResult.getNextRetryTime());
        //判断是否重试
        if (retryResult.isRetry()) {
            log.info("消息消费异常msgId：{}, 重试次数：{}", msgConsume.getId(), msgConsume.getFailCount());
            //队列名称
            String consumerQueue = message.getMessageProperties().getConsumerQueue();
            //延迟时长
            long delayTimeMills = LocalDateTimeUtil.between(LocalDateTime.now(), retryResult.getNextRetryTime()).toMillis();
            //消费失败，采用延迟的方式，将消息再次投递到队列
            // 进行重试（这里exchange为null，routingKey为队列名称时，会直接将消息投递到队列）
            msgSender.send(null, consumerQueue, delayTimeMills, TimeUnit.MILLISECONDS, Collections.singletonList(messageEnvelope));
        } else {
            log.info("消息消费异常msgId：{}, 重试次数超限，发送邮件通知", msgConsume.getId());
            //邮件预警
            MailMessage mailMessage = new MailMessage();
            mailMessage.setMsgId(msgConsume.getId());
            mailMessage.setTopic("消息消费失败已达最大重试次数");
            mailMessage.setTo("1835500316@qq.com");
            mailMessage.setMsgFail(failMsg);
            mailMessage.setUserName("吴安康");
            mailMessage.setMsgBody(msgConsume.getBodyJson());
            this.mailService.sendThymeleaf(mailMessage);
        }
    }
}
