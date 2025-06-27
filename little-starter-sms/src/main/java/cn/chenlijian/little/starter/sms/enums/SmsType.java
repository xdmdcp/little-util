package cn.chenlijian.little.starter.sms.enums;

/**
 * 短信服务提供商类型枚举
 */
public enum SmsType {
    ALIYUN,
    TENCENT;

    /**
     * 根据字符串名称获取对应的枚举值（忽略大小写）
     *
     * @param name 枚举名称
     * @return 对应的枚举值
     */
    public static SmsType get(String name) {
        return valueOf(name.toUpperCase());
    }
}
