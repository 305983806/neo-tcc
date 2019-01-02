package com.neo.tcc.core;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 20:35
 * @Description:
 */
public class CancellingException extends RuntimeException {
    public CancellingException(Throwable cause) {
        super(cause);
    }
}
