package com.wak.consumemsg.uitls;

import com.google.common.collect.Range;
import com.wak.consumemsg.entities.MsgConsume;
import com.wak.consumemsg.enums.DelayLevelEnums;
import com.wak.consumemsg.retry.MsgConsumeRetryResult;
import com.wak.msgspringbootstarter.common.Constant;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wuankang
 * @Date 2024/10/31 17:08:47
 * @Description 重试结果工具类
 * @Version 1.0
 */
public class MsgConsumeRetryResultUtil {

    /**
     * 延迟等级集合
     */
    private static final Map<Range<Integer>, DelayLevelEnums> DELAY_LEVEL_MAP = new LinkedHashMap<>();

    static {
        DELAY_LEVEL_MAP.put(Range.closed(0, 5), DelayLevelEnums.SECOND_10);
        DELAY_LEVEL_MAP.put(Range.closed(6, 10), DelayLevelEnums.SECOND_30);
        DELAY_LEVEL_MAP.put(Range.closed(11, 15), DelayLevelEnums.MINUTE_1);
        DELAY_LEVEL_MAP.put(Range.closed(16, 20), DelayLevelEnums.MINUTE_5);
        DELAY_LEVEL_MAP.put(Range.closed(21, 50), DelayLevelEnums.MINUTE_10);
    }

    /**
     * 获取延迟时长
     * <p>
     *  failureCount在{@link #DELAY_LEVEL_MAP}中找到对应的{@link DelayLevelEnums},
     *  <p>
     *  如果{@link #DELAY_LEVEL_MAP}中没有找到对应的{@link DelayLevelEnums},则采用{@link DelayLevelEnums#HOUR_1}
     *
     * @param failCount 失败次数
     * @return 延迟时长
     */
    private static long getDelayTimeInMills(int failCount) {
        for (Map.Entry<Range<Integer>, DelayLevelEnums> entry : DELAY_LEVEL_MAP.entrySet()) {
            Range<Integer> range = entry.getKey();
            if (range.contains(failCount)) {
                return entry.getValue().getDelayTimeInMills();
            }
        }
        return DelayLevelEnums.HOUR_1.getDelayTimeInMills();
    }

    /**
     * 获取消息消费重试结果
     * <p>
     * 根据消息消费记录中的失败次数，判断是否可以继续重试。
     * 如果失败次数小于最大重试次数，则计算下次重试时间并设置重试标志为true。
     * 否则，设置重试标志为false。
     *
     * @param msgConsumerPo 消息消费记录对象
     * @return {@link MsgConsumeRetryResult} 包含重试标志和下次重试时间的信息
     */
    public static MsgConsumeRetryResult getRetryResult(MsgConsume msgConsumerPo) {
        MsgConsumeRetryResult mqSendRetryResult = new MsgConsumeRetryResult();
        //当前失败次数 < 最大重试次数，则可以继续重试
        if (msgConsumerPo.getFailCount() < Constant.MAX_RETRY_COUNT) {
            //获取延迟时间
            long delayTimeInMills = getDelayTimeInMills(msgConsumerPo.getFailCount());
            //下次重试时间（当前时间+延迟时间）
            LocalDateTime nextRetryTime = LocalDateTime.now().plus(Duration.ofMillis(delayTimeInMills));
            mqSendRetryResult.setNextRetryTime(nextRetryTime);
            mqSendRetryResult.setRetry(true);
        } else {
            mqSendRetryResult.setRetry(false);
        }
        return mqSendRetryResult;
    }
}
