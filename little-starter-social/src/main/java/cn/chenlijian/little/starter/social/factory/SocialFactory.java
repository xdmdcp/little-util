package cn.chenlijian.little.starter.social.factory;

import cn.chenlijian.little.starter.social.properties.SocialProperties;
import cn.chenlijian.little.starter.social.util.SocialUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.request.AuthRequest;

import java.util.List;

/**
 * 认证工厂
 *
 * @author chenlijian
 * @since 2025-04-25 20:02
 **/
@Slf4j
@RequiredArgsConstructor
public class SocialFactory {

    // 第三方社交账号认证配置
    private final SocialProperties properties;

    /**
     * 获取类型列表
     * <p>
     * 此方法旨在生成一个包含默认类型和扩展类型的列表通过检查属性对象及其扩展配置来实现
     * 它首先检查属性对象是否存在如果不存在，则返回一个空列表
     * 接着，它会构建一个默认列表，如果属性对象的类型为空，则默认列表为空，否则根据类型对象的键构建列表
     * 扩展列表的构建较为复杂，它需要检查扩展配置是否存在以及是否包含有效的枚举类和配置
     * 在处理扩展列表时，为了防止潜在的错误，使用了try-catch块来捕获并记录异常
     * 最后，将默认列表和扩展列表合并，并返回结果列表
     *
     * @return 类型列表如果属性对象为空或不包含有效数据，则返回空列表
     */
    public List<String> getTypeList() {
        return SocialUtil.getTypeList(properties);
    }

    /**
     * 根据来源获取认证请求对象
     * 此方法首先检查来源字符串是否为空，如果为空，则抛出异常
     * 然后尝试获取默认的认证请求对象，如果成功获取，则返回该对象
     * 如果默认请求对象获取失败，则尝试根据扩展枚举类获取认证请求对象
     * 如果扩展枚举类未配置，则返回null
     * 如果根据扩展枚举类无法获取认证请求对象，则抛出不支持的来源异常
     *
     * @param source 认证请求的来源，例如特定的认证系统或服务
     * @return AuthRequest对象，如果无法处理来源，则可能返回null或抛出异常
     * @throws AuthException 如果来源为空、不支持或发生其他意外错误，则抛出此异常
     */
    public AuthRequest getAuthRequest(String source) throws AuthException {
        // 检查来源字符串是否为空，如果为空，则记录错误日志并抛出异常
        if (StrUtil.isBlank(source)) {
            log.error("Auth source is blank or null: {}", source);
            throw new AuthException(AuthResponseStatus.NO_AUTH_SOURCE);
        }

        try {
            return SocialUtil.getAuthRequest(source, properties);
        } catch (Exception e) {
            // 如果在处理认证请求过程中发生意外错误，则记录错误日志并抛出异常
            log.error("Unexpected error occurred while processing auth request for source: {}", source, e);
            throw new AuthException("Unexpected error: " + e.getMessage());
        }
    }
}
