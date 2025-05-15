package cn.chenlijian.little.starter.security.aspect;

import cn.chenlijian.little.common.biz.security.annotation.PreAuth;
import cn.chenlijian.little.starter.security.props.LittleSecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Aspect
public class UriSecurityAspect implements ApplicationContextAware {

    /**
     * 表达式处理
     */
    private final LittleSecurityProperties properties;
    private ApplicationContext applicationContext;


    public UriSecurityAspect(LittleSecurityProperties properties) {
        this.properties = properties;
    }

    @Around("@annotation(cn.chenlijian.little.common.biz.security.annotation.PreAuth) || @within(cn.chenlijian.little.common.biz.security.annotation.PreAuth)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        if (!properties.isEnabled()) {
            return joinPoint.proceed();
        }

        log.debug("UriSecurityAspect start.");
        // 权限验证
        handleAuth(joinPoint);

        return joinPoint.proceed();
    }

    public void handleAuth(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前请求路径
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestURI = Objects.requireNonNull(attributes).getRequest().getRequestURI();

        // 排除路径检查
        if (properties.getExcludePaths().contains(requestURI)) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated.");
        }

        PreAuth preAuth = getPreAuthAnnotation(joinPoint);
        if (preAuth == null) {
            return;
        }

        String requiredPermission = preAuth.value();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasPermission = authorities.stream()
                .anyMatch(a -> isAuthorityMatch(requiredPermission, a.getAuthority(), properties.getCaseSensitive()));

        if (!hasPermission) {
            throw new RuntimeException("Permission denied: " + requiredPermission);
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private PreAuth getPreAuthAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(PreAuth.class);
    }

    /**
     * 判断权限是否匹配
     *
     * @param requiredPermission 需要的权限
     * @param userAuthority      用户拥有的权限
     * @param caseSensitive      是否区分大小写
     * @return 是否匹配
     */
    public static boolean isAuthorityMatch(String requiredPermission, String userAuthority, boolean caseSensitive) {
        if (requiredPermission == null || userAuthority == null) {
            return false;
        }
        if (caseSensitive) {
            return requiredPermission.equals(userAuthority);
        } else {
            return requiredPermission.equalsIgnoreCase(userAuthority);
        }
    }
}
