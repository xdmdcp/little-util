package cn.chenlijian.little.starter.log.aspect;

import cn.chenlijian.little.common.annotation.log.ApiLog;
import cn.chenlijian.little.common.entity.log.ApiLogDTO;
import cn.chenlijian.little.core.context.ContextUtil;
import cn.chenlijian.little.starter.log.props.LittleLogProperties;
import cn.chenlijian.little.starter.log.publisher.LogPublisher;
import cn.chenlijian.little.starter.log.utils.LogUtil;
import cn.chenlijian.little.starter.log.utils.WebUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author chenlijian xdmdcp@163.com
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class ApiLogAspect {

    private static final int MAX_LENGTH = 65535;
    private final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    private final LittleLogProperties logProperties;
    private final LogPublisher logPublisher;

    @Around("@annotation(apiLog)")
    public Object around(ProceedingJoinPoint point, ApiLog apiLog) throws Throwable {
        // 记录方法开始执行的时间
        long start = System.currentTimeMillis();
        Object result;

        try {
            // 执行原始方法
            result = point.proceed();
        } catch (Throwable t) {
            // 异常情况下记录日志
            if (check(point, apiLog)) {
                long cost = System.currentTimeMillis() - start;
                ApiLogDTO logDTO = buildApiLogDTO(point, apiLog, null, t, cost);
                logPublisher.publish(logDTO);
            }
            throw t;
        }

        // 正常流程记录日志
        if (check(point, apiLog)) {
            long cost = System.currentTimeMillis() - start;
            ApiLogDTO logDTO = buildApiLogDTO(point, apiLog, result, null, cost);
            logPublisher.publish(logDTO);
        }

        return result;
    }

    private ApiLogDTO buildApiLogDTO(ProceedingJoinPoint joinPoint, ApiLog apiLog, Object result, Throwable e, long costTime) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();

        String description = apiLog.value();

        // TODO 解析描述信息，注意异常处理防止SPeL解析失败导致整体日志失败
//        String description;
//        try {
//            description = parseSpelExpression(apiLog.value(), method, args);
//        } catch (Exception ex) {
//            description = "Failed to parse SpEL expression: " + ex.getMessage();
//        }

        HttpServletRequest request = WebUtil.getRequest();

        ApiLogDTO apiLogDTO = new ApiLogDTO();

        LogUtil.addRequestInfoToLog(request, apiLogDTO);

        apiLogDTO.setConsumingTime(costTime);
        apiLogDTO.setCreatedBy(ContextUtil.getUserId());
        apiLogDTO.setUserName(ContextUtil.getUserName());
        apiLogDTO.setDescription(description);
        apiLogDTO.setClassPath(className);
        apiLogDTO.setActionMethod(methodName);
        apiLogDTO.setResult(Objects.toString(result, null));
        if (e != null) {
            apiLogDTO.setExDetail(ExceptionUtil.stacktraceToString(e, MAX_LENGTH));
        }

        return apiLogDTO;
    }

    private String parseSpelExpression(String expression, Method method, Object[] args) {
        if (expression == null || expression.trim().isEmpty()) {
            return "";
        }

        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(expression, method, args, PARAMETER_NAME_DISCOVERER);

        try {
            return EXPRESSION_PARSER.parseExpression(expression).getValue(context, String.class);
        } catch (Exception e) {
            log.error("解析表达式失败: {}", expression, e);
            return null;
        }
    }


    /**
     * 监测是否需要记录日志
     *
     * @param joinPoint 端点
     * @param apiLog    操作日志
     * @return true 表示需要记录日志
     */
    private boolean check(JoinPoint joinPoint, ApiLog apiLog) {
        // TODO 跳过需要忽略的url
        return apiLog.enabled();
    }
}
