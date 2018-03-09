package com.core.common.exception;

/**
 * 返回结果异常
 * @author 彭佳佳
 * @data 2018年3月6日
 */
@SuppressWarnings("serial")
public class ResultException extends ApplicationException {

    public ResultException() {
    }

    public ResultException(String message) {
        super(message);
    }

    public ResultException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResultException(Throwable cause) {
        super(cause);
    }

    public ResultException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
