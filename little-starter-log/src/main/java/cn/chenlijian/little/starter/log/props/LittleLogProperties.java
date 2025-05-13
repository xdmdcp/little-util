package cn.chenlijian.little.starter.log.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 使用Lombok的@Data注解自动生成getter和setter方法，简化代码
 * 使用@ConfigurationProperties注解将本类与配置文件中的属性关联起来
 * prefix定义了配置属性的前缀，ignoreInvalidFields设为true以忽略未知字段
 *
 * @author chenlijian xdmdcp@163.com
 */
@Data
@ConfigurationProperties(prefix = LittleLogProperties.PREFIX)
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
     * 需要排除的日志路径列表
     * 用于指定某些路径的日志不被记录，以避免不必要的日志信息
     */
    private List<String> excludePaths = List.of("/health");

    /**
     * 请求Header中 traceId 的 Key
     * 用于指定日志追踪ID的key
     */
    private String traceIdKey = "X-Trace-ID";

}
