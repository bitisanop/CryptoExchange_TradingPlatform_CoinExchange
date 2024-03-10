package com.bitisan.user.event;

import com.alibaba.fastjson.JSONObject;
import com.bitisan.constant.PromotionLevel;
import com.bitisan.constant.PromotionRewardType;
import com.bitisan.constant.RewardRecordType;
import com.bitisan.constant.TransactionType;
import com.bitisan.user.entity.*;
import com.bitisan.user.service.*;
import com.bitisan.util.BigDecimalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Bitisan  E-mail:xunibidev@gmail.com
 * @date 2020年01月09日
 */
@Service
@Slf4j
public class MemberEvent {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberPromotionService memberPromotionService;
    @Autowired
    private RewardPromotionSettingService rewardPromotionSettingService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private RewardRecordService rewardRecordService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberTransactionService memberTransactionService;
    @Autowired
    private MemberWeightUpperService memberWeightUpperService;
    /**
     * 如果值为1，推荐注册的推荐人必须被推荐人实名认证才能获得奖励
     */
    @Value("${commission.need.real-name:1}")
    private int needRealName;

    @Value("${commission.promotion.second-level:0}")
    private int promotionSecondLevel ;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 注册成功事件
     *
     * @param member 持久化对象
     */
    public void onRegisterSuccess(Member member, String promotionCode) throws Exception {
        JSONObject json = new JSONObject();
        json.put("uid", member.getId());
//        Message<String> message = MessageBuilder.withPayload(json.toJSONString()).build();

        //发送给user项目consumer处理（）
        rocketMQTemplate.convertAndSend("member-register",json.toJSONString());
        //发送给contract-swap-api项目consumer处理
        rocketMQTemplate.convertAndSend("member-register-swap",json.toJSONString());
        //发送给contract-swapcoin-api项目consumer处理
        rocketMQTemplate.convertAndSend("member-register-coinswap",json.toJSONString());

        //推广活动
        if (StringUtils.hasText(promotionCode)) {
            Member member1 = memberService.findMemberByPromotionCode(promotionCode);
            if (member1 != null) {
                member.setInviterId(member1.getId());
                //如果不需要实名认证，直接发放奖励
                if (needRealName == 0) {
                    promotion(member1, member);
                }
            }
        }
        //更新
        memberService.updateById(member);
        //增加upper关系
        memberWeightUpperService.saveMemberWeightUpper(member);


    }

    /**
     * 设置邀请人
     * @param member
     * @param inviterMember
     * @throws InterruptedException
     */
    public void setMemberInviter(Member member, Member inviterMember) throws InterruptedException {
        //推广活动
        if (inviterMember != null) {
            member.setInviterId(inviterMember.getId());
            //如果不需要实名认证，直接发放奖励
            if (needRealName == 0) {
                promotion(inviterMember, member);
            }
        }
        //更新
        memberService.updateById(member);
        //增加upper关系
        memberWeightUpperService.saveMemberWeightUpper(member);
    }

    /**
     * 登录成功事件
     *
     * @param member 持久化对象
     */
    public void onLoginSuccess(Member member, String ip) {

    }

