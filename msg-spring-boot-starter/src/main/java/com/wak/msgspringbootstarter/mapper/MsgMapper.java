package com.wak.msgspringbootstarter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wak.msgspringbootstarter.entities.MsgPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * @author wuankang
 * @date 2024/10/8 19:44
 * @Description TODO
 * @Version 1.0
 */
@Mapper
public interface MsgMapper extends BaseMapper<MsgPO> {
    /**
     * 更新状态成功
     *
     * @param id             ID
     * @param status         已更新状态
     * @param actualSendTime 更新了实际发送时间
     * @param updateTime     更新时间
     */
    @Update("update t_msg set `STATUS`=#{status},actual_send_time=#{actualSendTime},update_time=#{updateTime} where id = #{id}")
    void updateStatusSuccess(@Param("status") Integer status, @Param("actualSendTime") LocalDateTime actualSendTime, @Param("updateTime") LocalDateTime updateTime, @Param("id") String id);

    /**
     * 更新状态失败
     *
     * @param status         状态
     * @param failMsg        失败消息
     * @param sendRetry      发送重试
     * @param nextRetryTime  下次重试时间
     * @param actualSendTime 实际发送时间
     * @param updateTime     更新时间
     * @param id             ID
     */
    @Update("update t_msg set `STATUS`=#{status},fail_msg=#{failMsg},fail_count = fail_count + 1,send_retry=#{sendRetry},next_retry_time=#{nextRetryTime},actual_send_time=#{actualSendTime},update_time=#{updateTime} where id = #{id}")
    void updateStatusFailure(@Param("status") Integer status, @Param("failMsg") String failMsg, @Param("sendRetry") Integer sendRetry, @Param("nextRetryTime") LocalDateTime nextRetryTime, @Param("actualSendTime") LocalDateTime actualSendTime, @Param("updateTime") LocalDateTime updateTime, @Param("id") String id);
}