package com.bitisan.user.controller;


import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.sms.EmailProvider;
import com.bitisan.user.entity.Member;
import com.bitisan.user.service.MemberService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.GeneratorUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.bitisan.constant.SysConstant.EMAIL_WITHDRAW_MONEY_CODE_PREFIX;
import static com.bitisan.util.MessageResult.error;
import static com.bitisan.util.MessageResult.success;


/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @date 2021年01月08日
 */
@Slf4j
@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MemberService memberService;
    @Resource
    private LocaleMessageSourceService localeMessageSourceService;

    @Autowired
    private EmailProvider emailProvider;

    /**
     * 重置交易密码验证码
     *
     * @param user
     * @return
     */
    @PermissionOperation
    @RequestMapping(value = "/transaction/code", method = RequestMethod.POST)
    public MessageResult sendResetTransactionCode(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member member = memberService.getById(user.getId());
        String email = member.getEmail();
        Assert.hasText(member.getEmail(), localeMessageSourceService.getMessage("NOT_BIND_EMAIL"));
        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = SysConstant.EMAIL_TRANSACTION_CODE_PREFIX + email;
        if (valueOperations.get(key) != null) {
            return error(localeMessageSourceService.getMessage("EMAIL_ALREADY_SEND"));
        }
        try {
            emailProvider.sendEmail(email, randomCode,null,"bindCodeEmail.ftl");
            valueOperations.set(key, randomCode, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }

    /**
     * 提币验证码
     *
     * @return
     */
    @PermissionOperation
    @RequestMapping(value = "/withdraw/code", method = RequestMethod.POST)
    public MessageResult withdrawCode(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member member = memberService.getById(user.getId());
        String email = member.getEmail();
        Assert.hasText(member.getEmail(), localeMessageSourceService.getMessage("NOT_BIND_EMAIL"));
        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = EMAIL_WITHDRAW_MONEY_CODE_PREFIX + email;
        if (valueOperations.get(key) != null) {
            return error(localeMessageSourceService.getMessage("EMAIL_ALREADY_SEND"));
        }
        try {
            emailProvider.sendEmail(email, randomCode,null,"bindCodeEmail.ftl");
            valueOperations.set(key, randomCode, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }

    /**
     * 更改登录密码验证码
     *
     * @return
     */
    @PermissionOperation
    @RequestMapping(value = "/update/password/code", method = RequestMethod.POST)
    public MessageResult updatePasswordCode(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member member = memberService.getById(user.getId());
        String email = member.getEmail();
        Assert.hasText(member.getEmail(), localeMessageSourceService.getMessage("NOT_BIND_EMAIL"));
        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = SysConstant.EMAIL_UP_PWD_CODE_PREFIX + email;
        if (valueOperations.get(key) != null) {
            return error(localeMessageSourceService.getMessage("EMAIL_ALREADY_SEND"));
        }
        try {
            emailProvider.sendEmail(email, randomCode,null,"bindCodeEmail.ftl");
            valueOperations.set(key, randomCode, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }

}
