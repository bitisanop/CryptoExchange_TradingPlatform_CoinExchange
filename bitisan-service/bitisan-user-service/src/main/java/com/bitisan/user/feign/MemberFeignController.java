package com.bitisan.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.constant.MemberLevelEnum;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.InviteManagementScreen;
import com.bitisan.screen.MemberScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Member;
import com.bitisan.user.event.MemberEvent;
import com.bitisan.user.service.MemberApplicationService;
import com.bitisan.user.service.MemberService;
import com.bitisan.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notNull;
import static org.springframework.util.Assert.isTrue;

/*****
 * @Author:
 * @Description:
 ****/
@RestController
@RequestMapping("/memberFeign")
public class MemberFeignController extends BaseController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberEvent memberEvent;
    @Autowired
    private MemberApplicationService memberApplicationService;
    @Autowired
    private IdWorkByTwitter idWorkByTwitter;

    @Resource
    private LocaleMessageSourceService localeMessageSourceService;

    @Value("${member.duf.pwd}")
    private String dufPwd;

//    @ApiOperation(value = "根据会员id查找会员")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "id"),
//    })
    @GetMapping("findMemberById")
    public Member findMemberById(@RequestParam("id") Long id){
        Member member = memberService.getById(id);
        return member;

    }

//    @ApiOperation(value = "更新会员")
    @PostMapping("updateMemberById")
    public MessageResult updateMemberById(@RequestBody Member member){
        boolean update = memberService.updateById(member);
        if(update){
            return success();
        }else {
            return error("更新失败");
        }
    }

//    @ApiOperation(value = "根据会员id列表查询邀请者")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "upper", value = "会员id列表"),
//    })
    @PostMapping("findSuperPartnerMembersByIds")
    public MessageResult findSuperPartnerMembersByIds(@RequestParam("upper") String upper){
        List<Member> list= memberService.findSuperPartnerMembersByIds(upper);
        return success(list);
    }

//    @ApiOperation(value = "获取全部")
    @PostMapping("findAll")
    public Page<Member> findAll(
            @RequestBody MemberScreen screen,
            @RequestParam("pageNo")Integer pageNo,
            @RequestParam("pageSize")Integer pageSize){
        Page<Member> page= memberService.findAll(screen,pageNo,pageSize);
        return page;
    }

//    @ApiOperation(value = "获取全部列表")
    @GetMapping("findAllList")
    public List<Member> findAllList(){
        List<Member> list= memberService.list();
        return list;
    }

//    @ApiOperation(value = "根据条件查找会员")
    @GetMapping(value = "findAllWithCondition")
    public List<Member> findAllWithCondition(@RequestBody MemberScreen screen) {
        return memberService.findAllWithCondition(screen);
    }

    /**
     * 邀请管理默认查询所有的用户
     *
     * @return
     */
//    @ApiOperation(value = "邀请管理默认查询所有的用户")
    @RequestMapping(value = "look")
    public Page<Member> lookAll(@RequestBody InviteManagementScreen screen) {
        Page<Member> page = memberService.lookAll(screen);
        return page;
    }

    /**
     * 根据id查询1级2级用户
     * @return
     */
    @RequestMapping(value = "queryFirstAndSecondById")
    public Page<Member> queryFirstAndSecondById(@RequestBody InviteManagementScreen screen){
        Page<Member> page = memberService.queryFirstAndSecondById(screen);
        return page;
    }

    /**
     * 通过邀请id 获取用户
     * @return
     */
    @RequestMapping(value = "findPromotionMember")
    public List<Member> findPromotionMember(@RequestParam("id") Long id){
        List<Member> list = memberService.findPromotionMember(id);
        return list;
    }

    @RequestMapping(value = "findByPhone")
    public Member findByPhone(@RequestParam("phone")String phone){
        return memberService.findByPhone(phone);
    }

    @RequestMapping(value = "findByEmail")
    public Member findByEmail(@RequestParam("email")String email){
        return memberService.findByEmail(email);
    }

//    @ApiOperation(value = "保存")
    @PostMapping(value = "save")
    MessageResult save(@RequestBody Member member){
        memberService.updateById(member);
        return MessageResult.success();
    }

//    @ApiOperation(value = "根据账户查找会员id")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "account", value = "账户"),
//    })
    @PostMapping(value = "findMemberIdsByAccount")
    public List<Long> findMemberIdsByAccount(@RequestParam("account") String account){
        return memberService.findMemberIdsByAccount(account);
    }

//    @ApiOperation(value = "根据未认证账户查找会员id")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "account", value = "账户"),
//    })
    @PostMapping(value = "findMemberIdsByAccountAndNotCertified")
    public List<Long> findMemberIdsByAccountAndNotCertified(@RequestParam("account") String account){
        return memberService.findMemberIdsByAccountAndNotCertified(account);
    }

