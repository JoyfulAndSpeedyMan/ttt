package com.bolingx.common.util.sign;

import com.bolingx.common.sign.strategy.SignStrategy;
import com.bolingx.common.sign.strategy.impl.DefaultSignStrategy;
import com.bolingx.common.web.secret.AppSecret;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SignUtils {
    private static final Map<String, SignStrategy> map = new ConcurrentHashMap<>(8);
    private static final SignStrategy defaultSignStrategy = new DefaultSignStrategy();

    static {
        ServiceLoader<SignStrategy> registries = ServiceLoader.load(SignStrategy.class);
        for (SignStrategy registry : registries) {
            map.putIfAbsent(registry.getName(), registry);
            log.info("register signcode:{}", registry.getName());
        }
    }

    public static String generateSignCode(String signData, String appKey, String keySecret) {
        return generateSignCode(signData, appKey, keySecret, null);
    }

    public static String generateSignCode(String signData, AppSecret appSecret) {
        return generateSignCode(signData, appSecret.getAppKey(), appSecret.getSecret(), appSecret.getAlgorithm());
    }

    public static String generateSignCode(String signData, String appKey, String keySecret, String name) {
        if (StringUtils.isNotBlank(name)) {
            return map.getOrDefault(name, defaultSignStrategy).generateSignCode(signData, appKey, keySecret);
        } else {
            return defaultSignStrategy.generateSignCode(signData, appKey, keySecret);
        }
    }

    public static String generateSignCode(Map<String, String> parameterMap, String appKey, String keySecret) {
        return generateSignCode(parameterMap, appKey, keySecret, null);
    }

    public static String generateSignCode(Map<String, String> parameterMap, AppSecret appSecret) {
        return generateSignCode(parameterMap, appSecret.getAppKey(), appSecret.getSecret(), appSecret.getAlgorithm());
    }

    public static String generateSignCode(Map<String, String> parameterMap, String appKey, String keySecret, String name) {
        if (StringUtils.isNotBlank(name)) {
            return map.getOrDefault(name, defaultSignStrategy).generateSignCode(parameterMap, appKey, keySecret);
        } else {
            return defaultSignStrategy.generateSignCode(parameterMap, appKey, keySecret);
        }
    }

    public static String generateSignCodeArrayMap(Map<String, String[]> parameterMap, String appKey, String keySecret) {
        return generateSignCodeArrayMap(parameterMap, appKey, keySecret, null);
    }


    public static String generateSignCodeArrayMap(Map<String, String[]> parameterMap, AppSecret appSecret) {
        return generateSignCodeArrayMap(parameterMap, appSecret.getAppKey(), appSecret.getSecret(), appSecret.getAlgorithm());
    }

    public static String generateSignCodeArrayMap(Map<String, String[]> parameterMap, String appKey, String keySecret, String name) {
        if (StringUtils.isNotBlank(name)) {
            return map.getOrDefault(name, defaultSignStrategy).generateSignCodeArrayMap(parameterMap, appKey, keySecret);
        } else {
            return defaultSignStrategy.generateSignCodeArrayMap(parameterMap, appKey, keySecret);
        }
    }
}
