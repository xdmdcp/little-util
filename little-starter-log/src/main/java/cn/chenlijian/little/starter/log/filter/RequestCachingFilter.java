package cn.chenlijian.little.starter.log.filter;

import cn.chenlijian.little.starter.log.props.LittleLogProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 请求缓存过滤器，用于缓存HTTP请求内容
 * 主要用途是记录请求信息，以便于日志记录和问题追踪
 */
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = LittleLogProperties.PREFIX, name = "enabled", havingValue = "true")
public class RequestCachingFilter implements WebMvcConfigurer {

    /**
     * 创建并注册请求缓存过滤器
     * 该过滤器将包装HttpServletRequest对象，以实现请求内容的缓存
     *
     * @return FilterRegistrationBean 包含过滤器注册信息的Bean
     */
    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> requestCachingFilter() {
        // 创建FilterRegistrationBean对象，用于注册过滤器
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        // 设置过滤器的具体逻辑
        registration.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                // 使用ContentCachingRequestWrapper包装请求对象，以实现内容缓存
                HttpServletRequest wrappedRequest = new ContentCachingRequestWrapper(request);
                // 继续执行其他过滤器或目标方法
                filterChain.doFilter(wrappedRequest, response);
            }
        });
        // 添加URL模式，使该过滤器应用于所有请求
        registration.addUrlPatterns("/*");
        return registration;
    }
}
