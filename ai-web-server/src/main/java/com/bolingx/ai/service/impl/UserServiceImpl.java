package com.bolingx.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bolingx.ai.dto.user.EmailRegisterDto;
import com.bolingx.ai.dto.user.login.UserLoginInfoDto;
import com.bolingx.ai.security.user.UserDetailImpl;
import com.bolingx.ai.util.RedisKeyConstant;
import com.bolingx.common.exception.MessageBizException;
import com.bolingx.common.model.Message;
import com.bolingx.common.model.MessageHelper;
import com.bolingx.ai.dto.user.ModifyUserInfoDto;
import com.bolingx.ai.entity.UserEntity;
import com.bolingx.ai.mapper.UserMapper;
import com.bolingx.ai.service.UserService;
import com.bolingx.ai.util.Constant;
import com.bolingx.common.util.generator.RandomCodeGenerator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    private RedisTemplate<String, Object> redisTemplate;

    private AuthenticationManager authenticationManager;

    private PasswordEncoder passwordEncoder;

    @Override
    public UserEntity selectById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserEntity selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public UserEntity selectOneByEmail(String email) {
        return userMapper.selectOneByEmail(email);
    }

    @Override
    public UserEntity selectOneByMobile(String mobile) {
        LambdaQueryWrapper<UserEntity> query = new LambdaQueryWrapper<>();
        query.eq(UserEntity::getMobile, mobile);
        return userMapper.selectOne(query);
    }

    @Override
    public Message<UserLoginInfoDto> loginByUsername(String username, String password,
                                                     HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        authenticationRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationRequest);
        } catch (AuthenticationException ex) {
            return messageHelper.of(Constant.MESSAGE_CODE_USER_OR_PASSWD_ERROR);
        }
        successfulAuthentication(request, response, authentication);

        UserEntity userEntity = selectById(((UserDetailImpl) authentication.getPrincipal()).getId());
        return messageHelper.success(UserLoginInfoDto.builder()
                .id(userEntity.getId())
                .username(username)
                .avatar(userEntity.getAvatar())
                .nickname(userEntity.getNickname())
                .gender(userEntity.getGender())
                .build());
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

    @Override
    public Message<?> sendEmailRegisterCaptcha(String email) {
        String key = RedisKeyConstant.CODE_REGISTER_EMAIL + email;
        UserEntity userEntity = this.selectOneByEmail(email);
        if (userEntity != null) {
            return messageHelper.of(Constant.MESSAGE_CODE_EMAIL_EXIST);
        }
        Message<?> result;
        if ((result = sendCaptcha(key)) != null) {
            return result;
        }
        return messageHelper.success();
    }

    @Override
    public Message<?> emailRegister(EmailRegisterDto emailRegisterDto) {
        String key = RedisKeyConstant.CODE_REGISTER_EMAIL + emailRegisterDto.getEmail();
        Message<?> result;
        if ((result = validCaptcha(key, emailRegisterDto.getCaptcha())) != null) {
            return result;
        }
        UserEntity userEntity = this.selectOneByEmail(emailRegisterDto.getEmail());
        if (userEntity != null) {
            return messageHelper.of(Constant.MESSAGE_CODE_EMAIL_EXIST);
        }
        userEntity = UserEntity.builder()
                .username(emailRegisterDto.getEmail())
                .nickname(emailRegisterDto.getNickname())
                .email(emailRegisterDto.getEmail())
                .password(passwordEncoder.encode(emailRegisterDto.getPassword()))
                .gender((byte) 0)
                .build();
        userMapper.insert(userEntity);
        redisTemplate.delete(key);
        return messageHelper.success();
    }

    @Override
    public Message<?> sendModifyPasswordCaptcha(UserEntity userEntity) {
        if (StringUtils.isBlank(userEntity.getEmail())) {
            return messageHelper.of(Constant.MESSAGE_CODE_NOT_BINDING_EMAIL);
        }
        String key = RedisKeyConstant.CODE_MODIFY_PASSWORD_EMAIL + userEntity.getEmail();
        Message<?> result;
        if ((result = sendCaptcha(key)) != null) {
            return result;
        }
        return messageHelper.success();
    }

    private Message<?> sendCaptcha(String redisKey) {
        String captcha = (String) redisTemplate.opsForValue().get(redisKey);
        if (captcha != null) {
            log.info("存在现有code: {}", captcha);
            return messageHelper.success();
        }
        captcha = RandomCodeGenerator.generateCode(6);
        log.info("captcha: {}", captcha);
        redisTemplate.opsForValue().set(redisKey, captcha, 5, TimeUnit.MINUTES);
        return null;
    }

    private Message<?> validCaptcha(String redisKey, String captcha) {
        String captcha2;
        try {
            captcha2 = (String) redisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (captcha2 == null) {
            return messageHelper.of(Constant.MESSAGE_CODE_CAPTCHA_EXPIRED);
        }
        if (!Objects.equals(captcha2, captcha)) {
            return messageHelper.of(Constant.MESSAGE_CODE_CAPTCHA_ERROR);
        }
        return null;
    }


    @Override
    public Message<?> modifyPassword(UserEntity userEntity, String email, String password, String captcha) {
        String sendEmail = null;
        if (userEntity != null) {
            sendEmail = userEntity.getEmail();
        }
        if (StringUtils.isNotBlank(email)) {
            sendEmail = email;
            userEntity = selectOneByEmail(email);
            if (userEntity == null) {
                return messageHelper.of(Constant.MESSAGE_CODE_USER_NOT_EXIST);
            }
        }
        if (userEntity == null) return messageHelper.reqError();

        String key = RedisKeyConstant.CODE_MODIFY_PASSWORD_EMAIL + sendEmail;
        Message<?> result;
        if ((result = validCaptcha(key, captcha)) != null) {
            return result;
        }
        userMapper.updatePasswordById(userEntity.getId(), passwordEncoder.encode(password));
        redisTemplate.delete(key);
        return messageHelper.success();
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
