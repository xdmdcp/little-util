package cn.chenlijian.little.starter.security.aspect;

import cn.chenlijian.little.starter.security.props.LittleSecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Slf4j
@Aspect
public class UriSecurityAspect implements ApplicationContextAware {

    /**
     * 表达式处理
     */
    private static final ExpressionParser SPEL_PARSER = new SpelExpressionParser();
    private final LittleSecurityProperties properties;
    private ApplicationContext applicationContext;


    public UriSecurityAspect(LittleSecurityProperties properties) {
        this.properties = properties;
    }

    @Around("@annotation(cn.chenlijian.little.common.annotation.security.PreAuth) || @within(cn.chenlijian.little.common.annotation.security.PreAuth)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (properties.isEnabled()) {
            log.debug("UriSecurityAspect start.");
            // 权限验证
            handleAuth(joinPoint);
        }

        return joinPoint.proceed();
    }

    public void handleAuth(ProceedingJoinPoint joinPoint) throws Throwable {
        // TODO 权限验证
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
