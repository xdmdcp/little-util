package cn.chenlijian.little.starter.security.exception;

public class ExpiredJwtException extends InvalidJwtTokenException {
    public ExpiredJwtException(String message) {
        super(message);
    }
}