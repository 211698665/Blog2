package cn.henu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置静态资源的访问路径
 *
 * @author syw
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 静态文件过滤器
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        //文件上传路径的映射
        registry.addResourceHandler("/image/**").addResourceLocations("file:C:/image/");
    }

    //自定义拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        LoginHandlerInterceptor loginInceptor = new LoginHandlerInterceptor();//自定义的拦截器
        registry.addInterceptor(loginInceptor).addPathPatterns("/**")
                .excludePathPatterns(loginInceptor.getUrl());
        WebMvcConfigurer.super.addInterceptors(registry);
        //"/login","/","/admin/login","/static/**","/webjars/**","/admin/css/**","/admin/js/**"
    }
}
