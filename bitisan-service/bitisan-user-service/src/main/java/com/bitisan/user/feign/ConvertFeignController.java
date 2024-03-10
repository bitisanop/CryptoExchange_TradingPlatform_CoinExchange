package com.bitisan.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.market.feign.MarketFeign;
import com.bitisan.screen.ConvertCoinScreen;
import com.bitisan.screen.ConvertOrderScreen;
import com.bitisan.user.entity.ConvertCoin;
import com.bitisan.user.entity.ConvertOrder;
import com.bitisan.user.service.ConvertCoinService;
import com.bitisan.user.service.ConvertOrderService;
import com.bitisan.user.service.MemberTransactionService;
import com.bitisan.user.service.MemberWalletService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 动态兑换币种（前端CExchange）
 */
@Api(tags = "动态兑换币种")
@RestController
@RequestMapping("convertFeign")
public class ConvertFeignController extends BaseController {

    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private MemberTransactionService transactionService;
    @Autowired
    private ConvertCoinService coinService;
    @Autowired
    private ConvertOrderService convertOrderService;
    @Autowired
    private MarketFeign marketFeign;

//
//    @ApiOperation(value = "根据币种单位查找")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "coinUnit", value = "币种单位"),
//    })
    @GetMapping("/findByCoinUnit")
    public ConvertCoin findByCoinUnit(@RequestParam("coinUnit")String coinUnit){
        return coinService.findByCoinUnit(coinUnit);
    }
//    @ApiOperation(value = "保存")
    @PostMapping(value = "/save")
    public ConvertCoin save(@RequestBody ConvertCoin convertCoin){
        coinService.saveOrUpdate(convertCoin);
        return convertCoin;
    }

//    @ApiOperation(value = "查询全部")
    @PostMapping(value = "/findAll")
    public Page<ConvertCoin> findAll(@RequestBody ConvertCoinScreen convertScreen){
        return coinService.findAll(convertScreen);
    }

//    @ApiOperation(value = "查询闪兑订单列表")
    @PostMapping(value = "/findOrderAll")
    public Page<ConvertOrder> findOrderAll(@RequestBody ConvertOrderScreen orderScreen){
        return convertOrderService.findAll(orderScreen);
    }
}
