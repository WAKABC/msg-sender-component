package com.wak.msgspringbootstarter.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wak.msgspringbootstarter.entities.SequentialMsgConsumePositionPO;
import com.wak.msgspringbootstarter.mapper.SequentialMsgConsumeInfoMapper;
import com.wak.msgspringbootstarter.service.ISequentialMsgConsumePositionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author wuankang
 * @date 2024/10/22 18:05
 * @Description TODO
 * @Version 1.0
 */
@Service
public class SequentialMsgConsumePositionServiceImpl extends ServiceImpl<SequentialMsgConsumeInfoMapper, SequentialMsgConsumePositionPO> implements ISequentialMsgConsumePositionService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SequentialMsgConsumePositionPO getAndCreate(String groupId, String queueName) {
        LambdaQueryWrapper<SequentialMsgConsumePositionPO> queryWrapper = Wrappers.lambdaQuery(SequentialMsgConsumePositionPO.class)
                .eq(SequentialMsgConsumePositionPO::getGroupId, groupId)
                .eq(SequentialMsgConsumePositionPO::getQueueName, queueName);
        SequentialMsgConsumePositionPO po = this.getOne(queryWrapper);
        if (Objects.isNull(po)) {
            po = new SequentialMsgConsumePositionPO();
            po.setId(IdUtil.fastSimpleUUID());
            po.setGroupId(groupId);
            po.setQueueName(queueName);
            po.setConsumeNumbering(0L);
            this.save(po);
        }
        return po;
    }
}
