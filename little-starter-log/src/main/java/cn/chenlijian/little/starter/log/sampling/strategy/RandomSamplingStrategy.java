package cn.chenlijian.little.starter.log.sampling.strategy;

import cn.chenlijian.little.starter.log.sampling.SamplingStrategy;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 随机采样策略类，用于决定是否对请求进行采样
 * 该类实现了SamplingStrategy接口，通过随机方式来决定请求是否被采样
 */
public class RandomSamplingStrategy implements SamplingStrategy {

    // 采样率，默认为1.0，表示100%采样
    private volatile double sampleRate = 1.0;

    /**
     * 设置采样率
     * 该方法确保传入的采样率在0.0到1.0之间，如果超出范围，则自动调整到最近的边界值
     *
     * @param sampleRate 采样率，范围在0.0到1.0之间，表示不采样到完全采样的概率
     */
    public void setSampleRate(double sampleRate) {
        // 确保采样率在0.0到1.0之间
        this.sampleRate = Math.max(0.0, Math.min(1.0, sampleRate));
    }

    /**
     * 判断是否应该对请求进行采样
     * 该方法通过比较随机数和采样率来决定是否应该采样当前请求
     *
     * @param request HTTP请求对象，未使用
     * @param hasError 请求是否包含错误，未使用
     * @return 如果随机数小于采样率，则返回true，表示应该采样；否则返回false，表示不应该采样
     */
    @Override
    public boolean shouldSample(HttpServletRequest request, boolean hasError) {
        // 通过比较随机数和采样率来决定是否采样
        return Math.random() < sampleRate;
    }
}
