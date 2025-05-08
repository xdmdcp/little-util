package cn.chenlijian.little.starter.validation;

import cn.chenlijian.little.starter.validation.advice.ValidationExceptionAdvice;
import cn.chenlijian.little.starter.validation.props.LittleValidationProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = LittleValidationProperties.PREFIX, name = "enabled", havingValue = "true")
@ConditionalOnClass(Validator.class)
public class LittleValidationAutoConfiguration {

    private final LittleValidationProperties properties;

    public LittleValidationAutoConfiguration(LittleValidationProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        if (properties == null) {
            throw new IllegalStateException("LittleValidationProperties is not properly initialized.");
        }
        log.info("Initializing little-starter-validation successfully.");
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationExceptionAdvice validationExceptionAdvice() {
        return new ValidationExceptionAdvice();
    }

    // 可选：配置快速失败模式
    @Bean
    public Validator validator() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setProviderClass(HibernateValidator.class);
        // 推荐方式：通过 addProperty 设置 fail_fast
        factoryBean.getValidationPropertyMap().put("hibernate.validator.fail_fast", String.valueOf(properties.isFailFast()));
        factoryBean.afterPropertiesSet(); // 初始化
        return factoryBean;
    }
}
