package com.wak.msgspringbootstarter.mail;

import com.wak.msgspringbootstarter.entities.MailMessage;

/**
 * @author wuankang
 * @date 2024/10/10 18:59
 * @Description 邮件发送接口
 * @Version 1.0
 */
public interface IMailService {
    /**
     * 发送邮件
     *
     * @return {@code String }
     */
    String sendMail();

    /**
     * 使用Thymeleaf模板
     *
     * @return {@code String }
     */
    String sendThymeleaf(MailMessage message);
}
