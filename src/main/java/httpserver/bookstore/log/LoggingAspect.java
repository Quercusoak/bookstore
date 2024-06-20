package httpserver.bookstore.log;

import httpserver.bookstore.dto.ServerResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private Integer requestCounter =0;

    private static String  REQUEST_COUNTER= "REQUEST_COUNTER";

    private String REQUEST_LOG_INFO_MESSAGE(String resource, String method) {
        return "Incoming request | #"+requestCounter+" | resource: "+resource+" | HTTP Verb "+method;
    }

    private String REQUEST_LOG_DEBUG_MESSAGE(long duration) {
        return "request #"+requestCounter+" duration: "+duration+"ms";
    }

    private static final Logger requestLogger = LogManager.getLogger("request-logger");

    private static final Logger booksLogger = LogManager.getLogger("books-logger");


    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {}


    @Around("restController()"/*"within(httpserver.bookstore.controllers.*)"*/)
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {

        // request info logger
        HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];
        requestCounter++;
        ThreadContext.put(REQUEST_COUNTER, requestCounter.toString());
        requestLogger.info(REQUEST_LOG_INFO_MESSAGE(request.getRequestURI(), request.getMethod()));

        // request debug logger
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;
        requestLogger.debug(REQUEST_LOG_DEBUG_MESSAGE(duration));

        //error logging
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        if (response.getBody() instanceof ServerResponse<?> && ((ServerResponse<?>) response.getBody()).getErrorMessage()!=null) {
            booksLogger.error(((ServerResponse<?>) response.getBody()).getErrorMessage());
        }

        return result;
    }
}