package com.pet.framework.sms;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Spug 短信客户端，通过 Spug 暴露的 HTTP 接口发送短信
 *
 * @author Pet
 */
@Component
public class SpugSmsClient
{
    private static final Logger log = LoggerFactory.getLogger(SpugSmsClient.class);

    @Value("${spug.sms.enabled:true}")
    private boolean enabled;

    @Value("${spug.sms.url:}")
    private String url;

    @Value("${spug.sms.appointment-url:}")
    private String appointmentUrl;

    @Value("${spug.sms.service-completed-url:}")
    private String serviceCompletedUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * 初始化后检查配置
     */
    @jakarta.annotation.PostConstruct
    public void init()
    {
        log.info("========== Spug 短信配置检查 ==========");
        log.info("短信功能启用状态: {}", enabled);
        log.info("验证码短信URL: {}", url != null && !url.isEmpty() ? url.replaceAll("(https://push\\.spug\\.cc/[^/]+/)[^?]+", "$1***") : "未配置");
        log.info("预约确认短信URL: {}", appointmentUrl != null && !appointmentUrl.isEmpty() ? appointmentUrl.replaceAll("(https://push\\.spug\\.cc/[^/]+/)[^?]+", "$1***") : "未配置");
        log.info("服务完成短信URL: {}", serviceCompletedUrl != null && !serviceCompletedUrl.isEmpty() ? serviceCompletedUrl.replaceAll("(https://push\\.spug\\.cc/[^/]+/)[^?]+", "$1***") : "未配置");
        
        if (!enabled)
        {
            log.warn("Spug 短信功能已禁用，所有短信将模拟发送");
        }
        else
        {
            if (url == null || url.isEmpty())
            {
                log.warn("警告：验证码短信URL未配置，验证码短信将无法发送");
            }
            if (appointmentUrl == null || appointmentUrl.isEmpty())
            {
                log.warn("警告：预约确认短信URL未配置，预约确认短信将无法发送");
            }
            if (serviceCompletedUrl == null || serviceCompletedUrl.isEmpty())
            {
                log.warn("警告：服务完成短信URL未配置，服务完成短信将无法发送");
            }
        }
        log.info("=====================================");
    }

    /**
     * 发送验证码短信
     *
     * Spug 推送链接格式：https://push.spug.cc/send/xxx?code=验证码&targets=手机号
     * 验证码通过 URL 参数 code 传递，手机号通过 targets 参数传递
     * 短信内容由 Spug 后台模板自动生成（格式：您的验证码是${code}，十分钟内有效，如非本人操作请忽略。）
     *
     * @param phone 手机号
     * @param code  验证码（4-6位数字）
     * @return 是否调用成功
     */
    public boolean sendVerifyCodeSms(String phone, String code)
    {
        try
        {
            if (!enabled)
            {
                log.info("Spug 短信已关闭(enabled=false)，模拟发送验证码 phone={}, code={}", phone, code);
                return true;
            }

            if (url == null || url.isEmpty())
            {
                log.warn("Spug 短信 url 未配置，跳过实际发送");
                return false;
            }

            // 构建 URL，添加 code 和 targets 参数
            String finalUrl = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("code", code)
                    .queryParam("targets", phone)
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            log.debug("Spug 验证码短信请求 URL: {}", finalUrl.replace(phone, "***").replace(code, "****")); // 日志中隐藏手机号和验证码

            // 使用 GET 请求
            ResponseEntity<String> response = restTemplate.getForEntity(finalUrl, String.class);

            String responseBody = response.getBody();
            boolean httpSuccess = response.getStatusCode().is2xxSuccessful();
            boolean actualSuccess = interpretSpugPushResult(responseBody, httpSuccess);

            if (actualSuccess)
            {
                log.info("Spug 验证码短信发送成功，phone={}, code={}, response={}", 
                    phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"),
                    code,
                    responseBody != null ? responseBody.substring(0, Math.min(100, responseBody.length())) : "null");
            }
            else
            {
                log.warn("Spug 验证码短信发送失败，phone={}, code={}, status={}, response={}", 
                    phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"),
                    code,
                    response.getStatusCode(), 
                    responseBody);
            }
            return actualSuccess;
        }
        catch (Exception e)
        {
            log.error("Spug 验证码短信发送异常，phone={}, code={}", 
                phone != null ? phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2") : "null",
                code, e);
            return false;
        }
    }

