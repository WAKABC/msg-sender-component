package com.wak.msgspringbootstarter.utils;

import com.wak.msgspringbootstarter.entities.MailMessage;
import com.wak.msgspringbootstarter.entities.MsgPO;

/**
 * @author wuankang
 * @date 2024/10/11 17:32
 * @Description 邮件消息工具类
 * @Version 1.0
 */
public class MailMessageUtil {
    /**
     * 组装邮件消息
     *
     * @param msg     消息
     * @param failMsg 失败消息
     * @return {@code MailMessage }
     */
    public static MailMessage assembleMailMessage(MsgPO msg, String failMsg) {
        MailMessage mailMessage = new MailMessage();
        mailMessage.setTopic("消息发送失败已达最大重试次数");
        mailMessage.setTo("1835500316@qq.com");
        mailMessage.setUserName("吴安康");
        mailMessage.setMsgId(msg.getId());
        mailMessage.setMsgFail(failMsg);
        mailMessage.setMsgBody(msg.getBodyJson());
        return mailMessage;
    }
}
