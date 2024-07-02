package com.bolingx.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bolingx.common.exception.MessageBizException;
import com.bolingx.common.model.Message;
import com.bolingx.common.model.MessageHelper;
import com.bolingx.ai.dto.user.ModifyUserInfoDto;
import com.bolingx.ai.dto.user.login.UserLoginInfo;
import com.bolingx.ai.dto.user.login.UserLoginVo;
import com.bolingx.ai.dto.user.login.UserTokenInfo;
import com.bolingx.ai.entity.UserEntity;
import com.bolingx.ai.mapper.UserMapper;
import com.bolingx.ai.service.UserService;
import com.bolingx.ai.util.Constant;
import com.bolingx.ai.util.user.UserTokenUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author p
 * @since 2024-6-14
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private MessageHelper messageHelper;

    private UserMapper userMapper;

    @Resource
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;

    @Resource
    private SecurityContextRepository securityContextRepository;

    @Resource
    private RememberMeServices rememberMeServices;

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    private ApplicationEventPublisher eventPublisher;

    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource
            = new WebAuthenticationDetailsSource();

    @Override
    public UserEntity selectById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserEntity selectByUsername(String username) {
        LambdaQueryWrapper<UserEntity> query = new LambdaQueryWrapper<>();
        query.eq(UserEntity::getUsername, username);
        return userMapper.selectOne(query);
    }

    @Override
    public Message<UserLoginVo> loginByUsername(String username, String password,
                                                HttpServletRequest request, HttpServletResponse response) {
        UserEntity userEntity = this.selectByUsername(username);
        if (userEntity == null) {
            return messageHelper.of(Constant.MESSAGE_CODE_USER_OR_PASSWD_ERROR);
        }
        if (!Objects.equals(userEntity.getPassword(), password)) {
            return messageHelper.of(Constant.MESSAGE_CODE_USER_OR_PASSWD_ERROR);
        }
        UsernamePasswordAuthenticationToken authentication
                = UsernamePasswordAuthenticationToken.authenticated(username, password, null);
        authentication.setDetails(this.authenticationDetailsSource.buildDetails(request));
        successfulAuthentication(request, response, authentication);
        return messageHelper.of(Message.SUCCESS_CODE);
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authResult) {
        sessionAuthenticationStrategy.onAuthentication(authResult, request, response);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authResult);
        this.securityContextHolderStrategy.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);
        this.rememberMeServices.loginSuccess(request, response, authResult);
        if (this.eventPublisher != null) {
            this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }
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
