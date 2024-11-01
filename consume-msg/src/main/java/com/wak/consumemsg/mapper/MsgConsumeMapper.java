package com.wak.consumemsg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wak.consumemsg.entities.MsgConsume;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * @author wuankang
 * @Date 2024/10/31 17:06:15
 * @Description TODO
 * @Version 1.0
 */
public interface MsgConsumeMapper extends BaseMapper<MsgConsume> {
    /**
     * 更新状态成功
     *
     * @param status     已更新状态
     * @param updateTime 更新更新时间
     * @param id                ID
     * @return int
     */
    int updateStatusSuccess(@Param("status")Integer status, @Param("updateTime")LocalDateTime updateTime, @Param("id")String id);

    /**
     * 更新状态失败
     *
     * @param status        已更新状态
     * @param failMsg       已更新失败消息
     * @param consumeRetry  更新消耗重试
     * @param nextRetryTime 下次重试时间
     * @param id                   ID
     * @return int
     */
    int updateStatusFail(@Param("status")Integer status, @Param("failMsg")String failMsg, @Param("consumeRetry")Integer consumeRetry, @Param("nextRetryTime")LocalDateTime nextRetryTime, @Param("id")String id);
}