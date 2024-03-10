package com.bitisan.admin.controller.system;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.screen.PageParam;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Automainconfig;
import com.bitisan.user.entity.Coinprotocol;
import com.bitisan.user.feign.CoinprotocolFeign;
import com.bitisan.util.BindingResultUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 币种协议管理
 */
@Slf4j
@RestController
@RequestMapping("/system/coinprotocol")
public class CoinProtocolController extends BaseAdminController {

    @Autowired
    private CoinprotocolFeign coinprotocolService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("system:coinprotocol:page-query")
    @PostMapping("/page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "获取所有协议列表")
    public MessageResult pageQuery(PageParam pageParam) {

        Page<Coinprotocol> pageResult = coinprotocolService.findAll(pageParam.getPageNo(),pageParam.getPageSize());
        return success(IPage2Page(pageResult));

    }

    @RequiresPermissions("system:coinprotocol:merge")
    @PostMapping("/merge")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建/修改协议")
    public MessageResult merge(@Valid Coinprotocol coinprotocol, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }

        // 查询是否存在
        Coinprotocol one = coinprotocolService.findByProtocol(coinprotocol.getProtocol());
        if (coinprotocol.getId() != null) {
            if (one != null && !one.getId().equals(coinprotocol.getId())) {
                result = error(messageSource.getMessage("CURRENT_PROTOCOL_ALREADY_EXISTS"));
                return result;
            }
        } else if (one != null) {
            result = error(messageSource.getMessage("CURRENT_PROTOCOL_ALREADY_EXISTS"));
            return result;
        }

        // 删除redis缓存
        redisTemplate.delete("coinprotocol");

        coinprotocol = coinprotocolService.save(coinprotocol);

        result = success(messageSource.getMessage("OPERATION_SUCCESS"));
        result.setData(coinprotocol);
        return result;
    }

}
