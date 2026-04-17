package dwe.holding.admin.authorisation.tenant;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class ControllerMethodNameAdvice {
    @Before("within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Controller *)")
    public void logMethodName(JoinPoint joinPoint) {
        log.info("Method called: {}", joinPoint.getSignature().getDeclaringTypeName() +":"+ joinPoint.getSignature().getName());
    }
}
