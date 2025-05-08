package cn.chenlijian.little.common.api;

import lombok.Data;

import java.util.List;

/**
 * 统一响应格式类，用于封装API接口的返回数据
 * <p>
 * 本类通过Lombok的@Data注解简化了getter和setter的编写，旨在减少 boilerplate code（模板代码）
 * </p>
 *
 * @param <T> 泛型参数，用于存放响应数据的实际类型
 */
@Data
public class R<T> {
    /**
     * 响应状态码，用于标识响应的成功或失败
     */
    private final int code;

    /**
     * 响应消息，用于提供更详细的响应描述
     */
    private final String message;

    /**
     * 响应数据，存放实际返回给客户端的数据
     */
    private final T data;

    /**
     * 私有构造器，强制使用静态工厂方法创建实例
     */
    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 私有构造器，强制使用静态工厂方法创建实例
     */
    private R(ResultCode code, String message, T data) {
        this.code = code.getCode();
        this.message = message;
        this.data = data;
    }

    /**
     * 静态工厂方法，用于创建表示成功响应的R对象
     *
     * @return 成功响应的R对象，包含相应的成功状态码和提示消息
     */
    public static <T> R<T> success() {
        return new R<>(ResultCode.SUCCESS, "操作成功", null);
    }

    /**
     * 静态工厂方法，用于创建表示成功响应的R对象并携带数据
     *
     * @param data 响应数据
     * @return 成功响应的R对象，包含相应的成功状态码和提示消息
     */
    public static <T> R<T> success(T data) {
        return new R<>(ResultCode.SUCCESS, "操作成功", data);
    }

    /**
     * 静态工厂方法，用于创建表示失败响应的R对象
     *
     * @return 失败响应的R对象，包含相应的失败状态码和提示消息
     */
    public static <T> R<T> fail() {
        return new R<>(ResultCode.FAILURE, "操作失败", null);
    }

    public static <T> R<T> fail(String message) {
        return new R<>(ResultCode.FAILURE, message, null);
    }

    /**
     * 静态工厂方法，用于创建自定义失败响应的R对象
     *
     * @param code 自定义失败状态码
     * @param message 自定义失败消息
     * @return 失败响应的R对象
     */
    public static <T> R<T> fail(ResultCode code, String message) {
        return new R<>(code, message, null);
    }

    /**
     * 静态工厂方法，用于创建自定义失败响应的R对象
     *
     * @param code 自定义失败状态码
     * @param message 自定义失败消息
     * @return 失败响应的R对象
     */
    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }


    public static <T> R<T> fail(ResultCode code, String message, List<FieldErrorVO> errors) {
        return new R<>(code, errors.toString(), null);
    }


}
