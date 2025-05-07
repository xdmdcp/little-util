package cn.chenlijian.little.starter.log.aspect;

import cn.chenlijian.little.starter.log.annotation.OptLog;
import cn.chenlijian.little.starter.log.entity.OptLogDTO;
import cn.chenlijian.little.starter.log.service.LogPublisher;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * @author chenlijian xdmdcp@163.com
 */
@Aspect
@RequiredArgsConstructor
public class OptLogAspect {

    private final LogPublisher logPublisher;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(optLog)")
    public Object around(ProceedingJoinPoint joinPoint, OptLog optLog) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            OptLogDTO logDTO = buildLogRecord(joinPoint, optLog, result, exception, end - start);
            logPublisher.publish(logDTO);
        }
    }

    private OptLogDTO buildLogRecord(ProceedingJoinPoint joinPoint, OptLog logAnnotation, Object result, Exception exception, long costTime) {
        // 解析SpEL表达式
//        EvaluationContext context = new MethodBasedEvaluationContext(null, method, args, new ParameterNameDiscoverer());
//
//        String businessId = parser.parseExpression(logAnnotation.businessId())
//                .getValue(context, String.class);

        // 填充其他字段...

        OptLogDTO optLogDTO = new OptLogDTO();

        return optLogDTO;
    }
}
