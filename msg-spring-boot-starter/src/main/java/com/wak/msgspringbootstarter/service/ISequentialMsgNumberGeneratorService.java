package com.wak.msgspringbootstarter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wak.msgspringbootstarter.entities.SequentialMsgNumberGeneratorPO;

/**
 * 顺序消息编号生成器
 */
public interface ISequentialMsgNumberGeneratorService extends IService<SequentialMsgNumberGeneratorPO> {

    /**
     * 获取一个编号，相同的groupId中的编号是从1连续递增的
     *
     * @param groupId
     * @return long
     */
    long get(String groupId);
}
