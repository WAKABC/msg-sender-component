package com.wak.msgspringbootstarter.common;

/**
 * @author wuankang
 * @date 2024/10/18 12:00
 * @Description 常量
 * @Version 1.0
 */
public interface Constant {
    /**
     * 日志链路ID，MDC key
     */
    String TRACE_ID = "traceId";

    /**
     * 最大重试次数
     */
    int MAX_RETRY_COUNT = 3;
}
