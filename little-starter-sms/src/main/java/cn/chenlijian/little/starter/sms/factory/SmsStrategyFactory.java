package cn.chenlijian.little.starter.sms.factory;

import cn.chenlijian.little.starter.sms.enums.SmsType;
import cn.chenlijian.little.starter.sms.strategy.SmsStrategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SmsStrategyFactory {

    private final Map<SmsType, SmsStrategy> strategyMap;

    public SmsStrategyFactory(List<SmsStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(SmsStrategy::name, Function.identity()));
    }

    public SmsStrategy get(SmsType type) {
        SmsStrategy service = strategyMap.get(type);
        if (service == null) {
            throw new IllegalArgumentException("No SMS service found for provider: " + type);
        }
        return service;
    }
}
