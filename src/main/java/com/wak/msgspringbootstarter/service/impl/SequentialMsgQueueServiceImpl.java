package com.wak.msgspringbootstarter.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wak.msgspringbootstarter.entities.SequentialMsgQueuePO;
import com.wak.msgspringbootstarter.mapper.SequentialMsgQueueMapper;
import com.wak.msgspringbootstarter.service.ISequentialMsgQueueService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author wuankang
 * @date 2024/10/22 17:23
 * @Description TODO 顺序消息服务
 * @Version 1.0
 */
public class SequentialMsgQueueServiceImpl extends ServiceImpl<SequentialMsgQueueMapper, SequentialMsgQueuePO> implements ISequentialMsgQueueService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SequentialMsgQueuePO push(@NonNull String groupId, @NonNull Long numbering, @NonNull String queueName, @NonNull String msgJson) {
        SequentialMsgQueuePO sequentialMsgQueuePO = this.get(groupId, numbering, queueName);
        if (Objects.isNull(sequentialMsgQueuePO)){
            sequentialMsgQueuePO = new SequentialMsgQueuePO();
            sequentialMsgQueuePO.setId(IdUtil.fastSimpleUUID());
            sequentialMsgQueuePO.setGroupId(groupId);
            sequentialMsgQueuePO.setNumbering(numbering);
            sequentialMsgQueuePO.setQueueName(queueName);
            sequentialMsgQueuePO.setMsgJson(msgJson);
            this.save(sequentialMsgQueuePO);
        }
        return sequentialMsgQueuePO;
    }

    private SequentialMsgQueuePO get(String groupId, Long numbering, String queueName) {
        LambdaQueryWrapper<SequentialMsgQueuePO> eq = Wrappers.lambdaQuery(SequentialMsgQueuePO.class)
                .eq(SequentialMsgQueuePO::getGroupId, groupId)
                .eq(SequentialMsgQueuePO::getNumbering, numbering)
                .eq(SequentialMsgQueuePO::getQueueName, queueName);
        return this.getOne(eq);
    }

    @Override
    public SequentialMsgQueuePO getFirst(String groupId, String queueName) {
        LambdaQueryWrapper<SequentialMsgQueuePO> wrapper = Wrappers.lambdaQuery(SequentialMsgQueuePO.class)
                .eq(SequentialMsgQueuePO::getGroupId, groupId)
                .eq(SequentialMsgQueuePO::getQueueName, queueName)
                .orderByAsc(SequentialMsgQueuePO::getNumbering)
                .last("limit 1");
        return this.getOne(wrapper);
    }

    @Override
    public void delete(String id) {
        this.removeById(id);
    }
}
