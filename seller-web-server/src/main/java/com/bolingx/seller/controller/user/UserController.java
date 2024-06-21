package com.bolingx.seller.controller.user;

import com.bolingx.common.model.Message;
import com.bolingx.seller.annotation.AutoUser;
import com.bolingx.seller.dto.user.UserDetailDto;
import com.bolingx.seller.dto.user.login.UserLoginVo;
import com.bolingx.seller.entity.UserEntity;
import com.bolingx.seller.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.LongAdder;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
public class UserController {

    private UserService userService;

    private final LongAdder count = new LongAdder();

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

    @GetMapping("/hello")
    public Message<String> hello(){
        log.info("hello");
        return Message.success("hello");
    }


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
