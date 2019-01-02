package com.neo.tcc.core;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 16:37
 * @Description:
 */
public class SystemException extends RuntimeException {
    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }
}