//    @ApiOperation(value = "会员id列表映射")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "ids", value = "ids"),
//    })
    @PostMapping(value = "mapByMemberIds")
    public Map<Long, Member> mapByMemberIds(@RequestParam("ids")List<Long> ids){
        if(ids!=null && ids.size()>0) {
            return memberService.mapByMemberIds(ids);
        }else {
            return new HashMap<>();
        }
    }

//    @ApiOperation(value = "根据用户名查找会员")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "name", value = "用户名"),
//    })
    @PostMapping(value = "findByUsername")
    public Member findByUsername(@RequestParam("name")String name){
        return memberService.findByUsername(name);
    }

//    @PostMapping(value = "findByInviterId")
//    public List<Member> findByInviterId(@RequestParam("id") Long id){
//        return memberService.findPromotionMember(id);
//    }


//    @ApiOperation(value = "登录")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "name", value = "用户名"),
//            @ApiImplicitParam(name = "password", value = "密码")
//    })
    @PostMapping(value = "login")
    public Member login(@RequestParam("name")String name, @RequestParam("password")String password) throws Exception {
        return memberService.login(name, password);
    }

    @PostMapping(value = "countAuditing")
    public Integer countAuditing(){
        return memberApplicationService.countAuditing();
    }


    @PostMapping(value = "getRegistrationNum")
    public int getRegistrationNum(@RequestParam("dateStr")String dateStr){
        return this.memberService.getRegistrationNum(dateStr);
    }

    @PostMapping(value = "getBussinessNum")
    public int getBussinessNum(@RequestParam("dateStr")String dateStr) {
        return this.memberService.getBussinessNum(dateStr);
    }

    @PostMapping(value = "getApplicationNum")
    public int getApplicationNum(@RequestParam("dateStr")String dateStr) {
        return this.memberService.getApplicationNum(dateStr);
    }
    @PostMapping(value = "getStartRegistrationDate")
    public Date getStartRegistrationDate(){
        return this.memberService.getStartRegistrationDate();
    }


    @PostMapping(value = "setMemberInviter")
    MessageResult setMemberInviter(@RequestParam("id") Long id, @RequestParam("inviterId") Long inviterId) throws InterruptedException {
        Member member = memberService.getById(id);
        notNull(member, "validate id!");
        Member pMember = memberService.getById(inviterId);
        notNull(member, "validate id!");
        if(member.getInviterId()!=null){
            return error("已存在邀请人");
        }
        memberEvent.setMemberInviter(member,pMember);
        return success();

    }
    @PostMapping(value = "resetPwd")
    String resetPwd(@RequestParam("memberId")Long memberId) throws Exception {
        Member member = memberService.getById(memberId);
        notNull(member, "validate id!");
        //生成密码
        String password = MD5.md5(dufPwd + member.getSalt()).toLowerCase();
        member.setPassword(password);
        memberService.updateById(member);
        return dufPwd;
    }

    @PostMapping(value = "createUser")
    MessageResult createUser(@RequestParam("email")String email,
                             @RequestParam("inviteCode")String inviteCode) throws Exception {

        isTrue(!StringUtils.isEmpty(email), localeMessageSourceService.getMessage("MISSING_EMAIL"));
        isTrue(!memberService.emailIsExist(email), localeMessageSourceService.getMessage("EMAIL_ALREADY_BOUND"));


        //不可重复随机数
        String loginNo = String.valueOf(idWorkByTwitter.nextId());
        //盐
        String credentialsSalt =MD5.md5(loginNo);
        //生成密码
        String password = MD5.md5(dufPwd + credentialsSalt).toLowerCase();
        Member member = new Member();

        member.setMemberLevel(MemberLevelEnum.GENERAL.getCode());
        member.setCountry("美国");
        member.setLocal("美国");
        member.setUsername(email);
        member.setPassword(password);
        member.setEmail(email);
        member.setSalt(credentialsSalt);
        member.setAvatar("https://bizzanex.oss-cn-hangzhou.aliyuncs.com/defaultavatar.png"); // 默认用户头像
        memberService.save(member);

        if (member.getId() != null) {
            // Member为@entity注解类，与数据库直接映射，因此，此处setPromotionCode会直接同步到数据库
            member.setPromotionCode(GeneratorUtil.getPromotionCode(member.getId()));
            memberEvent.onRegisterSuccess(member, inviteCode);
            return success(localeMessageSourceService.getMessage("REGISTRATION_SUCCESS"));
        } else {
            return error(localeMessageSourceService.getMessage("REGISTRATION_FAILED"));
        }
    }

}
