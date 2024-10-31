package com.wak.msgspringbootstarter.lock;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author wuankang
 * @date 2024/10/21 19:35
 * @Description 单机锁接口
 * @Version 1.0
 */
public interface LocalLock {

    /**
     * 上锁执行业务操作，上锁失败返回false
     *
     * @param lockKey  锁键
     * @param consumer 消费者
     * @return boolean
     */
    boolean accept(String lockKey, Consumer<String> consumer);

    /**
     * 上锁执行业务操作
     *
     * @param lockKey           锁键
     * @param consumer          消费者
     * @param lockFailException 锁失败异常
     */
    void accept(String lockKey, Consumer<String> consumer, Function<String, ? extends RuntimeException> lockFailException);
}
