package ru.vudovenko.micro.planner.plannerutils.aop;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Log4j2
public class LoggingAspect {

    @Around("execution(* ru.vudovenko.micro.planner.*.controllers..*(..)))")
    public Object profileControllerMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        log.debug("-------- Executing " + className + "." + methodName + "   ----------- ");

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = proceedingJoinPoint.proceed();
        stopWatch.stop();

        log.debug("-------- Execution time of " + className
                + "." + methodName
                + ":: " + stopWatch.getTotalTimeMillis() + " ms"
                + " ----------- ");

        return result;
    }
}
