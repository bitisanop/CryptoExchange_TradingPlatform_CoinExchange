package com.bitisan.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.MemberScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.dto.MemberDTO;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.MemberWalletFeign;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.FileUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import static org.springframework.util.Assert.notNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 后台管理会员
 * @date 2019/12/25 16:50
 */
@RestController
@RequestMapping("member")
@Slf4j
public class MemberController extends BaseController {

    @Autowired
    private MemberFeign memberFeign;

    @Autowired
    private MemberWalletFeign memberWalletFeign;

    @Autowired
    private LocaleMessageSourceService messageSource;

    @PostMapping("all")
    public MessageResult all() {
        List<Member> all = memberFeign.findAllList();
        if (all != null && all.size() > 0) {
            return success(all);
        }
        return error(messageSource.getMessage("REQUEST_FAILED"));
    }

    @RequestMapping(value = "/detail")
    @PermissionOperation
    @Transactional(rollbackFor = Exception.class)
    public MessageResult detail(@RequestParam("id") Long id, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member checkMember = memberFeign.findMemberById(user.getId());
        if(!checkMember.getSuperPartner().equals("1")) {
            return error(messageSource.getMessage("NOT_AN_AGENT"));
        }

        Member member = memberFeign.findMemberById(id);
        notNull(member, "validate id!");
        List<MemberWallet> list = memberWalletFeign.findAllByMemberId(member.getId());
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setMember(member);
        memberDTO.setList(list);
        return success(memberDTO);
    }

    /**
     * 分页获取用户列表
     * @param screen
     * @return
     */
    @RequestMapping(value = "/page-query")
    @Transactional(rollbackFor = Exception.class)
    @PermissionOperation
    public MessageResult page(
            MemberScreen screen,
            @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        // 检查用户是否是代理商
        Member checkMember = memberFeign.findMemberById(user.getId());
        if(!checkMember.getSuperPartner().equals("1")) {
            return error(messageSource.getMessage("NOT_AN_AGENT"));
        }
        screen.setInviterId(checkMember.getId());
        Page<Member> all = memberFeign.findAll(screen, screen.getPageNo(),screen.getPageSize());
        return success(IPage2Page(all));
    }

    /**
     * 获取指定用户资产列表
     * @param memberId
     * @return
     */
    @RequestMapping(value = "/assets-list")
    @Transactional(rollbackFor = Exception.class)
    @PermissionOperation
    public MessageResult getUserAssets(
            @RequestHeader(SysConstant.SESSION_MEMBER) String authMember,
            Long memberId) {
        List<MemberWallet> list = memberWalletFeign.findAllByMemberId(memberId);
        return success(messageSource.getMessage("SUCCESS"), list);
    }

    @RequestMapping(value = "/alter-superpartner")
    @PermissionOperation
    @Transactional(rollbackFor = Exception.class)
    public MessageResult alterSuperPartner(
            @RequestHeader(SysConstant.SESSION_MEMBER) String authMember,
            @RequestParam("superPartner") String superPartner,
            @RequestParam("memberId") Long memberId) {
        // 对比等级，等级低的不能修改下面的用户为更高等级
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member currentUser = memberFeign.findMemberById(user.getId());
        if(superPartner.compareTo(currentUser.getSuperPartner()) <= 0) {
            return error(messageSource.getMessage("CANNOT_SET_HIGHER_LEVEL_THAN_YOURSELF"));
        }
        Member member = memberFeign.findMemberById(memberId);
        member.setSuperPartner(superPartner);
        memberFeign.save(member);
        return success(messageSource.getMessage("SUCCESS"));
    }

    @GetMapping("out-excel")
    public MessageResult outExcel(
            MemberScreen screen,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        List list = memberFeign.findAllWithCondition(screen);
        return new FileUtil().exportExcel(request, response, list, "member");
    }
}
