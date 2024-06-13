package com.bolingx.erp.controller.user;

import com.bolingx.common.model.Message;
import com.bolingx.erp.annotation.AutoUser;
import com.bolingx.erp.dto.user.UserDetailDto;
import com.bolingx.erp.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/")
@Validated
@Slf4j
public class UserController {


    @GetMapping("/detail")
    public Message<UserDetailDto> getUserDetail(@AutoUser UserEntity userEntity) {
        UserDetailDto userDetailVo = new UserDetailDto();
        BeanUtils.copyProperties(userEntity, userDetailVo);
        if (StringUtils.isNotBlank(userDetailVo.getMobile()) && userDetailVo.getMobile().length() >= 7) {
            userDetailVo.setMobile(userDetailVo.getMobile().substring(0, 3) + "****" + userDetailVo.getMobile().substring(7));
        }
        return Message.success(userDetailVo);
    }

}
