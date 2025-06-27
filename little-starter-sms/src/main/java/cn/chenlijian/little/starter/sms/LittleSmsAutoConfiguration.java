package cn.chenlijian.little.starter.sms;

import cn.chenlijian.little.starter.sms.enums.SmsType;
import cn.chenlijian.little.starter.sms.factory.SmsStrategyFactory;
import cn.chenlijian.little.starter.sms.props.LittleSmsProperties;
import cn.chenlijian.little.starter.sms.strategy.SmsStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties(LittleSmsProperties.class)
@ConditionalOnProperty(prefix = LittleSmsProperties.PREFIX, name = "enabled", havingValue = "true")
public class LittleSmsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SmsStrategyFactory smsStrategyFactory(List<SmsStrategy> strategies) {
        return new SmsStrategyFactory(strategies);
    }

    @Bean
    @ConditionalOnMissingBean
    public SmsStrategy smsStrategy(LittleSmsProperties properties, SmsStrategyFactory factory) {
        if (!properties.isEnable()) {
            log.warn("SMS service is disabled.");
            return null;
        }

        SmsType type = SmsType.get(properties.getType());
        return factory.get(type);
    }
}
