package com.wak.consumemsg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wak.consumemsg.entities.MsgConsumeLog;
import com.wak.consumemsg.mapper.MsgConsumeLogMapper;
import com.wak.consumemsg.service.IMsgConsumerLogService;
import org.springframework.stereotype.Service;

/**
 * @author wuankang
 * @Date 2024/10/31 17:06:38
 * @Description 消息消费服务
 * @Version 1.0
 */
@Service
public class MsgConsumerLogLogServiceImpl extends ServiceImpl<MsgConsumeLogMapper, MsgConsumeLog> implements IMsgConsumerLogService {
}
