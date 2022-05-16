package com.clairepay.gateway.error;

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
    public static String getDescription(int code) {
        for (ApiErrorCode errorCode : ApiErrorCode.values()) {
            if (code == errorCode.getCode()) {
                return errorCode.toString();
            }
        }
        return  null;
    }
}

