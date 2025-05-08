package cn.chenlijian.little.starter.validation.advice;

import cn.chenlijian.little.common.api.ResultCode;
import cn.chenlijian.little.common.api.FieldErrorVO;
import cn.chenlijian.little.common.api.R;
import cn.chenlijian.little.starter.validation.exception.ValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionAdvice {

    // 处理 Spring @Valid 抛出的参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<List<FieldErrorVO>> handleValidationException(
            MethodArgumentNotValidException ex) {
        List<FieldErrorVO> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorVO(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return R.fail(ResultCode.PARAM_ERROR, "参数校验失败", errors);
    }

    // 处理自定义校验异常（如手动抛出的校验错误）
    @ExceptionHandler(ValidationException.class)
    public R<?> handleCustomValidationException(ValidationException ex) {
        return R.fail(ex.getCode(), ex.getMessage());
    }
}