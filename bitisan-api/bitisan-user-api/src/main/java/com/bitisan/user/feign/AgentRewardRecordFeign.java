package com.bitisan.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "agentRewardRecordFeign")
public interface AgentRewardRecordFeign {

    @PostMapping("/agentRewardRecordFeign/saveAgentRewardRecord")
    void saveAgentRewardRecord(@RequestParam("memberId")Long memberId, @RequestParam("upMemberId")Long upMemberId,
                               @RequestParam("reward")BigDecimal reward, @RequestParam("unit")String unit,
                               @RequestParam("type")int type, @RequestParam("orderId")Long orderId);
}
