package com.bolingx.ai.controller.user;

import com.bolingx.ai.dto.user.EmailRegisterDto;
import com.bolingx.ai.dto.user.ModifyUserInfoDto;
import com.bolingx.ai.dto.user.login.UserLoginInfoDto;
import com.bolingx.common.model.Message;
import com.bolingx.ai.annotation.AutoUser;
import com.bolingx.ai.dto.user.UserDetailDto;
import com.bolingx.ai.entity.UserEntity;
import com.bolingx.ai.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@Validated
@Slf4j
public class UserController {

    private UserService userService;

    @PostMapping("/login")
    public Message<UserLoginInfoDto> login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        return userService.loginByUsername(username, password, request, response);
    }


    @PostMapping("/captcha/sendRegister")
    public Message<?> sendEmailRegisterCaptcha(@NotBlank @Email String email) {
        return userService.sendEmailRegisterCaptcha(email);
    }

    @PostMapping("/register/email")
    public Message<?> emailRegister(@Validated EmailRegisterDto emailRegisterDto) {
        return userService.emailRegister(emailRegisterDto);
    }


    @GetMapping("/detail")
    public Message<UserDetailDto> getUserDetail(@AutoUser UserEntity userEntity) {
        UserDetailDto userDetailVo = new UserDetailDto();
        BeanUtils.copyProperties(userEntity, userDetailVo);
        if (StringUtils.isNotBlank(userDetailVo.getMobile()) && userDetailVo.getMobile().length() >= 7) {
            userDetailVo.setMobile(userDetailVo.getMobile().substring(0, 3) + "****" + userDetailVo.getMobile().substring(7));
        }
        return Message.success(userDetailVo);
    }

    @PostMapping("/modifyInfo")
    public Message<?> modifyInfo(@AutoUser UserEntity userEntity, ModifyUserInfoDto modifyUserInfoDto) {
        userService.modifyInfo(userEntity, modifyUserInfoDto);
        return Message.success();
    }

    @PostMapping("/captcha/sendModifyPassword")
    public Message<?> sendModifyPasswordCaptcha(@AutoUser UserEntity userEntity) {
        return userService.sendModifyPasswordCaptcha(userEntity);
    }

    @PostMapping("/modifyPassword")
    public Message<?> modifyPassword(@AutoUser(required = false) UserEntity userEntity,
                                     @Email String email,
                                     @NotBlank @Size(min = 8) String password,
                                     @Digits(integer = 6, fraction = 6) String captcha) {
        return userService.modifyPassword(userEntity, email, password, captcha);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
