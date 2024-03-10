package com.bitisan.agent.controller;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.MemberDepositScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberDeposit;
import com.bitisan.user.feign.MemberDepositFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("deposit")
@Slf4j
public class MemberDepositRecordController extends BaseController {
    @Autowired
    private MemberDepositFeign memberDepositFeign;
    @Autowired
    private LocaleMessageSourceService messageSource;
    @Autowired
    private MemberFeign memberFeign;
    /**
     * 充币记录
     *
     * @param screen
     * @return
     */

    @RequestMapping(value = "/page-query")
    @Transactional(rollbackFor = Exception.class)
    @PermissionOperation
    public MessageResult page(MemberDepositScreen screen, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member checkMember = memberFeign.findMemberById(user.getId());
        if(!checkMember.getSuperPartner().equals("1")) {
            return error(messageSource.getMessage("NOT_AN_AGENT"));
        }
        screen.setInviterId(checkMember.getId());
        Page<MemberDeposit> page = memberDepositFeign.findAll(screen);
        return success(messageSource.getMessage("SUCCESS"), IPage2Page(page));
    }
}
