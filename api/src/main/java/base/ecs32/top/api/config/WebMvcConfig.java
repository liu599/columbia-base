package base.ecs32.top.api.config;

import base.ecs32.top.api.interceptor.AdminInterceptor;
import base.ecs32.top.api.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/v1/user/register",
                        "/api/v1/user/login",
                        "/api/v1/user/health"
                );

        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/hyancie/api/v1/admin/**");
    }
}
