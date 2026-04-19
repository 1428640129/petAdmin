package com.pet.framework.sms;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.pet.common.utils.StringUtils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;

/**
 * 阿里云短信发送客户端封装
 *
 * @author Pet
 */
@Component
public class AliyunSmsClient
{
    private static final Logger log = LoggerFactory.getLogger(AliyunSmsClient.class);

    @Value("${aliyun.sms.accessKeyId:}")
    private String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret:}")
    private String accessKeySecret;

    @Value("${aliyun.sms.signName:}")
    private String defaultSignName;

    private volatile Client client;

    /**
     * 懒加载初始化客户端
     */
    private Client getClient() throws Exception
    {
        if (client != null)
        {
            return client;
        }
        synchronized (this)
        {
            if (client != null)
            {
                return client;
            }
            if (StringUtils.isEmpty(accessKeyId) || StringUtils.isEmpty(accessKeySecret))
            {
                log.warn("阿里云短信未配置 accessKeyId/accessKeySecret，跳过实际短信发送");
                return null;
            }
            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret);
            config.endpoint = "dysmsapi.aliyuncs.com";
            client = new Client(config);
            return client;
        }
    }

    /**
     * 发送短信
     *
     * @param phoneNumber   手机号
     * @param signName      短信签名（为空则使用默认签名）
     * @param templateCode  模板编码
     * @param templateParam 模板参数（会转换为 JSON 字符串）
     * @return 是否发送成功（只代表调用成功，不代表用户一定收到）
     */
    public boolean sendSms(String phoneNumber, String signName, String templateCode, Map<String, String> templateParam)
    {
        try
        {
            Client smsClient = getClient();
            if (smsClient == null)
            {
                // 未配置 AK/SK 时，仅记录日志，避免影响业务流程
                log.info("模拟发送短信：phone={}, signName={}, templateCode={}, params={}",
                        phoneNumber, signName, templateCode, templateParam);
                return true;
            }

            if (StringUtils.isEmpty(signName))
            {
                signName = defaultSignName;
            }

            String paramJson = templateParam != null ? JSON.toJSONString(templateParam) : null;

            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumber)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam(paramJson);

            SendSmsResponse response = smsClient.sendSms(request);
            if (response != null
                    && response.getBody() != null
                    && "OK".equalsIgnoreCase(response.getBody().getCode()))
            {
                return true;
            }

            if (response != null && response.getBody() != null)
            {
                log.warn("阿里云短信发送失败，code={}, message={}",
                        response.getBody().getCode(), response.getBody().getMessage());
            }
            else
            {
                log.warn("阿里云短信发送失败，返回为空");
            }
        }
        catch (Exception e)
        {
            log.error("阿里云短信发送异常", e);
        }
        return false;
    }
}








