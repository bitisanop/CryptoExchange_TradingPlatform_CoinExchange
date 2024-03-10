package com.bitisan.user.controller;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.impl.BasicCaptchaTrackValidator;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Country;
import com.bitisan.user.entity.Member;
import com.bitisan.user.service.CountryService;
import com.bitisan.user.service.MemberService;
import com.bitisan.user.system.GeetestLib;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.user.vo.LoginInfo;
import com.bitisan.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author Bitisan  E-mail:xunibidev@gmail.com
 * @date 2020年01月10日
 */
@Api(tags = "登录")
@RestController
@Slf4j
public class LoginController extends BaseController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private CountryService countryService;
//    @Autowired
//    private MemberEvent memberEvent;
    @Autowired
    private LocaleMessageSourceService messageSourceService;
    @Autowired
    private RedisTemplate redisTemplate;
//    @Autowired
//    private LocaleMessageSourceService msService;
    @Autowired
    private GeetestLib gtSdk;


    @Value("${person.promote.prefix:}")
    private String promotePrefix;

    @ApiOperation(value = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名"),
            @ApiImplicitParam(name = "password", value = "密码"),
            @ApiImplicitParam(name = "code", value = "验证码"),
            @ApiImplicitParam(name = "country", value = "国家")
    })
    @RequestMapping(value = "/login")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult login(HttpServletRequest request, String username, String password,Long code,String country) throws Exception {
        Assert.hasText(username, messageSourceService.getMessage("MISSING_USERNAME"));
        Assert.hasText(password, messageSourceService.getMessage("MISSING_PASSWORD"));
        String ip = IPUtils.getIpAddr(request);
        // 验证成功
        LoginInfo loginInfo = getLoginInfo(username, password, ip,code,country,request);
        return success(loginInfo);
    }


    private LoginInfo getLoginInfo(String username, String password, String ip, Long code,String country,HttpServletRequest request) throws Exception {
        Member member = memberService.loginWithCode(username, password,code,country);
        //封装令牌

        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put(SysConstant.SESSION_MEMBER, JSON.toJSONString(AuthMember.toAuthMember(member)));
        dataMap.put("roles",1);
        //获取IP
        dataMap.put("ip", MD5.md5(ip));
        //'创建令牌
        String token = JwtToken.createToken(dataMap);
        // 获取登录次数
        int loginCount = member.getLoginCount();
        member.setLoginCount(loginCount+1);
        memberService.updateById(member);
        //将token保存到redis
        String key = SysConstant.TOKEN_MEMBER+member.getId();
        redisTemplate.boundValueOps(key).set(token);
        redisTemplate.expire(key,7, TimeUnit.DAYS);
        QueryWrapper<Country> cq = new QueryWrapper<>();
        cq.eq("zh_name",member.getLocal());
        Country countryEntry = countryService.getOne(cq);
        // 签到活动是否进行
//        Sign sign = signService.fetchUnderway();
//        if (sign == null) {
        LoginInfo loginInfo = LoginInfo.getLoginInfo(member,countryEntry, token, false, promotePrefix);
//        } else {
//            loginInfo = LoginInfo.getLoginInfo(member, request.getSession().getId(), true, promotePrefix);
//        }
        return loginInfo;
    }



    /**
     * 登出
     *
     * @return
     */
    @ApiOperation(value = "登出")
    @PermissionOperation
    @RequestMapping(value = "/loginout")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult loginOut(HttpServletRequest request, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        MessageResult messageResult = new MessageResult();
        log.info(">>>>>退出登陆接口开始>>>>>");
        try {
            String key = SysConstant.TOKEN_MEMBER+user.getId();
            redisTemplate.expire(key,1, TimeUnit.MILLISECONDS);
            messageResult = success(messageSourceService.getMessage("LOGOUT_SUCCESS"));
        } catch (Exception e) {
            e.printStackTrace();
            messageResult = error(messageSourceService.getMessage("LOGOUT_FAILED")) ;
            log.info(">>>>>登出失败>>>>>"+e);
        }
        log.info(">>>>>退出登陆接口结束>>>>>");
        return messageResult;
    }

    /**
     * 检查是否登录
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "检查是否登录")
    @PermissionOperation
    @RequestMapping("/check/login")
    public MessageResult checkLogin(HttpServletRequest request ,@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        MessageResult result = MessageResult.success();
        if (user != null) {
            result.setData(true);
        } else {
            result.setData(false);
        }
        return result;
    }

    /**
     * 获取滑块验证码图片
     * @param request
     * @return
     */
    @ApiOperation(value = "获取滑块验证码图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名"),
            @ApiImplicitParam(name = "type", value = "登录方式")
    })
    @RequestMapping(value = "/getYZMPic")
    public MessageResult getYZMPic(HttpServletRequest request,String username,String type) {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager).init(true);
         /*
                生成滑块验证码图片, 可选项
                SLIDER (滑块验证码)
                ROTATE (旋转验证码)
                CONCAT (滑动还原验证码)
                WORD_IMAGE_CLICK (文字点选验证码)

                更多验证码支持 详见 cloud.tianai.captcha.common.constant.CaptchaTypeConstant
         */
        ImageCaptchaInfo imageCaptchaInfo = imageCaptchaGenerator.generateCaptchaImage(CaptchaTypeConstant.SLIDER);
        MessageResult result = MessageResult.success();
        Map<String,String> map = new HashMap<>();
        map.put("backgroundImage",imageCaptchaInfo.getBackgroundImage());
        map.put("sliderImage",imageCaptchaInfo.getSliderImage());
        result.setData(map);
        String key = "SLIDER_"+type+"_"+username;
        BoundValueOperations ops = redisTemplate.boundValueOps(key);
        Float percentage =Float.valueOf(imageCaptchaInfo.getRandomX())/(imageCaptchaInfo.getBgImageWidth()-imageCaptchaInfo.getSliderImageWidth());
        ops.set(percentage);

        return result;
    }

    /**
     * 验证滑块
     * @param request
     * @return
     */
    @ApiOperation(value = "验证滑块")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名"),
            @ApiImplicitParam(name = "code", value = "验证码"),
            @ApiImplicitParam(name = "type", value = "登录方式")
    })
    @RequestMapping(value = "/checkYZMPic")
    public MessageResult checkYZMPic(HttpServletRequest request,String username,String code,String type) {
        String key = "SLIDER_"+type+"_"+username;
        BoundValueOperations ops = redisTemplate.boundValueOps(key);
        Object per = ops.get();
        if(per!=null && !StringUtils.isEmpty(code)){
            Float percentage = Float.valueOf(per.toString());
            ImageCaptchaValidator sliderCaptchaValidator = new BasicCaptchaTrackValidator();
            boolean check = sliderCaptchaValidator.checkPercentage(Float.valueOf(code), percentage,0.1f);
            if(check){
                String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
                ops.set(randomCode);
                MessageResult result = MessageResult.success();
                result.setData(randomCode);
                return result;
            }
        }
        return error(messageSourceService.getMessage("GEETEST_FAIL"));
    }

}
