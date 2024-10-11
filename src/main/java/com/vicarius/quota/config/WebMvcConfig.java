package com.vicarius.quota.config;

import com.vicarius.quota.interceptor.ConsumeQuotaInterceptor;
import com.vicarius.quota.service.UserQuotaService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ConsumeQuotaInterceptor consumeQuotaInterceptor;

    public WebMvcConfig(ConsumeQuotaInterceptor consumeQuotaInterceptor) {
        this.consumeQuotaInterceptor = consumeQuotaInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(consumeQuotaInterceptor)
                .addPathPatterns("/users/*/quota");
    }
}
