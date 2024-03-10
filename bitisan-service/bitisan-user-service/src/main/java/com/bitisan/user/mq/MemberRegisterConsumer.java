package com.bitisan.user.mq;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.service.CoinService;
import com.bitisan.user.service.MemberWalletService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RocketMQMessageListener(topic = "member-register", consumerGroup = "user-member-register")
public class MemberRegisterConsumer implements RocketMQListener<String> {

    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberWalletService memberWalletService;

    @Override
    public void onMessage(String content) {
        log.info("handle member-register={}", content);
        if (StringUtils.isEmpty(content)) {
            return;
        }
        JSONObject json = JSON.parseObject(content);
        if (json == null) {
            return;
        }
        Long memberId = json.getLong("uid");
        //获取所有支持的币种
        List<Coin> coins =  coinService.list();
            for(Coin coin:coins) {
                log.info("memberId:{},unit:{}",json.getLong("uid"),coin.getUnit());
                MemberWallet wallet = memberWalletService.findByCoinUnitAndMemberId(coin.getUnit(), memberId);
                if(wallet==null){
                    wallet = new MemberWallet();
                    wallet.setCoinId(coin.getUnit());
                    wallet.setMemberId(memberId);
                    wallet.setBalance(new BigDecimal(0));
                    wallet.setFrozenBalance(new BigDecimal(0));
                    //保存
                    memberWalletService.save(wallet);
                }

        }
        //注册活动奖励
//        RewardActivitySetting rewardActivitySetting = rewardActivitySettingService.findByType(ActivityRewardType.REGISTER);
//        if (rewardActivitySetting!=null){
//            MemberWallet memberWallet=memberWalletService.findByCoinAndMemberId(rewardActivitySetting.getCoin(),json.getLong("uid"));
//            if (memberWallet==null){return;}
//            // 奖励币
//            BigDecimal amount3=JSONObject.parseObject(rewardActivitySetting.getInfo()).getBigDecimal("amount");
//            memberWallet.setBalance(BigDecimalUtils.add(memberWallet.getBalance(),amount3));
//            memberWalletService.save(memberWallet);
//            // 保存奖励记录
//            Member member = memberService.findOne(json.getLong("uid"));
//            RewardRecord rewardRecord3 = new RewardRecord();
//            rewardRecord3.setAmount(amount3);
//            rewardRecord3.setCoin(rewardActivitySetting.getCoin());
//            rewardRecord3.setMember(member);
//            rewardRecord3.setRemark(rewardActivitySetting.getType().getCnName());
//            rewardRecord3.setType(RewardRecordType.ACTIVITY);
//            rewardRecordService.save(rewardRecord3);
//            // 保存资产变更记录
//            MemberTransaction memberTransaction = new MemberTransaction();
//            memberTransaction.setFee(BigDecimal.ZERO);
//            memberTransaction.setAmount(amount3);
//            memberTransaction.setSymbol(rewardActivitySetting.getCoin().getUnit());
//            memberTransaction.setType(TransactionType.ACTIVITY_AWARD);
//            memberTransaction.setMemberId(member.getId());
//            memberTransaction.setDiscountFee("0");
//            memberTransaction.setRealFee("0");
//            memberTransaction = memberTransactionService.save(memberTransaction);
//        }

    }
}
