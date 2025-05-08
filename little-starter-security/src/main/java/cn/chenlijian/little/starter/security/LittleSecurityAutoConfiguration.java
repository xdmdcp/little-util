package cn.chenlijian.little.starter.security;

import cn.chenlijian.little.starter.security.aspect.UriSecurityAspect;
import cn.chenlijian.little.starter.security.props.LittleSecurityProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 安全模块自动配置类
 * 用于配置应用程序的安全属性，通过配置文件加载相关设置
 */
@Slf4j
@Configuration
@EnableAspectJAutoProxy
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = LittleSecurityProperties.PREFIX, name = "enabled", havingValue = "true")
public class LittleSecurityAutoConfiguration {

    private final LittleSecurityProperties properties;

    public LittleSecurityAutoConfiguration(LittleSecurityProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        if (properties == null) {
            throw new IllegalStateException("LittleSecurityProperties is not properly initialized.");
        }
        log.info("Initializing little-starter-security successfully.");
    }

    @Bean
    public UriSecurityAspect uriSecurityAspect() {
        return new UriSecurityAspect(properties);
    }

}
