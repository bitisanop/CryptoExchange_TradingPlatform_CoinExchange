package com.bitisan.agent.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.MemberTransactionScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberTransaction;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.MemberTransactionFeign;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.user.vo.MemberTransactionVO;
import com.bitisan.util.DateUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.Assert.notNull;

import javax.persistence.EntityManager;
import java.util.ArrayList;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 交易记录
 * @date 2019/1/17 17:07
 */
@RestController
@RequestMapping("transactions")
@Slf4j
public class MemberTransactionController extends BaseController {
    @Autowired
    private MemberFeign memberFeign;
    @Autowired
    private MemberTransactionFeign memberTransactionFeign;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequestMapping(value = "/detail")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult detail(@RequestParam(value = "id") Long id) {
        MemberTransaction memberTransaction = memberTransactionFeign.findOne(id);
        notNull(memberTransaction, "validate id!");
        return success(memberTransaction);
    }

    @RequestMapping(value = "/page-query")
    @Transactional(rollbackFor = Exception.class)
    @PermissionOperation
    public MessageResult pageQuery(
            MemberTransactionScreen screen,
            @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member checkMember = memberFeign.findMemberById(user.getId());
        if(!checkMember.getSuperPartner().equals("1")) {
            return error(messageSource.getMessage("NOT_AN_AGENT"));
        }
        Page<MemberTransactionVO> results = memberTransactionFeign.joinFind(screen);

        return success(IPage2Page(results));
    }
}
