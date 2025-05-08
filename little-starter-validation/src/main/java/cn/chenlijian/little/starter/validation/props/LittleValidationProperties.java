package cn.chenlijian.little.starter.validation.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = LittleValidationProperties.PREFIX)
public class LittleValidationProperties {

    public static final String PREFIX = "little.validation";

    private boolean enabled = true;

    /**
     * 遇到第一个校验错误立即返回
     */
    private boolean failFast = true;
}
