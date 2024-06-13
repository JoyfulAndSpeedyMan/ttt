package com.bolingx.common.web.servlet.hepler;

import com.alibaba.fastjson2.JSON;
import com.bolingx.common.model.Message;
import com.bolingx.common.model.config.DebugConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

@Slf4j
public class ResponseHelper {
    private final MessageSource messageSource;

    private final DebugConfig debugConfig;

    public ResponseHelper(MessageSource messageSource) {
        this(messageSource, new DebugConfig());
    }

    public ResponseHelper(MessageSource messageSource, DebugConfig debugConfig) {
        this.messageSource = messageSource;
        this.debugConfig = debugConfig;
    }

    public void writeRes(HttpServletResponse response, String code) throws IOException {
        writeRes(response, code, null, null, 200, true);
    }


    public void writeRes(HttpServletResponse response, String code, int httpCode) throws IOException {
        writeRes(response, code, null, null, httpCode, true);
    }

    public void writeRes(HttpServletResponse response, String code, String msg) throws IOException {
        writeRes(response, code, msg, null, 200, true);
    }

    public void writeRes(HttpServletResponse response, String code, Object data) throws IOException {
        writeRes(response, code, null, data, 200, true);
    }

    public void writeRes(HttpServletResponse response, String code, String msg, Object data) throws IOException {
        writeRes(response, code, msg, data, 200, true);
    }


    public void writeRes(HttpServletResponse response, String code, boolean writeDetail) throws IOException {
        writeRes(response, code, null, null, 200, writeDetail);
    }

    public void writeRes(HttpServletResponse response, String code, int httpCode, boolean writeDetail) throws IOException {
        writeRes(response, code, null, null, httpCode, writeDetail);
    }

    public void writeRes(HttpServletResponse response, String code, String msg, boolean writeDetail) throws IOException {
        writeRes(response, code, msg, null, 200, writeDetail);
    }

    public void writeRes(HttpServletResponse response, String code, Object data, boolean writeDetail) throws IOException {
        writeRes(response, code, null, data, 200, writeDetail);
    }

    public void writeRes(HttpServletResponse response, String code, String msg, Object data, boolean writeDetail) throws IOException {
        writeRes(response, code, msg, data, 200, writeDetail);
    }


    public void writeResNoDetail(HttpServletResponse response, String code) throws IOException {
        writeRes(response, code, null, null, 200, false);
    }

    public void writeResNoDetail(HttpServletResponse response, String code, int httpCode) throws IOException {
        writeRes(response, code, null, null, httpCode, false);
    }

    public void writeResNoDetail(HttpServletResponse response, String code, String msg) throws IOException {
        writeRes(response, code, msg, null, 200, false);
    }

    public void writeRes(HttpServletResponse response, String code, String msg, Object data, int httpCode, boolean writeDetail) throws IOException {
        if (msg == null) {
            msg = messageSource.getMessage(code, null, Locale.getDefault());
        }
        response.setStatus(httpCode);
        if (debugConfig.isEnableDetailedErrorMessages() || writeDetail) {
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(JSON.toJSONString(Message.of(code, msg, null)));
        } else {
            log.debug("writeRes code: {}, msg: {}, httpCode: {}", code, msg, httpCode);
        }
    }
}
