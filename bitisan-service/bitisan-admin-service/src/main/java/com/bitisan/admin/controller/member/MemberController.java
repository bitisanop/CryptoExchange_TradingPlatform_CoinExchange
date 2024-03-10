package com.bitisan.admin.controller.member;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.*;
import com.bitisan.p2p.entity.BusinessAuthApply;
import com.bitisan.p2p.entity.BusinessAuthDeposit;
import com.bitisan.p2p.entity.DepositRecord;
import com.bitisan.p2p.feign.BusinessAuthFeign;
import com.bitisan.screen.MemberScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.dto.MemberDTO;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.MemberWalletFeign;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.bitisan.constant.CertifiedBusinessStatus.*;
import static com.bitisan.constant.MemberLevelEnum.IDENTIFICATION;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 后台管理会员
 * @date 2019/12/25 16:50
 */
@RestController
@RequestMapping("/member")
@Slf4j
public class MemberController extends BaseAdminController {

    @Autowired
    private MemberFeign memberService;
    @Autowired
    private MemberWalletFeign memberWalletService;
    @Autowired
    private BusinessAuthFeign businessAuthFeign;
    @Autowired
    private CoinFeign coinFeign;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("member:page-query")
    @PostMapping("page-query")
    @ResponseBody
    @AccessLog(module = AdminModule.MEMBER, operation = "分页查找会员Member")
    public MessageResult page(MemberScreen screen) {
        Page<Member> all = memberService.findAll(screen,screen.getPageNo(),screen.getPageSize());

        return success(IPage2Page(all));
    }

