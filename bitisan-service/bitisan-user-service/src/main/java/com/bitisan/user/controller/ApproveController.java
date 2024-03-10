package com.bitisan.user.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.AuditStatus;
import com.bitisan.constant.RealNameStatus;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.*;
import com.bitisan.user.service.CountryService;
import com.bitisan.user.service.MemberApplicationService;
import com.bitisan.user.service.MemberService;
import com.bitisan.user.service.MemberWalletService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static com.bitisan.constant.BooleanEnum.IS_FALSE;
import static com.bitisan.constant.BooleanEnum.IS_TRUE;
import static org.springframework.util.Assert.*;


/**
 * 用户中心认证
 *
 * @author Bitisan  E-mail:xunibidev@gmail.com
 * @date 2020年01月09日
 */
@Api(tags = "用户中心认证")
@RestController
@RequestMapping("/approve")
@Slf4j
public class ApproveController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ApproveController.class);

    @Autowired
    private MemberService memberService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LocaleMessageSourceService msService;
    @Autowired
    private MemberApplicationService memberApplicationService;
    @Autowired
    private CountryService countryService;

    @Autowired
    private MemberWalletService memberWalletService;

    /**
     * 设置或更改用户头像
     *
     * @param
     * @param url
     * @return
     */
    @ApiOperation(value = "设置或更改用户头像")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "url", value = "url"),
    })
    @PermissionOperation
    @RequestMapping("/change/avatar")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult update(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, String url) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member member = memberService.getById(user.getId());
        member.setAvatar(url);
        memberService.updateById(member);
        return MessageResult.success();
    }

    /**
     * 安全设置
     *
     * @return
     */
    @ApiOperation(value = "安全设置")
    @PermissionOperation
    @RequestMapping("/security/setting")
    public MessageResult securitySetting(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member member = memberService.getById(user.getId());
        String idNumber = member.getIdNumber();
        MemberSecurity memberSecurity = MemberSecurity.builder().username(member.getUsername())
                .createTime(member.getRegistrationTime())
                .id(member.getId())
                .emailVerified(StringUtils.isEmpty(member.getEmail()) ? IS_FALSE.getCode() : IS_TRUE.getCode())
                .email(member.getEmail())
                .mobilePhone(member.getMobilePhone())
                .fundsVerified(StringUtils.isEmpty(member.getJyPassword()) ? IS_FALSE.getCode() : IS_TRUE.getCode())
                .loginVerified(IS_TRUE.getCode())
                .phoneVerified(StringUtils.isEmpty(member.getMobilePhone()) ? IS_FALSE.getCode() : IS_TRUE.getCode())
                .realName(member.getRealName())
                .idCard(StringUtils.isEmpty(idNumber) ? null : idNumber.substring(0, 2) + "**********" + idNumber.substring(idNumber.length() - 2))
                .realVerified(StringUtils.isEmpty(member.getRealName()) ? IS_FALSE.getCode() : IS_TRUE.getCode())
                .realAuditing(member.getRealNameStatus().equals(RealNameStatus.AUDITING) ? IS_TRUE.getCode() : IS_FALSE.getCode())
                .avatar(member.getAvatar())
                .googleStatus(member.getGoogleState())
                .build();
        if (memberSecurity.getRealAuditing().equals(IS_FALSE.getCode()) && memberSecurity.getRealVerified().equals(IS_FALSE.getCode())) {
            List<MemberApplication> memberApplication = memberApplicationService.findLatelyReject(member.getId());
            memberSecurity.setRealNameRejectReason(memberApplication == null||memberApplication.size()==0 ? null : memberApplication.get(0).getRejectReason());
        }
        MessageResult result = MessageResult.success("success");
        result.setData(memberSecurity);
        return result;
    }

    /**
     * 设置资金密码
     *
     * @param jyPassword
     * @return
     */
    @ApiOperation(value = "设置资金密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jyPassword", value = "交易密码"),
    })
    @PermissionOperation
    @RequestMapping("/transaction/password")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult approveTransaction(String jyPassword, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        hasText(jyPassword, msService.getMessage("MISSING_JY_PASSWORD"));
        isTrue(jyPassword.length() >= 6 && jyPassword.length() <= 20, msService.getMessage("JY_PASSWORD_LENGTH_ILLEGAL"));
        Member member = memberService.getById(user.getId());
        Assert.isNull(member.getJyPassword(), msService.getMessage("REPEAT_SETTING"));
        //生成密码
        String jyPass = MD5.md5(jyPassword + member.getSalt()).toLowerCase();
        member.setJyPassword(jyPass);
        memberService.updateById(member);
        return MessageResult.success(msService.getMessage("SETTING_JY_PASSWORD"));
    }

    /**
     * 修改资金密码
     *
     * @param oldPassword
     * @param newPassword
     * @param authMember
     * @return
     */
    @ApiOperation(value = "修改资金密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPassword", value = "旧密码"),
            @ApiImplicitParam(name = "newPassword", value = "新密码"),
    })
    @PermissionOperation
    @RequestMapping("/update/transaction/password")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult updateTransaction(String oldPassword, String newPassword, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        hasText(oldPassword, msService.getMessage("MISSING_OLD_JY_PASSWORD"));
        hasText(newPassword, msService.getMessage("MISSING_NEW_JY_PASSWORD"));
        isTrue(newPassword.length() >= 6 && newPassword.length() <= 20, msService.getMessage("JY_PASSWORD_LENGTH_ILLEGAL"));
        Member member = memberService.getById(user.getId());
        isTrue(MD5.md5(oldPassword + member.getSalt()).toLowerCase().equals(member.getJyPassword()), msService.getMessage("ERROR_JYPASSWORD"));
        member.setJyPassword(MD5.md5(newPassword + member.getSalt()).toLowerCase());
        memberService.updateById(member);
        return MessageResult.success(msService.getMessage("SETTING_JY_PASSWORD"));
    }

    /**
     * 重置资金密码
     *
     * @param newPassword
     * @param code
     * @param authMember
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "重置资金密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newPassword", value = "新密码"),
            @ApiImplicitParam(name = "code", value = "验证码"),
    })
    @PermissionOperation
    @RequestMapping("/reset/transaction/password")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult resetTransaction(String newPassword, String code, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        hasText(newPassword, msService.getMessage("MISSING_NEW_JY_PASSWORD"));
        isTrue(newPassword.length() >= 6 && newPassword.length() <= 20, msService.getMessage("JY_PASSWORD_LENGTH_ILLEGAL"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object cache = valueOperations.get(SysConstant.PHONE_RESET_TRANS_CODE_PREFIX + user.getMobilePhone());
        notNull(cache, msService.getMessage("NO_GET_VERIFICATION_CODE"));
        hasText(code, msService.getMessage("MISSING_VERIFICATION_CODE"));
        if (!code.equals(cache.toString())) {
            return MessageResult.error(msService.getMessage("VERIFICATION_CODE_INCORRECT"));
        } else {
            valueOperations.getOperations().delete(SysConstant.PHONE_RESET_TRANS_CODE_PREFIX + user.getMobilePhone());
        }
        Member member = memberService.getById(user.getId());
        member.setJyPassword(MD5.md5(newPassword + member.getSalt()).toLowerCase());
        memberService.updateById(member);
        return MessageResult.success(msService.getMessage("SETTING_JY_PASSWORD"));
    }

    /**
     * 绑定手机号
     *
     * @param password
     * @param phone
     * @param code
     * @param authMember
     * @return
     */
    @ApiOperation(value = "绑定手机号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "password", value = "密码"),
            @ApiImplicitParam(name = "phone", value = "手机号"),
            @ApiImplicitParam(name = "code", value = "验证码"),
    })
    @PermissionOperation
    @RequestMapping("/bind/phone")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult bindPhone(HttpServletRequest request, String password, String phone, String code, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        hasText(password, msService.getMessage("MISSING_LOGIN_PASSWORD"));
        hasText(phone, msService.getMessage("MISSING_PHONE"));
        hasText(code, msService.getMessage("MISSING_VERIFICATION_CODE"));
        if ("中国".equals(user.getLocation().getCountry())) {
            if (!ValidateUtil.isMobilePhone(phone.trim())) {
                return MessageResult.error(msService.getMessage("PHONE_FORMAT_ERROR"));
            }
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object cache = valueOperations.get(SysConstant.PHONE_BIND_CODE_PREFIX + phone);
        notNull(cache, msService.getMessage("NO_GET_VERIFICATION_CODE"));
        Member member1 = memberService.findByPhone(phone);
        isTrue(member1 == null, msService.getMessage("PHONE_ALREADY_BOUND"));
        if (!code.equals(cache.toString())) {
            return MessageResult.error(msService.getMessage("VERIFICATION_CODE_INCORRECT"));
        } else {
            valueOperations.getOperations().delete(SysConstant.PHONE_BIND_CODE_PREFIX + phone);
        }
        Member member = memberService.getById(user.getId());
        isTrue(member.getMobilePhone() == null, msService.getMessage("REPEAT_PHONE_REQUEST"));
        if (member.getPassword().equals(MD5.md5(password + member.getSalt()).toLowerCase())) {
            member.setMobilePhone(phone);
            memberService.updateById(member);
            return MessageResult.success(msService.getMessage("SETTING_SUCCESS"));
        } else {
            request.removeAttribute(SysConstant.SESSION_MEMBER);
            return MessageResult.error(msService.getMessage("PASSWORD_ERROR"));
        }
    }


    /**
     *
     * 更改登录密码
     *
     * @param request
     * @param oldPassword
     * @param newPassword
     * @param code
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "更改登录密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPassword", value = "旧密码"),
            @ApiImplicitParam(name = "newPassword", value = "新密码"),
            @ApiImplicitParam(name = "code", value = "验证码"),
    })
    @PermissionOperation
    @RequestMapping("/update/password")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult updateLoginPassword(HttpServletRequest request, String oldPassword, String newPassword, String code, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        hasText(oldPassword, msService.getMessage("MISSING_OLD_PASSWORD"));
        hasText(newPassword, msService.getMessage("MISSING_NEW_PASSWORD"));
        isTrue(newPassword.length() >= 6 && newPassword.length() <= 20, msService.getMessage("PASSWORD_LENGTH_ILLEGAL"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object cache = valueOperations.get(SysConstant.PHONE_UPDATE_PASSWORD_PREFIX + user.getMobilePhone());
        notNull(cache, msService.getMessage("NO_GET_VERIFICATION_CODE"));
        hasText(code, msService.getMessage("MISSING_VERIFICATION_CODE"));
        if (!code.equals(cache.toString())) {
            return MessageResult.error(msService.getMessage("VERIFICATION_CODE_INCORRECT"));
        } else {
            valueOperations.getOperations().delete(SysConstant.PHONE_UPDATE_PASSWORD_PREFIX + user.getMobilePhone());
        }
        Member member = memberService.getById(user.getId());
        request.removeAttribute(SysConstant.SESSION_MEMBER);
        isTrue(MD5.md5(oldPassword + member.getSalt()).toLowerCase().equals(member.getPassword()), msService.getMessage("PASSWORD_ERROR"));
        member.setPassword(MD5.md5(newPassword + member.getSalt()).toLowerCase());
        memberService.updateById(member);
        return MessageResult.success(msService.getMessage("SETTING_SUCCESS"));
    }

    /**
     * 绑定邮箱
     *
     * @param request
     * @param password
     * @param code
     * @param email
     * @return
     */
    @ApiOperation(value = "绑定邮箱")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "password", value = "密码"),
            @ApiImplicitParam(name = "code", value = "验证码"),
            @ApiImplicitParam(name = "email", value = "电子邮箱"),
    })
    @PermissionOperation
    @RequestMapping("/bind/email")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult bindEmail(HttpServletRequest request, String password, String code, String email, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        hasText(password, msService.getMessage("MISSING_LOGIN_PASSWORD"));
        hasText(code, msService.getMessage("MISSING_VERIFICATION_CODE"));
        hasText(email, msService.getMessage("MISSING_EMAIL"));
        isTrue(ValidateUtil.isEmail(email), msService.getMessage("EMAIL_FORMAT_ERROR"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object cache = valueOperations.get(SysConstant.EMAIL_BIND_CODE_PREFIX + email);
        notNull(cache, msService.getMessage("NO_GET_VERIFICATION_CODE"));
        isTrue(code.equals(cache.toString()), msService.getMessage("VERIFICATION_CODE_INCORRECT"));
        Member member = memberService.getById(user.getId());
        isTrue(member.getEmail() == null, msService.getMessage("REPEAT_EMAIL_REQUEST"));
        if (!MD5.md5(password + member.getSalt()).toLowerCase().equals(member.getPassword())) {
            request.removeAttribute(SysConstant.SESSION_MEMBER);
            return MessageResult.error(msService.getMessage("PASSWORD_ERROR"));
        } else {
            member.setEmail(email);
            memberService.updateById(member);
            return MessageResult.success(msService.getMessage("SETTING_SUCCESS"));
        }
    }

    /**
     * 实名认证
     *
     * @param realName
     * @param idCard
     * @return
     */
    @ApiOperation(value = "实名认证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "realName", value = "真实姓名"),
            @ApiImplicitParam(name = "idCard", value = "身份证号"),
            @ApiImplicitParam(name = "idCardFront", value = "证件 手持"),
            @ApiImplicitParam(name = "idCardBack", value = "证件 反面"),
            @ApiImplicitParam(name = "handHeldIdCard", value = "证件 正面"),
    })
    @PermissionOperation
    @RequestMapping("/real/name")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult realApprove(String realName, String idCard, String idCardFront,
                                     String idCardBack, String handHeldIdCard,
                                     @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        hasText(realName, msService.getMessage("MISSING_REAL_NAME"));
        hasText(idCard, msService.getMessage("MISSING_ID_CARD"));
        hasText(idCardFront, msService.getMessage("MISSING_ID_CARD_FRONT"));
        hasText(idCardBack, msService.getMessage("MISSING_ID_CARD_BACK"));
        hasText(handHeldIdCard, msService.getMessage("MISSING_ID_CARD_HAND"));
        Member member = memberService.getById(user.getId());
        Country country = countryService.findOne(member.getLocal());
        if ("China".equals(country.getEnName())) {
            isTrue(ValidateUtil.isChineseName(realName), msService.getMessage("REAL_NAME_ILLEGAL"));
            isTrue(IdcardValidator.isValidate18Idcard(idCard), msService.getMessage("ID_CARD_ILLEGAL"));
        }
        isTrue(member.getRealNameStatus() == RealNameStatus.NOT_CERTIFIED.getCode(), msService.getMessage("REPEAT_REAL_NAME_REQUEST"));
        int count = memberApplicationService.queryByIdCard(idCard);
        if(count>0){
            return MessageResult.error(msService.getMessage("ONLY_AUTHENTICATE_ONCE"));
        }
        MemberApplication memberApplication = new MemberApplication();
        memberApplication.setAuditStatus(AuditStatus.AUDIT_ING);
        memberApplication.setRealName(realName);
        memberApplication.setIdCard(idCard);
        memberApplication.setMemberId(member.getId());
        memberApplication.setIdentityCardImgFront(idCardFront);
        memberApplication.setIdentityCardImgInHand(handHeldIdCard);
        memberApplication.setIdentityCardImgReverse(idCardBack);
        memberApplication.setCreateTime(new Date());
        memberApplicationService.save(memberApplication);
        member.setRealNameStatus(RealNameStatus.AUDITING.getCode());
        memberService.updateById(member);
        return MessageResult.success(msService.getMessage("REAL_APPLY_SUCCESS"));
    }


    /**
     * 查询实名认证情况
     *
     * @return
     */
    @ApiOperation(value = "查询实名认证情况")
    @PermissionOperation
    @PostMapping("/real/detail")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult realNameApproveDetail(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        QueryWrapper<MemberApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id",user.getId());
        queryWrapper.orderByDesc("id");
        List<MemberApplication> list = memberApplicationService.list(queryWrapper);
        MemberApplication memberApplication = new MemberApplication();
        if (list != null && list.size() > 0) {
            memberApplication = list.get(0);
        }
        MessageResult result = MessageResult.success();
        result.setData(memberApplication);
        return result;
    }

    /**
     * 账户设置
     *
     * @return
     */
    @ApiOperation(value = "账户设置")
    @PermissionOperation
    @RequestMapping("/account/setting")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult accountSetting(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member member = memberService.getById(user.getId());
        hasText(member.getIdNumber(), msService.getMessage("NO_REAL_NAME"));
        hasText(member.getJyPassword(), msService.getMessage("NO_JY_PASSWORD"));
        MemberAccount memberAccount = MemberAccount.builder()
                .realName(member.getRealName())
                .build();
        MessageResult result = MessageResult.success();
        result.setData(memberAccount);
        return result;
    }


    /**
     * 原来的手机还能用的情况下更换手机
     *
     * @param request
     * @param password
     * @param phone
     * @param code
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "原来的手机还能用的情况下更换手机")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "password", value = "密码"),
            @ApiImplicitParam(name = "phone", value = "手机号"),
            @ApiImplicitParam(name = "url", value = "验证码"),
    })
    @PermissionOperation
    @RequestMapping("/change/phone")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult changePhone(HttpServletRequest request, String password, String phone, String code, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member member = memberService.getById(user.getId());
        hasText(password, msService.getMessage("MISSING_LOGIN_PASSWORD"));
        hasText(phone, msService.getMessage("MISSING_PHONE"));
        hasText(code, msService.getMessage("MISSING_VERIFICATION_CODE"));
        Member member1 = memberService.findByPhone(phone);
        isTrue(member1 == null, msService.getMessage("PHONE_ALREADY_BOUND"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object cache = valueOperations.get(SysConstant.PHONE_CHANGE_CODE_PREFIX + member.getMobilePhone());
        notNull(cache, msService.getMessage("NO_GET_VERIFICATION_CODE"));
        Country country = countryService.findOne(member.getLocal());
        if ("86".equals(country.getAreaCode())) {
            if (!ValidateUtil.isMobilePhone(phone.trim())) {
                return MessageResult.error(msService.getMessage("PHONE_FORMAT_ERROR"));
            }
        }
        if (member.getPassword().equals(MD5.md5(password + member.getSalt()).toLowerCase())) {
            if (!code.equals(cache.toString())) {
                return MessageResult.error(msService.getMessage("VERIFICATION_CODE_INCORRECT"));
            } else {
                valueOperations.getOperations().delete(SysConstant.PHONE_CHANGE_CODE_PREFIX + member.getMobilePhone());
            }
            member.setMobilePhone(phone);
            return MessageResult.success(msService.getMessage("SETTING_SUCCESS"));
        } else {
            request.removeAttribute(SysConstant.SESSION_MEMBER);
            return MessageResult.error(msService.getMessage("PASSWORD_ERROR"));
        }
    }

}
