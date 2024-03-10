package com.bitisan.user.feign;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.dto.CoinDTO;
import com.bitisan.user.dto.ContractDTO;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.service.CoinService;
import com.bitisan.user.service.CoinextService;
import com.bitisan.user.service.MemberWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * 会员用户 前端控制器
 * </p>
 *
 * @author markchao
 * @since 2021-06-14
 */
@RestController
@RequestMapping("/coinFeign")
public class CoinFeignController extends BaseController {

    @Autowired
    private CoinService coinService;
    @Autowired
    private CoinextService coinextService;
    @Autowired
    private MemberWalletService memberWalletService;




    @GetMapping("findByUnit")
    public Coin findByUnit(@RequestParam("coinUnit")String coinUnit) {
        Coin coin = coinService.findByUnit(coinUnit);
        return coin;
    }

//    @ApiOperation(value = "获取全部")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pageNo", value = "页码"),
//            @ApiImplicitParam(name = "pageSize", value = "每页总数"),
//    })
    @GetMapping(value = "/findAll")
    public Page<Coin> findAll(@RequestParam("pageNo")Integer pageNo, @RequestParam("pageSize")Integer pageSize){
        return coinService.findAll(pageNo,pageSize);
    }

//    @ApiOperation(value = "保存")
    @PostMapping(value = "/save")
    public Boolean save(@RequestBody Coin coin){
        return coinService.saveOrUpdate(coin);
    }


//    @ApiOperation(value = "获取全部币种与单价")
    @PostMapping(value = "/getAllCoinNameAndUnit")
    public List<CoinDTO> getAllCoinNameAndUnit() {
        List<CoinDTO> allNameAndUnit = coinService.findAllNameAndUnit();
        return allNameAndUnit;
    }

//    @ApiOperation(value = "获取全部币种名称")
    @PostMapping(value = "/getAllCoinName")
    public List<String> getAllCoinName() {
        List<String> list = coinService.getAllCoinName();
        return list;
    }

//    @ApiOperation(value = "根据币种ID查找")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "coinId", value = "币种ID"),
//    })
    @GetMapping("findByCoinId")
    public Coin findByCoinId(@RequestParam("coinId")String coinId){
        return coinService.getById(coinId);
    }


//    @ApiOperation(value = "根据协议获取合约")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "protocol", value = "协议"),
//    })
    @GetMapping("getContractByProtocol")
    public List<ContractDTO> getContractByProtocol(@RequestParam(value = "protocol")String protocol){
        List<ContractDTO> list=coinService.getContractByProtocol(protocol);
        return list;
    }






}

