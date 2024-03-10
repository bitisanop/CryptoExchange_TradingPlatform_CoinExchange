package com.bitisan.admin.task;


import com.bitisan.swap.feign.ContractOrderEntrustFeign;
import com.bitisan.user.feign.MemberTransactionFeign;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RakeBackJob {
    private Logger logger = LoggerFactory.getLogger(RakeBackJob.class);

    @Autowired
    private ContractOrderEntrustFeign contractOrderEntrustService;
    @Autowired
    private MemberTransactionFeign memberTransactionService;

    // 5分钟一次合约返佣
//    @Scheduled(cron = "0 0/5 * * * ?")
    @XxlJob("autoRakeBack")
    public void autoRakeBack(){
        logger.info("start rake back...");
        //更新机器人返利状态
        memberTransactionService.updateRewardRobot();
        //u本位永续合约返佣
        contractOrderEntrustService.sendReward();
        //币币返佣
        memberTransactionService.sendExchangeReward();
        // 秒合约 盈亏
        memberTransactionService.sendSecondReward();
        // 期权 盈亏
        memberTransactionService.sendOptionReward();

        logger.info("end rake back...");
    }


}
