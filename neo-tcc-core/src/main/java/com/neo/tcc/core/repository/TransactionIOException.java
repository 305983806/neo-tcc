package com.neo.tcc.core.repository;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 17:04
 * @Description:
 */
public class TransactionIOException extends RuntimeException {
    public TransactionIOException(String message) {
        super(message);
    }

    public TransactionIOException(Throwable cause) {
        super(cause);
    }
}
