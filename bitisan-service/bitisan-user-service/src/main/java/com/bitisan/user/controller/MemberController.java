package com.bitisan.user.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Country;
import com.bitisan.user.entity.Member;
import com.bitisan.user.event.MemberEvent;
import com.bitisan.user.service.CountryService;
import com.bitisan.user.service.MemberService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.user.vo.LoginInfo;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会员用户 前端控制器
 * </p>
 *
 * @author markchao
 * @since 2021-06-14
 */
@Api(tags = "会员用户")
@RestController
@RequestMapping("/member")
public class MemberController extends BaseController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CountryService countryService;

    @Autowired
    private LocaleMessageSourceService messageSourceService;

    @Value("${person.promote.prefix:}")
    private String promotePrefix;

    @Autowired
    private MemberEvent memberEvent;

    /**
     * 获取用户信息
     * @param authMember
     * @return
     */
    @ApiOperation(value = "获取用户信息")
    @PermissionOperation
    @PostMapping("my-info")
    public MessageResult myInfo(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        //校验 签到活动 币种 会员 会员钱包
        AuthMember user = AuthMember.toAuthMember(authMember);
        Assert.notNull(user, messageSourceService.getMessage("RE_LOGIN"));
        Member member = memberService.getById(user.getId());
        Assert.notNull(member, messageSourceService.getMessage("RE_LOGIN"));
        String key = SysConstant.TOKEN_MEMBER+ member.getId();
        Object rToke = redisTemplate.boundValueOps(key).get();
        QueryWrapper<Country> cq = new QueryWrapper<>();
        cq.eq("zh_name",member.getLocal());
        Country country = countryService.getOne(cq);
        LoginInfo  loginInfo = LoginInfo.getLoginInfo(member,country, rToke.toString(), false, promotePrefix);
//        try {
//            memberEvent.onRegisterSuccess(member,"12321");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return success(loginInfo);
    }

    @ApiOperation(value = "获取推广等级")
    @PostMapping("promotion-rank")
    public MessageResult getPromotionRank() {

        return null;
    }

    @ApiOperation(value = "会员id列表映射")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "ids"),
    })
    @PostMapping(value = "mapByMemberIds")
    public Map<Long, Member> mapByMemberIds(@RequestParam("ids")List<Long> ids){
        if(ids!=null && ids.size()>0) {
            return memberService.mapByMemberIds(ids);
        }else {
            return new HashMap<>();
        }
    }



}

