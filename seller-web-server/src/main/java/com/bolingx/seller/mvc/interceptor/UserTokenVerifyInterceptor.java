package com.bolingx.seller.mvc.interceptor;

import com.bolingx.common.web.servlet.hepler.ResponseHelper;
import com.bolingx.seller.dto.user.login.UserTokenInfo;
import com.bolingx.seller.util.user.UserTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserTokenVerifyInterceptor implements HandlerInterceptor {

    public static final String USER_TOKEN_KEY = "tj";

    public static final String USER_TOKEN_ATTR_KEY = "token_attr";

    private UserTokenUtils userTokenUtils;

    private ResponseHelper responseHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(USER_TOKEN_KEY);
        if(StringUtils.isBlank(token)){
            return true;
        }
        try {
            UserTokenInfo userTokenInfo = userTokenUtils.parseUserToken(token);
//            if(userTokenInfo.getTokenExpireTime() < System.currentTimeMillis()){
//                responseHelper.writeRes(response, Constant.MESSAGE_CODE_TOKEN_EXPIRED);
//                return false;
//            }
            request.setAttribute(USER_TOKEN_ATTR_KEY, userTokenInfo);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Autowired
    public void setUserTokenUtils(UserTokenUtils userTokenUtils) {
        this.userTokenUtils = userTokenUtils;
    }

    @Autowired
    public void setResponseHelper(ResponseHelper responseHelper) {
        this.responseHelper = responseHelper;
    }
}
