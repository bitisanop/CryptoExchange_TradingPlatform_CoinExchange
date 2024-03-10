package com.bitisan.admin.controller.option;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.option.entity.ContractOption;
import com.bitisan.option.feign.ContractOptionFeign;
import com.bitisan.screen.ContractOptionScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/option")
@Slf4j
public class ContractOptionController extends BaseAdminController {
    @Autowired
    private ContractOptionFeign contractOptionService;

    @Autowired
    private LocaleMessageSourceService messageSource;

    /**
     * 查询
     * @param screen
     * @return
     */
    @RequiresPermissions("option:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约交易对 列表")
    public MessageResult detail(
            ContractOptionScreen screen) {
        //获取查询条件
        Page<ContractOption> all = contractOptionService.findAll(screen);
        return success(IPage2Page(all));
    }


    /**
     * 修改预设价格
     * @param presetPrice 预设价格
     * @return
     */
    @RequiresPermissions("option:alter")
    @PostMapping("alter")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约 修改预设价格")
    public MessageResult alter(
            @RequestParam(value = "id",required = true) Long id,
            @RequestParam(value = "presetPrice", required = true) BigDecimal presetPrice// 允许投注数量
    ) {
        ContractOption option = contractOptionService.findOne(id);
        if(option == null) {
            return error(messageSource.getMessage("OPTIONS_CONTRACT") + id + messageSource.getMessage("NOT_FOUND"));
        }
        if(presetPrice != null) option.setPresetPrice(presetPrice);
        contractOptionService.alert(option);
        return success(messageSource.getMessage("SAVE_SUCCESS"));
    }
}
