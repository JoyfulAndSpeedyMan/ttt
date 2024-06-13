package com.bolingx.common.web.servlet.wrapper.request;

import com.bolingx.common.io.tomcat.coyote.MarkSupportedCoyoteInputStream;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.catalina.connector.CoyoteInputStream;
import org.apache.catalina.connector.InputBuffer;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.tomcat.util.res.StringManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

public class HttpServletRequestBodyResetWrapper extends HttpServletRequestWrapper {
    public static final Set<String> supportContentType = Set.of("application/json");

    public static final StringManager sm = StringManager.getManager(Request.class);

    protected MarkSupportedCoyoteInputStream markSupportedCoyoteInputStream;

    protected BufferedReader reader;

    protected Request connectorRequest;

    protected InputBuffer ib;
    protected static Field bbField;
    protected static Field usingInputStreamField;
    protected static Field usingReaderField;
    protected static Field cbField;

    static {
        try {
            bbField = Request.class.getDeclaredField("inputBuffer");
            usingInputStreamField = Request.class.getDeclaredField("usingInputStream");
            usingReaderField = Request.class.getDeclaredField("usingReader");

            bbField.setAccessible(true);
            usingInputStreamField.setAccessible(true);
            usingReaderField.setAccessible(true);

            cbField = InputBuffer.class.getDeclaredField("cb");
            cbField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

    }

    public HttpServletRequestBodyResetWrapper(HttpServletRequest request) {
        super(request);
        String contentType = request.getContentType();
        if (!supportContentType.contains(contentType)) {
            throw new UnsupportedOperationException("不支持的contentType");
        }
        Request connectorRequest = getConnectorRequest(request);
        try {
            this.connectorRequest = connectorRequest;
            ib = (InputBuffer) bbField.get(connectorRequest);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected Request getConnectorRequest(HttpServletRequest request) {
        try {
            if (request instanceof RequestFacade) {
                Class<? extends HttpServletRequest> aClass = request.getClass();
                Field requestField = aClass.getDeclaredField("request");
                requestField.setAccessible(true);
                return (Request) requestField.get(request);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        throw new UnsupportedOperationException("不支持的类型：" + request.getClass().getName());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (markSupportedCoyoteInputStream == null) {
            ServletInputStream inputStream = connectorRequest.getInputStream();
            Class<? extends ServletInputStream> aClass = inputStream.getClass();
            if (!aClass.isAssignableFrom(CoyoteInputStream.class)) {
                throw new RuntimeException("不支持的ServletInputStream : " + aClass.getName());
            }
            markSupportedCoyoteInputStream = new MarkSupportedCoyoteInputStream(ib);
        }
        return markSupportedCoyoteInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (reader == null) {
            reader = connectorRequest.getReader();
        }
        return reader;
    }


    public void resetBody(int bodyStart, int bodyLimit) throws IOException {
        try {
            if (reader != null) {
                ib.reset();
            }
            ib.getByteBuffer().limit(bodyLimit);
            ib.getByteBuffer().position(bodyStart);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            usingInputStreamField.set(connectorRequest, false);
            usingReaderField.set(connectorRequest, false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int cbLimit(){
        return ib.getByteBuffer().limit();
    }
}
