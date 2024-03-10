package com.bitisan.admin.controller.p2p;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.Admin;
import com.bitisan.constant.CertifiedBusinessStatus;
import com.bitisan.constant.CommonStatus;
import com.bitisan.constant.SysConstant;
import com.bitisan.p2p.entity.BusinessAuthApply;
import com.bitisan.p2p.entity.BusinessAuthDeposit;
import com.bitisan.p2p.entity.OtcCoin;
import com.bitisan.p2p.feign.BusinessAuthFeign;
import com.bitisan.p2p.feign.OtcCoinFeign;
import com.bitisan.screen.ApplyScreen;
import com.bitisan.screen.DepositScreen;
import com.bitisan.screen.PageParam;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 商家认证可用保证金类型
 *
 * @author Bitisan  E-mail:xunibidev@gmail.com
 * @date 2019/5/5
 */
@RestController
@RequestMapping("business-auth")
@Slf4j
public class BusinessAuthController extends BaseAdminController {
    @Autowired
    private BusinessAuthFeign businessAuthService;
    @Autowired
    private OtcCoinFeign coinService;

    @RequiresPermissions("business:auth:deposit:page")
    @GetMapping("page")
    public MessageResult getAll(@RequestParam("pageNo")Integer pageNo,
                                @RequestParam("pageSize")Integer pageSize,
                                @RequestParam(value = "status",required = false) CommonStatus status) {
        DepositScreen screen = new DepositScreen();
        screen.setPageNo(pageNo);
        screen.setPageSize(pageSize);
        screen.setStatus(status);
        Page<BusinessAuthDeposit> depositPage = businessAuthService.findAllDeposit(screen);
        MessageResult result = MessageResult.success();
        result.setData(IPage2Page(depositPage));
        return result;
    }

    @RequiresPermissions("business:auth:deposit:create")
    @PostMapping("create")
    public MessageResult create(@SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin,
                                @RequestParam("amount") Double amount,
                                @RequestParam("coinUnit") String coinUnit) {
        OtcCoin coin = coinService.findByUnit(coinUnit);
        if (coin == null) {
            return error("validate coinUnit");
        }
        BusinessAuthDeposit businessAuthDeposit = new BusinessAuthDeposit();
        businessAuthDeposit.setAmount(new BigDecimal(amount));
        businessAuthDeposit.setCoinId(coin.getId()+"");
        businessAuthDeposit.setCreateTime(new Date());
        businessAuthDeposit.setAdminId(admin.getId());
        businessAuthDeposit.setStatus(CommonStatus.NORMAL);
        businessAuthService.add(businessAuthDeposit);
        return success();
    }

    @RequiresPermissions("business-auth:apply:detail")
    @PostMapping("apply/detail")
    public MessageResult detail(@RequestParam("id") Long id) {
        MessageResult result = businessAuthService.detail(id);
        return result;
    }

    @RequiresPermissions("business:auth:deposit:update")
    @PatchMapping("update")
    public MessageResult update(
            @RequestParam("id") Long id,
            @RequestParam("amount") Double amount,
            @RequestParam("status") CommonStatus status) {
        BusinessAuthDeposit oldData = businessAuthService.findDepositById(id);
        if (amount != null) {
            oldData.setAmount(new BigDecimal(amount));
        }
        if (status != null) {
            oldData.setStatus(status);
        }
        businessAuthService.updateDeposit(oldData);
        return success();
    }

    @PostMapping("apply/page-query")
    @RequiresPermissions("business-auth:apply:page-query")
    public MessageResult page(
            @RequestParam(value = "pageNo",defaultValue = "1")Integer pageNo,
            @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
            @RequestParam(value = "status", required = false) CertifiedBusinessStatus status,
            @RequestParam(value = "account", defaultValue = "") String account) {
        ApplyScreen screen = new ApplyScreen();
        screen.setPageNo(pageNo);
        screen.setPageSize(pageSize);
        screen.setAccount(account);
        screen.setStatus(status);
        Page<BusinessAuthApply> page = businessAuthService.pageApply(screen);
        return success(IPage2Page(page));

    }

    @PostMapping("get-search-status")
    public MessageResult getSearchStatus() {
        CertifiedBusinessStatus[] statuses = CertifiedBusinessStatus.values();
        List<Map> list = new ArrayList<>();
        for (CertifiedBusinessStatus status : statuses) {
            if (status == CertifiedBusinessStatus.NOT_CERTIFIED
                    || status.getCode() >= CertifiedBusinessStatus.DEPOSIT_LESS.getCode()) {
                continue;
            }
            Map map = new HashMap();
            map.put("name", status.getDescription());
            map.put("value", status.getCode());
            list.add(map);
        }
        return success(list);
    }
}
