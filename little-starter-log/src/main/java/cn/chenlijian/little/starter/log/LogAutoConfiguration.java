package cn.chenlijian.little.starter.log;

import cn.chenlijian.little.starter.log.aspect.OptLogAspect;
import cn.chenlijian.little.starter.log.props.OptLogProperties;
import cn.chenlijian.little.starter.log.service.LogPublisher;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author chenlijian xdmdcp@163.com
 */
@EnableAsync
@Configuration
@AllArgsConstructor
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = OptLogProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogAutoConfiguration {

    private final LogPublisher logPublisher;
    private final OptLogProperties optLogProperties;

    @Bean
    @ConditionalOnMissingBean
    public OptLogAspect optLogAspect() {
        return new OptLogAspect(logPublisher);
    }
}