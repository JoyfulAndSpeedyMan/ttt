package com.bolingx.erp.service;

import com.bolingx.common.model.Message;
import com.bolingx.erp.dto.user.ModifyUserInfoDto;
import com.bolingx.erp.dto.user.login.UserLoginVo;
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

    UserEntity selectByUsername(String username);

    Message<UserLoginVo> loginByUsername(String username, String password);

    void modifyInfo(UserEntity userEntity, ModifyUserInfoDto modifyUserInfoDto);
}
