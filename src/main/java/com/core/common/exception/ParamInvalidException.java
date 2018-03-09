package com.core.common.exception;

/**
 * 参数验证异常
 * @author 彭佳佳
 * @data 2018年3月6日
 */
@SuppressWarnings("serial")
public class ParamInvalidException extends ApplicationException{

    public ParamInvalidException() {
    }

    public ParamInvalidException(String message) {
        super(message);
    }

    public ParamInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamInvalidException(Throwable cause) {
        super(cause);
    }

    public ParamInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
