package com.clairepay.gateway.config;

public enum ApiErrorCode {
    MISSING_PARAMETER(203),
    UNREADABLE_MESSAGE(204),
    INVALID_PARAMETER(205);



    int code;
    ApiErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

