package cn.chenlijian.little.starter.sms.strategy.aliyun;

import cn.chenlijian.little.starter.sms.enums.SmsType;
import cn.chenlijian.little.starter.sms.props.LittleSmsProperties;
import cn.chenlijian.little.starter.sms.strategy.SmsStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class AliyunSmsStrategy implements SmsStrategy {

    private final LittleSmsProperties.ProviderConfig config;

    public AliyunSmsStrategy(LittleSmsProperties.ProviderConfig config) {
        this.config = config;
    }

    /**
     * 发送短信
     *
     * @param phoneNumber    接收短信的手机号码
     * @param templateId     短信模板ID
     * @param templateParams 短信模板参数
     */
    @Override
    public void sendSms(String phoneNumber, String templateId, Map<String, String> templateParams) {

    }

    /**
     * 获取当前短信服务对应的提供商类型
     */
    @Override
    public SmsType name() {
        return SmsType.ALIYUN;
    }
}
