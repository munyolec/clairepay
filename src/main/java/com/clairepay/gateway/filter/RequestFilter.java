package com.clairepay.gateway.filter;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
@Order(1)
public class RequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // generate a request id
        // log time the request took
        long startTime = System.nanoTime();
        String requestId = String.valueOf(startTime);
        MDC.put("RequestId", requestId);
        ThreadLocalRequest.setRequestId(requestId);

        chain.doFilter(request, response);

        long endTime = System.nanoTime();
        long timeTaken = endTime - startTime;
        long durationInMs = TimeUnit.MILLISECONDS.convert(timeTaken, TimeUnit.NANOSECONDS);
        log.debug("The request took " + durationInMs + " ms");
        ThreadLocalRequest.clear();
        MDC.clear();
    }




}
