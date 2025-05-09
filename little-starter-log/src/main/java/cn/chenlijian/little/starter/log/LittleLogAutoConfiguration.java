package cn.chenlijian.little.starter.log;

import cn.chenlijian.little.starter.log.aspect.ApiLogAspect;
import cn.chenlijian.little.starter.log.props.LittleLogProperties;
import cn.chenlijian.little.starter.log.publisher.LogPublisher;
import cn.chenlijian.little.starter.log.publisher.DefaultLogPublisher;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author chenlijian xdmdcp@163.com
 */
@Slf4j
@Configuration
@EnableAspectJAutoProxy
@EnableAsync
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = LittleLogProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(LittleLogProperties.class)
public class LittleLogAutoConfiguration {

    private final LittleLogProperties properties;

    public LittleLogAutoConfiguration(LittleLogProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        if (properties == null) {
            throw new IllegalStateException("LittleLogProperties is not properly initialized.");
        }
        log.info("Initializing little-starter-log successfully.");
    }

    @Bean
    public ApiLogAspect apiLogAspect(LogPublisher logPublisher) {
        return new ApiLogAspect(properties, logPublisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public LogPublisher logPublisher() {
        return new DefaultLogPublisher();
    }
}
