package cn.chenlijian.little.starter.security.filter;

import cn.chenlijian.little.starter.security.exception.ExpiredJwtException;
import cn.chenlijian.little.starter.security.exception.InvalidJwtTokenException;
import cn.chenlijian.little.starter.security.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

/**
 * JWT身份验证过滤器，用于解析请求中的JWT令牌并设置相应的身份验证信息
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    // JWT工具类，用于处理JWT令牌的解析和验证
    private final JwtUtil jwtUtil;

    /**
     * 执行过滤器的主要方法
     *
     * @param request   HTTP请求
     * @param response  HTTP响应
     * @param filterChain 过滤器链，用于将控制权传递给下一个过滤器或目标资源
     * @throws ServletException 如果过滤过程中发生Servlet异常
     * @throws IOException 如果过滤过程中发生IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = jwtUtil.extractToken(request);
            if (token == null) {
                log.info("No JWT token found in request headers.");
                filterChain.doFilter(request, response);
                return;
            }

            Claims claims = jwtUtil.parseToken(token);

            if (jwtUtil.isTokenExpired(claims)) {
                throw new ExpiredJwtException("JWT token expired");
            }

            String username = jwtUtil.getUsernameFromClaims(claims);
            log.debug("Extracted username from JWT: {}", username);

            if (username == null) {
                throw new InvalidJwtTokenException("Invalid JWT token");
            }

            UserDetails userDetail = this.userDetailsService.loadUserByUsername(username);
            Collection<? extends GrantedAuthority> authorities = userDetail.getAuthorities();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

            log.trace("Setting authentication for user: {}", username);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (InvalidJwtTokenException e) {
            log.warn("JWT validation failed: {}. Stack trace: ", e.getMessage(), e);
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        } catch (Exception ex) {
            log.error("Unexpected error during JWT processing: ", ex);
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication error");
            return;
        }

        filterChain.doFilter(request, response);
    }


}
