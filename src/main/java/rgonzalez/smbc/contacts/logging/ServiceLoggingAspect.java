package rgonzalez.smbc.contacts.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class ServiceLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    /**
     * Pointcut that matches all public methods in classes within the service package
     */
    @Pointcut("within(rgonzalez.smbc.contacts.service..*) && execution(public * *(..))")
    public void serviceMethods() {
    }

    /**
     * Around advice that logs the service name, method name, and parameter
     * values
     * 
     * @param joinPoint the join point
     * @return the result of the method invocation
     * @throws Throwable if an exception occurs during method execution
     */
    @Around("serviceMethods()")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String serviceName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("Entering Service - Class: {}, Method: {}, Parameters: {}",
                serviceName, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("Exiting Service - Class: {}, Method: {}, Execution Time: {}ms",
                    serviceName, methodName, executionTime);
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Exception in Service - Class: {}, Method: {}, Execution Time: {}ms, Error: {}",
                    serviceName, methodName, executionTime, throwable.getMessage(), throwable);
            throw throwable;
        }
    }
}
