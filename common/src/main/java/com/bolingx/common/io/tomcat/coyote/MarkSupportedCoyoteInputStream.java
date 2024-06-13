package com.bolingx.common.io.tomcat.coyote;

import org.apache.catalina.connector.CoyoteInputStream;
import org.apache.catalina.connector.InputBuffer;

import java.io.IOException;

public class MarkSupportedCoyoteInputStream extends CoyoteInputStream {


    public MarkSupportedCoyoteInputStream(InputBuffer ib) {
        super(ib);
    }

    @Override
    public synchronized void mark(int readlimit) {
        ib.getByteBuffer().mark();
    }

    @Override
    public synchronized void reset() throws IOException {
        ib.getByteBuffer().reset();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

}
