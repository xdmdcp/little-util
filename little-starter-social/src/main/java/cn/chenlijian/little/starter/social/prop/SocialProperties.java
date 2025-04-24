package cn.chenlijian.little.starter.social.prop;

import cn.hutool.core.map.MapUtil;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * SocialProperties 类用于配置社交登录的相关属性
 * 它通过 @ConfigurationProperties 注解绑定配置文件中的属性前缀 "social"
 *
 * @author chenlijian
 */
//@Getter
//@Setter
@ConfigurationProperties(prefix = "social", ignoreInvalidFields = true)
public class SocialProperties {

    /**
     * 启用社交登录功能的标志
     * 默认为 false，表示未启用
     */
    private Boolean enabled = false;

    /**
     * 社交登录的域名地址
     * 用于构建回调URL等
     */
    private String domain;

    /**
     * 存储不同社交平台的配置信息
     * 使用 Map 结构，键为社交平台的默认源（如 GitHub，Google 等），
     * 值为对应平台的配置信息（如客户端ID，客户端密钥等）
     */
    private Map<AuthDefaultSource, AuthConfig> oauth = MapUtil.newHashMap();

    /**
     * 存储社交平台的别名
     * 使用 Map 结构，键为社交平台的别名，值为对应平台的源名称
     * 这允许在配置中使用更友好的名称来代替复杂的源名称
     */
    private Map<String, String> alias = MapUtil.newHashMap();


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Map<AuthDefaultSource, AuthConfig> getOauth() {
        return oauth;
    }

    public void setOauth(Map<AuthDefaultSource, AuthConfig> oauth) {
        this.oauth = oauth;
    }

    public Map<String, String> getAlias() {
        return alias;
    }

    public void setAlias(Map<String, String> alias) {
        this.alias = alias;
    }
}
