package com.jobprep.jobprep_platform.config;
import com.jobprep.jobprep_platform.filter.TraceIdFilter;
import com.jobprep.jobprep_platform.interceptor.TokenInterceptor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;



@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${upload.path:user/ziweigao/Desktop/workspace/jobprep-platform/upload}")
    private String uploadPath;
    @Autowired
    private TokenInterceptor tokenInterceptor;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:"+uploadPath+"/");
    }

    /**
     * Add an interceptor to verify the token
     *  and initialize user-related information during the request cycle.
     */
    @Override 
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login","/error");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter(){
        FilterRegistrationBean<TraceIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TraceIdFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }


}
