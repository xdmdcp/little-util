package cn.chenlijian.little.starter.sms.strategy;// cn.chenlijian.little.sms.SmsService.java

import cn.chenlijian.little.starter.sms.enums.SmsType;

import java.util.Map;

public interface SmsStrategy {
    /**
     * 发送短信
     *
     * @param phoneNumber     接收短信的手机号码
     * @param templateId      短信模板ID
     * @param templateParams  短信模板参数
     */
    void sendSms(String phoneNumber, String templateId, Map<String, String> templateParams);

    /**
     * 获取当前短信服务对应的提供商类型
     */
    SmsType name();
}
