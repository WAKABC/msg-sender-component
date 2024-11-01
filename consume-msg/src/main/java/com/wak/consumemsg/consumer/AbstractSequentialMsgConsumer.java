package com.wak.consumemsg.consumer;

import cn.hutool.json.JSONUtil;
import com.wak.consumemsg.entities.MsgConsume;
import com.wak.msgspringbootstarter.dto.MessageEnvelope;
import com.wak.msgspringbootstarter.entities.SequentialMsgConsumePositionPO;
import com.wak.msgspringbootstarter.entities.SequentialMsgQueuePO;
import com.wak.msgspringbootstarter.lock.LocalLock;
import com.wak.msgspringbootstarter.service.ISequentialMsgConsumePositionService;
import com.wak.msgspringbootstarter.service.ISequentialMsgQueueService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;

/**
 * @author wuankang
 * @Date 2024/10/23 14:37
 * @Description TODO 顺序消息消费抽象类
 * @Version 1.0
 */
@Slf4j
public abstract class AbstractSequentialMsgConsumer<B, M extends MessageEnvelope<B>> extends AbstractConsumerService<B, M> {
    @Resource
    private ISequentialMsgQueueService msgQueueService;

    @Resource
    private ISequentialMsgConsumePositionService consumePositionService;

    @Resource
    private LocalLock localLock;

    @Resource
    protected TransactionTemplate transactionTemplate;

    /**
     * 具体消费逻辑
     *
     * @param message    消息
     * @param msg        消息
     * @param msgConsume 消息消费
     */
    @Override
    protected void consume(Message message, M msg, MsgConsume msgConsume) {
        //收到将要消费的消息后将消息入库，然后按序号消费
        this.pushMsg2Queue(message, msg);
        //从db中队列表中拉取消息进行消费（循环拉取最小的一条记录，看看是不是要消费的记录，如果是，则进行消费）
        this.pullMsgFromQueueConsume(message, msg);
    }

    /**
     * 顺序消费的实现逻辑
     *
     * @param message 消息
     * @param msg     消息
     */
    protected void pullMsgFromQueueConsume(Message message, M msg) {
        String groupId = msg.getSequentialMsgGroupId();
        String consumerQueue = message.getMessageProperties().getConsumerQueue();
        String lockKey = String.format("%s-%s", groupId, consumerQueue);
        //加锁，此处使用单机锁，按需升级分布式锁
        localLock.accept(lockKey, lock -> {
            while (true) {
                //获取第一条数据
                SequentialMsgQueuePO sequentialMsgQueue = this.msgQueueService.getFirst(groupId, consumerQueue);
                if (Objects.isNull(sequentialMsgQueue)) {
                    break;
                }
                //获取当前消费位置
                SequentialMsgConsumePositionPO consumePosition = this.consumePositionService.getAndCreate(groupId, consumerQueue);
                long numbering = consumePosition.getConsumeNumbering();
                //轮到自己消费？
                if (sequentialMsgQueue.getNumbering() == numbering + 1) {
                    M msgObj = this.msgJson2Obj(sequentialMsgQueue.getMsgJson());
                    //1.消费消息，子类实现
                    this.sequentialMsgConsume(msgObj);
                    //2.更新消费位置
                    transactionTemplate.executeWithoutResult(action -> {
                        consumePosition.setConsumeNumbering(numbering + 1);
                        boolean success = this.consumePositionService.updateById(consumePosition);
                        if (!success) {
                            throw new RuntimeException("系统繁忙，请稍后重试");
                        }
                        //3.移除消费过的记录，后续继续获取最小记录逐渐消费
                        msgQueueService.delete(sequentialMsgQueue.getId());
                    });
                } else if (sequentialMsgQueue.getNumbering() < numbering) {
                    log.info("消费过的消息，直接删除");
                    //消费过的消息直接删除
                    this.msgQueueService.delete(sequentialMsgQueue.getId());
                } else {
                    log.info("未到自己消费，退出循环");
                    //未到自己消费
                    break;
                }
            }
        });
    }


    /**
     * 顺序消息入库
     *
     * @param message 消息
     * @param msg     消息
     */
    private void pushMsg2Queue(Message message, M msg) {
        String groupId = msg.getSequentialMsgGroupId();
        Long numbering = msg.getSequentialMsgNumbering();
        String consumerQueue = message.getMessageProperties().getConsumerQueue();
        String msgBody = JSONUtil.toJsonStr(msg);
        this.msgQueueService.push(groupId, numbering, consumerQueue, msgBody);
    }

    /**
     * 子类实现的具体的消费逻辑
     *
     * @param msg 消息
     */
    protected abstract void sequentialMsgConsume(M msg);
}
