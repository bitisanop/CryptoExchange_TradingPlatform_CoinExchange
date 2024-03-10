package com.bitisan.user.controller;

import com.alibaba.fastjson.JSONObject;

import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.user.entity.Member;
import com.bitisan.user.service.MemberService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.GoogleAuthenticatorUtil;
import com.bitisan.util.MD5;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @time 2020.04.09 11:07
 */
@RestController
@Slf4j
@RequestMapping("/google")
public class GoogleAuthenticationController extends BaseController {

    @Autowired
    private MemberService memberService;

    /**
     * 验证google
     * @author Bitisan  E-mail:bitisanop@gmail.com
     * @time 2020.04.09 11:36
     * @param authMember
     * @param codes
     * @return true
     */
    @PermissionOperation
    @RequestMapping(value = "/yzgoogle",method = RequestMethod.GET)
    public MessageResult yzgoogle(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, String codes) {
        // enter the code shown on device. Edit this and run it fast before the
        // code expires!
        long code = Long.parseLong(codes);
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member member = memberService.getById(user.getId());
        long t = System.currentTimeMillis();
        GoogleAuthenticatorUtil ga = new GoogleAuthenticatorUtil();
        //  ga.setWindowSize(0); // should give 5 * 30 seconds of grace...
        boolean r = ga.check_code(member.getGoogleKey(), code, t);
        System.out.println("rrrr="+r);
        if(!r){
            return MessageResult.error("Validation failed");
        }
        else{
            return MessageResult.success("Verification passed");
        }
    }


    /**
     * 生成谷歌认证码
     * @return
     */
    @PermissionOperation
    @RequestMapping(value = "/sendgoogle",method = RequestMethod.GET)
    public MessageResult  sendgoogle(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        /*for(int i = 0;i<50;i++){
            log.info("######################       开始循环次数={}    ######################",i+1);
            GoogleAuthenticatorUtil.generateSecretKey();
            log.info("######################       结束循环次数={}    ######################",i+1);
        }*/
        AuthMember user = AuthMember.toAuthMember(authMember);
        log.info("开始进入用户id={}",user.getId());
        long current = System.currentTimeMillis();
        Member member = memberService.getById(user.getId());
        log.info("查询完毕 耗时={}",System.currentTimeMillis()-current);
        if (member == null){
            return  MessageResult.error("未登录");
        }
        String secret = GoogleAuthenticatorUtil.generateSecretKey();
        log.info("secret完毕 耗时={}",System.currentTimeMillis()-current);
        String qrBarcodeURL = GoogleAuthenticatorUtil.getQRBarcodeURL(member.getId().toString(),
                "www.bitisan.org", secret);
        log.info("qrBarcodeURL完毕 耗时={}",System.currentTimeMillis()-current);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("link",qrBarcodeURL);
        jsonObject.put("secret",secret);
        log.info("jsonObject完毕 耗时={}",System.currentTimeMillis()-current);
        MessageResult messageResult = new MessageResult();
        messageResult.setData(jsonObject);
        messageResult.setMessage("获取成功");
        log.info("执行完毕 耗时={}",System.currentTimeMillis()-current);
        return  messageResult;

    }


    /**
     * google解绑
     * @author Bitisan  E-mail:bitisanop@gmail.com
     * @time 2020.04.09 12:47
     * @param codes
     * @param authMember
     * @return true
     */
    @PermissionOperation
    @RequestMapping(value = "/jcgoogle" ,method = RequestMethod.POST)
    public MessageResult jcgoogle(String codes, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember,String password) {
        // enter the code shown on device. Edit this and run it fast before the
        // code expires!
        //String GoogleKey = (String) request.getSession().getAttribute("googleKey");
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member member = memberService.getById(user.getId());
        String GoogleKey = member.getGoogleKey();
        if(StringUtils.isEmpty(password)){
            return MessageResult.error("Password cannot be empty");
        }
        try {
            if(!(MD5.md5Digest(password + member.getSalt()).toLowerCase().equals(member.getPassword().toLowerCase()))){
                return MessageResult.error("密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long code = Long.parseLong(codes);
        long t = System.currentTimeMillis();
        GoogleAuthenticatorUtil ga = new GoogleAuthenticatorUtil();
        // ga.setWindowSize(0); // should give 5 * 30 seconds of grace...
        boolean r = ga.check_code(GoogleKey, code, t);
        if(!r){
            return MessageResult.error("Validation failed");

        }else{
            member.setGoogleDate(new Date());
            member.setGoogleState(0);
            boolean result = memberService.saveOrUpdate(member);
            if(result){
                return MessageResult.success("Unbinding succeeded");
            }else{
                return MessageResult.error("Unbinding failed");
            }
        }
    }




        //ga.setWindowSize(0); // should give 5 * 30 seconds of grace...
        /**
         * 绑定google
         * @author Bitisan  E-mail:bitisanop@gmail.com
         * @time 2020.04.09 15:19
         * @param codes
         * @param authMember
         * @return true
         */
        @PermissionOperation
        @RequestMapping(value = "/googleAuth" ,method = RequestMethod.POST)
        public MessageResult googleAuth(String codes, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember,String secret) {
            AuthMember user = AuthMember.toAuthMember(authMember);
//            if(user.getId()==600800){
//                return MessageResult.error("绑定失败，特殊账号不能绑定");
//            }
            Member member = memberService.getById(user.getId());
            long code = Long.parseLong(codes);
            long t = System.currentTimeMillis();
            GoogleAuthenticatorUtil ga = new GoogleAuthenticatorUtil();
            boolean r = ga.check_code(secret, code, t);
            if(!r){
                return MessageResult.error("Validation failed");
            }else{
                member.setGoogleState(1);
                member.setGoogleKey(secret);
                member.setGoogleDate(new Date());
                boolean result = memberService.saveOrUpdate(member);
                if(result){
                    return MessageResult.success("Binding succeeded");
                }else{
                    return MessageResult.error("Binding failed");
                }
            }
        }

}
