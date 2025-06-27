package cn.chenlijian.little.starter.sms.props;// cn.chenlijian.little.sms.SmsProperties.java

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

import static cn.chenlijian.little.starter.sms.props.LittleSmsProperties.PREFIX;

@Data
@ConfigurationProperties(PREFIX)
public class LittleSmsProperties {

    public static final String PREFIX = "little.sms";
    private final Map<String, ProviderConfig> providers = new HashMap<>();
    private boolean enable = true;
    private String type;

    @Data
    public static class ProviderConfig {
        private String accessKey;
        private String secretKey;
        private String region;
    }
}
