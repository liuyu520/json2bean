package com.javaear.json4bean.exception;

/**
 * @author aooer
 */
public class Json4BeanIOException extends RuntimeException {

    /**
     * 异常构造方法
     *
     * @param cause cause
     */
    public Json4BeanIOException(Throwable cause) {
        super("json4bean IO exception", cause);
    }

}
