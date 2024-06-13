package com.bolingx.erp.mvc;

import com.alibaba.druid.support.jakarta.StatViewServlet;
import com.alibaba.druid.support.jakarta.WebStatFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.bolingx.common.model.config.DebugConfig;
import com.bolingx.common.web.secret.AppSecretDao;
import com.bolingx.common.web.secret.AppSecretService;
import com.bolingx.common.web.secret.DefaultAppSecretService;
import com.bolingx.common.web.servlet.filter.RequestValidationConfig;
import com.bolingx.common.web.servlet.filter.RequestValidationFilter;
import com.bolingx.common.web.servlet.hepler.ResponseHelper;
import com.bolingx.erp.mvc.interceptor.UserTokenVerifyInterceptor;
import com.bolingx.erp.mvc.resolver.UserArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    public static final int FILTER_ORDER_CORS = -3;

    public static final int FILTER_ORDER_WEB_STAT = -2;

    public static final int FILTER_ORDER_REQUEST_VALIDATION = -1;

    private DebugConfig debugConfig;

    @Bean
    public UserArgumentResolver userArgumentResolver(){
        return new UserArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
        resolvers.add(userArgumentResolver());
    }

    @Bean
    public UserTokenVerifyInterceptor userTokenVerifyInterceptor() {
        return new UserTokenVerifyInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(userTokenVerifyInterceptor());
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        if(debugConfig.isCorsAllOrigin()) {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedOriginPattern("*");
            config.addAllowedHeader("*");
            config.addAllowedMethod("*");
            config.setAllowCredentials(true);
            source.registerCorsConfiguration("/**", config); // CORS 配置对所有接口都有效
        }
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(FILTER_ORDER_CORS);
        return bean;
    }

    @Bean
    public AppSecretService appSecretService(AppSecretDao appSecretDao) {
        return new DefaultAppSecretService(appSecretDao);
    }

    @Bean
    public RequestValidationFilter requestValidationFilter(ServerProperties serverProperties,
                                                           ResponseHelper responseHelper,
                                                           AppSecretService appSecretService) {
        RequestValidationConfig config = new RequestValidationConfig();
        config.setErrorPage(serverProperties.getError().getPath());
        config.getIgnorePathSet().add("/druid/**");
        config.getIgnorePathSet().add("/inner/**");
        return new RequestValidationFilter(config, responseHelper, appSecretService);
    }


    @Bean
    public FilterRegistrationBean<RequestValidationFilter> myFilterFilterRegistrationBean(RequestValidationFilter filter) {
        FilterRegistrationBean<RequestValidationFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(filter);
        bean.setOrder(FILTER_ORDER_REQUEST_VALIDATION);
        bean.setUrlPatterns(List.of("/*"));
        return bean;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        // 这些参数可以在 com.alibaba.druid.support.http.StatViewServlet
        // 的父类 com.alibaba.druid.support.http.ResourceServlet 中找到
        Map<String, String> initParams = new HashMap<>();
        initParams.put("loginUsername", "admin");
        //后台管理界面的登录账号（固定参数）
        initParams.put("loginPassword", "123456");
        //后台管理界面的登录密码（固定参数）
        //后台允许谁可以访问
        //设置初始化参数
        bean.setInitParameters(initParams);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> webStatFilter() {
        FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());
        //exclusions：设置哪些请求进行过滤排除掉，从而不进行统计
        Map<String, String> initParams = new HashMap<>();
        initParams.put("exclusions", "*.js,*.css,/druid/*,/jdbc/*");
        bean.setInitParameters(initParams);
        //"/*" 表示过滤所有请求
        bean.setUrlPatterns(List.of("/*"));
        bean.setOrder(FILTER_ORDER_WEB_STAT);
        return bean;
    }

    @Autowired
    public void setDebugConfig(DebugConfig debugConfig) {
        this.debugConfig = debugConfig;
    }
}
