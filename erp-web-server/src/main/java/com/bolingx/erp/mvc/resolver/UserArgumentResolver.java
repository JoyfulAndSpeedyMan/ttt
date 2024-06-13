package com.bolingx.erp.mvc.resolver;

import com.bolingx.erp.annotation.AutoUser;
import com.bolingx.erp.entity.UserEntity;
import com.bolingx.erp.exception.user.AutoUserException;
import com.bolingx.erp.mvc.interceptor.UserTokenVerifyInterceptor;
import com.bolingx.erp.pojo.token.UserTokenInfo;
import com.bolingx.erp.service.UserService;
import jakarta.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
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
        ServletRequest servletRequest = (ServletRequest) webRequest.getNativeRequest();

        UserTokenInfo userTokenInfo = (UserTokenInfo) servletRequest.getAttribute(UserTokenVerifyInterceptor.USER_TOKEN_ATTR_KEY);

        final AutoUser autoUser = parameter.getParameterAnnotation(AutoUser.class);
        assert autoUser != null;
        if (userTokenInfo == null && autoUser.required()) {
            throw new AutoUserException(autoUser.message());
        } else {
            if (userTokenInfo == null) {
                return null;
            }
            UserEntity userEntity = userService.selectById(userTokenInfo.getUid());
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
