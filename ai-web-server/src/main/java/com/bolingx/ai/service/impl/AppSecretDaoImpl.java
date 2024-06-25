package com.bolingx.ai.service.impl;

import com.bolingx.common.web.secret.AppSecret;
import com.bolingx.common.web.secret.AppSecretDao;
import com.bolingx.ai.mapper.AppSecretMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppSecretDaoImpl implements AppSecretDao {

    @Resource
    private AppSecretMapper appSecretMapper;

    @Override
    public List<AppSecret> selectByStatus(byte status) {
        return appSecretMapper.selectByStatus(status);
    }

    @Override
    public AppSecret selectByAppKey(String appKey) {
        return appSecretMapper.selectByAppKey(appKey);
    }
}
