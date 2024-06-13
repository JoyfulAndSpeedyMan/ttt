package com.bolingx.common.web.secret;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DefaultAppSecretService implements AppSecretService {

    private final AppSecretDao appSecretDao;

    private final LoadingCache<Byte, Map<String, AppSecret>> cache = Caffeine.newBuilder()
            .refreshAfterWrite(20, TimeUnit.SECONDS)
            .build(new AppSecretLoader());

    public DefaultAppSecretService(AppSecretDao appSecretDao) {
        this.appSecretDao = appSecretDao;
    }

    @Override
    public List<AppSecret> selectByStatus(byte status) {
        return appSecretDao.selectByStatus(status);
    }

    @Override
    public AppSecret getByAppKeyForCache(String appKey) {
        return cache.get((byte) 1).get(appKey);
    }

    @Override
    public AppSecret getByAppKey(String appKey) {
        return appSecretDao.selectByAppKey(appKey);
    }

    class AppSecretLoader implements CacheLoader<Byte, Map<String, AppSecret>> {
        @Override
        public @Nullable Map<String, AppSecret> load(Byte status) {
            List<AppSecret> appSecretList = DefaultAppSecretService.this.selectByStatus(status);
            HashMap<String, AppSecret> result = new HashMap<>(appSecretList.size());
            for (AppSecret appSecret : appSecretList) {
                result.put(appSecret.getAppKey(), appSecret);
            }
            return result;
        }
    }
}
