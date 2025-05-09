package cn.chenlijian.little.starter.log.utils;

import cn.chenlijian.little.common.annotation.log.ApiLog;
import cn.chenlijian.little.common.entity.log.ApiLogDTO;
import cn.chenlijian.little.core.utils.StrPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;

import java.lang.reflect.Method;

@Slf4j
public class LogUtil {
    private LogUtil() {
    }


    /**
     * 向log中添加补齐request的信息
     *
     * @param request     请求
     * @param logDTO 日志基础类
     */
    public static void addRequestInfoToLog(HttpServletRequest request, ApiLogDTO logDTO) {
        if (ObjectUtil.isNotEmpty(request)) {
            logDTO.setHttpMethod(request.getMethod());
            logDTO.setRequestPath(URLUtil.getPath(request.getRequestURI()));
            logDTO.setRequestParams(WebUtil.getRequestParamString(request));
            logDTO.setClientIp(WebUtil.getIP(request));
            logDTO.setUserAgent(request.getHeader(WebUtil.USER_AGENT_HEADER));
        }
    }

    /**
     * 从HTTP请求中获取追踪ID
     * 追踪ID用于跟踪和监控请求在系统中的处理过程，有助于问题定位和性能分析
     *
     * @param request HTTP请求对象，不能为空
     * @param traceIdKey 追踪ID的键名，用于从请求头中获取追踪ID
     * @return 返回追踪ID字符串，如果请求中没有对应的追踪ID，则返回null
     */
    public static String getTraceId(HttpServletRequest request, String traceIdKey) {
        // 检查请求对象是否非空，以避免空指针异常
        if (request != null) {
            // 从请求头中获取追踪ID，并返回
            return request.getHeader(traceIdKey);
        }
        // 如果请求对象为空，则返回null，表示无法获取追踪ID
        return null;
    }


    /**
     * 获取方法的描述信息
     * 该方法用于获取被ApiLog注解标记的方法的描述信息，以便于在日志中记录
     * 如果方法没有被ApiLog注解标记，则返回空字符串
     *
     * @param point 切入点对象，包含被拦截方法的信息
     * @return 方法的描述信息如果找不到ApiLog注解，则返回空字符串
     */
    public static String getDescribe(JoinPoint point) {
        // 获取目标方法上的ApiLog注解
        ApiLog annotation = getTargetAnnotation(point);
        if (annotation == null) {
            // 如果目标方法上没有ApiLog注解，则返回空字符串
            return StrPool.EMPTY;
        }
        // 返回ApiLog注解中的描述信息
        return annotation.value();
    }

    /**
     * 获取ApiLog注解的描述信息
     *
     * @param annotation ApiLog注解实例，用于获取注解的描述信息
     * @return 如果annotation为null，则返回空字符串；否则返回注解的描述信息
     */
    public static String getDescribe(ApiLog annotation) {
        // 检查传入的注解是否为null
        if (annotation == null) {
            // 如果注解为null，则返回空字符串
            return StrPool.EMPTY;
        }
        // 如果注解不为null，则返回注解的描述信息
        return annotation.value();
    }

    /**
     * 获取目标方法上的 @ApiLog 注解实例
     * 此方法用于在AOP（面向切面编程）中提取被拦截方法的注解信息，以便进行日志记录或其他操作
     *
     * @param point 切入点对象，包含被拦截方法的信息
     * @return 返回 @ApiLog 注解实例，如果方法上没有该注解，则返回 null
     */
    public static ApiLog getTargetAnnotation(JoinPoint point) {
        try {
            ApiLog annotation = null;
            // 检查签名是否为方法签名
            if (point.getSignature() instanceof MethodSignature) {
                // 获取方法对象
                Method method = ((MethodSignature) point.getSignature()).getMethod();
                if (method != null) {
                    // 获取方法上的 @ApiLog 注解
                    annotation = method.getAnnotation(ApiLog.class);
                }
            }
            return annotation;
        } catch (Exception e) {
            // 记录获取注解失败的日志
            log.warn("获取 {}.{} 的 @ApiLog 注解失败", point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), e);
            return null;
        }
    }
}
