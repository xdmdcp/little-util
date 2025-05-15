package cn.chenlijian.little.starter.security;

import cn.chenlijian.little.starter.security.aspect.UriSecurityAspect;
import cn.chenlijian.little.starter.security.filter.JwtAuthenticationFilter;
import cn.chenlijian.little.starter.security.props.LittleSecurityProperties;
import cn.chenlijian.little.starter.security.utils.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 安全模块自动配置类
 * 用于配置应用程序的安全属性，通过配置文件加载相关设置
 */
@Slf4j
@Configuration
@EnableAspectJAutoProxy
@ConditionalOnProperty(prefix = LittleSecurityProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties({LittleSecurityProperties.class})
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
    @ConditionalOnMissingBean
    public JwtAuthenticationFilter jwtAuthenticationFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        return new JwtAuthenticationFilter(userDetailsService, jwtUtil);
    }

    /**
     * 配置Security过滤链
     * 该方法用于定义Web安全配置，如请求的认证方式、CSRF保护、会话管理等
     * 使用@Bean注解声明该方法返回一个由Spring管理的Bean
     * 使用@ConditionalOnMissingBean注解表示只有在容器中没有找到同类型的Bean时才创建该Bean
     *
     * @param http 用于配置Web安全设置的对象
     * @param jwtAuthenticationFilter 自定义的JWT认证过滤器，用于在该链中添加JWT验证功能
     * @return 返回配置好的SecurityFilterChain对象
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        log.debug("Exclude paths configured: {}", properties.getExcludePaths());

        return http
                // 禁用CSRF保护，适用于无状态认证方案
                .csrf(AbstractHttpConfigurer::disable)
                // 配置会话管理策略为无状态
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 在UsernamePasswordAuthenticationFilter之前添加JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> {
                    // 批量配置不需要认证的请求路径
                    for (String path : properties.getExcludePaths()) {
                        auth.requestMatchers(path).permitAll();
                    }
                    // 配置所有其他请求需要认证
                    auth.anyRequest().authenticated();
                })
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public UriSecurityAspect uriSecurityAspect() {
        return new UriSecurityAspect(properties);
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(properties);
    }
}
