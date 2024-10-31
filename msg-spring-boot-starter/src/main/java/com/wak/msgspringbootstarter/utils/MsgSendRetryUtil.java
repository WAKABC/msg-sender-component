package com.wak.msgspringbootstarter.utils;

import com.google.common.collect.Range;
import com.wak.msgspringbootstarter.common.Constant;
import com.wak.msgspringbootstarter.entities.MsgSendRetryPO;
import com.wak.msgspringbootstarter.enums.MsgSendRetryDelayLevelEnum;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author wuankang
 * @date 2024/10/10 16:44
 * @Description 消息重试工具类
 * @Version 1.0
 */
public class MsgSendRetryUtil {
    /**
     * 延迟等级集合
     */
    private final static HashMap<Range<Integer>, MsgSendRetryDelayLevelEnum> DELAY_LEVEL_MAP = new HashMap<>();

    static {
        DELAY_LEVEL_MAP.put(Range.closed(0, 10), MsgSendRetryDelayLevelEnum.SECOND_10);
        DELAY_LEVEL_MAP.put(Range.closed(11, 20), MsgSendRetryDelayLevelEnum.SECOND_20);
        DELAY_LEVEL_MAP.put(Range.closed(21, 30), MsgSendRetryDelayLevelEnum.SECOND_30);
        DELAY_LEVEL_MAP.put(Range.closed(31, 40), MsgSendRetryDelayLevelEnum.MINUTE_1);
        DELAY_LEVEL_MAP.put(Range.closed(41, 45), MsgSendRetryDelayLevelEnum.MINUTE_2);
        DELAY_LEVEL_MAP.put(Range.closed(46, 50), MsgSendRetryDelayLevelEnum.MINUTE_5);
    }

    /**
     * 获取延迟时间
     *
     * @param retryCount 重试次数
     * @return long
     */
    private static long getDelayTime(int retryCount){
        //从map中获取枚举对象
        for (Range<Integer> range : DELAY_LEVEL_MAP.keySet()) {
            if (range.contains(retryCount)){
                MsgSendRetryDelayLevelEnum delayLevelEnum = DELAY_LEVEL_MAP.get(range);
                return delayLevelEnum.getDelayTimeInMills();
            }
        }
        return MsgSendRetryDelayLevelEnum.HOUR_1.getDelayTimeInMills();
    }

    /**
     * 得到消息发送重试信息对象
     *
     * @param retryCount 重试次数
     * @return {@code MsgSendRetryPO }
     */
    public static MsgSendRetryPO getMsgSendRetry(int retryCount){
        //消息重试对象
        MsgSendRetryPO msgSendRetry = new MsgSendRetryPO();
        if (retryCount > Constant.MAX_RETRY_COUNT){
            msgSendRetry.setSendRetry(0);
            return msgSendRetry;
        }
        long delayTime = getDelayTime(retryCount);
        //期望发送时间
        LocalDateTime nextSendRetryTime = LocalDateTime.now().plus(Duration.ofMillis(delayTime));
        msgSendRetry.setSendRetry(1);
        msgSendRetry.setNextSendRetryTime(nextSendRetryTime);
        return msgSendRetry;
    }
}
