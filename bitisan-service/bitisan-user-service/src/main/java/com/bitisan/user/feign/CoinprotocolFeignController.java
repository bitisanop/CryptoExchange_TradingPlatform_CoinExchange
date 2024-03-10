package com.bitisan.user.feign;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.dto.CoinprotocolDTO;
import com.bitisan.user.entity.Coinprotocol;
import com.bitisan.user.service.CoinprotocolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/coinprotocolFeign")
public class CoinprotocolFeignController extends BaseController {

    @Autowired
    private CoinprotocolService coinprotocolService;


//    @ApiOperation(value = "币种协议列表")
    @PostMapping(value = "/list")
    public List<CoinprotocolDTO> list() {
        List<CoinprotocolDTO> list = coinprotocolService.allCoinprotocolList();
        return list;
    }

//    @ApiOperation(value = "根据币种协议查找")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "protocol", value = "币种协议"),
//    })
    @PostMapping(value = "/findByProtocol")
    public Coinprotocol findByProtocol(@RequestParam("protocol") Integer protocol){
        Coinprotocol coinprotocol = coinprotocolService.findByProtocol(protocol);
        return coinprotocol;
    }

//    @ApiOperation(value = "币种协议列表分页")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pageNo", value = "页码"),
//            @ApiImplicitParam(name = "pageSize", value = "每页总数"),
//    })
    @GetMapping(value = "/findAll")
    public Page<Coinprotocol> findAll(@RequestParam("pageNo")Integer pageNo, @RequestParam("pageSize")Integer pageSize){
        return coinprotocolService.findAll(pageNo,pageSize);
    }

//    @ApiOperation(value = "保存")
    @PostMapping(value = "/save")
    public Coinprotocol save(@RequestBody Coinprotocol coinprotocol){
        coinprotocolService.saveOrUpdate(coinprotocol);
        return coinprotocol;
    }

    @PostMapping(value = "/findBySymbol")
    public Coinprotocol findBySymbol(@RequestParam("symbol")String symbol){
        LambdaQueryWrapper<Coinprotocol> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Coinprotocol::getSymbol,symbol);
        List<Coinprotocol> list = coinprotocolService.list(queryWrapper);
        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

}

