package com.bitisan.user.feign;

import com.bitisan.controller.BaseController;
import com.bitisan.user.entity.AgentWallet;
import com.bitisan.user.service.AgentWalletService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author Bitisan E-Mali:bitisanop@gmail.com
 * @date 2021年01月26日
 */
@Api(tags = "代理商奖励钱包")
@RestController
@Slf4j
@RequestMapping(value = "/agentWalletFeign", method = RequestMethod.POST)
public class AgentWalletFeignController extends BaseController {

    @Autowired
    private AgentWalletService agentWalletService;

    @PostMapping("findWalletByMemberIdAndCoinUnit")
    AgentWallet findWalletByMemberIdAndCoinUnit(@RequestParam("memberId")Long memberId, @RequestParam("coinUnit")String coinUnit){
        return agentWalletService.findWalletByMemberIdAndCoinUnit(memberId,coinUnit);
    }

    @PostMapping("increaseBalance")
    void increaseBalance(@RequestParam("id")Long id, @RequestParam("reward") BigDecimal reward){
        agentWalletService.increaseBalance(id,reward);
    }

}
