package com.bitisan.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.user.entity.MemberApiKey;
import com.bitisan.user.service.MemberApiKeyService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.user.util.RedisUtil;
import com.bitisan.util.GeneratorUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @description: OpenApiController
 * @author: Bitisan  E-mail:bitisanop@gmail.com  E-mail:bitisanop@gmail.com
 * @create: 2020/05/07 10:33
 */
@Slf4j
@RestController
@RequestMapping("open")
public class OpenApiController extends BaseController {

    @Autowired
    private MemberApiKeyService memberApiKeyService;

    @Autowired
    private RedisUtil redisUtil ;

    /**
     * 获取ApiKey
     * @param authMember
     * @return
     */
    @PermissionOperation
    @RequestMapping(value = "get_key",method = RequestMethod.GET)
    public MessageResult queryApiKey(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember){
        AuthMember member = AuthMember.toAuthMember(authMember);
        List<MemberApiKey> result = memberApiKeyService.findAllByMemberId(member.getId());
        return success(result);
    }


    /**
     * 新增api-key
     * @param authMember
     * @param memberApiKey
     * @return
     */
    @PermissionOperation
    @RequestMapping(value = "api/save",method = RequestMethod.POST)
    public MessageResult saveApiKey(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember,MemberApiKey memberApiKey){
        AuthMember member = AuthMember.toAuthMember(authMember);
        log.info("-------新增API-key:"+ JSONObject.toJSONString(memberApiKey));
        String code = memberApiKey.getCode();
        Assert.isTrue(StringUtils.isNotEmpty(code),"请输入验证码");
        Object cacheCode = redisUtil.get(SysConstant.API_BIND_CODE_PREFIX+member.getMobilePhone());
        if(cacheCode == null){
            return MessageResult.error("验证码已过期");
        }
        if(!code.equalsIgnoreCase(cacheCode.toString())){
            return MessageResult.error("验证码不正确");
        }
        List<MemberApiKey> all = memberApiKeyService.findAllByMemberId(member.getId());
        if (all.isEmpty() || all.size()<5){
            memberApiKey.setId(null);
            if (StringUtils.isBlank(memberApiKey.getBindIp())){
                //不绑定IP时默认90天过期
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH,90);
                memberApiKey.setExpireTime(calendar.getTime());
            }
            memberApiKey.setApiName(member.getId()+"");
            memberApiKey.setApiKey(GeneratorUtil.getUUID());
            String secret = GeneratorUtil.getUUID();
            memberApiKey.setSecretKey(secret);
            memberApiKey.setMemberId(member.getId());
            memberApiKey.setCreateTime(new Date());
            memberApiKeyService.saveOrUpdate(memberApiKey);
            redisUtil.delete(SysConstant.API_BIND_CODE_PREFIX+member.getMobilePhone());
            return success("新增成功",secret);
        }else {
            return error("数量超过最大限制");
        }
    }


    /**
     * 修改API-key
     * @param authMember
     * @param memberApiKey
     * @return
     */
    @PermissionOperation
    @RequestMapping(value = "api/update",method = RequestMethod.POST)
    public MessageResult updateApiKey(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember,MemberApiKey memberApiKey){
        AuthMember member = AuthMember.toAuthMember(authMember);
        log.info("-------修改API-key:"+ JSONObject.toJSONString(memberApiKey));
        if (memberApiKey.getId() != null){
            MemberApiKey findMemberApiKey = memberApiKeyService.findByMemberIdAndId(member.getId(),memberApiKey
                    .getId());
            if (findMemberApiKey != null){
                if (!memberApiKey.getRemark().equals(findMemberApiKey.getRemark())){
                    findMemberApiKey.setRemark(memberApiKey.getRemark());
                }
                if (StringUtils.isNotEmpty(memberApiKey.getBindIp())){
                    findMemberApiKey.setBindIp(memberApiKey.getBindIp());
                }else {
                    findMemberApiKey.setBindIp(null);
                }

                memberApiKeyService.saveOrUpdate(findMemberApiKey);
                return success("修改成功");
            }else {
                return error("记录不存在");

            }
        }else {
            return error("记录不存在");
        }

    }


    /**
     * 删除API-key
     * @param authMember
     * @param id
     * @return
     */
    @PermissionOperation
    @RequestMapping(value = "api/del/{id}",method = RequestMethod.GET)
    public MessageResult updateApiKey(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, @PathVariable("id")Long id){
        AuthMember member = AuthMember.toAuthMember(authMember);
        log.info("------删除api-key：memberId={},id={}",member.getId(),id);
        MemberApiKey memberApiKey = memberApiKeyService.findByMemberIdAndId(member.getId(),id);
        if(memberApiKey!=null){
            memberApiKeyService.removeById(id);
        }else {
            return error("记录不存在");
        }

        return success("删除成功");
    }

}
