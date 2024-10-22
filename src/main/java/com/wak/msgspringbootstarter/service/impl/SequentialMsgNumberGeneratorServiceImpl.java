package com.wak.msgspringbootstarter.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wak.msgspringbootstarter.entities.SequentialMsgNumberGeneratorPO;
import com.wak.msgspringbootstarter.entities.SequentialMsgQueuePO;
import com.wak.msgspringbootstarter.mapper.SequentialMsgNumberGeneratorMapper;
import com.wak.msgspringbootstarter.service.ISequentialMsgNumberGeneratorService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


/**
 * @author wuankang
 * @date 2024/10/22 17:03
 * @Description TODO
 * @Version 1.0
 */
@Service
public class SequentialMsgNumberGeneratorServiceImpl extends ServiceImpl<SequentialMsgNumberGeneratorMapper, SequentialMsgNumberGeneratorPO> implements ISequentialMsgNumberGeneratorService {
    /**
     * 获取 SequentialMsgNumberGeneratorPO
     *
     * @param groupId 组 ID
     * @return long
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long get(String groupId) {
        Objects.requireNonNull(groupId);
        SequentialMsgNumberGeneratorPO sequentialMsgNumberGeneratorPO = this.findByGroupId(groupId);
        //numbering++
        long numbering = sequentialMsgNumberGeneratorPO.getNumbering() + 1;
        //update
        boolean success = this.updateById(sequentialMsgNumberGeneratorPO);
        if (!success){
            throw new RuntimeException("系统繁忙，请稍后重试");
        }
        return numbering;
    }


    /**
     * 根据groupId查询
     *
     * @param groupId 组 ID
     * @return {@code SequentialMsgNumberGeneratorPO }
     */
    public SequentialMsgNumberGeneratorPO findByGroupId(String groupId) {
        LambdaQueryWrapper<SequentialMsgNumberGeneratorPO> eq = Wrappers.lambdaQuery(SequentialMsgNumberGeneratorPO.class).eq(SequentialMsgNumberGeneratorPO::getGroupId, groupId);
        SequentialMsgNumberGeneratorPO sequentialMsgNumberGeneratorPO = getOne(eq);
        if (Objects.isNull(sequentialMsgNumberGeneratorPO)) {
            sequentialMsgNumberGeneratorPO = new SequentialMsgNumberGeneratorPO();
            sequentialMsgNumberGeneratorPO.setId(IdUtil.fastSimpleUUID());
            sequentialMsgNumberGeneratorPO.setGroupId(groupId);
            sequentialMsgNumberGeneratorPO.setNumbering(0L);
            sequentialMsgNumberGeneratorPO.setVersion(0L);
            this.save(sequentialMsgNumberGeneratorPO);
        }
        return sequentialMsgNumberGeneratorPO;
    }
}
