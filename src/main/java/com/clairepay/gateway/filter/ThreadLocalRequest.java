package com.clairepay.gateway.filter;

public class ThreadLocalRequest {
    private ThreadLocalRequest() {
    }

    private static final ThreadLocal<String> requestIdMap = new InheritableThreadLocal<>();

    public static void setRequestId(String requestId) {
        requestIdMap.set(requestId);
    }

    public static void clear() {
        requestIdMap.remove();
    }

    public static String getRequestId() {
        return requestIdMap.get();
    }
}
