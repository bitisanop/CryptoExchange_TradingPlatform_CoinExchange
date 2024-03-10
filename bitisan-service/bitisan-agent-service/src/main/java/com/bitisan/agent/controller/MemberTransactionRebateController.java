package com.bitisan.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.ContractRewardRecordScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.swap.entity.ContractRewardRecord;
import com.bitisan.swap.feign.ContractRewardRecordFeign;
import com.bitisan.user.entity.Member;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import static org.springframework.util.Assert.notNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sulinxin
 */
@RestController
@RequestMapping("transactionRebates")
@Slf4j
public class MemberTransactionRebateController extends BaseController {

    @Autowired
    private MemberFeign memberFeign;
    @Autowired
    private ContractRewardRecordFeign contractRewardRecordFeign;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequestMapping(value = "/page-query")
    @Transactional(rollbackFor = Exception.class)
    @PermissionOperation
    public MessageResult pageQuery(
            ContractRewardRecordScreen screen,
            @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member checkMember = memberFeign.findMemberById(user.getId());
        if(!checkMember.getSuperPartner().equals("1")) {
            return error(messageSource.getMessage("NOT_AN_AGENT"));
        }

        if (screen.getDirection() == null && screen.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            screen.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("createTime");
            screen.setProperty(property);
        }

        Page<ContractRewardRecord> results = contractRewardRecordFeign.findAll(screen);

        return success(IPage2Page(results));
    }
}
