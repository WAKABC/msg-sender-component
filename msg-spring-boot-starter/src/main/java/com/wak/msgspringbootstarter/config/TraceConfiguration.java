package com.wak.msgspringbootstarter.config;

import com.wak.msgspringbootstarter.filter.TraceFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuankang
 * @date 2024/10/16 20:49
 * @Description TODO
 * @Version 1.0
 */
@Configuration
public class TraceConfiguration {
    @Bean
    public TraceFilter traceFilter() {
        return new TraceFilter();
    }
}
