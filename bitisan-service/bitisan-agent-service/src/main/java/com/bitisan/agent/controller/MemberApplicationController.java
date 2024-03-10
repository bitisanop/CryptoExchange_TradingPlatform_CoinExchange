package com.bitisan.agent.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.MemberApplicationScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.sms.SMSProvider;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberApplication;
import com.bitisan.user.feign.MemberApplicationFeign;

import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.user.vo.MemberApplicationVo;
import com.bitisan.util.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.Assert.notNull;

import java.util.List;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 实名审核单
 * @date 2019/12/26 15:05
 */
@RestController
@RequestMapping("member/member-application")
public class MemberApplicationController extends BaseController {
	@Autowired
    private SMSProvider smsProvider;
    @Autowired
    private MemberApplicationFeign memberApplicationFeign;
    @Autowired
    private LocaleMessageSourceService messageSource;
    @Autowired
    private MemberFeign memberFeign;

    @PostMapping("all")
    public MessageResult all() {
        List<MemberApplication> all = memberApplicationFeign.fetch();
        if (all != null && all.size() > 0) {
            return success(all);
        }
        return error(messageSource.getMessage("NO_DATA"));
    }

    @PostMapping("detail")
    public MessageResult detail(@RequestParam("id") Long id) {
        MemberApplication memberApplication = memberApplicationFeign.findById(id);
        notNull(memberApplication, "validate id!");
        return success(memberApplication);
    }

    /**
     * -----------------------------------------------------
     * @param screen
     * @param authMember
     * @return
     */
    @PostMapping("page-query")
    @PermissionOperation
    public MessageResult queryPage(MemberApplicationScreen screen,
                                   @RequestHeader(SysConstant.SESSION_MEMBER) String authMember
    ) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member checkMember = memberFeign.findMemberById(user.getId());
        if(!checkMember.getSuperPartner().equals("1")) {
            return error(messageSource.getMessage("NOT_AN_AGENT"));
        }
        screen.setInviterId(checkMember.getId());
        Page<MemberApplicationVo> all = memberApplicationFeign.findAll(screen);
        return success(IPage2Page(all));
    }

    /**
     * --------------------------------------------------------------------
     * @param id
     * @return
     */

    @PatchMapping("{id}/pass")
    public MessageResult pass(@PathVariable("id") Long id) {
        //校验
        MemberApplication application = memberApplicationFeign.findById(id);
        notNull(application, "validate id!");
        //业务
        memberApplicationFeign.auditPass(application);
        // 发送通知
        try {
            Member memberById = memberFeign.findMemberById(application.getMemberId());
            smsProvider.sendCustomMessage(memberById.getMobilePhone(), messageSource.getMessage("CONGRATULATIONS_AUTH_APPROVED"));
		} catch (Exception e) {
			return error(e.getMessage());
		}
        //返回
        return success();
    }

    @PatchMapping("{id}/no-pass")
    public MessageResult noPass(
            @PathVariable("id") Long id,
            @RequestParam(value = "rejectReason", required = false) String rejectReason) {
        //校验
        MemberApplication application = memberApplicationFeign.findById(id);
        notNull(application, "validate id!");
        //业务
//        application.setRejectReason(rejectReason);//拒绝原因
        memberApplicationFeign.auditNotPass(application);

        try {
            Member memberById = memberFeign.findMemberById(application.getMemberId());
			smsProvider.sendCustomMessage(memberById.getMobilePhone(), messageSource.getMessage("AUTH_REJECTED_PLEASE_REAPPLY"));
		} catch (Exception e) {
			return error(e.getMessage());
		}
        //返回
        return success();
    }

    private void sendMsg() {

    }
}
