package cn.chenlijian.little.common.constant;

/**
 * 上下文常量
 * @author chenlijian
 */
public final class ContextConstants {

    /**
     * JWT中封装的 用户id
     */
    public static final String JWT_KEY_USER_ID = "userid";
    /**
     * JWT中封装的 用户名称
     */
    public static final String JWT_KEY_NAME = "name";

    /**
     * JWT中封装的 用户账号
     */
    public static final String JWT_KEY_ACCOUNT = "account";

    /**
     * 刷新 Token
     */
    public static final String REFRESH_TOKEN_KEY = "refresh_token";

    /**
     * User信息 认证请求头
     */
    public static final String BEARER_HEADER_KEY = "token";
    /**
     * User信息 认证请求头前缀
     */
    public static final String BEARER_HEADER_PREFIX = "Bearer ";
    /**
     * User信息 认证请求头前缀
     */
    public static final String BEARER_HEADER_PREFIX_EXT = "Bearer%20";
    /**
     * User信息 认证请求头前缀长度
     */
    public static final int BEARER_HEADER_PREFIX_LENGTH = BEARER_HEADER_PREFIX.length();

    /**
     * Client信息认证请求头
     */
    public static final String BASIC_HEADER_KEY = "Authorization";

    /**
     * 灰度发布版本号
     */
    public static final String GRAY_VERSION = "grayversion";
}
