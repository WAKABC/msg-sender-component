package com.wak.msgspringbootstarter.utils;

import cn.hutool.core.util.StrUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wuankang
 * @date 2024/7/9 11:56
 * @Description TODO 懒汉单例线程池
 * @Version 1.0
 */
public class ThreadPoolUtil {

    private static volatile ThreadPoolTaskExecutor taskExecutor;

    /*私有化构造器*/
    private ThreadPoolUtil() {
    }

    /**
     * 获得实例，名称一样的用一个，不同的新建
     *
     * @return {@code ThreadPoolTaskExecutor }
     */
    public static ThreadPoolTaskExecutor getInstance(String threadPoolName, int concurrency) {
        if (taskExecutor == null || !StrUtil.equals(taskExecutor.getThreadNamePrefix(), threadPoolName)) {
            synchronized (ThreadPoolUtil.class) {
                if (taskExecutor == null || !StrUtil.equals(taskExecutor.getThreadNamePrefix(), threadPoolName)) {
                    generateTaskExecutor(threadPoolName, concurrency);
                }
            }
        }
        return taskExecutor;
    }

    /**
     * 创建任务执行器
     */
    private static void generateTaskExecutor(String threadPoolName, int concurrency) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(concurrency);
        threadPoolTaskExecutor.setMaxPoolSize(concurrency);
        threadPoolTaskExecutor.setKeepAliveSeconds(0);
        threadPoolTaskExecutor.setQueueCapacity(100);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setThreadNamePrefix(threadPoolName);
        //等待所有任务完成后再关闭线程池
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setAwaitTerminationSeconds(3);
        //启用所有核心线程
        threadPoolTaskExecutor.setPrestartAllCoreThreads(true);
        threadPoolTaskExecutor.initialize();
        taskExecutor = threadPoolTaskExecutor;
    }
}
