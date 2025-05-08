package cn.chenlijian.little.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 定义返回结果代码的枚举类，用于统一错误代码和消息
 */
@AllArgsConstructor
@Getter
public enum ResultCode {
    SUCCESS(0, "操作成功"),
    FAILURE(400, "业务异常"),
    // 参数错误，用于表示请求参数有误
    PARAM_ERROR(400, "参数错误"),
    // 手机号格式错误，用于表示输入的手机号格式不正确
    PHONE_FORMAT_ERROR(40001, "手机号格式错误");

    // 错误代码
    private final Integer code;
    // 错误消息
    private final String message;
}
