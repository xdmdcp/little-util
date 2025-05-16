package cn.chenlijian.little.starter.swagger;

import cn.chenlijian.little.starter.swagger.props.LittleSwaggerProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 自动配置类，基于 LittleSwaggerProperties 构建 OpenAPI 文档
 */
@Slf4j
@Configuration
@AllArgsConstructor
@AutoConfigureBefore(SpringDocConfiguration.class)
@EnableConfigurationProperties(LittleSwaggerProperties.class)
@ConditionalOnProperty(prefix = LittleSwaggerProperties.PREFIX, name = "enabled", havingValue = "true")
public class LittleSwaggerAutoConfiguration {

    private static final String DEFAULT_BASE_PATH = "/**";
    private static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_HEADER = "Little-Auth";

    private final LittleSwaggerProperties properties;

    /**
     * 初始化OpenAPI对象
     *
     * @return OpenAPI对象，包含API文档的基本信息、安全策略等
     */
    @Bean
    public OpenAPI openApi() {
        // 初始化OpenAPI对象，并设置API的基本信息、安全策略、联系人信息、许可信息以及外部文档链接
        return new OpenAPI()
                .components(new Components()
                        // 添加安全策略，配置API密钥（Token）和鉴权机制
                        .addSecuritySchemes(TOKEN_HEADER,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .name(TOKEN_HEADER)
                        )
                        // 添加安全策略，配置API密钥（Authorization）和鉴权机制
                        .addSecuritySchemes(AUTHORIZATION_HEADER,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name(AUTHORIZATION_HEADER)
                        )
                )
                // 设置API文档的基本信息，包括标题、描述、联系方式和许可信息
                .info(new Info()
                        .title(properties.getTitle())
                        .description(properties.getDescription())
                        .termsOfService(properties.getTermsOfServiceUrl())
                        .contact(new Contact()
                                .name(properties.getContact().getName())
                                .email(properties.getContact().getEmail())
                                .url(properties.getContact().getUrl())
                        )
                        .license(new License()
                                .name(properties.getLicense())
                                .url(properties.getLicenseUrl())
                        )
                        .version(properties.getVersion())
                );
    }

    /**
     * 初始化GlobalOpenApiCustomizer对象
     *
     * @return GlobalOpenApiCustomizer对象，用于全局定制OpenAPI
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((s, pathItem) ->
                        pathItem.readOperations().forEach(operation ->
                                operation.addSecurityItem(new SecurityRequirement()
                                        .addList(AUTHORIZATION_HEADER)
                                        .addList(TOKEN_HEADER))));
            }
        };
    }

    /**
     * 初始化GroupedOpenApi对象
     *
     * @return GroupedOpenApi对象，用于定义默认的API分组
     */
    @Bean
    @ConditionalOnMissingBean
    public GroupedOpenApi defaultApi() {
        // 获取并防御性复制配置项
        List<String> basePath = getOrDefault(properties.getBasePath(), Collections.singletonList(DEFAULT_BASE_PATH));
        List<String> excludePath = getOrDefault(properties.getExcludePath(), DEFAULT_EXCLUDE_PATH);
        List<String> basePackages = getOrDefault(properties.getBasePackages(), Collections.emptyList());
        List<String> excludePackages = getOrDefault(properties.getExcludePackages(), Collections.emptyList());

        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch(basePath.toArray(new String[0]))
                .pathsToExclude(excludePath.toArray(new String[0]))
                .packagesToScan(basePackages.toArray(new String[0]))
                .packagesToExclude(excludePackages.toArray(new String[0]))
                .build();
    }

    /**
     * 安全获取配置值，防止 null 引发异常
     *
     * @param list 配置项列表
     * @param defaultValue 默认值
     * @return 配置项列表或默认值
     */
    private <T> List<T> getOrDefault(List<T> list, List<T> defaultValue) {
        return list == null ? defaultValue : list;
    }
}
