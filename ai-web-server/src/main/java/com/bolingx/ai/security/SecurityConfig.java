package com.bolingx.ai.security;

import com.bolingx.ai.mapper.UserMapper;
import com.bolingx.ai.security.user.UserDetailServiceImpl;
import com.bolingx.ai.service.UserService;
import com.bolingx.common.model.Message;
import com.bolingx.common.model.config.DebugConfig;
import com.bolingx.common.web.servlet.hepler.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private ResponseHelper responseHelper;

    private DebugConfig debugConfig;

    private HttpSecurity httpSecurity;

    public HttpSecurity httpSecurity(HttpSecurity http,
                                     PersistentTokenRepository tokenRepository,
                                     SecurityContextRepository securityContextRepository,
                                     SecurityProperties securityProperties) throws Exception {
        return http
                .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                        .requestMatchers(new AntPathRequestMatcher("/druid/**")).permitAll()
                        .anyRequest().permitAll()
                )
                .cors(corsConfigurer -> {
                    if (debugConfig.isCorsAllOrigin()) {
                        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                        CorsConfiguration config = new CorsConfiguration();
                        config.addAllowedOriginPattern("*");
                        config.addAllowedHeader("*");
                        config.addAllowedMethod("*");
                        config.setAllowCredentials(true);
                        source.registerCorsConfiguration("/**", config); // CORS 配置对所有接口都有效
                        corsConfigurer.configurationSource(source);
                    }
                })
                .csrf(AbstractHttpConfigurer::disable)
                .rememberMe(rememberMe -> {
                    rememberMe.rememberMeParameter("rememberMe");
                    rememberMe.key("rememberMeKey");
                    rememberMe.tokenRepository(tokenRepository);
                    rememberMe.rememberMeCookieDomain(securityProperties.getRememberMeCookieDomain());
                })
                .securityContext(contextConfigurer -> contextConfigurer.securityContextRepository(securityContextRepository))
                .exceptionHandling(eh -> {
                    eh.accessDeniedHandler((req, res, ex) -> {
                        if (debugConfig.isEnableDetailedErrorMessages()) {
                            responseHelper.writeRes(res, Message.REQUEST_ERROR, ex.getMessage());
                        }
                        res.setStatus(HttpStatus.FORBIDDEN.value());
                    });
                    eh.authenticationEntryPoint((req, res, ex) -> {
                        if (debugConfig.isEnableDetailedErrorMessages()) {
                            responseHelper.writeRes(res, Message.REQUEST_ERROR, ex.getMessage());
                        }
                        res.setStatus(HttpStatus.FORBIDDEN.value());
                    });
                })
                .logout(logoutConfigurer -> logoutConfigurer.logoutUrl("/user/logout")
                        .logoutSuccessHandler((req, res, auth) -> responseHelper.writeRes(res, Message.SUCCESS_CODE)))
                ;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   PersistentTokenRepository tokenRepository,
                                                   SecurityContextRepository securityContextRepository,
                                                   SecurityProperties securityProperties) throws Exception {
        this.httpSecurity = httpSecurity(http, tokenRepository, securityContextRepository, securityProperties);
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public UserDetailsService userDetailsService(UserMapper userMapper) {
        UserDetailServiceImpl userDetailService = new UserDetailServiceImpl();
        userDetailService.setUserMapper(userMapper);
        return userDetailService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    @DependsOn("securityFilterChain")
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return httpSecurity.getSharedObject(SessionAuthenticationStrategy.class);
    }

    @Bean
    @DependsOn("securityFilterChain")
    public RememberMeServices rememberMeServices() {
        return httpSecurity.getSharedObject(RememberMeServices.class);
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository());
    }

    @Autowired
    public void setResponseHelper(ResponseHelper responseHelper) {
        this.responseHelper = responseHelper;
    }

    @Autowired
    public void setDebugConfig(DebugConfig debugConfig) {
        this.debugConfig = debugConfig;
    }


}
