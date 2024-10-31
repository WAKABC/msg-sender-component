package com.wak.msgspringbootstarter.mail;

import cn.hutool.core.bean.BeanUtil;
import com.wak.msgspringbootstarter.entities.MailMessage;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.Map;


/**
 * @author wuankang
 * @date 2024/10/10 19:03
 * @Description 邮件发送实现类
 * @Version 1.0
 */
public class MailServiceImpl implements IMailService {
    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);
    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sendUserMailName;

    @Resource
    private TemplateEngine templateEngine;

    /**
     * 发送简单邮件
     *
     * @return {@code String }
     */
    @Override
    public String sendMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        //发送人
        message.setFrom(sendUserMailName);
        //主题
        message.setSubject("事务消息投递失败");
        //收件人，多个用,隔开
        message.setTo("1835500316@qq.com");
        message.setSentDate(new Date());
        message.setText("msg投递到mq失败，已达最大重试次数，请及时处理！");
        javaMailSender.send(message);
        return "send ok.";
    }

    /**
     * 使用模板发送
     *
     * @return {@code String }
     */
    @Override
    public String sendThymeleaf(MailMessage mailMessage) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            //设置邮件主题部分
            helper.setSubject(mailMessage.getTopic());
            helper.setFrom(sendUserMailName);
            helper.setTo(mailMessage.getTo());
            helper.setSentDate(new Date());
            //设置邮件内容
            Context context = new Context();
            //设置模板变量
            Map<String, Object> error = BeanUtil.beanToMap(mailMessage);
            error.forEach(context::setVariable);
            //第一个参数作为模板的，名称
            String process = templateEngine.process("email.html", context);
            //第二个参数true表示这是html文本
            helper.setText(process, true);
            javaMailSender.send(message);
            log.info("邮件发送成功");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return "send ok.";
    }
}
