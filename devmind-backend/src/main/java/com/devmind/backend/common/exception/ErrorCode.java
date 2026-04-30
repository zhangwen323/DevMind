package com.devmind.backend.common.exception;

public final class ErrorCode {

    public static final int VALIDATION_ERROR = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int INTERNAL_ERROR = 500;

    private ErrorCode() {
    }
}
