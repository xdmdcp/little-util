package cn.chenlijian.little.starter.security.utils;

import cn.chenlijian.little.starter.security.exception.ExpiredJwtException;
import cn.chenlijian.little.starter.security.exception.InvalidJwtTokenException;
import cn.chenlijian.little.starter.security.props.LittleSecurityProperties;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * JWT工具类，用于生成、解析和验证JWT Token
 */
public class JwtUtil {

    public static final String SUB = "sub";
    // JWT过期时间（毫秒）
    private final long expiration;
    private final SecretKey secretKey;
    private final JwtParser jwtParser;
    private final String jwtHeader;
    private final String jwtHeaderPrefix;
    private final int jwtHeaderPrefixLength;

    /**
     * 构造函数，初始化JWT密钥和过期时间
     *
     * @param properties 安全配置属性对象
     */
    public JwtUtil(LittleSecurityProperties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("LittleSecurityProperties must not be null.");
        }

        String secret = properties.getJwtSecret();
        // 确保密钥不为空且长度足够，否则抛出异常
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must be configured with at least 32 characters. " +
                            "Please set 'little.security.jwt-secret' in your configuration file, " +
                            "and ensure it is a strong, random string (e.g., base64-encoded)."
            );
        }


        this.expiration = properties.getJwtExpiration();
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // 指定 UTF-8 编码

        this.jwtHeader = properties.getJwtHeader();
        this.jwtHeaderPrefix = properties.getJwtHeaderPrefix();
        if (StrUtil.isBlank(jwtHeaderPrefix)) {
            throw new IllegalArgumentException("JWT header prefix must not be null or empty.");
        }
        this.jwtHeaderPrefixLength = jwtHeaderPrefix.length();

        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }


    /**
     * 根据用户名生成JWT Token
     *
     * @param username 用户名
     * @return 生成的Token字符串
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SUB, username);
        return generateToken(claims);
    }

    /**
     * 根据自定义负载生成JWT Token
     *
     * @param claims 自定义负载数据
     * @return 生成的Token字符串
     */
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setExpiration(getExpiration()).signWith(secretKey).compact();
    }

    /**
     * 解析JWT Token
     *
     * @param token Token字符串
     * @return 解析后的Claims对象
     */
    public Claims parseToken(String token) {
        if (StrUtil.isBlank(token)) {
            throw new InvalidJwtTokenException("Token is empty.");
        }
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            if (e instanceof io.jsonwebtoken.ExpiredJwtException) {
                throw new ExpiredJwtException("Token has expired");
            }
            throw new InvalidJwtTokenException("Invalid JWT token: " + e.getMessage(), e);
        }
    }


    /**
     * 从Token中获取用户名
     *
     * @param token Token字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 判断Token是否过期
     *
     * @param token Token字符串
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = parseToken(token).getExpiration();
            return expiration != null && expiration.before(new Date());
        } catch (IllegalArgumentException e) {
            if (e.getCause() instanceof JwtException) {
                return true;
            }
            throw e;
        }
    }

    /**
     * 获取Token过期时间
     *
     * @return 过期时间
     */
    private Date getExpiration() {
        return new Date(System.currentTimeMillis() + expiration);
    }

    /**
     * 刷新Token
     *
     * @param token 原Token字符串
     * @return 刷新后的Token字符串
     */
    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        if (isTokenExpired(claims)) {
            throw new IllegalArgumentException("Cannot refresh an expired token.");
        }
        return Jwts.builder().setClaims(claims).setExpiration(getExpiration()).signWith(secretKey).compact();
    }

    /**
     * 从Claims中获取用户名
     *
     * @param claims 解析后的Claims对象
     * @return 用户名
     */
    public String getUsernameFromClaims(Claims claims) {
        if (claims == null) {
            throw new IllegalArgumentException("Claims cannot be null.");
        }
        return claims.getSubject();
    }

    /**
     * 判断Token是否过期
     *
     * @param claims 解析后的Claims对象
     * @return 是否过期
     */
    public boolean isTokenExpired(Claims claims) {
        if (claims == null || claims.getExpiration() == null) {
            return true;
        }
        return claims.getExpiration().before(new Date());
    }

    /**
     * 验证Token有效性
     *
     * @param token Token字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        try {
            Claims claims = parseToken(token);
            return !isTokenExpired(claims);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从HTTP请求中提取token字符串
     * 该方法主要用于从请求头中获取token，以便后续进行验证和处理
     *
     * @param request HttpServletRequest对象，用于从请求头中提取信息
     * @return 如果请求头中包含有效的token，则返回token字符串；否则返回null
     */
    public String extractToken(HttpServletRequest request) {
        // 从请求头中获取指定的授权信息
        String header = request.getHeader(jwtHeader);
        // 检查请求头是否不为空且以指定的前缀开始
        if (header != null && header.startsWith(jwtHeaderPrefix)) {
            // 进一步检查请求头在前缀之后是否有内容
            if (header.length() > jwtHeaderPrefixLength) {
                // 如果有内容，则提取并返回token字符串
                return header.substring(jwtHeaderPrefixLength);
            } else {
                // 可选：记录日志，提示 token 为空的情况
                // log.warn("Authorization header is empty after prefix.");
                // 如果请求头在前缀之后没有内容，则返回null
                return null;
            }
        }
        // 如果请求头为空或不以指定前缀开始，则返回null
        return null;
    }

    public Claims extractClaims(String token) {
        return parseToken(token); // 已经是缓存后的版本
    }

}
