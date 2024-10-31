package com.wak.msgspringbootstarter.utils;

import io.micrometer.common.util.StringUtils;
import org.slf4j.MDC;

/**
 * @author wuankang
 * @date 2024/10/16 20:44
 * @Description TODO
 * @Version 1.0
 */
public class TraceContext {
    private static final String TRACE_ID = "traceId";

    public static void setTraceId(String traceId) {
        if (StringUtils.isNotBlank(traceId)) {
            MDC.put(TRACE_ID, traceId);
        }
    }

    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID);
        return StringUtils.isNotBlank(traceId) ? traceId : "";
    }

    public static void removeTraceId() {
        MDC.remove(TRACE_ID);
    }
}
