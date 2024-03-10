package com.bitisan.user.feign;

import com.bitisan.user.entity.AgentWallet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "agentWalletFeign")
public interface AgentWalletFeign {

    @PostMapping("/agentWalletFeign/findWalletByMemberIdAndCoinUnit")
    AgentWallet findWalletByMemberIdAndCoinUnit(@RequestParam("memberId")Long memberId, @RequestParam("coinUnit")String coinUnit);

    @PostMapping("/agentWalletFeign/increaseBalance")
    void increaseBalance(@RequestParam("id")Long id, @RequestParam("reward")BigDecimal reward);
}
