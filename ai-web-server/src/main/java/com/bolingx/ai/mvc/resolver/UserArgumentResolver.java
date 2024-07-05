package com.bolingx.ai.mvc.resolver;

import com.bolingx.ai.annotation.AutoUser;
import com.bolingx.ai.entity.UserEntity;
import com.bolingx.ai.exception.user.AutoUserException;
import com.bolingx.ai.security.user.UserDetailImpl;
import com.bolingx.ai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return UserEntity.class.isAssignableFrom(parameter.getParameterType()) && parameter.hasParameterAnnotation(AutoUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        final AutoUser autoUser = parameter.getParameterAnnotation(AutoUser.class);
        assert autoUser != null;

        boolean isLogin = authentication != null && authentication.isAuthenticated();
        if (!isLogin && autoUser.required()) {
            throw new AutoUserException(autoUser.message());
        } else {
            if (!isLogin) {
                return null;
            }
            Object principal = authentication.getPrincipal();
            UserEntity userEntity;
            if(principal instanceof UserDetailImpl detail){
                userEntity = userService.selectByUsername(detail.getUsername());
            } else {
                throw new RuntimeException("未知的身份类型");
            }
            if (userEntity == null) {
                throw new AutoUserException(autoUser.missUserMessage());
            }
            return userEntity;
        }
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
