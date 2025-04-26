package cn.chenlijian.little.starter.social.properties;

import cn.hutool.core.map.MapUtil;
import lombok.Getter;
import lombok.Setter;
import me.zhyd.oauth.config.AuthConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.net.Proxy;
import java.util.Map;

/**
 * SocialProperties 类用于配置社交登录的相关属性
 * 它通过 @ConfigurationProperties 注解绑定配置文件中的属性前缀 "social"
 *
 * @author chenlijian
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "social", ignoreInvalidFields = true)
public class SocialProperties {

    /**
     * 启用社交登录功能的标志
     * 默认为 false，表示未启用
     */
    private Boolean enabled = false;

    /**
     * 存储不同社交平台的配置信息
     * 使用 Map 结构，键为社交平台的默认源（如 GitHub，Google 等），
     * 值为对应平台的配置信息（如客户端ID，客户端密钥等）
     */
    private Map<String, AuthConfig> type = MapUtil.newHashMap();

    /**
     * 扩展配置属性
     * 通过嵌套配置属性，可以更细化地配置社交登录的各个方面
     */
    @NestedConfigurationProperty
    private SocialExtendProperties extend;

    /**
     * http 相关的配置，可设置请求超时时间和代理配置
     */
    private AuthHttpConfig httpConfig;

    /**
     * 代理配置类
     * 包含代理类型、主机名和端口配置
     */
    @Getter
    @Setter
    public static class AuthProxyConfig {
        private String type = Proxy.Type.HTTP.name();
        private String hostname;
        private int port;
    }

    /**
     * HTTP 配置类
     * 包含请求超时时间和代理配置
     */
    @Getter
    @Setter
    public static class AuthHttpConfig {
        private int timeout;
        private Map<String, AuthProxyConfig> proxy;
    }
}
