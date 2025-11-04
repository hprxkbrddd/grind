package com.grind.security.autoconfiguration;

import com.grind.security.spring.exception.SecurityExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ExceptionHandlerAutoConfig {

    @Bean
    @ConditionalOnMissingBean(SecurityExceptionHandler.class)
    public SecurityExceptionHandler securityExceptionHandler(){
        return new SecurityExceptionHandler();
    }
}
