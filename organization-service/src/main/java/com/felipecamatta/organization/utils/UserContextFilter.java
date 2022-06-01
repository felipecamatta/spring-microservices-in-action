package com.felipecamatta.organization.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class UserContextFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(UserContextFilter.class);

    private final Tracer tracer;

    public UserContextFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse
            servletResponse, FilterChain filterChain) throws IOException,
            ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        UserContextHolder.getContext();
        UserContext.setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
        UserContext.setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));

        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            String traceId = currentSpan.context().traceId();
            logger.debug("Organization Service Incoming Correlation id: {}", traceId);
        }

        filterChain.doFilter(httpServletRequest, servletResponse);
    }

}
