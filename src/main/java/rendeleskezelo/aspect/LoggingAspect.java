package rendeleskezelo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* rendeleskezelo.controller.UserController.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String args = Arrays.stream(joinPoint.getArgs())
                .map(this::sanitizeArg)
                .collect(Collectors.joining(", "));
        log.info("Method called: {}.{}() with arguments: [{}]",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                args);
    }

    @AfterReturning(pointcut = "execution(* rendeleskezelo.controller.UserController.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method returned: {}.{}() with result: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                sanitizeResult(result));
    }
    @Before("execution(* rendeleskezelo.controller.HomeController.*(..))")
    public void logBeforeHome(JoinPoint joinPoint) {
        String args = Arrays.stream(joinPoint.getArgs())
                .map(this::sanitizeArg)
                .collect(Collectors.joining(", "));
        log.info("Method called: {}.{}() with arguments: [{}]",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                args);
    }

    @AfterReturning(pointcut = "execution(* rendeleskezelo.controller.HomeController.*(..))", returning = "result")
    public void logAfterReturningHome(JoinPoint joinPoint, Object result) {
        log.info("Method returned: {}.{}() with result: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                sanitizeResult(result));
    }



    private String sanitizeArg(Object arg) {
        if (arg instanceof rendeleskezelo.model.User) {
            // Ha a User objektumot naplóznánk, akkor a password mezőt eltávolítjuk
            rendeleskezelo.model.User user = (rendeleskezelo.model.User) arg;
            return String.format("User{id=%d, username='%s', roles='%s'}",
                    user.getId(), user.getUsername(), user.getRole());
        }
        return arg != null ? arg.toString() : "null";
    }



    private Object sanitizeResult(Object result) {
        // Ha szükséges, az eredményt is megtisztítjuk (például listák esetén)
        if (result instanceof Iterable) {
            return StreamSupport.stream(((Iterable<?>) result).spliterator(), false)
                    .map(this::sanitizeArg)
                    .collect(Collectors.toList());
        }
        return result;
    }

}
