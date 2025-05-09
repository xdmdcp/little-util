package cn.chenlijian.little.starter.log.aspect;

import cn.chenlijian.little.common.annotation.log.ApiLog;
import cn.chenlijian.little.common.entity.log.ApiLogDTO;
import cn.chenlijian.little.core.context.ContextUtil;
import cn.chenlijian.little.core.utils.StrPool;
import cn.chenlijian.little.starter.log.props.LittleLogProperties;
import cn.chenlijian.little.starter.log.publisher.LogPublisher;
import cn.chenlijian.little.starter.log.utils.LogUtil;
import cn.chenlijian.little.starter.log.utils.WebUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateAwareExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.AntPathMatcher;

import java.lang.reflect.Method;

/**
 * AOP切面类，用于记录API日志
 * @author chenlijian xdmdcp@163.com
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class ApiLogAspect {

    private static final int MAX_LENGTH = 65535;
    private final TemplateAwareExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    private final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private final LittleLogProperties properties;
    private final LogPublisher logPublisher;

    /**
     * 环绕通知，用于记录API日志
     * @param point 切入点
     * @param apiLog ApiLog注解
     * @return 方法执行结果
     * @throws Throwable 方法执行过程中抛出的异常
     */
    @Around("@annotation(apiLog)")
    public Object around(ProceedingJoinPoint point, ApiLog apiLog) throws Throwable {
        Long start = System.currentTimeMillis();
        Object result = null;
        Throwable throwable = null;
        try {
            result = point.proceed();
        } catch (Throwable t) {
            throwable = t;
        } finally {
            // 不管是否抛出异常，end 时间都在最后统一记录
            Long end = System.currentTimeMillis();
            HttpServletRequest request = WebUtil.getRequest();
            HttpServletResponse response = WebUtil.getResponse();
            // 只有需要记录日志时才构造并发布日志
            if (check(request, apiLog)) {
                ApiLogDTO logDTO = buildApiLogDTO(point, apiLog, request, response, result, throwable, start, end);
                logPublisher.publish(logDTO);
            }

            // 如果有异常，重新抛出
            if (throwable != null) {
                throw throwable;
            }
        }

        return result;
    }

    /**
     * 构建ApiLogDTO对象
     * @param joinPoint 切入点
     * @param apiLog ApiLog注解
     * @param request HTTP请求
     * @param result 方法执行结果
     * @param e 方法执行过程中抛出的异常
     * @param start 开始时间
     * @param end 结束时间
     * @return 构建好的ApiLogDTO对象
     */
    private ApiLogDTO buildApiLogDTO(ProceedingJoinPoint joinPoint, ApiLog apiLog, HttpServletRequest request, HttpServletResponse response,
                                     Object result, Throwable e, Long start, Long end) {
        ApiLogDTO apiLogDTO = new ApiLogDTO();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();

        // ========== 基础信息 ==========
        // 从请求头或上下文中获取 Trace ID
        String traceId = LogUtil.getTraceId(request, properties.getTraceIdKey());
        apiLogDTO.setTraceId(traceId);

        // ========== 请求信息 ==========
        LogUtil.addRequestInfoToLog(request, apiLogDTO);

        apiLogDTO.setRequestTime(start);
        apiLogDTO.setRequestBody(getText(WebUtil.getRequestContent(request)));
        apiLogDTO.setClassPath(className);
        apiLogDTO.setActionMethod(methodName);

        // ========== 响应信息 ==========
        apiLogDTO.setResponseTime(end);
        apiLogDTO.setResponseBody(getText(String.valueOf(result)));
        apiLogDTO.setResponseStatus(response.getStatus());

        // ========== 业务信息 ==========
        // 描述信息（支持SpEL表达式）
        String description = parseSpelExpression(apiLog.value(), method, args);
        apiLogDTO.setDescription(description);
        apiLogDTO.setUsername(ContextUtil.getUsername());
        apiLogDTO.setUserId(ContextUtil.getUserId());

        // ========== 系统信息 ==========
        if (e != null) {
            apiLogDTO.setErrorMessage(e.getMessage());
            apiLogDTO.setErrorStack(ExceptionUtil.stacktraceToString(e, MAX_LENGTH));
        }

        return apiLogDTO;
    }

    /**
     * 解析SpEL表达式
     * @param expression 表达式
     * @param method 方法
     * @param args 方法参数
     * @return 解析后的字符串
     */
    private String parseSpelExpression(String expression, Method method, Object[] args) {
        if (StrUtil.isBlank(expression)) {
            return "";
        }

        // 如果不是 SpEL 模板格式（即没有 #{...}），直接返回原值
        if (!expression.contains("#{")) {
            return expression;
        }

        try {
            MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(expression, method, args, PARAMETER_NAME_DISCOVERER);

            // 使用 TemplateAwareExpressionParser 支持混合表达式解析
            return EXPRESSION_PARSER.parseExpression(expression, ParserContext.TEMPLATE_EXPRESSION).getValue(context, String.class);
        } catch (SpelEvaluationException ex) {
            log.warn("SpEL 表达式解析失败: {}, 错误详情: {}", expression, ex.getMessage());
            return "[SpEL Error]";
        } catch (SpelParseException ex) {
            log.warn("SpEL 表达式格式错误: {}", expression, ex);
            return "[Invalid SpEL]";
        } catch (Exception ex) {
            log.error("解析表达式失败: {}", expression, ex);
            return StrPool.EMPTY;
        }
    }

    /**
     * 检查是否需要记录日志
     * @param apiLog ApiLog注解
     * @return true 表示需要记录日志
     */
    private boolean check(HttpServletRequest request, ApiLog apiLog) {
        // 如果注解标记为不启用，直接返回 false
        if (!apiLog.enabled()) {
            return false;
        }

        if (request == null) {
            return false;
        }

        String requestUri = request.getRequestURI();
        return properties.getExcludePaths().stream().noneMatch(pattern -> PATH_MATCHER.match(pattern, requestUri));
    }

    /**
     * 限制字符串长度
     * @param val 字符串内容
     * @return 限制长度后的字符串
     */
    private String getText(String val) {
        return StrUtil.sub(val, 0, MAX_LENGTH);
    }
}
