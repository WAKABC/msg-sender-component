package com.wak.msgspringbootstarter.delay;

import com.wak.msgspringbootstarter.utils.ThreadPoolUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuankang
 * @date 2024/9/30 17:29
 * @Description 延迟任务执行器
 * @Version 1.0
 */
public class DelayQueueTaskProcessor implements Runnable, DisposableBean {
    /**
     * 日志记录器
     */
    private static final Logger log = LoggerFactory.getLogger(DelayQueueTaskProcessor.class);
    /**
     * 队列名称
     */
    private final String queueName;
    /**
     * 容量
     */
    private final int capacity;
    /**
     * 并发线程数
     */
    private final int concurrency;
    /**
     * 延迟队列
     */
    private final DelayQueue<DelayTask> delayQueue = new DelayQueue<>();
    /**
     * 停止
     */
    private volatile boolean stop = false;
    /**
     * 待处理任务大小
     */
    private final AtomicInteger PendingTaskSize = new AtomicInteger();
    /**
     * 消费者线程池
     */
    private ThreadPoolTaskExecutor taskExecutor = null;
    /**
     * 延迟队列任务处理器
     *
     * @param queueName   队列名称
     * @param capacity    容量
     * @param concurrency 并发性
     */
    public DelayQueueTaskProcessor(String queueName, int capacity, int concurrency) {
        this.queueName = queueName;
        this.capacity = capacity;
        this.concurrency = concurrency;
    }

    /**
     * 获取队列名称
     *
     * @return {@code String }
     */
    private String getQueueName() {
        return String.format("DelayTaskProcessor-ConsumerThread-%s", this.queueName);
    }

    /**
     * 添加任务到延迟队列
     *
     * @param taskExecuteTimeMs 任务执行时间
     * @param task              任务
     * @return boolean
     */
    public boolean put(long taskExecuteTimeMs, Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("delay task is null");
        }
        if (delayQueue.size() == capacity) {
            if (log.isInfoEnabled()) {
                log.info("队列已达最大容量上限，无法添加.");
            }
            return false;
        }
        //添加延迟任务
        DelayTask delayTask = new DelayTask(task, taskExecuteTimeMs);
        boolean offer = delayQueue.offer(delayTask);
        if (offer) {
            log.info("延迟任务添加成功.");
            //任务数+1
            PendingTaskSize.incrementAndGet();
        }
        return true;
    }

    @Override
    public void run() {
        while (true) {
            if (stop) {
                break;
            }
            //取出任务消费，没有任务时阻塞
            DelayTask delayTask = delayQueue.poll();
            if (Objects.isNull(delayTask)) {
                continue;
            }
            Runnable task = delayTask.getTask();
            if (task == null) {
                continue;
            }
            //任务数-1
            PendingTaskSize.decrementAndGet();
            //执行任务
            this.taskRun(task);
        }
    }

    /**
     * 任务运行
     */
    private void taskRun(Runnable task) {
        Throwable throwable = null;
        try {
            //执行本地任务
            task.run();
        } catch (Exception e) {
            //任务执行出错
            if (log.isErrorEnabled()) {
                log.error("task run error: {}", e.getMessage(), e);
            }
            throwable = e;
        } finally {
            //后置处理，钩子函数
            this.afterExecute(task, throwable);
        }
    }

    /**
     * 根据并发数初始化延迟队列处理器
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initProcess() {
        taskExecutor = ThreadPoolUtil.getInstance(getQueueName(), concurrency);
        for (int i = 0; i < concurrency; i++) {
            taskExecutor.execute(this);
        }
    }

    /**
     * 销毁程序
     *
     * @throws Exception 异常
     */
    @Override
    public void destroy() throws Exception {
        //设置停止循环，中断消费者线程
        this.stop = true;
        taskExecutor.shutdown();
    }

    /**
     * @author wuankang
     * @version 1.0.0
     * @date 2024/10/08
     * @description 延迟任务
     */
    @Getter
    @Setter
    static class DelayTask implements Delayed {
        /**
         * 执行的任务
         */
        private Runnable task;
        /**
         * 延时时长
         */
        private long delayTime;

        public DelayTask(Runnable task, long delayTime) {
            this.task = task;
            this.delayTime = delayTime;
        }

        /**
         * 获取剩余延时时间
         *
         * @param unit 单位
         * @return long
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(@NonNull Delayed o) {
            if (o instanceof DelayTask o1) {
                return Long.compare(this.getDelayTime(), o1.getDelayTime());
            }
            return 0;
        }
    }

    /**
     * 任务执行完成后回调
     *
     * @param task 任务
     * @param t    异常信息
     */
    protected void afterExecute(Runnable task, Throwable t) {
    }
}