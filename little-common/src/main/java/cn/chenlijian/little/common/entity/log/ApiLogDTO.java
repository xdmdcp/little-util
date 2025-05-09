package cn.chenlijian.little.common.entity.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 操作日志
 * 用于记录API请求的相关信息，包括请求和响应的细节以及业务和系统信息
 * @author chenlijian xdmdcp@163.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiLogDTO {
    // ========== 基础信息 ==========
    /**
     * 请求的追踪ID，用于跟踪请求在系统中的流转
     */
    private String traceId;

    // ========== 请求信息 ==========
    /**
     * HTTP请求方法，如GET、POST等
     */
    private String httpMethod;
    /**
     * 请求路径，记录请求的URL路径
     */
    private String requestPath;
    /**
     * 请求参数，记录GET请求的查询参数或POST请求的表单参数
     */
    private String requestParams;
    /**
     * 请求体，记录POST、PUT等请求的原始请求体内容
     */
    private String requestBody;
    /**
     * 客户端IP，记录发起请求的客户端IP地址
     */
    private String clientIp;
    /**
     * 用户代理，记录客户端的用户代理信息，用于识别客户端的类型和操作系统
     */
    private String userAgent;
    /**
     * 请求时间，记录请求到达服务器的时间戳
     */
    private Long requestTime;

    // ========== 响应信息 ==========
    /**
     * 响应状态码，记录HTTP响应的状态码
     */
    private int responseStatus;
    /**
     * 响应体，记录HTTP响应的原始内容
     */
    private String responseBody;
    /**
     * 类路径，记录处理请求的类的路径
     */
    private String classPath;
    /**
     * 动作方法，记录处理请求的具体方法名
     */
    private String actionMethod;
    /**
     * 响应时间，记录服务器发送响应的时间戳
     */
    private Long responseTime;

    // ========== 业务信息 ==========
    /**
     * 描述，记录API请求的业务描述或操作说明
     */
    private String description;
    /**
     * 用户名，记录执行API请求的用户名
     */
    private String username;
    /**
     * 用户ID，记录执行API请求的用户ID
     */
    private Long userId;

    // ========== 系统信息 ==========
    /**
     * 处理时间，记录服务器处理请求的耗时
     */
    private Long processingTime;
    /**
     * 错误信息，记录请求处理过程中发生的错误信息
     */
    private String errorMessage;
    /**
     * 错误堆栈，记录请求处理过程中发生的异常的堆栈跟踪信息
     */
    private String errorStack;

    /**
     * 处理时间，记录服务器处理请求的耗时
     */
    public Long getProcessingTime() {
        if (getRequestTime() == null || getResponseTime() == null) {
            return 0L;
        }
        return getResponseTime() - getRequestTime();
    }

}
