package com.bapocalypse.Jerrymouse.exception;

/**
 * @package: com.bapocalypse.Jerrymouse.exception
 * @Author: 陈淼
 * @Date: 2017/1/13
 * @Description: 生命周期异常类
 */
public class LifecycleException extends Exception {
    private String message = null;
    private Throwable throwable = null;

    public LifecycleException(String message, Throwable throwable) {
        super(message);
        this.message = message;
        this.throwable = throwable;
    }

    public LifecycleException() {
        this(null, null);
    }

    public LifecycleException(String message) {
        this(message, null);
    }

    public LifecycleException(Throwable throwable) {
        this(null, throwable);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("LifecycleException:  ");
        if (message != null) {
            sb.append(message);
        }
        if (throwable != null) {
            sb.append(": ");
            sb.append(throwable.toString());
        }
        return sb.toString();
    }
}
