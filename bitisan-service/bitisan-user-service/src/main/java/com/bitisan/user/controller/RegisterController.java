package com.bitisan.user.controller;

import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.constant.CommonStatus;
import com.bitisan.constant.MemberLevelEnum;
import com.bitisan.sms.EmailProvider;
import com.bitisan.user.entity.Country;
import com.bitisan.user.entity.LoginByEmail;
import com.bitisan.user.entity.LoginByPhone;
import com.bitisan.user.entity.Member;
import com.bitisan.user.event.MemberEvent;
import com.bitisan.user.service.CountryService;
import com.bitisan.user.service.EmailService;
import com.bitisan.user.service.MemberService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.*;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;


/**
 * 会员注册
 *
 * @author Bitisan  E-mail:xunibidev@gmail.com
 * @date 2020年12月29日
 */
@Api(tags = "会员注册")
@Controller
@Slf4j
public class RegisterController extends BaseController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MemberService memberService;

    @Autowired
    private IdWorkByTwitter idWorkByTwitter;

    @Autowired
    private MemberEvent memberEvent;

    @Autowired
    private CountryService countryService;

    @Autowired
    private EmailService emailService;

    @Resource
    private LocaleMessageSourceService localeMessageSourceService;




    /**
     * 注册支持的国家
     *
     * @return
     */
    @ApiOperation(value = "注册支持的国家")
    @RequestMapping(value = "/support/country", method = RequestMethod.POST)
    @ResponseBody
    public MessageResult allCountry(@RequestHeader(value = "lang") String headerLanguage) {
        MessageResult result = success();
        List<Country> list = countryService.getAllCountry();
        if("zh_CN".equals(headerLanguage)){
            list.stream().peek(e->{
                e.setName(e.getZhName());
            }).collect(Collectors.toList());
        }else{
            list.stream().peek(e->{
                e.setName(e.getEnName());
            }).collect(Collectors.toList());
        }
        result.setData(list);
        return result;
    }

    /**
     * 检查用户名是否重复
     *
     * @param username
     * @return
     */
    @ApiOperation(value = "检查用户名是否重复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名"),
    })
    @RequestMapping(value = "/register/check/username")
    @ResponseBody
    public MessageResult checkUsername(String username) {
        MessageResult result = success();
        if (memberService.usernameIsExist(username)) {
            result.setCode(500);
            result.setMessage(localeMessageSourceService.getMessage("ACTIVATION_FAILS_USERNAME"));
        }
        return result;
    }


    /**
     * 激活邮件  暂时注掉
     *
     * @param key
     * @param request
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "激活邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "key"),
    })
    @RequestMapping(value = "/register/active")
    @Transactional(rollbackFor = Exception.class)
    public String activate(String key, HttpServletRequest request) throws Exception {
        if (StringUtils.isEmpty(key)) {
            request.setAttribute("result", localeMessageSourceService.getMessage("INVALID_LINK"));
            return "registeredResult";
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object info = valueOperations.get(key);
        LoginByEmail loginByEmail = null;
        if (info instanceof LoginByEmail) {
            loginByEmail = (LoginByEmail) info;
        }
        if (loginByEmail == null) {
            request.setAttribute("result", localeMessageSourceService.getMessage("INVALID_LINK"));
            return "registeredResult";
        }
        if (memberService.emailIsExist(loginByEmail.getEmail())) {
            request.setAttribute("result", localeMessageSourceService.getMessage("ACTIVATION_FAILS_EMAIL"));
            return "registeredResult";
        } else if (memberService.usernameIsExist(loginByEmail.getUsername())) {
            request.setAttribute("result", localeMessageSourceService.getMessage("ACTIVATION_FAILS_USERNAME"));
            return "registeredResult";
        }
        //删除redis里存的键值
        valueOperations.getOperations().delete(key);
        valueOperations.getOperations().delete(loginByEmail.getEmail());
        //不可重复随机数
        String loginNo = String.valueOf(idWorkByTwitter.nextId());
        //盐

        String credentialsSalt = MD5.md5(loginNo);
        //生成密码
        String password = MD5.md5(loginByEmail.getPassword() + credentialsSalt).toLowerCase();
        Member member = new Member();

        member.setMemberLevel(MemberLevelEnum.GENERAL.getCode());
        member.setCountry(loginByEmail.getCountry());
        member.setLocal(loginByEmail.getCountry());
        member.setUsername(loginByEmail.getUsername());
        member.setPassword(password);
        member.setEmail(loginByEmail.getEmail());
        member.setSalt(credentialsSalt);
        member.setAvatar("https://bizzanex.oss-cn-hangzhou.aliyuncs.com/defaultavatar.png"); // 默认用户头像
        memberService.save(member);
        if (member.getId()!=null) {
        	// Member为@entity注解类，与数据库直接映射，因此，此处setPromotionCode会直接同步到数据库
            member.setPromotionCode(GeneratorUtil.getPromotionCode(member.getId()));
            memberEvent.onRegisterSuccess(member, loginByEmail.getPromotion().trim());
        }
        return "registeredResult";
    }

    /**
     * 邮箱注册
     *
     * @param loginByEmail
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "邮箱注册")
    @RequestMapping("/register/email")
    @ResponseBody
    public MessageResult registerByEmail(@Valid LoginByEmail loginByEmail, BindingResult bindingResult) throws Exception {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        String email = loginByEmail.getEmail();
        isTrue(!memberService.emailIsExist(email), localeMessageSourceService.getMessage("EMAIL_ALREADY_BOUND"));
        isTrue(!memberService.usernameIsExist(loginByEmail.getUsername()), localeMessageSourceService.getMessage("USERNAME_ALREADY_EXISTS"));
        if(!emailService.checkCode4Reg(email,loginByEmail.getCode())){
            return error(localeMessageSourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
        }

        //不可重复随机数
        String loginNo = String.valueOf(idWorkByTwitter.nextId());
        //盐
        String credentialsSalt =MD5.md5(loginNo);
        //生成密码
        String password = MD5.md5(loginByEmail.getPassword() + credentialsSalt).toLowerCase();
        Member member = new Member();

        member.setMemberLevel(MemberLevelEnum.GENERAL.getCode());
        member.setCountry(loginByEmail.getCountry());
        member.setLocal(loginByEmail.getCountry());
        member.setUsername(loginByEmail.getUsername());
        member.setPassword(password);
        member.setEmail(loginByEmail.getEmail());
        member.setSalt(credentialsSalt);
        member.setAvatar("https://bizzanex.oss-cn-hangzhou.aliyuncs.com/defaultavatar.png"); // 默认用户头像
        memberService.save(member);

        if (member.getId() != null) {
            // Member为@entity注解类，与数据库直接映射，因此，此处setPromotionCode会直接同步到数据库
            member.setPromotionCode(GeneratorUtil.getPromotionCode(member.getId()));
            memberEvent.onRegisterSuccess(member, loginByEmail.getPromotion().trim());
            return success(localeMessageSourceService.getMessage("REGISTRATION_SUCCESS"));
        } else {
            return error(localeMessageSourceService.getMessage("REGISTRATION_FAILED"));
        }
    }

//    @Async
//    public void sentEmail(ValueOperations valueOperations, LoginByEmail loginByEmail, String email) throws MessagingException, IOException, TemplateException {
//        //缓存邮箱和注册信息
//        String token = UUID.randomUUID().toString().replace("-", "");
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = null;
//        helper = new MimeMessageHelper(mimeMessage, true);
//        helper.setFrom(from);
//        helper.setTo(email);
//        helper.setSubject(company);
//        Map<String, Object> model = new HashMap<>(16);
//        model.put("username", loginByEmail.getUsername());
//        model.put("token", token);
//        model.put("host", host);
//        model.put("name", company);
//        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
//        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
//        Template template = cfg.getTemplate("activateEmail.ftl");
//        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
//        helper.setText(html, true);
//        //发送邮件
//        javaMailSender.send(mimeMessage);
//        valueOperations.set(token, loginByEmail, 12, TimeUnit.HOURS);
//        valueOperations.set(email, "", 12, TimeUnit.HOURS);
//    }

    /**
     * 手机注册
     *
     * @param loginByPhone
     * @param bindingResult
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "手机注册")
    @RequestMapping("/register/phone")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public MessageResult loginByPhone(
            @Valid LoginByPhone loginByPhone,
            BindingResult bindingResult,HttpServletRequest request) throws Exception {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }

        if ("中国".equals(loginByPhone.getCountry())) {
            Assert.isTrue(ValidateUtil.isMobilePhone(loginByPhone.getPhone().trim()), localeMessageSourceService.getMessage("PHONE_EMPTY_OR_INCORRECT"));
        }
        String ip = request.getHeader("X-Real-IP");
        String phone = loginByPhone.getPhone();
        ValueOperations valueOperations = redisTemplate.opsForValue();

        isTrue(!memberService.phoneIsExist(phone), localeMessageSourceService.getMessage("PHONE_ALREADY_EXISTS"));
        isTrue(!memberService.usernameIsExist(loginByPhone.getUsername()), localeMessageSourceService.getMessage("USERNAME_ALREADY_EXISTS"));
        if (StringUtils.hasText(loginByPhone.getPromotion().trim())) {
        	isTrue(memberService.userPromotionCodeIsExist(loginByPhone.getPromotion()),localeMessageSourceService.getMessage("USER_PROMOTION_CODE_EXISTS"));
        }
        //校验是否通过验证码 短信上行
        // isTrue(verifier.verify(loginByPhone.getValidate(),""),localeMessageSourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
        //腾讯防水注册校验
        //isTrue(gtestCon.watherProof(loginByPhone.getTicket(),loginByPhone.getRandStr(),ip),localeMessageSourceService.getMessage("VERIFICATION_PICTURE_NOT_CORRECT"));
        // 短信验证码检查
//        Object code =valueOperations.get(SysConstant.PHONE_REG_CODE_PREFIX + phone);
//        notNull(code, localeMessageSourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
//        if (!code.toString().equals(loginByPhone.getCode())) {
//            return error(localeMessageSourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
//        } else {
//            valueOperations.getOperations().delete(SysConstant.PHONE_REG_CODE_PREFIX + phone);
//        }
        //不可重复随机数
        String loginNo = String.valueOf(idWorkByTwitter.nextId());
        //盐
        String credentialsSalt = MD5.md5(loginNo);
        //生成密码
        String password = MD5.md5(loginByPhone.getPassword() + credentialsSalt).toLowerCase();
        Member member = new Member();
        //新增超级合伙人 判断0  普通 默认普通用户   1 超级合伙人 >>2.专业超级合伙人
        if(!StringUtils.isEmpty(loginByPhone.getSuperPartner())){
            member.setSuperPartner(loginByPhone.getSuperPartner());
            if (!"0".equals(loginByPhone.getSuperPartner())) {
                //需要后台审核解禁
                member.setStatus(CommonStatus.ILLEGAL.getCode());
            }
        }
        member.setMemberLevel(MemberLevelEnum.GENERAL.getCode());
        member.setLocal(loginByPhone.getCountry());
        member.setCountry(loginByPhone.getCountry());
        member.setUsername(loginByPhone.getUsername());
        member.setPassword(password);
        member.setMobilePhone(phone);
        member.setSalt(credentialsSalt);
        member.setAvatar("https://bizzanex.oss-cn-hangzhou.aliyuncs.com/defaultavatar.png"); // 默认用户头像
        memberService.save(member);
        if (member.getId() != null) {
        	// Member为@entity注解类，与数据库直接映射，因此，此处setPromotionCode会直接同步到数据库
            member.setPromotionCode(GeneratorUtil.getPromotionCode(member.getId()));
            memberEvent.onRegisterSuccess(member, loginByPhone.getPromotion().trim());
            return success(localeMessageSourceService.getMessage("REGISTRATION_SUCCESS"));
        } else {
            return error(localeMessageSourceService.getMessage("REGISTRATION_FAILED"));
        }
    }

    /**
     * 手机注册暂时关闭
     * @param loginByPhone
     * @param bindingResult
     * @param request
     * @return
     * @throws Exception
     */
