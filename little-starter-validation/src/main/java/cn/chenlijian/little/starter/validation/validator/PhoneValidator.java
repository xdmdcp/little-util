package cn.chenlijian.little.starter.validation.validator;

import cn.chenlijian.little.starter.validation.annotation.Phone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<Phone, String> {
    private static final Pattern PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // 允许空值，配合 @NotNull 使用
        return PATTERN.matcher(value).matches();
    }
}