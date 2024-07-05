package com.bolingx.ai.service;

import com.bolingx.ai.dto.user.EmailRegisterDto;
import com.bolingx.ai.dto.user.login.UserLoginInfoDto;
import com.bolingx.common.model.Message;
import com.bolingx.ai.dto.user.ModifyUserInfoDto;
import com.bolingx.ai.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

    UserEntity selectOneByEmail(String email);

    UserEntity selectOneByMobile(String mobile);

    Message<UserLoginInfoDto> loginByUsername(String username, String password,
                                              HttpServletRequest request, HttpServletResponse response);


    void modifyInfo(UserEntity userEntity, ModifyUserInfoDto modifyUserInfoDto);

    Message<?> sendEmailRegisterCaptcha(String email);

    Message<?> emailRegister(EmailRegisterDto emailRegisterDto);

    Message<?> sendModifyPasswordCaptcha(UserEntity userEntity);

    Message<?> modifyPassword(UserEntity userEntity, String email, String password, String captcha);

}