//    @RequestMapping("/register/for_phone")
//    @ResponseBody
//    @Transactional(rollbackFor = Exception.class)
//    public MessageResult loginByPhone4Mobiles(
//            @Valid LoginByPhone loginByPhone,
//            BindingResult bindingResult,HttpServletRequest request) throws Exception {
//        log.info("============请到PC端注册---registerPC");
//        return error(localeMessageSourceService.getMessage("REGISTER_TO_PC"));
//    }
    /**
     * 为了手机注册
     * @param loginByPhone
     * @param bindingResult
     * @param request
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "为了手机注册")
    @RequestMapping("/register/for_phone")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public MessageResult loginByPhone4Mobile(
            @Valid LoginByPhone loginByPhone,
            BindingResult bindingResult,HttpServletRequest request) throws Exception {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }

        if (loginByPhone.getCountry().equals("中国")) {
            Assert.isTrue(ValidateUtil.isMobilePhone(loginByPhone.getPhone().trim()), localeMessageSourceService.getMessage("PHONE_EMPTY_OR_INCORRECT"));
        }
        String ip = request.getHeader("X-Real-IP");
        String phone = loginByPhone.getPhone();
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object code =valueOperations.get(SysConstant.PHONE_REG_CODE_PREFIX + phone);
        isTrue(!memberService.phoneIsExist(phone), localeMessageSourceService.getMessage("PHONE_ALREADY_EXISTS"));
        isTrue(!memberService.usernameIsExist(loginByPhone.getUsername()), localeMessageSourceService.getMessage("USERNAME_ALREADY_EXISTS"));
        if (StringUtils.hasText(loginByPhone.getPromotion().trim())) {
        	isTrue(memberService.userPromotionCodeIsExist(loginByPhone.getPromotion()),localeMessageSourceService.getMessage("USER_PROMOTION_CODE_EXISTS"));
        }
//        isTrue(memberService.userPromotionCodeIsExist(loginByPhone.getPromotion()),localeMessageSourceService.getMessage("USER_PROMOTION_CODE_EXISTS"));
        //校验是否通过验证码 短信上行
        //isTrue(verifier.verify(loginByPhone.getValidate(),""),localeMessageSourceService.getMessage("VERIFICATION_CODE_INCORRECT"));

        // 换种校验方式
        notNull(code, localeMessageSourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
        if (!code.toString().equals(loginByPhone.getCode())) {
            return error(localeMessageSourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
        } else {
            valueOperations.getOperations().delete(SysConstant.PHONE_REG_CODE_PREFIX + phone);
        }
        //不可重复随机数
        String loginNo = String.valueOf(idWorkByTwitter.nextId());
        //盐
        String credentialsSalt = MD5.md5(loginNo);
        //生成密码
        String password = MD5.md5(loginByPhone.getPassword() + credentialsSalt).toLowerCase();
        Member member = new Member();
        //新增超级合伙人 判断0  普通 默认普通用户   1 超级合伙人 >>2.专业超级合伙人
        if(!StringUtils.isEmpty(loginByPhone.getSuperPartner())){
            member.setSuperPartner(loginByPhone.getSuperPartner());
            if (!"0".equals(loginByPhone.getSuperPartner())) {
                //需要后台审核解禁
                member.setStatus(CommonStatus.ILLEGAL.getCode());
            }
        }
        member.setMemberLevel(MemberLevelEnum.GENERAL.getCode());
        member.setCountry(loginByPhone.getCountry());
        member.setLocal(loginByPhone.getCountry());
        member.setUsername(loginByPhone.getUsername());
        member.setPassword(password);
        member.setMobilePhone(phone);
        member.setSalt(credentialsSalt);
        memberService.save(member);
        if (member.getId() != null) {
            member.setPromotionCode(GeneratorUtil.getPromotionCode(member.getId()));
            // 增加钱包记录
            memberEvent.onRegisterSuccess(member, loginByPhone.getPromotion());
            return success(localeMessageSourceService.getMessage("REGISTRATION_SUCCESS"));
        } else {
            return error(localeMessageSourceService.getMessage("REGISTRATION_FAILED"));
        }
    }
    /**
     * 发送绑定邮箱验证码
     *
     * @param email
     * @param authMember
     * @return
     */
    @ApiOperation(value = "发送绑定邮箱验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "电子邮箱"),
    })
    @RequestMapping("/bind/email/code")
    @ResponseBody
    @PermissionOperation
    @Transactional(rollbackFor = Exception.class)
    public MessageResult sendBindEmail(String email, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        //校验 签到活动 币种 会员 会员钱包
        AuthMember user = AuthMember.toAuthMember(authMember);
        Assert.isTrue(ValidateUtil.isEmail(email), localeMessageSourceService.getMessage("WRONG_EMAIL"));
        Member member = memberService.getById(user.getId());
        Assert.isNull(member.getEmail(), localeMessageSourceService.getMessage("BIND_EMAIL_REPEAT"));
        Assert.isTrue(!memberService.emailIsExist(email), localeMessageSourceService.getMessage("EMAIL_ALREADY_BOUND"));
        try {
            emailService.sentBindEmailCode(email);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }

    /**
     * 增加提币地址验证码
     *
     * @param authMember
     * @return
     */
    @ApiOperation(value = "增加提币地址验证码")
    @RequestMapping("/add/address/code")
    @ResponseBody
    @PermissionOperation
    @Transactional(rollbackFor = Exception.class)
    public MessageResult sendAddAddress(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member member = memberService.getById(user.getId());
        String email = member.getEmail();
        if (email == null) {
            return error(localeMessageSourceService.getMessage("NOT_BIND_EMAIL"));
        }
        try {
            emailService.sentEmailAddCode(email);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }



    @ApiOperation(value = "重置邮箱验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账户"),
    })
    @RequestMapping("/reset/email/code")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public MessageResult sendResetPasswordCode(String account) {
        Member member = memberService.findByEmail(account);
        notNull(member, localeMessageSourceService.getMessage("MEMBER_NOT_EXISTS"));
        try {
            emailService.sentResetPassword(account);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }

    /**
     * 忘记密码后重置密码
     *
     * @param mode     0为手机验证，1为邮箱验证
     * @param account  手机或邮箱
     * @param code     验证码
     * @param password 新密码
     * @return
     */
    @ApiOperation(value = "忘记密码后重置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = "0为手机验证，1为邮箱验证"),
            @ApiImplicitParam(name = "account", value = "手机或邮箱"),
            @ApiImplicitParam(name = "code", value = "验证码"),
            @ApiImplicitParam(name = "password", value = "新密码"),
    })
    @RequestMapping(value = "/reset/login/password", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public MessageResult forgetPassword(int mode, String account, String code, String password) throws Exception {
        Member member = null;
        isTrue(password.length() >= 6 && password.length() <= 20, localeMessageSourceService.getMessage("PASSWORD_LENGTH_ILLEGAL"));
        if(emailService.checkCode4forgetPassword(account,code)){
            if (mode == 0) {
                member = memberService.findByPhone(account);
            } else if (mode == 1) {
                member = memberService.findByEmail(account);
            }
            notNull(member, localeMessageSourceService.getMessage("MEMBER_NOT_EXISTS"));

            //生成密码
            String newPassword = MD5.md5(password + member.getSalt()).toLowerCase();
            member.setPassword(newPassword);
            return success();
        }else {
            return error(localeMessageSourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
        }

    }



    /**
     * 发送解绑旧邮箱验证码
     *
     * @param authMember
     * @return
     */
    @ApiOperation(value = "发送解绑旧邮箱验证码")
    @PermissionOperation
    @RequestMapping(value = "/untie/email/code", method = RequestMethod.POST)
    @ResponseBody
    public MessageResult untieEmailCode(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember){
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member member = memberService.getById(user.getId());
        isTrue(member.getEmail()!=null, localeMessageSourceService.getMessage("NOT_BIND_EMAIL"));
        try {
            emailService.sentUntieEmailCode(member.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success();
    }

    /**
     * 发送新邮箱验证码
     *
     * @param authMember
     * @return
     */
    @ApiOperation(value = "发送新邮箱验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "电子邮箱"),
    })
    @PermissionOperation
    @RequestMapping(value = "/update/email/code", method = RequestMethod.POST)
    @ResponseBody
    public MessageResult updateEmailCode(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember,String email){
        AuthMember user = AuthMember.toAuthMember(authMember);
        if(memberService.emailIsExist(email)){
            return MessageResult.error(localeMessageSourceService.getMessage("REPEAT_EMAIL_REQUEST"));
        }
        Member member = memberService.getById(user.getId());
        isTrue(member.getEmail()!=null, localeMessageSourceService.getMessage("NOT_BIND_EMAIL"));
        try {
            emailService.sentUpdateEmailCode(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success();
    }

    @ApiOperation(value = "注册邮箱验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "电子邮箱"),
            @ApiImplicitParam(name = "code", value = "验证码"),
            @ApiImplicitParam(name = "type", value = "验证码方式")
    })
    @RequestMapping("/reg/email/code")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public MessageResult sendRegEmail(String email,String type,String code) {
        Assert.isTrue(ValidateUtil.isEmail(email), localeMessageSourceService.getMessage("WRONG_EMAIL"));
        Assert.isTrue(!memberService.emailIsExist(email), localeMessageSourceService.getMessage("EMAIL_ALREADY_BOUND"));
        try {
            int gtResult = 0;
            String key = "SLIDER_"+type+"_"+email;
            BoundValueOperations ops = redisTemplate.boundValueOps(key);
            Object per = ops.get();
            if(per!=null && !org.apache.commons.lang.StringUtils.isEmpty(code)){
                String percentage = per.toString();
                if(percentage.equals(code)){
                    gtResult = 1;
                }
            }
            if(gtResult==0){
                return error(localeMessageSourceService.getMessage("GEETEST_FAIL"));
            }
            emailService.sentRegEmailCode(email);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }


}
