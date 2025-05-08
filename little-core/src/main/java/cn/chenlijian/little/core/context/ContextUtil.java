package cn.chenlijian.little.core.context;

import cn.chenlijian.little.core.utils.StrPool;
import cn.hutool.core.convert.Convert;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文工具类
 * 获取当前线程变量中的用户id，用户昵称，账号等信息
 * @author chenlijian
 **/
public final class ContextUtil {

    private static final ThreadLocal<Map<String, String>> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    private ContextUtil() {
    }

    public static Map<String, String> getLocalContext() {
        return CONTEXT_THREAD_LOCAL.get();
    }

    public static void setLocalContext(Map<String, String> context) {
        CONTEXT_THREAD_LOCAL.set(context);
    }

    public static void removeLocalContext() {
        CONTEXT_THREAD_LOCAL.remove();
    }

    public static String get(String key) {
        Map<String, String> context = getLocalContext();
        if (context == null) {
            return null;
        }
        return context.get(key);
    }

    public static void set(String key, Object value) {
        Map<String, String> context = getLocalContext();
        if (context == null) {
            context = new HashMap<>();
            CONTEXT_THREAD_LOCAL.set(context);
        }
        context.put(key, value == null ? StrPool.EMPTY : value.toString());
    }

    public static <T> T get(String key, Class<T> clazz) {
        String value = get(key);
        if (value == null) {
            return null;
        }
        return Convert.convert(clazz, value);
    }

    public static <T> T get(String key, Class<T> clazz, T defaultValue) {
        T value = get(key, clazz);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static Long getUserId() {
        return get(ContextConstants.JWT_KEY_USER_ID, Long.class, 0L);
    }

    public static String getUserName() {
        return get(ContextConstants.JWT_KEY_NAME);
    }

    public static String getAccount() {
        return get(ContextConstants.JWT_KEY_ACCOUNT);
    }
}
