package cn.chenlijian.little.starter.log.sampling;

import jakarta.servlet.http.HttpServletRequest;

public interface SamplingStrategy {
    /**
     * 判断是否应该记录该请求的日志
     *
     * @param request 当前 HTTP 请求对象
     * @param hasError 是否发生异常
     * @return true 表示记录日志，false 表示跳过
     */
    boolean shouldSample(HttpServletRequest request, boolean hasError);
}
