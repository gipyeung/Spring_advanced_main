package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@org.aspectj.lang.annotation.Aspect
@Component
public class Aspect {


    public Aspect() {
    }

    @Pointcut("@annotation(org.example.expert.annotation.Information)")
    private void commentControllerPointcut() {}



    @Around("commentControllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String userId;
        String requestUrl = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();

        if (request.getUserPrincipal() != null) {
            userId = request.getUserPrincipal().getName();
        } else {
            userId = "null";
        }

        log.info("API Request - User ID: {}, URL: {}, Time: {}", userId, requestUrl, requestTime);

        try {
            joinPoint.proceed();
        } finally {
            LocalDateTime responseTime = LocalDateTime.now();
            log.info("API Response - User ID: {}, URL: {}, Request Time: {}",
                    userId, requestUrl, requestTime);
        }

        return null;
    }
}