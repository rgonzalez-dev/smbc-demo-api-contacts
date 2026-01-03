package rgonzalez.smbc.contacts.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Profile("mix2")
@Aspect
@Component
public class DataSourceTracker {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceTracker.class);

    @Before("execution(* rgonzalez.smbc.contacts.dao.*.find*(..))")
    public void trackReadOperations(JoinPoint joinPoint) {
        logger.info("READ Operation - Method: {}, Expected to use Secondary DataSource",
                joinPoint.getSignature().getName());
    }

    @Before("execution(* rgonzalez.smbc.contacts.dao.*.save*(..))")
    public void trackWriteOperations(JoinPoint joinPoint) {
        logger.info("WRITE Operation - Method: {}, Using Primary DataSource",
                joinPoint.getSignature().getName());
    }

    @Before("execution(* rgonzalez.smbc.contacts.dao.*.delete*(..))")
    public void trackDeleteOperations(JoinPoint joinPoint) {
        logger.info("DELETE Operation - Method: {}, Using Primary DataSource",
                joinPoint.getSignature().getName());
    }

    @Before("execution(* rgonzalez.smbc.contacts.dao.*.update*(..))")
    public void trackUpdateOperations(JoinPoint joinPoint) {
        logger.info("UPDATE Operation - Method: {}, Using Primary DataSource",
                joinPoint.getSignature().getName());
    }
}
