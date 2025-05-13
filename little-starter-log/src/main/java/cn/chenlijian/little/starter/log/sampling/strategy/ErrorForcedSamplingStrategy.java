package cn.chenlijian.little.starter.log.sampling.strategy;

import cn.chenlijian.little.starter.log.props.SamplingProperties;
import cn.chenlijian.little.starter.log.sampling.SamplingStrategy;
import jakarta.servlet.http.HttpServletRequest;

public class ErrorForcedSamplingStrategy implements SamplingStrategy {

    private final SamplingStrategy delegate;
    private final boolean includeErrors;

    public ErrorForcedSamplingStrategy(SamplingStrategy delegate,
                                       SamplingProperties properties) {
        this.delegate = delegate;
        this.includeErrors = properties.getIncludeError();
    }

    @Override
    public boolean shouldSample(HttpServletRequest request, boolean hasError) {
        if (includeErrors && hasError) {
            return true; // 强制记录错误请求
        }
        return delegate.shouldSample(request, hasError);
    }
}
