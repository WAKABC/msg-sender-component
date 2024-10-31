package com.wak.msgspringbootstarter.entities;

import lombok.Data;

/**
 * @author wuankang
 * @date 2024/10/10 19:41
 * @Description 邮件模板属性
 * @Version 1.0
 */
@Data
public class MailMessage {
    /**
     * 邮件主题
     */
    private String topic;
    /**
     * 收件人
     */
    private String to;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 消息ID
     */
    private String msgId;
    /**
     * 消息失败信息
     */
    private String msgFail;
    /**
     * 消息体
     */
    private String msgBody;
}
