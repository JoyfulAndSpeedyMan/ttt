package com.bolingx.common.web.secret;

import java.util.List;

/**
 * <p>
 * 人群站秘钥
 * </p>
 *
 * @author p
 * @since 2023-12-15
 */
public interface AppSecretService {

    List<AppSecret> selectByStatus(byte status);

    AppSecret getByAppKeyForCache(String appKey);

    AppSecret getByAppKey(String appKey);
}
