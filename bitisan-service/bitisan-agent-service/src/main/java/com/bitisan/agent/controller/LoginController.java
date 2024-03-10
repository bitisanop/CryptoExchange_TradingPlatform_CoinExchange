package com.bitisan.agent.controller;

import com.alibaba.fastjson.JSON;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Country;
import com.bitisan.user.entity.Member;
import com.bitisan.user.feign.CountryFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.user.vo.LoginInfo;
import com.bitisan.util.JwtToken;
import com.bitisan.util.MD5;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;



/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @date 2020年01月10日
 */
@RestController
@Slf4j
public class LoginController extends BaseController {

    @Autowired
    private MemberFeign memberFeign;

    @Autowired
    private CountryFeign countryFeign;
    @Autowired
    private LocaleMessageSourceService msService;

    @Value("${person.promote.prefix:}")
    private String promotePrefix;

    @Value("${spark.system.md5.key}")
    private String md5Key;


    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/login")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult login(HttpServletRequest request, String username, String password) {
        Assert.hasText(username, msService.getMessage("MISSING_USERNAME"));
        Assert.hasText(password, msService.getMessage("MISSING_PASSWORD"));
        String ip = getRemoteIp(request);

        try {
            LoginInfo loginInfo = getLoginInfo(username, password, ip, request);
            return success(loginInfo);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    private LoginInfo getLoginInfo(String username, String password, String ip, HttpServletRequest request) throws Exception {
        Member member = memberFeign.login(username, password);
        //封装令牌

        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put(SysConstant.SESSION_MEMBER, JSON.toJSONString(AuthMember.toAuthMember(member)));
        dataMap.put("roles",1);
        //获取IP
        dataMap.put("ip", MD5.md5(ip));
        //'创建令牌
        String token = JwtToken.createToken(dataMap);
        //将token保存到redis
        String key = SysConstant.TOKEN_MEMBER+member.getId();
        redisTemplate.boundValueOps(key).set(token);
        redisTemplate.expire(key,7, TimeUnit.DAYS);
        Country country = countryFeign.findByZhName(member.getLocal());
        LoginInfo loginInfo = LoginInfo.getLoginInfo(member,country, token, false, promotePrefix);
        return loginInfo;
    }

    /**
     * 登出
     *
     * @return
     */
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
            messageResult = success(msService.getMessage("LOGOUT_SUCCESS"));
        } catch (Exception e) {
            e.printStackTrace();
            messageResult = error(msService.getMessage("LOGOUT_FAILED")) ;
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
}
