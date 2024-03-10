package com.bitisan.admin.controller.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.Admin;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.SysConstant;
import com.bitisan.screen.ConvertCoinScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.ConvertCoin;
import com.bitisan.user.feign.ConvertFeign;
import com.bitisan.util.BindingResultUtil;
import com.bitisan.util.DateUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.util.Assert.notNull;

/**
 * @author Bitisan E-Mali:bitisanop@gmail.com
 * @description 后台货币web
 * @date 2021/12/29 15:01
 */
@RestController
@RequestMapping("/convert/coin")
@Slf4j
public class ConvertCoinController extends BaseAdminController {


    @Autowired
    private ConvertFeign convertFeign;

    @Autowired
    private LocaleMessageSourceService messageSource;


    @RequiresPermissions("convert:coin:create")
    @PostMapping("create")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建闪兑货币Coin")
    public MessageResult create(@Valid ConvertCoin convertCoin) {
        ConvertCoin one = convertFeign.findByCoinUnit(convertCoin.getCoinUnit());
        if (one != null) {
            return error(messageSource.getMessage("COIN_NAME_EXIST"));
        }
        convertCoin.setUpdateTime(DateUtil.getCurrentDate());
        convertCoin.setCreateTime(DateUtil.getCurrentDate());
        convertFeign.save(convertCoin);

        return success();
    }


    @RequiresPermissions("convert:coin:update")
    @PostMapping("update")
    @AccessLog(module = AdminModule.SYSTEM, operation = "更新闪兑货币Coin")
    public MessageResult update(
            @Valid ConvertCoin convertCoin,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin,
            BindingResult bindingResult) {

        notNull(admin, messageSource.getMessage("DATA_EXPIRED_LOGIN_AGAIN"));

        notNull(convertCoin.getCoinUnit(), "validate Coin.Unit!");
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        ConvertCoin one = convertFeign.findByCoinUnit(convertCoin.getCoinUnit());
        notNull(one, "validate coin.name!");
        convertCoin.setUpdateTime(DateUtil.getCurrentDate());
        convertFeign.save(convertCoin);
        return success();
    }

    @RequiresPermissions("convert:coin:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.SYSTEM, operation = "后台闪兑Coin详情")
    public MessageResult detail(@RequestParam("coinUnit") String coinUnit) {
        ConvertCoin convertCoin = convertFeign.findByCoinUnit(coinUnit);
        notNull(convertCoin, "validate Coin.Unit!");
        return success(convertCoin);
    }

    @RequiresPermissions("convert:coin:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "分页查找闪兑货币Coin")
    public MessageResult pageQuery(ConvertCoinScreen screen) {
//        if (pageModel.getProperty() == null) {
//            List<String> list = new ArrayList<>();
//            list.add("createTime");
//            List<Sort.Direction> directions = new ArrayList<>();
//            directions.add(Sort.Direction.DESC);
//            pageModel.setProperty(list);
//            pageModel.setDirection(directions);
//        }
        Page<ConvertCoin> pageResult = convertFeign.findAll(screen);
        return success(IPage2Page(pageResult));
    }


}