    /**
     * 发送短信（通用格式，用于非验证码短信）
     *
     * Spug 推送链接格式：https://push.spug.cc/send/xxx?targets=手机号&content=内容
     * 手机号通过 URL 参数 targets 传递，内容通过 content 参数传递
     *
     * @param phone   手机号
     * @param content 短信内容
     * @return 是否调用成功（仅根据 HTTP 状态简单判断）
     */
    public boolean sendSms(String phone, String content)
    {
        try
        {
            if (!enabled)
            {
                log.info("Spug 短信已关闭(enabled=false)，模拟发送 phone={}, content={}", phone, content);
                return true;
            }

            if (url == null || url.isEmpty())
            {
                log.warn("Spug 短信 url 未配置，跳过实际发送");
                return false;
            }

            // 构建 URL，添加 targets 和 content 参数
            String finalUrl = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("targets", phone)
                    .queryParam("content", content)
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            log.debug("Spug 短信请求 URL: {}", finalUrl.replace(phone, "***")); // 日志中隐藏手机号

            // 使用 GET 请求（Spug 推送链接通常支持 GET）
            ResponseEntity<String> response = restTemplate.getForEntity(finalUrl, String.class);

            String responseBody = response.getBody();
            boolean httpSuccess = response.getStatusCode().is2xxSuccessful();
            boolean actualSuccess = interpretSpugPushResult(responseBody, httpSuccess);

            if (actualSuccess)
            {
                log.info("Spug 短信发送成功，phone={}, response={}", 
                    phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"), 
                    responseBody != null ? responseBody.substring(0, Math.min(100, responseBody.length())) : "null");
            }
            else
            {
                log.warn("Spug 短信发送失败，status={}, response={}", response.getStatusCode(), responseBody);
            }
            return actualSuccess;
        }
        catch (Exception e)
        {
            log.error("Spug 短信发送异常，phone={}", phone != null ? phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2") : "null", e);
            return false;
        }
    }

    /**
     * 发送预约成功通知短信
     *
     * Spug 推送链接格式：https://push.spug.cc/sms/xxx?to=手机号
     * 手机号通过 URL 参数 to 传递
     *
     * @param phone 手机号
     * @return 是否调用成功
     */
    public boolean sendAppointmentSms(String phone)
    {
        try
        {
            if (!enabled)
            {
                log.info("Spug 短信已关闭(enabled=false)，模拟发送预约通知 phone={}", phone);
                return true;
            }

            if (appointmentUrl == null || appointmentUrl.isEmpty())
            {
                log.error("Spug 预约通知短信 url 未配置，跳过实际发送。请检查 application.yml 中的 spug.sms.appointment-url 配置");
                log.error("当前配置值 - enabled: {}, appointmentUrl: {}", enabled, appointmentUrl);
                return false;
            }
            
            // 构建 URL，只添加 to 参数
            String finalUrl = UriComponentsBuilder.fromHttpUrl(appointmentUrl)
                    .queryParam("to", phone)
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            log.info("Spug 预约通知短信请求 URL: {}", finalUrl.replace(phone, "***")); // 日志中隐藏手机号
            log.debug("Spug 预约通知短信完整 URL: {}", finalUrl); // 调试时显示完整URL
            log.debug("当前配置 - enabled: {}, appointmentUrl: {}", enabled, 
                appointmentUrl != null ? appointmentUrl.replaceAll("(https://push\\.spug\\.cc/[^/]+/)[^?]+", "$1***") : "null");

            // 使用 GET 请求
            ResponseEntity<String> response = restTemplate.getForEntity(finalUrl, String.class);

            String responseBody = response.getBody();
            boolean httpSuccess = response.getStatusCode().is2xxSuccessful();
            boolean actualSuccess = interpretSpugPushResult(responseBody, httpSuccess);

            if (actualSuccess)
            {
                log.info("Spug 预约通知短信发送成功，phone={}, response={}", 
                    phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"),
                    responseBody != null ? responseBody.substring(0, Math.min(100, responseBody.length())) : "null");
            }
            else
            {
                log.warn("Spug 预约通知短信发送失败，phone={}, status={}, response={}", 
                    phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"),
                    response.getStatusCode(), 
                    responseBody);
            }
            return actualSuccess;
        }
        catch (Exception e)
        {
            log.error("Spug 预约通知短信发送异常，phone={}", 
                phone != null ? phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2") : "null", e);
            return false;
        }
    }

    /**
     * 发送服务完成通知短信
     *
     * Spug 推送链接格式：https://push.spug.cc/sms/xxx?to=手机号
     * 手机号通过 URL 参数 to 传递
     *
     * @param phone 手机号
     * @return 是否调用成功
     */
    public boolean sendServiceCompletedSms(String phone)
    {
        try
        {
            if (!enabled)
            {
                log.info("Spug 短信已关闭(enabled=false)，模拟发送服务完成通知 phone={}", phone);
                return true;
            }

            if (serviceCompletedUrl == null || serviceCompletedUrl.isEmpty())
            {
                log.error("Spug 服务完成通知短信 url 未配置，跳过实际发送。请检查 application.yml 中的 spug.sms.service-completed-url 配置");
                log.error("当前配置值 - enabled: {}, serviceCompletedUrl: {}", enabled, serviceCompletedUrl);
                return false;
            }

            // 构建 URL，添加 to 参数
            String finalUrl = UriComponentsBuilder.fromHttpUrl(serviceCompletedUrl)
                    .queryParam("to", phone)
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            log.debug("Spug 服务完成通知短信请求 URL: {}", finalUrl.replace(phone, "***")); // 日志中隐藏手机号

            // 使用 GET 请求
            ResponseEntity<String> response = restTemplate.getForEntity(finalUrl, String.class);

            String responseBody = response.getBody();
            boolean httpSuccess = response.getStatusCode().is2xxSuccessful();
            boolean actualSuccess = interpretSpugPushResult(responseBody, httpSuccess);

            if (actualSuccess)
            {
                log.info("Spug 服务完成通知短信发送成功，phone={}, response={}", 
                    phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"),
                    responseBody != null ? responseBody.substring(0, Math.min(100, responseBody.length())) : "null");
            }
            else
            {
                log.warn("Spug 服务完成通知短信发送失败，phone={}, status={}, response={}", 
                    phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"),
                    response.getStatusCode(), 
                    responseBody);
            }
            return actualSuccess;
        }
        catch (Exception e)
        {
            log.error("Spug 服务完成通知短信发送异常，phone={}", 
                phone != null ? phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2") : "null", e);
            return false;
        }
    }

    /**
     * 解析 Spug 推送 HTTP 响应：仅当 JSON 含 {@code "code":200}（或带空格）且非明确失败文案时视为成功。
     * 避免仅因 HTTP 200 或缺少 body 误判为「已发短信」。
     */
    private boolean interpretSpugPushResult(String responseBody, boolean httpSuccess)
    {
        if (!httpSuccess)
        {
            return false;
        }
        if (responseBody == null || responseBody.trim().isEmpty())
        {
            log.warn("Spug 调用 HTTP 成功但响应体为空，不计入发送成功");
            return false;
        }
        String b = responseBody;
        if (b.contains("未匹配到推送对象") || b.contains("模板编码错误")
            || b.contains("需要订阅会员") || b.contains("\"code\":204") || b.contains("\"code\": 204")
            || b.contains("不能为空") || b.contains("变量"))
        {
            return false;
        }
        if (!b.contains("\"code\":"))
        {
            log.warn("Spug 响应中无 JSON code 字段，不计入发送成功。响应前200字：{}", b.length() > 200 ? b.substring(0, 200) : b);
            return false;
        }
        boolean is200 = b.contains("\"code\":200") || b.contains("\"code\": 200");
        if (!is200)
        {
            log.warn("Spug 响应 code 非 200。响应前200字：{}", b.length() > 200 ? b.substring(0, 200) : b);
            return false;
        }
        return true;
    }
}


