package cn.chenlijian.little.starter.social.util;

import cn.chenlijian.little.starter.social.properties.SocialExtendProperties;
import cn.chenlijian.little.starter.social.properties.SocialProperties;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.xkcoding.http.config.HttpConfig;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.request.*;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * SocialUtil类提供社交平台的认证工具方法
 */
@Slf4j
public class SocialUtil {

    private static final Map<AuthDefaultSource, Class<? extends AuthRequest>> DEFAULT_REQUEST_MAP = new ConcurrentHashMap<>();
    private static final Map<String, SocialExtendProperties.AuthRequestExtendConfig> EXTEND_CONFIG_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, AuthDefaultSource> SOURCE_CACHE = new HashMap<>();

    static {
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.GITHUB, AuthGithubRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.WEIBO, AuthWeiboRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.GITEE, AuthGiteeRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.DINGTALK, AuthDingTalkRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.DINGTALK_V2, AuthDingTalkV2Request.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.DINGTALK_ACCOUNT, AuthDingTalkAccountRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.BAIDU, AuthBaiduRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.CODING, AuthCodingRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.OSCHINA, AuthOschinaRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.ALIPAY, AuthAlipayRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.QQ, AuthQqRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.WECHAT_OPEN, AuthWeChatOpenRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.WECHAT_MP, AuthWeChatMpRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.TAOBAO, AuthTaobaoRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.GOOGLE, AuthGoogleRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.FACEBOOK, AuthFacebookRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.DOUYIN, AuthDouyinRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.LINKEDIN, AuthLinkedinRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.MICROSOFT, AuthMicrosoftRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.MICROSOFT_CN, AuthMicrosoftCnRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.MI, AuthMiRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.TOUTIAO, AuthToutiaoRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.TEAMBITION, AuthTeambitionRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.RENREN, AuthRenrenRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.PINTEREST, AuthPinterestRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.STACK_OVERFLOW, AuthStackOverflowRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.HUAWEI_V3, AuthHuaweiV3Request.class); // 替换弃用的HUAWEI
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.WECHAT_ENTERPRISE, AuthWeChatEnterpriseQrcodeRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.WECHAT_ENTERPRISE_V2, AuthWeChatEnterpriseQrcodeV2Request.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.WECHAT_ENTERPRISE_QRCODE_THIRD, AuthWeChatEnterpriseThirdQrcodeRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.WECHAT_ENTERPRISE_WEB, AuthWeChatEnterpriseWebRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.KUJIALE, AuthKujialeRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.GITLAB, AuthGitlabRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.MEITUAN, AuthMeituanRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.ELEME, AuthElemeRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.TWITTER, AuthTwitterRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.FEISHU, AuthFeishuRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.JD, AuthJdRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.ALIYUN, AuthAliyunRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.XMLY, AuthXmlyRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.AMAZON, AuthAmazonRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.SLACK, AuthSlackRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.LINE, AuthLineRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.OKTA, AuthOktaRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.PROGINN, AuthProginnRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.AFDIAN, AuthAfDianRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.APPLE, AuthAppleRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.FIGMA, AuthFigmaRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.WECHAT_MINI_PROGRAM, AuthWechatMiniProgramRequest.class);
        DEFAULT_REQUEST_MAP.put(AuthDefaultSource.QQ_MINI_PROGRAM, AuthQQMiniProgramRequest.class);

        for (AuthDefaultSource source : AuthDefaultSource.values()) {
            SOURCE_CACHE.put(source.name().toUpperCase(), source);
        }
    }

    private SocialUtil() {
    }

    /**
     * 获取类型列表。
     * <p>
     * 该方法根据传入的 SocialProperties 配置对象，生成一个包含默认类型和扩展类型的字符串列表。
     * 如果配置对象为空，则返回一个空列表。如果扩展配置无效或发生异常，会记录相应的日志信息。
     *
     * @param properties 包含社交平台配置信息的对象，用于提取默认类型和扩展类型。
     *                   - properties.getType()：获取默认类型的映射。
     *                   - properties.getExtend()：获取扩展配置，包括枚举类和扩展映射。
     * @return 返回一个包含默认类型和扩展类型的字符串列表。
     */
    @SuppressWarnings({"unchecked"})
    public static List<String> getTypeList(SocialProperties properties) {
        // 检查配置对象是否为空，如果为空则返回空列表
        if (ObjectUtil.isNull(properties)) {
            return CollUtil.newArrayList();
        }

        // 根据配置对象获取默认类型列表，如果类型映射为空，则创建空列表
        List<String> defaultList = ObjectUtil.isNull(properties.getType()) ? CollUtil.newArrayList() : new ArrayList<>(properties.getType().keySet());

        // 初始化扩展类型列表
        List<String> extendList = CollUtil.newArrayList();
        try {
            // 检查扩展配置是否有效，如果有效则处理扩展类型
            if (properties.getExtend() != null && properties.getExtend().getEnumClass() != null && properties.getExtend().getConfig() != null) {
                Class<?> enumClass = properties.getExtend().getEnumClass();
                List<String> names = EnumUtil.getNames((Class<? extends Enum<?>>) enumClass);
                if (CollUtil.isNotEmpty(names)) {
                    // 过滤并转换扩展类型的键，使其与枚举名称匹配，并转换为大写
                    extendList = properties.getExtend().getConfig().keySet().stream()
                            .filter(key -> key != null && names.contains(key.toUpperCase()))
                            .map(String::toUpperCase).toList();
                }
            }
        } catch (IllegalArgumentException e) {
            // 处理无效枚举类异常
            log.warn("Invalid enum class provided: {}", e.getMessage());
        } catch (Exception e) {
            // 处理其他异常
            log.error("Error occurred while processing extend list: {}", e.getMessage(), e);
        }

        // 合并默认类型和扩展类型列表，并返回结果
        List<String> result = new ArrayList<>(defaultList);
        result.addAll(extendList);
        return result;
    }

    /**
     * 根据来源和社交属性获取认证请求对象
     *
     * @param source     认证请求的来源，例如微博、微信等
     * @param properties 包含社交认证属性的配置对象
     * @return 认证请求对象
     * @throws AuthException 如果认证请求的来源为空或不支持，则抛出此异常
     */
    public static AuthRequest getAuthRequest(String source, SocialProperties properties) throws AuthException {
        // 检查认证来源是否为空，如果为空则抛出异常
        if (CharSequenceUtil.isBlank(source)) {
            throw new AuthException(AuthResponseStatus.NO_AUTH_SOURCE);
        }

        try {
            // 尝试获取默认的认证请求对象
            AuthRequest authRequest = getDefaultAuthRequest(source, properties);
            // 如果默认认证请求对象不为空，则直接返回
            if (authRequest != null) {
                return authRequest;
            }

            // 获取扩展请求的枚举类
            Class<?> enumClass = properties.getExtend().getEnumClass();
            // 如果枚举类为空，则抛出异常
            if (enumClass == null) {
                throw new AuthException("Enum class for extend request is null.");
            }

            // 根据枚举类获取扩展的认证请求对象
            authRequest = getExtendRequest(enumClass, source, properties);
            // 如果扩展认证请求对象为空，则抛出不支持的异常
            if (authRequest == null) {
                throw new AuthException(AuthResponseStatus.UNSUPPORTED);
            }

            // 返回扩展的认证请求对象
            return authRequest;

        } catch (Exception e) {
            // 捕获意外的异常，并包装为认证异常抛出
            throw new AuthException("Unexpected error: " + e.getMessage(), e);
        }
    }

    /**
     * 根据提供的源和社交属性配置，获取默认的认证请求对象
     * 此方法用于初始化一个AuthRequest对象，该对象包含了进行社交认证所需的所有配置信息
     *
     * @param source     社交平台的标识符，例如"weibo"，用于确定使用哪种社交平台进行认证
     * @param properties 包含了所有社交平台相关配置的属性对象，用于获取特定社交平台的详细配置信息
     * @return 返回一个初始化后的AuthRequest对象，如果配置无效或解析源失败，则返回null
     */
    public static AuthRequest getDefaultAuthRequest(String source, SocialProperties properties) {
        try {
            // 根据提供的源解析出对应的默认认证源
            AuthDefaultSource authDefaultSource = resolve(source);
            // 从配置中获取对应认证源的配置信息
            AuthConfig config = properties.getType().get(authDefaultSource.name());
            if (config == null) {
                // 如果没有找到对应的配置信息，则记录警告并返回null
                log.warn("No configuration found for source: {}", authDefaultSource);
                return null;
            }

            // 设置HTTP配置，这是进行社交认证时网络请求所需的配置
            setHttpConfig(authDefaultSource.name(), config, properties.getHttpConfig());
            // 使用解析得到的认证源和配置信息获取默认的认证请求对象
            return getDefaultAuthRequest(authDefaultSource, config);

        } catch (IllegalArgumentException e) {
            // 如果提供的源无效，则捕获异常并记录警告，同时返回null
            log.warn("Invalid source provided: {}", source, e);
            return null;
        }
    }

    /**
     * 根据指定的参数获取扩展的认证请求对象。
     *
     * @param clazz      枚举类类型，用于验证 source 是否为合法的枚举值。
     * @param source     数据来源标识符，用于匹配扩展配置中的具体配置项。
     * @param properties 社交属性配置对象，包含扩展配置和 HTTP 配置等信息。
     * @return 返回一个 AuthRequest 对象，如果无法创建或配置无效则返回 null。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static AuthRequest getExtendRequest(Class clazz, String source, SocialProperties properties) {
        // 如果 source 为空或空白字符串，直接返回 null。
        if (CharSequenceUtil.isBlank(source)) {
            return null;
        }

        // 将 source 转换为大写形式，并尝试通过 EnumUtil 验证其是否为合法的枚举值。
        String sourceName = source.toUpperCase();
        try {
            EnumUtil.fromString(clazz, sourceName);
        } catch (IllegalArgumentException e) {
            log.warn("No matching enum found for source: {}", sourceName, e);
            return null;
        }

        // 获取扩展配置，如果扩展配置为空或无效，记录警告日志并返回 null。
        Map<String, SocialExtendProperties.AuthRequestExtendConfig> extendConfig = properties.getExtend().getConfig();
        if (CollUtil.isEmpty(extendConfig)) {
            log.warn("Extend config is empty or null");
            return null;
        }

        // 如果缓存为空，则初始化缓存并将扩展配置转换为大写键值对后存入缓存。
        if (CollUtil.isEmpty(EXTEND_CONFIG_CACHE)) {
            synchronized (EXTEND_CONFIG_CACHE) {
                if (EXTEND_CONFIG_CACHE.isEmpty()) {
                    EXTEND_CONFIG_CACHE.putAll(getUpperConfigCache(extendConfig));
                }
            }
        }

        // 从缓存中获取指定 source 的扩展配置，如果未找到，记录警告日志并返回 null。
        SocialExtendProperties.AuthRequestExtendConfig config = EXTEND_CONFIG_CACHE.get(sourceName);
        if (config == null) {
            log.warn("No auth request extend config found for source: {}", sourceName);
            return null;
        }

        // 设置 HTTP 配置，确保扩展配置中的 HTTP 配置与全局配置一致。
        setHttpConfig(sourceName, config, properties.getHttpConfig());

        // 获取扩展配置中定义的请求类，如果请求类为空，记录警告日志并返回 null。
        Class<? extends AuthRequest> requestClass = config.getRequestClass();
        if (requestClass == null) {
            log.warn("Request class is null for source: {}", sourceName);
            return null;
        }

        // 尝试创建 AuthRequest 实例，如果创建失败，记录错误日志并返回 null。
        try {
            return createAuthRequestInstance(requestClass, config);
        } catch (Exception e) {
            log.error("Failed to create AuthRequest instance for source: {}", sourceName, e);
            return null;
        }
    }

    /**
     * 获取默认的认证请求对象
     * 根据提供的认证源和认证配置，实例化并返回对应的认证请求对象
     *
     * @param authSource 认证源，用于确定使用哪种认证方式
     * @param authConfig 认证配置，包含认证所需的配置信息
     * @return 实例化的认证请求对象
     * @throws AuthException 如果认证配置为空或没有找到对应的请求类，则抛出认证异常
     */
    private static AuthRequest getDefaultAuthRequest(AuthDefaultSource authSource, AuthConfig authConfig) {
        // 检查认证配置是否为空，如果为空则抛出认证异常
        if (authConfig == null) {
            throw new AuthException("Authentication configuration is null");
        }

        // 根据认证源获取对应的认证请求类
        Class<? extends AuthRequest> requestClass = DEFAULT_REQUEST_MAP.get(authSource);
        // 如果没有找到对应的请求类，则抛出认证异常
        if (requestClass == null) {
            throw new AuthException("No request class found for authentication source");
        }

        // 尝试实例化认证请求对象
        try {
            return ReflectUtil.newInstance(requestClass, authConfig);
        } catch (Exception e) {
            // 如果实例化失败，记录错误信息并抛出认证异常
            String errorMessage = "Failed to instantiate request class for source: " + authSource.name() + ", Error: " + e.getMessage();
            log.error(errorMessage, e);
            throw new AuthException(errorMessage);
        }
    }

    /**
     * 配置HTTP相关的代理和超时设置。
     *
     * @param authSource 认证来源，用于从代理配置中查找对应的代理信息。
     * @param authConfig 认证配置对象，用于存储最终生成的HTTP配置。
     * @param httpConfig HTTP配置对象，包含代理和超时等相关信息。
     *                   <p>
     *                   该方法会根据传入的HTTP配置对象，解析代理和超时设置，并将其应用到认证配置对象中。
     *                   如果配置无效或缺失，会记录警告日志并跳过相关配置。
     */
    private static void setHttpConfig(String authSource, AuthConfig authConfig, SocialProperties.AuthHttpConfig httpConfig) {
        // 如果HTTP配置为空，记录警告日志并直接返回
        if (httpConfig == null) {
            log.warn("HttpConfig is null, skipping configuration.");
            return;
        }

        // 获取代理配置映射表，如果为空或为空集合，记录警告日志并直接返回
        Map<String, SocialProperties.AuthProxyConfig> proxyConfigMap = httpConfig.getProxy();
        if (CollectionUtils.isEmpty(proxyConfigMap)) {
            log.warn("Proxy config map is empty, skipping configuration.");
            return;
        }

        // 根据认证来源获取对应的代理配置，如果未找到，记录警告日志并直接返回
        SocialProperties.AuthProxyConfig proxyConfig = proxyConfigMap.get(authSource);
        if (proxyConfig == null) {
            log.warn("No proxy config found for auth source: {}", authSource);
            return;
        }

        // 获取超时时间，如果为空或无效，使用默认值30秒
        int timeout = ObjectUtil.defaultIfNull(httpConfig.getTimeout(), 30);
        if (timeout <= 0) {
            log.warn("Invalid timeout value: {}. Using default timeout.", timeout);
            timeout = 30;
        }

        // 解析代理类型，如果类型无效，默认使用HTTP类型
        String proxyTypeStr = proxyConfig.getType();
        Proxy.Type proxyType = Proxy.Type.HTTP;
        try {
            proxyType = Proxy.Type.valueOf(proxyTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid proxy type: {}. Using default type 'HTTP'.", proxyTypeStr);
        }

        // 验证代理主机名和端口的有效性，如果无效，记录错误日志并直接返回
        String hostname = proxyConfig.getHostname();
        int port = proxyConfig.getPort();
        if (CharSequenceUtil.isBlank(hostname) || port <= 0 || port > 65535) {
            log.error("Invalid proxy hostname or port: hostname={}, port={}. Skipping proxy configuration.", hostname, port);
            return;
        }

        // 构建HTTP配置并设置到认证配置对象中
        authConfig.setHttpConfig(HttpConfig.builder()
                .timeout(timeout)
                .proxy(new Proxy(proxyType, new InetSocketAddress(hostname, port)))
                .build());
    }

    /**
     * 根据传入的 source 字符串解析并返回对应的 AuthDefaultSource 对象。
     *
     * @param source 社交平台来源的标识字符串，通常为平台名称或其缩写。
     *               该参数会被转换为大写后用于查找缓存中的对应对象。
     * @return 返回与 source 对应的 AuthDefaultSource 对象。
     * 如果缓存中不存在对应的对象，则抛出异常。
     * @throws AuthException 如果传入的 source 无法匹配到任何已知的社交平台来源，
     *                       则抛出此异常，并附带错误信息。
     */
    private static AuthDefaultSource resolve(String source) {
        // 尝试从缓存中获取与 source 对应的 AuthDefaultSource 对象
        AuthDefaultSource cachedSource = SOURCE_CACHE.get(source.toUpperCase());

        // 如果缓存中未找到对应的对象，抛出异常提示未知的社交平台来源
        if (cachedSource == null) {
            throw new AuthException("Unrecognized social platform source: " + source);
        }

        // 返回从缓存中获取到的 AuthDefaultSource 对象
        return cachedSource;
    }

    /**
     * 将输入的配置映射中的键转换为大写形式，并返回一个新的映射。
     *
     * @param extendConfig 原始配置映射，其中键为字符串，值为 SocialExtendProperties.AuthRequestExtendConfig 类型。
     *                     该参数不能为空，且应包含需要处理的键值对。
     * @return 一个新的映射，其中所有键都被转换为大写形式，值保持不变。
     * 如果存在重复的键（忽略大小写），则保留原始映射中首次出现的值。
     */
    private static Map<String, SocialExtendProperties.AuthRequestExtendConfig> getUpperConfigCache(Map<String, SocialExtendProperties.AuthRequestExtendConfig> extendConfig) {
        // 使用流操作将原始映射的键转换为大写形式，并收集到一个新的 HashMap 中。
        return extendConfig.entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey().toUpperCase(), // 将键转换为大写
                Map.Entry::getValue,                  // 保留原始值
                (existing, replacement) -> existing,  // 如果键冲突，保留现有值
                () -> new HashMap<>(extendConfig.size()))); // 初始化目标映射，预设容量以优化性能
    }

    /**
     * 创建一个指定类型的 AuthRequest 实例。
     *
     * @param requestClass 指定的 AuthRequest 类型，必须包含一个接受两个参数的构造函数。
     *                     第一个参数为 SocialExtendProperties.AuthRequestExtendConfig 类型，
     *                     第二个参数为其他可能的配置参数。
     * @param config       配置对象，用于初始化 AuthRequest 实例。不能为空。
     * @return 返回一个通过反射创建的 AuthRequest 实例。
     * @throws IllegalArgumentException 如果 config 为 null，或者 requestClass 不包含符合要求的构造函数。
     */
    private static AuthRequest createAuthRequestInstance(Class<? extends AuthRequest> requestClass, SocialExtendProperties.AuthRequestExtendConfig config) {
        // 检查配置对象是否为空，如果为空则抛出异常
        if (config == null) {
            throw new IllegalArgumentException("Config must not be null");
        }

        // 检查目标类是否包含符合要求的双参数构造函数，如果不满足条件则抛出异常
        if (!hasTwoArgsConstructor(requestClass)) {
            throw new IllegalArgumentException("Request class must have a two-parameter constructor: " + requestClass.getName());
        }

        // 使用反射工具类创建并返回 AuthRequest 实例
        return ReflectUtil.newInstance(requestClass, config);
    }

    /**
     * 检查给定的类是否包含一个接受两个 Object 类型参数的构造函数。
     *
     * @param clazz 要检查的类的 Class 对象。
     *              该参数不能为 null，且应为一个有效的类对象。
     * @return 如果类中存在一个接受两个 Object 类型参数的构造函数，则返回 true；
     * 如果不存在这样的构造函数，则返回 false。
     */
    private static boolean hasTwoArgsConstructor(Class<?> clazz) {
        try {
            // 尝试获取类中接受两个 Object 类型参数的构造函数。
            clazz.getConstructor(Object.class, Object.class);
            return true;
        } catch (NoSuchMethodException e) {
            // 如果未找到对应的构造函数，则捕获异常并返回 false。
            return false;
        }
    }
}
