package com.bolingx.ai.security;

import com.bolingx.common.model.Message;
import com.bolingx.common.model.config.DebugConfig;
import com.bolingx.common.web.servlet.hepler.ResponseHelper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Resource
    private ResponseHelper responseHelper;

    @Resource
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
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .rememberMe(rememberMe -> {
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
                ;
    }

    private static final class CsrfMatcher implements RequestMatcher {

        private final HashSet<String> allowedMethods = new HashSet<>(Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS"));

        private final Collection<String> ignoreList;

        private CsrfMatcher(Collection<String> ignoreList) {
            this.ignoreList = ignoreList;
        }

        @Override
        public boolean matches(HttpServletRequest request) {
            if (ignoreList.contains(request.getServletPath())) {
                return false;
            }
            return !this.allowedMethods.contains(request.getMethod());
        }

        @Override
        public String toString() {
            return "CsrfNotRequired " + this.allowedMethods;
        }

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
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
//        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
//        jdbcTokenRepository.setDataSource(dataSource);
        return new InMemoryTokenRepositoryImpl();
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
}
