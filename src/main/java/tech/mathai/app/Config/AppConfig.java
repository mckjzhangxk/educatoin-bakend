package tech.mathai.app.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.mathai.app.Interceptor.LoginInterceptor;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Bean
    public LoginInterceptor getLoginInterceptor(){
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(getLoginInterceptor())
//                .excludePathPatterns("/*")
//                .excludePathPatterns("/api/login")
//                .excludePathPatterns("/api/verify")
//                .excludePathPatterns("/api/logout")
//                .addPathPatterns("/api/**");
    }
}
