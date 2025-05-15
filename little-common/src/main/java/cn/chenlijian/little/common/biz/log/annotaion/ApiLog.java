package cn.chenlijian.little.common.biz.log.annotaion;

import java.lang.annotation.*;

/**
 * API日志注解
 * 用于在方法级别记录操作日志的信息，包括日志描述等
 *
 * @author chenlijian xdmdcp@163.com
 */
// 定义该注解应用于方法级别
@Target(value = ElementType.METHOD)
// 定义该注解在运行时可见
@Retention(RetentionPolicy.RUNTIME)
// 表示该注解将被包含在javadoc中
@Documented
public @interface ApiLog {

    /**
     * 日志描述，默认为空
     * 用于记录该操作的详细描述信息
     */
    String value() default "";

    /**
     * 是否启用 操作日志
     *
     * @return 是否启用
     */
    boolean enabled() default true;

    // TODO 待完善
//    /**
//     * 是否拼接Controller类上的描述值
//     *
//     * @return 是否拼接Controller类上的描述值
//     */
//    boolean controllerApiValue() default true;
//
//    /**
//     * 记录执行参数
//     *
//     * @return 是否记录执行参数
//     */
//    boolean request() default true;
//
//    /**
//     * 当 request = false时， 方法报错记录请求参数
//     *
//     * @return 当 request = false时， 方法报错记录请求参数
//     */
//    boolean requestByError() default true;
//
//    /**
//     * 记录返回参数
//     *
//     * @return 是否记录返回参数
//     */
//    boolean response() default true;
}