    public void promotion(Member member1, Member member) {
        RewardPromotionSetting rewardPromotionSetting = rewardPromotionSettingService.findByType(PromotionRewardType.REGISTER);
        if (rewardPromotionSetting != null) {
//            Coin coin = coinService.getById(rewardPromotionSetting.getCoinId());
            MemberWallet memberWallet1 = memberWalletService.findByCoinUnitAndMemberId(rewardPromotionSetting.getCoinId(), member1.getId());
            BigDecimal amount1 = JSONObject.parseObject(rewardPromotionSetting.getInfo()).getBigDecimal("one");
            memberWallet1.setReleaseBalance(BigDecimalUtils.add(memberWallet1.getReleaseBalance(), amount1));
            memberWalletService.updateById(memberWallet1);
            RewardRecord rewardRecord1 = new RewardRecord();
            rewardRecord1.setAmount(amount1);
            rewardRecord1.setCoinId(rewardPromotionSetting.getCoinId());
            rewardRecord1.setMemberId(member1.getId());
            rewardRecord1.setRemark(rewardPromotionSetting.getType().getDescription());
            rewardRecord1.setType(RewardRecordType.PROMOTION);
            rewardRecordService.save(rewardRecord1);
            MemberTransaction memberTransaction = new MemberTransaction();
            memberTransaction.setFee(BigDecimal.ZERO);
            memberTransaction.setAmount(amount1);
            memberTransaction.setSymbol(rewardPromotionSetting.getCoinId());
            memberTransaction.setType(TransactionType.PROMOTION_AWARD.getCode());
            memberTransaction.setMemberId(member1.getId());
            memberTransaction.setRealFee("0");
            memberTransaction.setDiscountFee("0");
            memberTransaction.setCreateTime(new Date());
            memberTransactionService.save(memberTransaction);
        }
        member1.setFirstLevel(member1.getFirstLevel() + 1);
        memberService.updateById(member1);
        MemberPromotion one = new MemberPromotion();
        one.setInviterId(member1.getId());
        one.setInviteesId(member.getId());
        one.setLevel(PromotionLevel.ONE);
        memberPromotionService.save(one);

        if (member1.getInviterId() != null) {
            Member member2 = memberService.getById(member1.getInviterId());
            // 当A推荐B，B推荐C，如果C通过实名认证，则B和A都可以获得奖励
            promotionLevelTwo(rewardPromotionSetting, member2, member);
        }
    }

    private void promotionLevelTwo(RewardPromotionSetting rewardPromotionSetting, Member member2, Member member) {
        if (rewardPromotionSetting != null) {
            Coin coin = coinService.getById(rewardPromotionSetting.getCoinId());
            MemberWallet memberWallet2 = memberWalletService.findByCoinUnitAndMemberId(rewardPromotionSetting.getCoinId(), member2.getId());
            BigDecimal amount2 = JSONObject.parseObject(rewardPromotionSetting.getInfo()).getBigDecimal("two");
            memberWallet2.setReleaseBalance(BigDecimalUtils.add(memberWallet2.getReleaseBalance(), amount2));
            memberWalletService.updateById(memberWallet2);
            RewardRecord rewardRecord2 = new RewardRecord();
            rewardRecord2.setAmount(amount2);
            rewardRecord2.setCoinId(rewardPromotionSetting.getCoinId());
            rewardRecord2.setMemberId(member2.getId());
            rewardRecord2.setRemark(rewardPromotionSetting.getType().getDescription());
            rewardRecord2.setType(RewardRecordType.PROMOTION);
            rewardRecordService.save(rewardRecord2);
            MemberTransaction memberTransaction = new MemberTransaction();
            memberTransaction.setFee(BigDecimal.ZERO);
            memberTransaction.setAmount(amount2);
            memberTransaction.setSymbol(coin.getUnit());
            memberTransaction.setType(TransactionType.PROMOTION_AWARD.getCode());
            memberTransaction.setMemberId(member2.getId());memberTransaction.setRealFee("0");
            memberTransaction.setDiscountFee("0");
            memberTransaction.setCreateTime(new Date());
            memberTransactionService.save(memberTransaction);
        }
        member2.setSecondLevel(member2.getSecondLevel() + 1);
        memberService.updateById(member2);
        MemberPromotion two = new MemberPromotion();
        two.setInviterId(member2.getId());
        two.setInviteesId(member.getId());
        two.setLevel(PromotionLevel.TWO);
        memberPromotionService.save(two);
        if (member2.getInviterId() != null) {
            Member member3 = memberService.getById(member2.getInviterId());
            member3.setThirdLevel(member3.getThirdLevel() + 1);
            memberService.updateById(member3);
        }
    }

}
