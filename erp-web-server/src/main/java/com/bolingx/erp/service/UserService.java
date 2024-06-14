package com.bolingx.erp.service;

import com.bolingx.erp.dto.user.ModifyUserInfoDto;
import com.bolingx.erp.entity.UserEntity;

/**
 * <p>
 * 
 * </p>
 *
 * @author p
 * @since 2024-6-14
 */
public interface UserService {

    UserEntity selectById(Long id);

    void modifyInfo(UserEntity userEntity, ModifyUserInfoDto modifyUserInfoDto);
}
