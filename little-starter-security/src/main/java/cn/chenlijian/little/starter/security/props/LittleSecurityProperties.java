package cn.chenlijian.little.starter.security.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 安全模块自动配置类
 * 用于配置应用程序的安全属性，通过配置文件加载相关设置
 */
@Data
@ConfigurationProperties(prefix = LittleSecurityProperties.PREFIX)
public class LittleSecurityProperties {
    /**
     * 配置属性前缀
     * 用于在配置文件中标识属于本类的属性
     */
    public static final String PREFIX = "little.log";

    /**
     * 是否启用安全模块
     * 默认值为 true，表示默认情况下安全模块是启用的
     */
    private boolean enabled = true;

    /**
     * 权限是否区分大小写
     */
    private Boolean caseSensitive = false;

    /**
     * 不需要鉴权的请求路径列表
     * 用于指定跳过某些路径的请求
     */
    private List<String> excludePaths;
}