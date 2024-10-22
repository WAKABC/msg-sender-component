package com.wak.msgspringbootstarter.filter;

import cn.hutool.core.util.IdUtil;
import com.wak.msgspringbootstarter.utils.TraceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author wuankang
 * @date 2024/10/16 20:42
 * @Description TODO
 * @Version 1.0
 */
@WebFilter(filterName = "traceFilter", urlPatterns = "/*")
@Slf4j
public class TraceFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //设置日志链路ID
        TraceContext.setTraceId(IdUtil.fastSimpleUUID());
        filterChain.doFilter(request, response);
    }
}
