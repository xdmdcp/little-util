/**
 * 用于标记操作日志的注解
 * 该注解主要用于方法级别，用于记录操作日志的相关信息
 */
package cn.chenlijian.little.starter.log.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于在方法级别记录操作日志的信息，包括业务ID、操作者、模块、类型、描述等
 * 可以配置是否启用日志记录以及是否异步记录
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OptLog {
    /**
     * 是否启用日志记录，默认启用
     * 当设置为false时，将不会记录该操作的日志
     */
    boolean enabled() default true;

    /**
     * 业务ID，默认为空
     * 用于记录该操作相关的业务ID，以便于追踪和审计
     */
    String businessId() default "";

    /**
     * 操作者，默认为空
     * 用于记录执行该操作的用户信息
     */
    String operator() default "";

    /**
     * 模块，默认为空
     * 用于记录该操作所属的模块，以便于分类和统计
     */
    String module() default "";

    /**
     * 类型，默认为空
     * 用于记录该操作的类型，可以是自定义的任何字符串，用于分类和过滤
     */
    String type() default "";

    /**
     * 描述，默认为空
     * 用于记录该操作的详细描述信息
     */
    String description() default "";

    /**
     * 是否异步记录，默认为true
     * 当设置为true时，日志记录将在异步线程中执行，不会阻塞当前操作
     */
    boolean async() default true;
}
