package com.felipecamatta.licensing.config;

import com.felipecamatta.licensing.utils.UserContext;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.request().url();
            requestTemplate.request().httpMethod();
            requestTemplate.header("Authorization", UserContext.getAuthToken());
        };
    }

}
