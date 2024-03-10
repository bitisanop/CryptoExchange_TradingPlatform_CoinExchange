package com.bitisan.user.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.controller.BaseController;
import com.bitisan.dto.CoinDTO;
import com.bitisan.user.dto.ContractDTO;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.Coinext;
import com.bitisan.user.service.CoinService;
import com.bitisan.user.service.CoinextService;
import com.bitisan.user.service.MemberWalletService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bitisan.constant.SysConstant.SESSION_MEMBER;

/**
 * <p>
 * 会员用户 前端控制器
 * </p>
 *
 * @author markchao
 * @since 2021-06-14
 */
@Api(tags = "币种")
@RestController
@RequestMapping("/coin")
public class CoinController extends BaseController {

    @Autowired
    private CoinService coinService;
    @Autowired
    private CoinextService coinextService;
    @Autowired
    private MemberWalletService memberWalletService;

    @ApiOperation(value = "获取合法币种")
    @GetMapping("legal")
    public MessageResult legal() {
        List<Coin> legalAll = coinService.findLegalAll();
        return success(legalAll);
    }

    @ApiOperation(value = "获取合法币种分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "页码"),
            @ApiImplicitParam(name = "pageSize", value = "每页总数"),
    })
    @GetMapping("legal/page")
    public MessageResult findLegalCoinPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage all = coinService.findLegalCoinPage(pageNo,pageSize);
        return success(IPage2Page(all));
    }

    @ApiOperation(value = "获取全部币种")
    @RequestMapping("supported")
    public List<Map<String,String>>  findCoins(){
        List<Coin> coins = coinService.list();
        List<Map<String,String>> result = new ArrayList<>();
        coins.forEach(coin->{
            if(coin.getHasLegal().equals(Boolean.FALSE)) {
                Map<String, String> map = new HashMap<>();
                map.put("name",coin.getName());
                map.put("nameCn",coin.getNameCn());
                map.put("withdrawFee",String.valueOf(coin.getMinTxFee()));
                map.put("enableRecharge",String.valueOf(coin.getCanRecharge()));
                map.put("minWithdrawAmount",String.valueOf(coin.getMinWithdrawAmount()));
                map.put("enableWithdraw",String.valueOf(coin.getCanWithdraw()));
                result.add(map);
            }
        });
        return result;
    }

    // 查询所有币种
    @ApiOperation(value = "查询所有币种")
    @GetMapping("list")
    public MessageResult list() {
        List<Coin> coinList = coinService.list();
        List<CoinDTO> coinDTOS = new ArrayList<>();
        for(Coin c:coinList){
            CoinDTO dto = new CoinDTO();
            dto.setName(c.getUnit());
            dto.setUnit(c.getUnit());
            coinDTOS.add(dto);
        }
        List<Coinext> coinextList = coinextService.list();
        Map<String, Object> map = new HashMap<>();
        map.put("coinList", coinDTOS);
        map.put("coinextList", coinextList);
        return success(map);
    }


    // 查询币种的余额
    @ApiOperation(value = "查询币种的余额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coinName", value = "币种名称"),
    })
    @PermissionOperation
    @GetMapping("balance")
    public MessageResult balance(@RequestHeader(SESSION_MEMBER) String authMember,
                                 @RequestParam(value = "coinName") String coinName) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Long memberId = user.getId();
        BigDecimal balance = memberWalletService.getBalance(memberId, coinName);
        return success(balance);
    }

}

