package com.wak.msgspringbootstarter.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wak.msgspringbootstarter.entities.MsgPO;
import com.wak.msgspringbootstarter.enums.MsgStatusEnum;
import com.wak.msgspringbootstarter.mapper.MsgMapper;
import com.wak.msgspringbootstarter.service.IMsgService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuankang
 * @date 2024/10/8 19:23
 * @Description TODO
 * @Version 1.0
 */
public class IMsgServiceImpl extends ServiceImpl<MsgMapper, MsgPO> implements IMsgService {

    @Resource
    private MsgMapper msgMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MsgPO> batchInsert(String exchange, String routingKey, LocalDateTime expectSendTime, List<String> msgBodyJsonList) {
        if (msgBodyJsonList.isEmpty()) {
            throw new IllegalArgumentException("msgBodyJsonList is empty.");
        }
        ArrayList<MsgPO> msgPOS = new ArrayList<>(msgBodyJsonList.size());
        for (String msgJson : msgBodyJsonList) {
            MsgPO msgPO = new MsgPO();
            msgPO.setId(IdUtil.fastSimpleUUID());
            msgPO.setExchange(exchange);
            msgPO.setRoutingKey(routingKey);
            msgPO.setExpectSendTime(expectSendTime);
            msgPO.setBodyJson(msgJson);
            msgPO.setStatus(MsgStatusEnum.INIT.getStatus());
            msgPO.setCreateTime(LocalDateTime.now());
            msgPOS.add(msgPO);
        }
        this.saveBatch(msgPOS);
        return msgPOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMsgStatusSuccess(MsgPO msgPO) {
        LocalDateTime now = LocalDateTime.now();
        this.msgMapper.updateStatusSuccess(MsgStatusEnum.SUCCESS.getStatus(), now, now, msgPO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMsgStatusFailure(MsgPO msgPO, int sendRetry, LocalDateTime nextSendRetryTime, String failMsg) {
        LocalDateTime now = LocalDateTime.now();
        this.msgMapper.updateStatusFailure(MsgStatusEnum.FAIL.getStatus(), failMsg, sendRetry, nextSendRetryTime, now, now, msgPO.getId());
    }
}
