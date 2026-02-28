package com.pet.framework.web.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pet.common.constant.CacheConstants;
import com.pet.common.core.redis.RedisCache;
import com.pet.common.exception.ServiceException;
import com.pet.common.utils.StringUtils;
import com.pet.framework.sms.SpugSmsClient;

/**
 * 短信验证码服务
 * 
 * @author Pet
 */
@Component
public class SmsCodeService
{
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SpugSmsClient spugSmsClient;

    /**
     * 短信验证码有效期（分钟）
     */
    private static final Integer SMS_CODE_EXPIRATION = 10;

    /**
     * 发送短信验证码
     * 
     * @param phone 手机号
     * @return 验证码
     */
    public String sendSmsCode(String phone)
    {
        if (StringUtils.isEmpty(phone))
        {
            throw new ServiceException("手机号不能为空");
        }

        // 检查手机号格式（简单验证，实际应该更严格）
        if (!phone.matches("^1[3-9]\\d{9}$"))
        {
            throw new ServiceException("手机号格式不正确");
        }

        // 检查是否在60秒内已发送过验证码（防刷）
        String rateLimitKey = CacheConstants.SMS_CODE_KEY + "rate_limit:" + phone;
        if (redisCache.hasKey(rateLimitKey))
        {
            throw new ServiceException("验证码发送过于频繁，请稍后再试");
        }

        // 生成6位数字验证码
        String code = generateCode();

        // 存储验证码到Redis，有效期5分钟
        String codeKey = CacheConstants.SMS_CODE_KEY + phone;
        redisCache.setCacheObject(codeKey, code, SMS_CODE_EXPIRATION, TimeUnit.MINUTES);

        // 设置60秒的发送频率限制
        redisCache.setCacheObject(rateLimitKey, "1", 60, TimeUnit.SECONDS);

        // 通过 Spug 短信服务发送验证码
        // Spug 后台模板格式：您的验证码是${code}，十分钟内有效，如非本人操作请忽略。
        // 只需要传递验证码，Spug 会自动用模板格式化
        boolean success = spugSmsClient.sendVerifyCodeSms(phone, code);

        if (!success)
        {
            // 如果短信发送失败，可以选择抛出异常或仅记录日志
            throw new ServiceException("验证码发送失败，请稍后重试");
        }

        return code;
    }

    /**
     * 验证短信验证码
     * 
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证通过
     */
    public boolean validateSmsCode(String phone, String code)
    {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code))
        {
            return false;
        }

        String codeKey = CacheConstants.SMS_CODE_KEY + phone;
        String cachedCode = redisCache.getCacheObject(codeKey);

        if (cachedCode == null)
        {
            return false;
        }

        // 验证码验证成功后删除
        if (code.equals(cachedCode))
        {
            redisCache.deleteObject(codeKey);
            return true;
        }

        return false;
    }

    /**
     * 生成6位数字验证码
     * 
     * @return 验证码
     */
    private String generateCode()
    {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}










