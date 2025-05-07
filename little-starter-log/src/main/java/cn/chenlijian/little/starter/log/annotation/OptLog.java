package cn.chenlijian.little.starter.log.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于在方法级别记录操作日志的信息，包括模块、日志描述等
 *
 * @author chenlijian xdmdcp@163.com
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OptLog {
    /**
     * 模块，默认为空
     * 用于记录该操作所属的模块，以便于分类和统计
     */
    String module() default "";

    /**
     * 日志描述，默认为空
     * 用于记录该操作的详细描述信息
     */
    String description() default "";
}
