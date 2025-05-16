package cn.chenlijian.little.starter.swagger.props;

import cn.chenlijian.little.common.constant.AppConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.chenlijian.little.starter.swagger.props.LittleSwaggerProperties.PREFIX;

/**
 * Swagger 自动配置属性类
 */
@Data
@Component
@Validated
@ConfigurationProperties(PREFIX)
public class LittleSwaggerProperties {

    public static final String PREFIX = "little.swagger";

    /**
     * 是否启用 Swagger 文档生成
     */
    private boolean enabled = true;

    /**
     * 要扫描的包路径列表
     */
    private List<String> basePackages = new ArrayList<>(Collections.singletonList(AppConstant.BASE_PACKAGES));

    /**
     * 排除扫描的包路径列表
     */
    private List<String> excludePackages = new ArrayList<>();

    /**
     * 要包含在文档中的 URL 路径规则（正则）
     */
    private List<String> basePath = new ArrayList<>(Collections.singletonList("^/api.*"));

    /**
     * 排除的 URL 路径规则（正则）
     */
    private List<String> excludePath = new ArrayList<>(Collections.singletonList("^/error.*"));

    /**
     * 文档标题
     */
    private String title = "Little API Documentation";

    /**
     * 文档描述
     */
    private String description = "OpenAPI 3 Documentation powered by Knife4j";

    /**
     * 文档版本号
     */
    private String version = AppConstant.APPLICATION_VERSION;

    /**
     * 许可证名称
     */
    private String license = "Apache License, Version 2.0";

    /**
     * 许可证链接
     */
    private String licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0.html";

    /**
     * 服务条款链接
     */
    private String termsOfServiceUrl = "https://github.com/xdmdcp/little";

    /**
     * 主机地址（可选）
     */
    private String host = "";

    /**
     * 联系人信息
     */
    private Contact contact = new Contact();

    /**
     * 全局鉴权配置
     */
    private Authorization authorization = new Authorization();

    // 嵌套类定义

    @Data
    public static class Contact {
        /**
         * 联系人姓名
         */
        private String name = "写代码的陈皮";

        /**
         * 联系人邮箱
         */
        private String email = "xdmdcp@163.com";

        /**
         * 联系人主页
         */
        private String url = "https://github.com/xdmdcp";
    }

    @Data
    public static class Authorization {
        /**
         * 鉴权策略名称（如 BearerAuth）
         */
        private String name = "BearerAuth";

        /**
         * 需要应用该鉴权的 URL 正则表达式
         */
        private String authRegex = "^.*$";

        /**
         * Token 获取地址列表（用于 OAuth2 等场景）
         */
        private List<String> tokenUrlList = new ArrayList<>();
    }
}
