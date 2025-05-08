package cn.chenlijian.little.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;

// 校验错误详情
@Data
@AllArgsConstructor
public class FieldErrorVO {
    private String field;
    private String message;
}