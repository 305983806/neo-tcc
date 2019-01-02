package com.neo.tcc.core;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 18:40
 * @Description: TCC Confirm 执行异常
 */
public class ConfirmingException extends RuntimeException {
    public ConfirmingException(Throwable cause) {
        super(cause);
    }
}
