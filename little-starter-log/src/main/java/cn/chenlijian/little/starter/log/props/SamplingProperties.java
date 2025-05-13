package cn.chenlijian.little.starter.log.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 采样配置属性类，用于控制日志采样的相关参数
 *
 * @author chenlijian xdmdcp@163.com
 */
@Data
@ConfigurationProperties(prefix = SamplingProperties.PREFIX)
public class SamplingProperties {

    /**
     * 配置属性的前缀
     */
    public static final String PREFIX = "little.log.sampling";

    /**
     * 采样率，表示日志记录的概率
     * 例如：0.1 表示 10% 的概率会记录日志，默认值为 1（即 100% 记录）
     */
    private double rate = 1;

    /**
     * 是否强制记录异常请求
     * 默认值为 true，表示在发生异常时总是记录日志，无论采样率如何
     */
    private Boolean includeError = true;
}
