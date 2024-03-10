package com.bitisan.user.feign;

import com.bitisan.controller.BaseController;
import com.bitisan.user.service.AgentRewardRecordService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author Bitisan E-Mali:bitisanop@gmail.com
 * @date 2021年01月26日
 */
@Api(tags = "代理商奖励记录")
@RestController
@Slf4j
@RequestMapping(value = "/agentRewardRecordFeign", method = RequestMethod.POST)
public class AgentRewardRecordFeignController extends BaseController {

    @Autowired
    private AgentRewardRecordService agentRewardRecordService;

    @PostMapping("saveAgentRewardRecord")
    void saveAgentRewardRecord(@RequestParam("memberId")Long memberId, @RequestParam("upMemberId")Long upMemberId,
                               @RequestParam("reward") BigDecimal reward, @RequestParam("unit")String unit,
                               @RequestParam("type")int type, @RequestParam("orderId")Long orderId){
        agentRewardRecordService.saveAgentRewardRecord(memberId,upMemberId,reward,unit,type,orderId);

    }

}
