package cn.chenlijian.little.starter.log;

import cn.chenlijian.little.starter.log.aspect.ApiLogAspect;
import cn.chenlijian.little.starter.log.props.LittleLogProperties;
import cn.chenlijian.little.starter.log.props.SamplingProperties;
import cn.chenlijian.little.starter.log.publisher.DefaultLogPublisher;
import cn.chenlijian.little.starter.log.publisher.LogPublisher;
import cn.chenlijian.little.starter.log.sampling.SamplingStrategy;
import cn.chenlijian.little.starter.log.sampling.strategy.ErrorForcedSamplingStrategy;
import cn.chenlijian.little.starter.log.sampling.strategy.RandomSamplingStrategy;
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
 * 日志自动配置类
 * 该类负责在满足特定条件时自动配置日志相关的组件
 * @author chenlijian xdmdcp@163.com
 */
@Slf4j
@Configuration
@EnableAspectJAutoProxy
@EnableAsync
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = LittleLogProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties({LittleLogProperties.class, SamplingProperties.class})
public class LittleLogAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("Initializing little-starter-log successfully.");
    }

    /**
     * 创建并配置ApiLogAspect切面
     * <p>
     * 该方法通过Spring的@Bean注解定义了一个Bean，名称为apiLogAspect
     * 此Bean负责处理API日志记录的切面逻辑，使用了AspectJ的切面编程方式
     *
     * @param properties   LittleLog属性配置，用于定制日志记录的行为和特性
     * @param logPublisher 日志发布者接口，用于将记录的日志信息发布到指定的目标
     * @return 返回配置好的ApiLogAspect切面实例
     */
    @Bean
    public ApiLogAspect apiLogAspect(LittleLogProperties properties, LogPublisher logPublisher, SamplingStrategy samplingStrategy) {
        return new ApiLogAspect(properties, logPublisher, samplingStrategy);
    }

    /**
     * 创建并配置一个日志发布者的Bean
     * 该方法使用Spring框架的@Bean注解来声明一个Bean，使用@ConditionalOnMissingBean注解来确保只有在容器中没有其他同类型Bean时才创建该Bean
     * 这样做是为了避免Bean的重复创建，并提供了一种默认实现，当用户没有提供自定义实现时
     *
     * @return LogPublisher 实例，返回一个默认的日志发布者实现
     */
    @Bean
    @ConditionalOnMissingBean
    public LogPublisher defaultLogPublisher() {
        return new DefaultLogPublisher();
    }

    /**
     * 配置默认的采样策略Bean
     * 该方法根据应用属性配置了一个采样策略，如果容器中没有其他SamplingStrategy bean存在，则创建一个
     *
     * @param properties 采样属性，包含了采样率等配置信息
     * @return 返回一个根据配置初始化的采样策略对象
     */
    @Bean
    @ConditionalOnMissingBean
    public SamplingStrategy defaultSamplingStrategy(SamplingProperties properties) {
        // 创建一个随机采样策略实例
        RandomSamplingStrategy strategy = new RandomSamplingStrategy();
        // 设置采样率，决定采样的频率
        strategy.setSampleRate(properties.getRate());
        // 返回一个错误强制采样策略，结合随机策略和配置，允许在某些情况下强制进行采样
        return new ErrorForcedSamplingStrategy(strategy, properties);
    }

}
