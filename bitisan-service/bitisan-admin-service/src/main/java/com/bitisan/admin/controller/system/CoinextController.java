package com.bitisan.admin.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.dto.CoinDTO;
import com.bitisan.dto.CoinprotocolDTO;
import com.bitisan.screen.CoinextScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.Coinext;
import com.bitisan.user.entity.Coinprotocol;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.user.feign.CoinextFeign;
import com.bitisan.user.feign.CoinprotocolFeign;
import com.bitisan.util.BindingResultUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 币种扩展管理
 */
@Slf4j
@RestController
@RequestMapping("/system/coinext")
public class CoinextController extends BaseAdminController {

    @Autowired
    private CoinFeign coinFeign;

    @Autowired
    private CoinprotocolFeign coinprotocolFeign;

    @Autowired
    private CoinextFeign coinextFeign;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("system:coinext:coin-list")
    @GetMapping("/coin-list")
    @AccessLog(module = AdminModule.SYSTEM, operation = "币种扩展里获取币种列表")
    public MessageResult coinList() {

        List<Coin> list = coinFeign.getAllCoinNameAndUnit();
         return success(list);
    }

    @RequiresPermissions("system:coinext:protocol-list")
    @GetMapping("/protocol-list")
    @AccessLog(module = AdminModule.SYSTEM, operation = "币种扩展里获取币种协议列表")
    public MessageResult protocolList() {

        List<CoinprotocolDTO> list = coinprotocolFeign.list();

        return success(list);
    }

    @RequiresPermissions("system:coinext:page-query")
    @PostMapping("/page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "获取币种扩展列表")
    public MessageResult pageQuery(CoinextScreen coinextScreen) {
        Page<Coinext> pageResult = coinextFeign.findAll(coinextScreen);
        return success(IPage2Page(pageResult));
    }

    @RequiresPermissions("system:coinext:merge")
    @PostMapping("/merge")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建/修改币种扩展")
    public MessageResult merge(@Valid Coinext coinext, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }

        // 查询是否存在
        Coinext one = coinextFeign.findFirstByCoinNameAndProtocol(coinext.getCoinName(), coinext.getProtocol());
        if (coinext.getId() != null) {
            if (one != null && !one.getId().equals(coinext.getId())) {
                result = error(messageSource.getMessage("CURRENCY_ALREADY_EXISTS"));
                return result;
            }
        } else if (one != null) {
            result = error(messageSource.getMessage("CURRENCY_ALREADY_EXISTS"));
            return result;
        }

        Coinprotocol byProtocol = coinprotocolFeign.findByProtocol(coinext.getProtocol());

        if (byProtocol == null) {
            result = error(messageSource.getMessage("CURRENT_PROTOCOL_NOT_FOUND"));
            return result;
        }

        coinext.setProtocolName(byProtocol.getProtocolName());

        // 删除redis缓存
        redisTemplate.delete("coinext");


        coinext = coinextFeign.save(coinext);
        result = success(messageSource.getMessage("OPERATION_SUCCESS"));
        result.setData(coinext);
        return result;
    }

}
