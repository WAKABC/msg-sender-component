package com.wak.msgspringbootstarter.utils;

import com.wak.msgspringbootstarter.common.Constant;
import io.micrometer.common.util.StringUtils;
import org.slf4j.MDC;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuankang
 * @date 2024/10/17 17:39
 * @Description MDC工具类
 * @Version 1.0
 */
public class MDCUtil {
    private static final ConcurrentHashMap<String, String> MDC_MAP = new ConcurrentHashMap<>();

    public static void put(String key) {
        String traceId = TraceContext.getTraceId();
        if (StringUtils.isNotBlank(traceId)) {
            MDC_MAP.put(key, traceId);
        }
    }

    public static String get(String key) {
        String value = MDC_MAP.get(key);
        return StringUtils.isNotBlank(value) ? value : "";
    }

    public static void remove(String key) {
        MDC_MAP.remove(key);
        MDC.remove(Constant.TRACE_ID);
    }

}
