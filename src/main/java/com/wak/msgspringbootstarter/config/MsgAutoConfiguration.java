package com.wak.msgspringbootstarter.config;

import com.wak.msgspringbootstarter.config.properties.MsgDelayQueueProperties;
import com.wak.msgspringbootstarter.delay.DelayQueueTaskProcessor;
import com.wak.msgspringbootstarter.job.MsgSendRetryJob;
import com.wak.msgspringbootstarter.lock.DefaultLocalLock;
import com.wak.msgspringbootstarter.lock.LocalLock;
import com.wak.msgspringbootstarter.mail.MailServiceImpl;
import com.wak.msgspringbootstarter.sender.DefaultMsgSenderImpl;
import com.wak.msgspringbootstarter.sender.IMsgSender;
import com.wak.msgspringbootstarter.service.IMsgService;
import com.wak.msgspringbootstarter.service.ISequentialMsgNumberGeneratorService;
import com.wak.msgspringbootstarter.service.impl.IMsgServiceImpl;
import com.wak.msgspringbootstarter.service.impl.SequentialMsgNumberGeneratorServiceImpl;
import jakarta.annotation.Resource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author wuankang
 * @date 2024/10/8 19:33
 * @Description 消息服务配置类
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties(MsgDelayQueueProperties.class)
@MapperScan("com.wak.msgspringbootstarter.mapper")
@Import({
    ThreadPoolConfiguration.class,
    RabbitMQConfiguration.class,
    TraceConfiguration.class,
    MyBatisPlusConfiguration.class
})
@EnableScheduling
public class MsgAutoConfiguration {
    /**
     * 延迟队列属性类
     */
    @Resource
    private MsgDelayQueueProperties properties;
    /**
     * 线程池任务执行器
     */
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    /**
     * rabbitmq
     */
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * transaction
     */
    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 延迟消息处理器
     *
     * @return {@code DelayQueueTaskProcessor }
     */
    @Bean
    public DelayQueueTaskProcessor delayMsgProcessor() {
        return new DelayQueueTaskProcessor("delayMsgProcessor", properties.getDelayMsgDelayQueueCapacity(), Runtime.getRuntime().availableProcessors());
    }

    /**
     * 延迟发送重试处理器
     *
     * @return {@code DelayQueueTaskProcessor }
     */
    @Bean
    public DelayQueueTaskProcessor delaySendRetryProcessor() {
        return new DelayQueueTaskProcessor("delaySendRetryProcessor", properties.getDelaySendRetryDelayQueueCapacity(), Runtime.getRuntime().availableProcessors());
    }

    /**
     * 邮件服务
     *
     * @return {@code MailServiceImpl }
     */
    @Bean
    public MailServiceImpl mailService() {
        return new MailServiceImpl();
    }

    @Bean
    public LocalLock localLock() {
        return new DefaultLocalLock();
    }

    @Bean
    public ISequentialMsgNumberGeneratorService numberGeneratorService() {
        return new SequentialMsgNumberGeneratorServiceImpl();
    }

    /**
     * 默认消息发件人暗示
     *
     * @return {@code msgSender }
     */
    @Bean
    public IMsgSender msgSender() {
        return new DefaultMsgSenderImpl(msgService(), mailService(), rabbitTemplate, threadPoolTaskExecutor, delayMsgProcessor(), delaySendRetryProcessor(), localLock(), transactionTemplate, numberGeneratorService());
    }

    /**
     * 消息service
     *
     * @return {@code IMsgService }
     */
    @Bean
    @ConditionalOnBean({DelayQueueTaskProcessor.class, IMsgSender.class})
    public IMsgService msgService() {
        return new IMsgServiceImpl();
    }

    @Bean
    public MsgSendRetryJob msgSendRetryJob() {
        return new MsgSendRetryJob(msgService(), msgSender());
    }
}
