package com.bolingx.common.web.servlet.filter;

import com.bolingx.common.model.Message;
import com.bolingx.common.util.sign.SignUtils;
import com.bolingx.common.web.secret.AppSecret;
import com.bolingx.common.web.secret.AppSecretService;
import com.bolingx.common.web.servlet.hepler.ResponseHelper;
import com.bolingx.common.web.servlet.wrapper.request.HttpServletRequestBodyCacheWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class RequestValidationFilter extends OncePerRequestFilter {

    private RequestValidationConfig config;

    @Setter
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private Set<String> ignorePathSet;

    private Set<String> ignoreAntPathSet;

    @Setter
    private ResponseHelper responseHelper;

    @Setter
    private AppSecretService appSecretService;

    public RequestValidationFilter(ResponseHelper responseHelper,
                                   AppSecretService appSecretService) {
        this(new RequestValidationConfig(), responseHelper, appSecretService);
    }

    public RequestValidationFilter(RequestValidationConfig config,
                                   ResponseHelper responseHelper,
                                   AppSecretService appSecretService) {
        this.setConfig(config);
        this.responseHelper = responseHelper;
        this.appSecretService = appSecretService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (Objects.equals(requestURI, config.getErrorPage())) {
            filterChain.doFilter(request, response);
            return;
        }
        if (ignorePathSet.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        for (String pattern : ignoreAntPathSet) {
            if (antPathMatcher.match(pattern, requestURI)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        verifySign(request, response, filterChain);
    }

    private void verifySign(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String reqSign = request.getHeader("sign");
        if (StringUtils.isBlank(reqSign)) {
            responseHelper.writeResNoDetail(response, Message.REQUEST_ERROR, 400);
            return;
        }
        String appKey = request.getHeader("app-key");
        if (StringUtils.isBlank(appKey)) {
            responseHelper.writeResNoDetail(response, Message.REQUEST_ERROR, 400);
            return;
        }
        String timestampS = request.getHeader("timestamp");
        long timestamp;
        try {
            timestamp = Long.parseLong(timestampS);
        } catch (NumberFormatException e) {
            responseHelper.writeResNoDetail(response, Message.REQUEST_ERROR, 400);
            return;
        }
        if (timestamp < 1702881535000L) {
            responseHelper.writeResNoDetail(response, Message.REQUEST_ERROR, 400);
            return;
        }
        AppSecret appSecret = appSecretService.getByAppKeyForCache(appKey);
        if (appSecret == null || appSecret.getStatus() != AppSecret.STATUS_NORMAL) {
            responseHelper.writeResNoDetail(response, Message.REQUEST_ERROR, 400);
            return;
        }
        String method = request.getMethod();
        ReqContext reqContext =
                new ReqContext(request, response, filterChain, reqSign, timestamp, appSecret);
        if (before(reqContext)) {
            return;
        }
        switch (method) {
            case "GET" -> get(reqContext);
            case "POST" -> post(reqContext);
            default -> other(reqContext);
        }
    }

    /**
     * @return 是否跳出执行
     */
    protected boolean before(ReqContext reqContext) throws IOException, ServletException {
        return false;
    }

    protected void other(ReqContext reqContext) throws IOException, ServletException {
    }

    protected void get(ReqContext reqContext) throws IOException, ServletException {
        Map<String, String[]> parameterMap
                = handleParameterMap(reqContext.request.getParameterMap(), reqContext.timestamp, reqContext.appSecret);
        String sign = SignUtils.generateSignCodeArrayMap(parameterMap, reqContext.appSecret);
        if (!Objects.equals(reqContext.reqSign, sign)) {
            responseHelper.writeResNoDetail(reqContext.response, Message.REQUEST_ERROR, 400);
            return;
        }
        reqContext.filterChain.doFilter(reqContext.request, reqContext.response);
    }

    protected void post(ReqContext reqContext) throws IOException, ServletException {
        String contentType = reqContext.request.getContentType();

        if (StringUtils.isBlank(contentType)) {
            responseHelper.writeResNoDetail(reqContext.response, Message.REQUEST_ERROR, 400);
            return;
        }
        contentType = contentType.split(";")[0];
        if ("application/x-www-form-urlencoded".equals(contentType)) {
            postFormUrlencoded(reqContext);
        } else if (contentType.contains("multipart/form-data")) {
            postMultipartFormUrlencoded(reqContext);
        } else if ("application/json".equals(contentType)) {
            postJson(reqContext);
        }
    }

    protected void postFormUrlencoded(ReqContext reqContext)
            throws IOException, ServletException {
        postForm(reqContext, "application/x-www-form-urlencoded");
    }

    protected void postMultipartFormUrlencoded(ReqContext reqContext)
            throws IOException, ServletException {
        postForm(reqContext, "multipart/form-data");
    }

    protected void postForm(ReqContext reqContext, String contentType)
            throws IOException, ServletException {
        get(reqContext);
    }

    protected Map<String, String[]> handleParameterMap(Map<String, String[]> reqMap, Long timestamp, AppSecret appSecret) {
        HashMap<String, String[]> result = new HashMap<>(reqMap);
        result.put("appKey", new String[]{appSecret.getAppKey()});
        result.put("timestamp", new String[]{timestamp.toString()});
        return result;
    }

    protected void postJson(ReqContext reqContext) throws IOException, ServletException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(reqContext.request.getInputStream(), byteArrayOutputStream);
        byte[] requestBody = byteArrayOutputStream.toByteArray();

        reqContext.request = new HttpServletRequestBodyCacheWrapper(reqContext.request, requestBody);

        String bodyString = new String(requestBody, StandardCharsets.UTF_8);
        String sign = SignUtils.generateSignCode(bodyString, reqContext.appSecret);
        if (!Objects.equals(reqContext.reqSign, sign)) {
            responseHelper.writeResNoDetail(reqContext.response, Message.REQUEST_ERROR, 400);
            return;
        }
        reqContext.filterChain.doFilter(reqContext.request, reqContext.response);
    }

    public void setConfig(RequestValidationConfig config) {
        this.config = config;
        ignorePathSet = new HashSet<>();
        ignoreAntPathSet = new HashSet<>();
        config.getIgnorePathSet().forEach(path -> {
            if (antPathMatcher.isPattern(path)) {
                ignoreAntPathSet.add(path);
            } else {
                ignorePathSet.add(path);
            }
        });
    }

    @AllArgsConstructor
    private static class ReqContext {
        HttpServletRequest request;
        HttpServletResponse response;
        FilterChain filterChain;
        String reqSign;
        Long timestamp;
        AppSecret appSecret;
    }
}
