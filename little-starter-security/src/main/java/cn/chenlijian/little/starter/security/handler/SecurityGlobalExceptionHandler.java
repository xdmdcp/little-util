package cn.chenlijian.little.starter.security.handler;

import cn.chenlijian.little.common.api.R;
import cn.chenlijian.little.starter.security.exception.SecurityException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SecurityGlobalExceptionHandler {

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<R> handleJwtException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(R.fail(401, "Invalid or expired token: " + ex.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<R> handlePermissionDenied(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(R.fail(403, "Permission denied: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<R> handleGeneralSecurityException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(R.fail(500, "Internal server error: " + ex.getMessage()));
    }
}
