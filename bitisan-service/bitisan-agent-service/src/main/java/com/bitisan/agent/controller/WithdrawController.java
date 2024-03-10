package com.bitisan.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.WithdrawScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.Withdraw;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.WithdrawFeign;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.Assert.notNull;

@RestController
@RequestMapping("withdraw")
@Slf4j
public class WithdrawController extends BaseController {
    @Autowired
    private WithdrawFeign withdrawFeign;

    @Autowired
    private LocaleMessageSourceService messageSource;

    @Autowired
    private MemberFeign memberFeign;

    @PermissionOperation
    @RequestMapping(value = "/page-query")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult pageQuery(
            WithdrawScreen screen,
            @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member checkMember = memberFeign.findMemberById(user.getId());
        if(!checkMember.getSuperPartner().equals("1")) {
            return error(messageSource.getMessage("NOT_AN_AGENT"));
        }

        Page<Withdraw> pageListMapResult = withdrawFeign.joinFind(screen);
        return success(IPage2Page(pageListMapResult));
    }

    @GetMapping("/{id}")
    public MessageResult detail(@PathVariable("id") Long id) {
        Withdraw withdraw = withdrawFeign.findOne(id);
        notNull(withdraw, messageSource.getMessage("NO_DATA"));
        return success(withdraw);
    }
}
