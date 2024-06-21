package com.bolingx.seller.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bolingx.common.web.secret.AppSecret;
import com.bolingx.seller.entity.AppSecretEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 秘钥 Mapper 接口
 * </p>
 *
 * @author p
 * @since 2024-6-14
 */
public interface AppSecretMapper extends BaseMapper<AppSecretEntity> {
    @Select("select * from app_secret where status = #{status}")
    List<AppSecret> selectByStatus(byte status);

    @Select("select * from app_secret where app_key = #{appKey}")
    AppSecret selectByAppKey(String appKey);
}
