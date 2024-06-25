package com.bolingx.ai.service;

import com.bolingx.common.model.Message;
import com.bolingx.ai.dto.user.ModifyUserInfoDto;
import com.bolingx.ai.dto.user.login.UserLoginVo;
import com.bolingx.ai.entity.UserEntity;

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
