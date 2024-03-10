package com.bitisan.admin.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.screen.MemberTransactionScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.feign.MemberTransactionFeign;
import com.bitisan.user.vo.MemberTransactionVO;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 交易记录
 * @date 2019/1/17 17:07
 */
@RestController
@Slf4j
@RequestMapping("/finance/member-transaction")
public class MemberTransactionController extends BaseAdminController {

    @Autowired
    private LocaleMessageSourceService messageSource;

    @Autowired
    private MemberTransactionFeign memberTransactionService;


    @RequiresPermissions(value = {"finance:member-transaction:page-query", "finance:member-transaction:page-query:recharge",
            "finance:member-transaction:page-query:check", "finance:member-transaction:page-query:fee"}, logical = Logical.OR)
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.FINANCE, operation = "分页查找交易记录MemberTransaction")
    public MessageResult pageQuery(
            MemberTransactionScreen screen) {
        Page<MemberTransactionVO> results = memberTransactionService.joinFind(screen);
        return success(IPage2Page(results));
    }


}
