package cn.chenlijian.little.common.entity.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author chenlijian xdmdcp@163.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiLogDTO {

    /**
     * 操作IP
     */
    private String requestIp;

    /**
     * 操作人
     */
    private String userName;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 类路径
     */
    private String classPath;

    /**
     * 请求类型
     */
    private String actionMethod;

    /**
     * 请求地址
     */
    private String requestUri;

    /**
     * 请求类型
     * #HttpMethod{GET:GET请求;POST:POST请求;PUT:PUT请求;DELETE:DELETE请求;PATCH:PATCH请求;TRACE:TRACE请求;HEAD:HEAD请求;OPTIONS:OPTIONS请求;}
     */
    private String httpMethod;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 返回值
     */
    private String result;

    /**
     * 异常描述
     */
    private String exDetail;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 消耗时间
     */
    private Long consumingTime;

    /**
     * 浏览器
     */
    private String ua;

    private Long createdBy;
}
