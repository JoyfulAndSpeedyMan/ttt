package com.bolingx.erp.controller.user;

import com.bolingx.common.model.Message;
import com.bolingx.erp.annotation.AutoUser;
import com.bolingx.erp.dto.user.UserDetailDto;
import com.bolingx.erp.dto.user.login.UserLoginVo;
import com.bolingx.erp.entity.UserEntity;
import com.bolingx.erp.service.UserService;
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
    public Message<UserLoginVo> login(String username, String password) {
        return userService.loginByUsername(username, password);
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

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
