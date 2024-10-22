package com.wak.msgspringbootstarter.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author wuankang
 * @date 2024/10/21 19:32
 * @Description 使用reentrantLock实现的单机锁
 * @Version 1.0
 */
public class DefaultLocalLock implements LocalLock {
    private static final Logger log = LoggerFactory.getLogger(DefaultLocalLock.class);
    private static Map<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    /**
     * 获取可重入锁
     *
     * @param key key
     * @return {@code ReentrantLock }
     */
    private ReentrantLock getReentrantLock(String key) {
        return lockMap.computeIfAbsent(key, val -> new ReentrantLock());
    }

    @Override
    public boolean accept(String lockKey, Consumer<String> consumer) {
        Objects.requireNonNull(lockKey);
        Objects.requireNonNull(consumer);
        ReentrantLock reentrantLock = getReentrantLock(lockKey);
        boolean flag = reentrantLock.tryLock();
        try {
            if (log.isInfoEnabled()) {
                log.info("get lock:[{}],[{}]", lockKey, flag);
            }
            if (!flag) {
                return false;
            }
            consumer.accept(lockKey);
        } finally {
            if (flag) {
                reentrantLock.unlock();
            }
        }
        return true;
    }

    @Override
    public void accept(String lockKey, Consumer<String> consumer, Function<String, ? extends RuntimeException> lockFailException) {
        boolean success = this.accept(lockKey, consumer);
        if (!success){
            throw lockFailException.apply(lockKey);
        }
    }
}
