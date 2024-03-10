package com.bitisan.admin.controller.member;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.screen.MemberApplicationScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.sms.SMSProvider;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberApplication;
import com.bitisan.user.feign.MemberApplicationFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.vo.MemberApplicationVo;
import com.bitisan.util.MessageResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.baomidou.mybatisplus.core.toolkit.Assert.notNull;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 实名审核单
 * @date 2019/12/26 15:05
 */
@RestController
@RequestMapping("member/member-application")
public class MemberApplicationController extends BaseAdminController {
	@Autowired
    private SMSProvider smsProvider;
    @Autowired
    private MemberApplicationFeign memberApplicationFeign;
    @Autowired
    private MemberFeign memberFeign;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("member:member-application:all")
    @PostMapping("all")
    @AccessLog(module = AdminModule.MEMBER, operation = "所有会员MemberApplication认证信息")
    public MessageResult all(MemberApplicationScreen screen) {
        Page<MemberApplicationVo> all = memberApplicationFeign.findAll(screen);
        return success(IPage2Page(all));

    }

    @RequiresPermissions("member:member-application:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.MEMBER, operation = "会员MemberApplication认证信息详情")
    public MessageResult detail(@RequestParam("id") Long id) {
        MemberApplication memberApplication = memberApplicationFeign.findById(id);
        notNull(memberApplication, "validate id!");
        return success(memberApplication);
    }

    @RequiresPermissions("member:member-application:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.MEMBER, operation = "分页查找会员MemberApplication认证信息")
    public MessageResult queryPage(MemberApplicationScreen screen) {
        Page<MemberApplicationVo> all = memberApplicationFeign.findAll(screen);
        return success(IPage2Page(all));
    }

    @RequiresPermissions("member:member-application:pass")
    @PatchMapping("{id}/pass")
    @AccessLog(module = AdminModule.MEMBER, operation = "会员MemberApplication认证通过审核")
    public MessageResult pass(@PathVariable("id") Long id) {
        //校验
        MemberApplication application = memberApplicationFeign.findById(id);
        notNull(application, "validate id!");
        //业务
        memberApplicationFeign.auditPass(application);
        // 发送通知
        try {
            Member member = memberFeign.findMemberById(application.getMemberId());
            smsProvider.sendCustomMessage(member.getMobilePhone(), messageSource.getMessage("CONGRATULATIONS_AUTH_APPROVED"));
		} catch (Exception e) {
			return error(e.getMessage());
		}
        //返回
        return success();
    }

    @RequiresPermissions("member:member-application:no-pass")
    @PatchMapping("{id}/no-pass")
    @AccessLog(module = AdminModule.MEMBER, operation = "会员MemberApplication认证不通过审核")
    public MessageResult noPass(
            @PathVariable("id") Long id,
            @RequestParam(value = "rejectReason", required = false) String rejectReason) {
        //校验
        MemberApplication application = memberApplicationFeign.findById(id);
        notNull(application, "validate id!");
        //业务
        application.setRejectReason(rejectReason);//拒绝原因
        memberApplicationFeign.auditNotPass(application);

        try {
            Member member = memberFeign.findMemberById(application.getMemberId());
			smsProvider.sendCustomMessage(member.getMobilePhone(), messageSource.getMessage("AUTH_REJECTED_PLEASE_REAPPLY"));
		} catch (Exception e) {
			return error(e.getMessage());
		}
        //返回
        return success();
    }
}
