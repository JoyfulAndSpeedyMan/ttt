package com.bolingx.common.web.secret;

import java.util.List;

public interface AppSecretDao {
    List<AppSecret> selectByStatus(byte status);

    AppSecret selectByAppKey(String appKey);
}