    @RequiresPermissions("member:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.MEMBER, operation = "会员Member详情")
    public MessageResult detail(@RequestParam("id") Long id) {
        Member member = memberService.findMemberById(id);
        notNull(member, "validate id!");
        List<MemberWallet> list = memberWalletService.findAllByMemberId(member.getId());
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setMember(member);
        memberDTO.setList(list);
        return success(memberDTO);
    }

    @RequiresPermissions("member:all")
    @PostMapping("all")
    @AccessLog(module = AdminModule.MEMBER, operation = "所有会员Member")
    public MessageResult all() {
        List<Member> all = memberService.findAllList();
        if (all != null && all.size() > 0) {
            return success(all);
        }
        return error(messageSource.getMessage("REQUEST_FAILED"));
    }

    @RequiresPermissions("member:delete")
    @PostMapping("delete")
    @AccessLog(module = AdminModule.MEMBER, operation = "删除会员Member")
    public MessageResult delete(@RequestParam(value = "id") Long id) {
        Member member = memberService.findMemberById(id);
        notNull(member, "validate id!");
        member.setStatus(CommonStatus.ILLEGAL.getCode());// 修改状态非法
        memberService.updateMemberById(member);
        return success();
    }

    @RequiresPermissions("member:update")
    @PostMapping(value = "update")
    @AccessLog(module = AdminModule.MEMBER, operation = "更新会员Member")
    public MessageResult update(Member member) {
        if (member.getId() == null) {
            return error(messageSource.getMessage("ID_REQUIRED"));
        }
        Member one = memberService.findMemberById(member.getId());
        if (one == null) {
            return error(messageSource.getMessage("USER_NOT_FOUND"));
        }
        if (StringUtils.isNotBlank(member.getUsername())) {
            one.setUsername(member.getUsername());
        }
        if (StringUtils.isNotBlank(member.getPassword())) {
            one.setPassword(member.getPassword());
        }
        if (StringUtils.isNotBlank(member.getRealName())) {
            one.setRealName(member.getRealName());
        }
        memberService.updateMemberById(one);
        return success(one);
    }

    @RequiresPermissions("member:audit-business")
    @PatchMapping("{id}/audit-business")
    @AccessLog(module = AdminModule.MEMBER, operation = "会员Member认证商家")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult auditBusiness(
            @PathVariable("id") Long id,
            @RequestParam("status") CertifiedBusinessStatus status,
            @RequestParam("detail") String detail) {
        Member member = memberService.findMemberById(id);
        notNull(member, "validate id!");
        //确认是审核中
        isTrue(member.getCertifiedBusinessStatus() == AUDITING, "validate member certifiedBusinessStatus!");
        //确认传入certifiedBusinessStatus值正确，审核通过或者不通过
        isTrue(status == VERIFIED || status == FAILED, "validate certifiedBusinessStatus!");
        //member.setCertifiedBusinessApplyTime(new Date());//time
        List<BusinessAuthApply> businessAuthApplyList = businessAuthFeign.findByMemberAndCertifiedBusinessStatus(member.getId(), AUDITING);
        if (status == VERIFIED) {
            //通过
            member.setCertifiedBusinessStatus(VERIFIED);//已认证
            member.setMemberLevel(IDENTIFICATION.getCode());//认证商家
            if (businessAuthApplyList != null && businessAuthApplyList.size() > 0) {
                BusinessAuthApply businessAuthApply = businessAuthApplyList.get(0);
                businessAuthApply.setCertifiedBusinessStatus(VERIFIED);
                //如果申请的时候选择了保证金策略
                if (businessAuthApply.getBusinessAuthDepositId() != null) {
                    BusinessAuthDeposit deposit = businessAuthFeign.findDepositById(businessAuthApply.getBusinessAuthDepositId());
                    deposit.setCoin(coinFeign.findByCoinId(deposit.getCoinId()));
                    //扣除保证金
                    MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(deposit.getCoin().getUnit(), member.getId());
                    memberWallet.setFrozenBalance(memberWallet.getFrozenBalance().subtract(businessAuthApply.getAmount()));
                    DepositRecord depositRecord = new DepositRecord();
                    depositRecord.setId(UUID.randomUUID().toString());
                    depositRecord.setAmount(businessAuthApply.getAmount());
                    depositRecord.setCoinId(deposit.getCoinId());
                    depositRecord.setMemberId(member.getId());
                    depositRecord.setStatus(DepositStatusEnum.PAY);
                    businessAuthFeign.saveDepositRecord(depositRecord);
                    businessAuthApply.setDepositRecordId(depositRecord.getId());
                }
            }
        } else {
            //不通过
            member.setCertifiedBusinessStatus(FAILED);//认证失败
            if (businessAuthApplyList != null && businessAuthApplyList.size() > 0) {
                BusinessAuthApply businessAuthApply = businessAuthApplyList.get(0);
                businessAuthApply.setCertifiedBusinessStatus(FAILED);
                businessAuthApply.setDetail(detail);
                //申请商家认证时冻结的金额退回
                if (businessAuthApply.getBusinessAuthDepositId() != null) {
                    BusinessAuthDeposit deposit = businessAuthFeign.findDepositById(businessAuthApply.getBusinessAuthDepositId());
                    deposit.setCoin(coinFeign.findByCoinId(deposit.getCoinId()));
                    MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(deposit.getCoin().getUnit(), member.getId());
                    memberWallet.setFrozenBalance(memberWallet.getFrozenBalance().subtract(businessAuthApply.getAmount()));
                    memberWallet.setBalance(memberWallet.getBalance().add(businessAuthApply.getAmount()));
                }
            }
        }
        member.setCertifiedBusinessCheckTime(new Date());
        memberService.save(member);
        return success();
    }

    @RequiresPermissions("member:alter-publish-advertisement-status")
    @PostMapping("alter-publish-advertisement-status")
    @AccessLog(module = AdminModule.SYSTEM, operation = "禁用/解禁发布广告")
    public MessageResult publishAdvertise(@RequestParam("memberId") Long memberId,
                                          @RequestParam("status") BooleanEnum status) {
        Member member = memberService.findMemberById(memberId);
        if (member.getCertifiedBusinessStatus() != CertifiedBusinessStatus.VERIFIED) {
            return error(messageSource.getMessage("PLEASE_CERTIFY_AS_MERCHANT"));
        }
        Assert.notNull(member, messageSource.getMessage("PLAYER_NOT_FOUND"));
        member.setPublishAdvertise(status.getCode());
        memberService.save(member);
        return success(status == BooleanEnum.IS_FALSE ? messageSource.getMessage("PROHIBIT_ADVERTISEMENT_PUBLICATION_SUCCESS") : messageSource.getMessage("PROHIBIT_REMOVAL_SUCCESS"));
    }

    @RequiresPermissions("member:alter-status")
    @PostMapping("alter-status")
    @AccessLog(module = AdminModule.SYSTEM, operation = "禁用/解禁会员账号")
    public MessageResult ban(@RequestParam("status") CommonStatus status,
                             @RequestParam("memberId") Long memberId) {
        Member member = memberService.findMemberById(memberId);
        member.setStatus(status.getCode());
        memberService.save(member);
        log.info(">>>>>>>>>>>>>>>开始初始化BZB数据>>>>>>>");
        return success(messageSource.getMessage("SUCCESS"));
    }

    @RequiresPermissions("member:alter-transaction-status")
    @PostMapping("alter-transaction-status")
    @AccessLog(module = AdminModule.SYSTEM, operation = "禁用/解禁会员账号")
    public MessageResult alterTransactionStatus(
            @RequestParam("status") BooleanEnum status,
            @RequestParam("memberId") Long memberId) {
        Member member = memberService.findMemberById(memberId);
        member.setTransactionStatus(status.getCode());
        memberService.save(member);
        return success(messageSource.getMessage("SUCCESS"));
    }

    /**
     * 更改用户等级（合伙人/代理商等）
     * @param superPartner
     * @param memberId
     * @return
     */
    @RequiresPermissions("member:alter-member-superpartner")
    @PostMapping("alter-member-superpartner")
    @AccessLog(module = AdminModule.SYSTEM, operation = "修改用户等级")
    public MessageResult alterSuperPartner(
            @RequestParam("superPartner") String superPartner,
            @RequestParam("memberId") Long memberId) {
        Member member = memberService.findMemberById(memberId);
        member.setSuperPartner(superPartner);
        memberService.save(member);
        return success(messageSource.getMessage("SUCCESS"));
    }

    /**
     * 查询代理商列表
     * @param
     * @param screen
     * @return
     */
    @RequiresPermissions("member:page-query-super")
    @PostMapping("page-query-super")
    @ResponseBody
    @AccessLog(module = AdminModule.MEMBER, operation = "分页查找会员Member")
    public MessageResult pageSuperPartner(MemberScreen screen) {
        screen.setSuperPartner("1"); // 默认选择代理商
        Page<Member> all = memberService.findAll(screen,screen.getPageNo(),screen.getPageSize());
        return success(IPage2Page(all));
    }

    /**
     * 查询代理商邀请用户列表
     * @param
     * @param screen
     * @param userId
     * @return
     */
    @RequiresPermissions("member:supermember-page-query")
    @PostMapping(value = "/supermember-page-query")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public MessageResult pageSuperMember(
            MemberScreen screen,
            Long userId) {
        // 检查用户是否是代理商
        Member checkMember = memberService.findMemberById(userId);
        if(!checkMember.getSuperPartner().equals("1")) {
            return error(messageSource.getMessage("NOT_AN_AGENT"));
        }
        screen.setInviterId(userId);
        Page<Member> all = memberService.findAll(screen, screen.getPageNo(),screen.getPageSize());
        return success(IPage2Page(all));
    }

    @RequiresPermissions("member:set-inviter")
    @PostMapping("setInviter")
    @AccessLog(module = AdminModule.MEMBER, operation = "设置邀请人")
    public MessageResult setInviter(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "inviterId") Long inviterId) throws Exception {
        memberService.setMemberInviter(id,inviterId);
        return success();
    }

    @RequiresPermissions("member:reset-pwd")
    @PostMapping("resetPwd")
    @AccessLog(module = AdminModule.SYSTEM, operation = "重置会员密码")
    public MessageResult resetPwd(@RequestParam("memberId") Long memberId) {
        log.info(">>>>>>>>>>>>>>>重置密码>>>>>>>");
        String pwd =  memberService.resetPwd(memberId);
        return success(messageSource.getMessage("CHANGE_PWD_FIELD_NEWPWD")+":"+pwd);
    }

    @RequiresPermissions("member:create-user")
    @PostMapping("createUser")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建用户")
    public MessageResult createUser(
            @RequestParam("email") String email,@RequestParam("inviteCode") String inviteCode) {
        log.info(">>>>>>>>>>>>>>>创建用户>>>>>>>");
        MessageResult result = memberService.createUser(email,inviteCode);
        return result;
    }
}
