package com.bolingx.erp.advice;

import com.bolingx.common.exception.BizException;
import com.bolingx.common.exception.MessageBizException;
import com.bolingx.common.model.Message;
import com.bolingx.common.model.MessageHelper;
import com.bolingx.common.model.config.DebugConfig;
import com.bolingx.common.web.servlet.hepler.ResponseHelper;
import com.bolingx.erp.exception.user.AutoUserException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.List;

@RestControllerAdvice("com.bolingx.erp.controller")
@Slf4j
public class GlobalExceptionHandler {

    @Resource
    private MessageHelper messageHelper;

    @Resource
    private ResponseHelper responseHelper;

    @Resource
    private DebugConfig debugConfig;


    @ExceptionHandler({BizException.class})
    public Message<?> bizException(BizException e) {
        String message = e.getMessage();
        if (e instanceof MessageBizException mbz) {
            if (message != null) {
                return Message.of(mbz.getCode(), e.getMessage(), null);
            } else {
                return messageHelper.of(mbz.getCode());
            }
        } else {
            return Message.of(Message.REQUEST_ERROR, e.getMessage(), null);
        }
    }

    @ExceptionHandler({Exception.class})
    public Message<?> exception(Exception e, HttpServletRequest request) {
        log.error("url {} exception", request.getContextPath(), e);
        return messageHelper.of(Message.SYSTEM_ERR_CODE);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public void constraintViolationException(MethodArgumentNotValidException e, HttpServletResponse response) throws IOException {
        if (debugConfig.isEnableDetailedErrorMessages()) {
            List<ObjectError> allErrors = e.getAllErrors();
            if (CollectionUtils.isNotEmpty(allErrors)) {
                ObjectError objectError = allErrors.getFirst();
                if (objectError instanceof FieldError fieldError) {
                    String field = fieldError.getField();
                    String message = fieldError.getDefaultMessage();
                    responseHelper.writeRes(response, Message.REQUEST_ERROR, field + ": " + message, 403);
                }
            }
        }
        response.setStatus(403);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public void constraintViolationException(ConstraintViolationException e, HttpServletResponse response) throws IOException {
        if (debugConfig.isEnableDetailedErrorMessages()) {
            responseHelper.writeRes(response, Message.REQUEST_ERROR, e.getMessage(), 403);
        }
        response.setStatus(403);
    }

    @ExceptionHandler({AutoUserException.class})
    public void autoUserException(ConstraintViolationException e, HttpServletResponse response) throws IOException {
        if (debugConfig.isEnableDetailedErrorMessages()) {
            responseHelper.writeRes(response, Message.REQUEST_ERROR, e.getMessage(), 403);
        }
        response.setStatus(403);
    }

//    @ExceptionHandler({ResolverMissUserException.class})
//    public void resolverMissUserException(ResolverMissUserException e, HttpServletResponse response) throws IOException {
//        if (debugConfig.isEnableDetailedErrorMessages()) {
//            responseHelper.writeRes(response, Message.REQUEST_ERROR, e.getMessage(), 403);
//            return;
//        }
//        response.setStatus(403);
//    }
//
//    @ExceptionHandler({AccessDeniedException.class})
//    public void accessDeniedException(AccessDeniedException e, HttpServletResponse response) throws IOException {
//        if (debugConfig.isEnableDetailedErrorMessages()) {
//            responseHelper.writeRes(response, Message.REQUEST_ERROR, e.getMessage(), 403);
//            return;
//        }
//        response.setStatus(403);
//    }
}
