package com.wkr.store_appointment.config;

import com.wkr.store_appointment.interceptor.ReadOnlyModeInterceptor;
import com.wkr.store_appointment.interceptor.AccountReadOnlyInterceptor;
import com.wkr.store_appointment.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ReadOnlyModeInterceptor readOnlyModeInterceptor;
    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;
    @Autowired
    private AccountReadOnlyInterceptor accountReadOnlyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/api/**", "/admin/**")
                .excludePathPatterns("/api/login");
        registry.addInterceptor(accountReadOnlyInterceptor)
                .addPathPatterns("/api/**", "/admin/**")
                .excludePathPatterns("/api/login");
        registry.addInterceptor(readOnlyModeInterceptor).addPathPatterns("/**");
    }
}
