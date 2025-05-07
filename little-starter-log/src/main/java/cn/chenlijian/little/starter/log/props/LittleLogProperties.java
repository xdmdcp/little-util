/**
 * 日志配置属性类
 * 用于配置日志相关的属性，以便在应用程序中根据这些属性决定日志行为
 */
package cn.chenlijian.little.starter.log.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 使用Lombok的@Data注解自动生成getter和setter方法，简化代码
 * 使用@ConfigurationProperties注解将本类与配置文件中的属性关联起来
 * prefix定义了配置属性的前缀，ignoreInvalidFields设为true以忽略未知字段
 */
@Data
@ConfigurationProperties(prefix = LittleLogProperties.PREFIX, ignoreInvalidFields = true)
public class LittleLogProperties {
    /**
     * 配置属性前缀
     * 用于在配置文件中标识属于本类的属性
     */
    public static final String PREFIX = "little.log";

    /**
     * 日志功能启用标志
     * 默认值为true，表示默认情况下日志功能是启用的
     */
    private boolean enabled = true;

    /**
     * 日志功能是否启用异步处理
     * 默认值为true，表示默认情况下日志功能是启用异步处理的
     */
    private boolean async = true;


}
