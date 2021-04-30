package com.arbor.mall.exception;

/**
 * 描述：
 */
public class ArborMallException extends RuntimeException{
    private final Integer code;
    private final String message;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public ArborMallException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ArborMallException (ArborMallExceptionEnum exceptionEnum){
        this(exceptionEnum.getCode(), exceptionEnum.msg);
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
