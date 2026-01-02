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
public class ControllerLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ControllerLoggingAspect.class);

    /**
     * Pointcut that matches all public methods in classes within the controller
     * package
     */
    @Pointcut("within(rgonzalez.smbc.contacts.controller..*) && execution(public * *(..))")
    public void restControllerMethods() {
    }

    /**
     * Around advice that logs the controller name, method name, and parameter
     * values
     * 
     * @param joinPoint the join point
     * @return the result of the method invocation
     * @throws Throwable if an exception occurs during method execution
     */
    @Around("restControllerMethods()")
    public Object logRestControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String controllerName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("Entering REST Controller - Class: {}, Method: {}, Parameters: {}",
                controllerName, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("Exiting REST Controller - Class: {}, Method: {}, Execution Time: {}ms",
                    controllerName, methodName, executionTime);
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Exception in REST Controller - Class: {}, Method: {}, Execution Time: {}ms, Error: {}",
                    controllerName, methodName, executionTime, throwable.getMessage(), throwable);
            throw throwable;
        }
    }
}
