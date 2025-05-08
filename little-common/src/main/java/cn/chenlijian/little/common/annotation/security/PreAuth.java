package cn.chenlijian.little.common.annotation.security;

import java.lang.annotation.*;

// 定义该注解应用于方法级别
@Target(value = ElementType.METHOD)
// 定义该注解在运行时可见
@Retention(RetentionPolicy.RUNTIME)
// 表示该注解将被包含在javadoc中
@Documented
public @interface PreAuth {

    String value() default "permit()";

    String replace() default "";
}
