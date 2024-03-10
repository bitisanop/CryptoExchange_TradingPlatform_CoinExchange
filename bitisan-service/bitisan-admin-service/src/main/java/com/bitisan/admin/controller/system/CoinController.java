package com.bitisan.admin.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.Admin;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.SysConstant;
import com.bitisan.dto.CoinDTO;
import com.bitisan.screen.CoinChainScreen;
import com.bitisan.screen.PageParam;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.MemberWalletFeign;
import com.bitisan.util.BindingResultUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.Assert.notNull;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 后台货币web
 * @date 2019/12/29 15:01
 */
@RestController
@RequestMapping("/system/coin")
@Slf4j
public class CoinController extends BaseAdminController {

    private Logger logger = LoggerFactory.getLogger(BaseAdminController.class);

    @Autowired
    private CoinFeign coinService;
    @Autowired
    private MemberFeign memberFeign;
    @Autowired
    private LocaleMessageSourceService msService;
    @Autowired
    private MemberWalletFeign memberWalletFeign;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequiresPermissions("system:coin:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "分页查找后台货币Coin")
    public MessageResult pageQuery(PageParam pageParam) {
        Page<Coin> pageResult = coinService.findAll(pageParam.getPageNo(),pageParam.getPageSize());
        return success(IPage2Page(pageResult));
    }



    @RequiresPermissions("system:coin:create")
    @PostMapping("create")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建后台货币Coin")
    public MessageResult create(@Valid Coin coin, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        if("decp".equalsIgnoreCase(coin.getName().trim()) || "dcep".equalsIgnoreCase(coin.getName().trim())){
            return error(msService.getMessage("COIN_NAME_EXIST"));
        }

        Coin one = coinService.findByCoinId(coin.getName());
        if (one != null) {
            return error(msService.getMessage("COIN_NAME_EXIST"));
        }
        coinService.save(coin);
        return success();
    }

    @RequiresPermissions("system:coin:page-query")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建后台货币Coin")
    public MessageResult detail(@Valid Coin coin, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        Coin one = coinService.findByCoinId(coin.getName());
        return success(one);
    }

    @RequiresPermissions("system:coin:update")
    @PostMapping("update")
    @AccessLog(module = AdminModule.SYSTEM, operation = "更新后台货币Coin")
    public MessageResult update(
            @Valid Coin coin,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin,
            BindingResult bindingResult) {
        Assert.notNull(admin, msService.getMessage("DATA_EXPIRED_LOGIN_AGAIN"));
        notNull(coin.getName(), "validate coin.name!");
        Coin one = coinService.findByCoinId(coin.getName());
        notNull(one, "validate coin.name!");
        coin.setId(one.getId());
        coinService.save(coin);
        return success();
    }

    @GetMapping("get-no-check-key")
    public MessageResult getKey(String phone) {
        String key = SysConstant.ADMIN_COIN_TRANSFER_COLD_PREFIX + phone + "_PASS";
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object object = valueOperations.get(key);
        if (object == null) {
            return error(msService.getMessage("NEED_CODE"));
        }
        return success(msService.getMessage("NO_NEED_CODE"), object);
    }

    /**
     * 为每个人添加新币种钱包记录
     * 1.使用JDBC批量插入
     * 2.默认不获取钱包地址，用户充值时自主获取
     *
     * @param coinName
     * @return
     */
    @RequiresPermissions("system:coin:newwallet")
    @RequestMapping("create-member-wallet")
    public MessageResult createCoin(String coinName) {
        Coin coin = coinService.findByCoinId(coinName);
        if (coin == null) {
            return MessageResult.error(msService.getMessage("CURRENCY_CONFIGURATION_NOT_FOUND"));
        }
        List<Member> list = memberFeign.findAllList();
        list.forEach(member -> {
            MemberWallet wallet = memberWalletFeign.findByCoinUnitAndMemberId(coin.getUnit(), member.getId());
            if (wallet == null) {
                MemberWallet wallet1 = new MemberWallet();
                wallet1.setCoinId(coin.getUnit());
                wallet1.setMemberId(member.getId());
                wallet1.setBalance(new BigDecimal(0));
                wallet1.setFrozenBalance(new BigDecimal(0));
                wallet1.setReleaseBalance(new BigDecimal(0));
                wallet1.setVersion(1);
                memberWalletFeign.save(wallet1);
            }
        });
        return MessageResult.success(msService.getMessage("SUCCESS"));
    }

    @PostMapping("all-name-and-unit")
    @AccessLog(module = AdminModule.SYSTEM, operation = "查找所有coin的name和unit")
    public MessageResult getAllCoinNameAndUnit() {
        List<Coin> list = coinService.getAllCoinNameAndUnit();
        return MessageResult.getSuccessInstance(msService.getMessage("SUCCESS"), list);
    }

}
