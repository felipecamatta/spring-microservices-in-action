package com.felipecamatta.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.sleuth.Span;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class ResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Span currentSpan = exchange.getAttribute(Span.class.getName());
            if (currentSpan != null) {
                String traceId = currentSpan.context().traceId();
                logger.debug("Adding the correlation id to the outbound headers {}.", traceId);
                exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, traceId);
            }
            logger.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
        }));
    }

}
