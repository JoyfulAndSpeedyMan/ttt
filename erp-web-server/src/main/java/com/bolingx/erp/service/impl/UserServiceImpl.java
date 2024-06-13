package com.bolingx.erp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bolingx.common.exception.MessageBizException;
import com.bolingx.common.model.Message;
import com.bolingx.common.model.MessageHelper;
import com.bolingx.erp.dto.user.ModifyUserInfoDto;
import com.bolingx.erp.entity.UserEntity;
import com.bolingx.erp.mapper.UserMapper;
import com.bolingx.erp.service.UserService;
import com.bolingx.erp.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author p
 * @since 2024-05-06
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;

    private MessageHelper messageHelper;

    @Override
    public UserEntity selectById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    @Transactional
    public void modifyInfo(UserEntity userEntity, ModifyUserInfoDto modifyUserInfoDto) {
        LambdaQueryWrapper<UserEntity> userEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userEntityLambdaQueryWrapper.eq(UserEntity::getId, userEntity.getId());
        UserEntity updateEntity = new UserEntity();

        boolean needUpdate = false;

        if (StringUtils.isNotBlank(modifyUserInfoDto.getUsername())) {
            updateEntity.setUsername(modifyUserInfoDto.getUsername());
            try {
                userMapper.update(updateEntity, userEntityLambdaQueryWrapper);
            } catch (DuplicateKeyException e) {
                throw new MessageBizException(Constant.MESSAGE_CODE_USERNAME_EXIST);
            }
        }

        if (StringUtils.isNotBlank(modifyUserInfoDto.getNickname())) {
            updateEntity.setNickname(modifyUserInfoDto.getNickname());
            needUpdate = true;
        }

        if (modifyUserInfoDto.getGender() != null) {
            if (modifyUserInfoDto.getGender() != 1 && modifyUserInfoDto.getGender() != 2) {
                throw new MessageBizException(Message.REQUEST_ERROR);
            }
            updateEntity.setGender(modifyUserInfoDto.getGender());
            needUpdate = true;
        }
        if (needUpdate) {
            userMapper.update(updateEntity, userEntityLambdaQueryWrapper);
        }
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
