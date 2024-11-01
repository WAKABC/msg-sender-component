package com.wak.consumemsg.enums;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * @author wuankang
 * @Date 2024/10/31 17:02:19
 * @Description TODO 延迟时间枚举
 * @Version 1.0
 */
@Getter
public enum DelayLevelEnums {
    // 1s 2s 3s 5s 10s 20s 30s 1m 2m 3m 5m 10m 20m 30m 1h 2h 1d
    SECOND_1(TimeUnit.SECONDS.toMillis(1), "1s"),
    SECOND_2(TimeUnit.SECONDS.toMillis(2), "2s"),
    SECOND_3(TimeUnit.SECONDS.toMillis(3), "3s"),
    SECOND_5(TimeUnit.SECONDS.toMillis(5), "5s"),
    SECOND_10(TimeUnit.SECONDS.toMillis(10), "10s"),
    SECOND_20(TimeUnit.SECONDS.toMillis(20), "20s"),
    SECOND_30(TimeUnit.SECONDS.toMillis(30), "30s"),
    MINUTE_1(TimeUnit.MINUTES.toMillis(1), "1m"),
    MINUTE_2(TimeUnit.MINUTES.toMillis(2), "2m"),
    MINUTE_3(TimeUnit.MINUTES.toMillis(3), "3m"),
    MINUTE_5(TimeUnit.MINUTES.toMillis(5), "5m"),
    MINUTE_10(TimeUnit.MINUTES.toMillis(10), "10m"),
    HOUR_1(TimeUnit.HOURS.toMillis(1), "1h"),
    HOUR_2(TimeUnit.HOURS.toMillis(2), "2h"),
    DAY_1(TimeUnit.DAYS.toMillis(1), "1d");

    /**
     * 延迟时间，单位：毫秒
     */
    private final long delayTimeInMills;
    /**
     * 描述
     */
    private final String desc;

    DelayLevelEnums(long delayTimeInMills, String desc) {
        this.delayTimeInMills = delayTimeInMills;
        this.desc = desc;
    }

}
