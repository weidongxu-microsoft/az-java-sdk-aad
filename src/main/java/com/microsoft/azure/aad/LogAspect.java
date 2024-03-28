package com.microsoft.azure.aad;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LogAspect {

    @Around("@annotation(com.microsoft.azure.aad.Log)")
    public Object doIdempotentOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("before");
        Object obj = joinPoint.proceed();
        System.out.println("after");
        return obj;
    }
}
