package cn.chenlijian.little.starter.social.config;

import cn.chenlijian.little.starter.social.prop.SocialProperties;
import com.xkcoding.http.HttpUtil;
import com.xkcoding.http.support.Http;
import com.xkcoding.http.support.httpclient.HttpClientImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * SocialConfiguration 类是Spring Boot的自动配置类
 * 它用于自动配置社交功能相关的bean
 * 该类通过使用Spring Boot的条件注解来决定是否创建和配置特定的bean
 *
 * @author chenlijian
 */
@AutoConfiguration
@EnableConfigurationProperties(SocialProperties.class)
public class SocialConfiguration {

    /**
     * 创建并配置一个简单的Http客户端实例
     * 此方法在没有现有Http bean定义时被调用，以提供一个默认的Http实现
     * 使用HttpClientImpl实现类初始化HttpClient，并通过HttpUtil类设置初始化后的实例
     *
     * @return Http 实例，提供HTTP功能
     */
    @Bean
    @ConditionalOnMissingBean(Http.class)
    public Http simpleHttp() {
        // 创建HttpClientImpl实例，这是一个具体的Http实现
        HttpClientImpl httpClient = new HttpClientImpl();
        // 使用HttpUtil工具类方法设置HttpClient实例，确保应用中其他地方可以使用同一Http实例
        HttpUtil.setHttp(httpClient);
        // 返回创建的HttpClient实例
        return httpClient;
    }

}
