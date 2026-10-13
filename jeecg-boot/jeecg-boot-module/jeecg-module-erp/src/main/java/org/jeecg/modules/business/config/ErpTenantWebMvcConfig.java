package org.jeecg.modules.business.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ErpTenantWebMvcConfig implements WebMvcConfigurer {

    private final ErpTenantHeaderInterceptor erpTenantHeaderInterceptor;

    public ErpTenantWebMvcConfig(ErpTenantHeaderInterceptor erpTenantHeaderInterceptor) {
        this.erpTenantHeaderInterceptor = erpTenantHeaderInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(erpTenantHeaderInterceptor).addPathPatterns("/business/**");
    }
}
