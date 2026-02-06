package base.ecs32.top.api.config;

import base.ecs32.top.api.interceptor.AdminInterceptor;
import base.ecs32.top.api.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/api/user/register", "/api/user/login", "/api/user/health");

        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/api/v1/admin/**");
    }
}
