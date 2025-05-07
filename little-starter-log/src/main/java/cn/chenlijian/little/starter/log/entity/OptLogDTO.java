package cn.chenlijian.little.starter.log.entity;

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
public class OptLogDTO {

    private String traceId;
    private String businessId;
    private String module;
    private String type;
    private String description;
    private String operator;
    private String request;
    private String response;
    private Long costTime;
    private Integer status;
    private String errorMsg;
    private LocalDateTime operateTime;
    private String clientIp;
}
