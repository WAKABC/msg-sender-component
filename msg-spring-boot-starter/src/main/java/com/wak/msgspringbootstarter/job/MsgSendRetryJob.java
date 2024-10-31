package com.wak.msgspringbootstarter.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wak.msgspringbootstarter.entities.MsgPO;
import com.wak.msgspringbootstarter.enums.MsgStatusEnum;
import com.wak.msgspringbootstarter.sender.IMsgSender;
import com.wak.msgspringbootstarter.service.IMsgService;
import com.wak.msgspringbootstarter.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wuankang
 * @date 2024/10/14 23:10
 * @Description 补偿机制，重发长时间未投递的消息
 * @Version 1.0
 */
@Slf4j
public class MsgSendRetryJob implements DisposableBean {
    private final IMsgService msgService;
    private final IMsgSender msgSender;
    private volatile boolean stop = false;

    public MsgSendRetryJob(IMsgService msgService, IMsgSender msgSender) {
        this.msgService = msgService;
        this.msgSender = msgSender;
    }

    /**
     * 发送两分钟内没有成功的或者失败需要重试的消息
     */
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void sendRetry() {
        MsgPO minMsg = getMinMsg();
        if (minMsg == null) {
            return;
        }
        String minMsgPOId = minMsg.getId();
        //循环中继续向后找出id>minMsgId的所有记录，然后投递重试
        while (true) {
            LambdaQueryWrapper<MsgPO> query = buildRetrySqlWrapper();
            query.gt(MsgPO::getId, minMsgPOId);
            //分页
            Page<MsgPO> page = new Page<>();
            page.setSize(500);
            page.setCurrent(1);
            this.msgService.page(page, query);
            if (CollUtils.isEmpty(page.getRecords()) || stop) {
                break;
            }
            List<MsgPO> records = page.getRecords();
            //重发
            records.forEach(msg -> {
                this.msgSender.sendRetry(msg);
                log.info("消息：{}，重新发送", msg.getId());
            });
            //设置最小id
            minMsgPOId = records.get(records.size() - 1).getId();
        }
    }

    /**
     * 构建重试发送 sql 包装器
     *
     * @return {@code LambdaQueryWrapper<MsgPO> }
     */
    private LambdaQueryWrapper<MsgPO> buildRetrySqlWrapper() {
        /*需要重发的消息条件，1、状态为初始状态， 2、状态为失败，需要重试，期待发送时间小于两分钟*/
        LocalDateTime time = LocalDateTime.now().plusMinutes(2);
        LambdaQueryWrapper<MsgPO> query = Wrappers.lambdaQuery(MsgPO.class)
                .and(lq -> lq
                        .and(q -> q
                                .eq(MsgPO::getStatus, MsgStatusEnum.INIT.getStatus())
                                .le(MsgPO::getExpectSendTime, time))
                        .or(q -> q
                                .eq(MsgPO::getStatus, MsgStatusEnum.FAIL.getStatus())
                                .eq(MsgPO::getSendRetry, 1)
                                .le(MsgPO::getExpectSendTime, time)));
        query.orderByAsc(MsgPO::getId);
        return query;
    }

    /**
     * 得到最小的消息记录
     *
     * @return {@code MsgPO }
     */
    private MsgPO getMinMsg() {
        //先获取最小的一条记录的id
        return this.msgService.getOne(buildRetrySqlWrapper().last("limit 1"));
    }

    @Override
    public void destroy() throws Exception {
        stop = true;
    }
}
