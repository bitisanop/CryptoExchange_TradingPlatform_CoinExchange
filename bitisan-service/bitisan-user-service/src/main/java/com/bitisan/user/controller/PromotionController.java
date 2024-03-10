package com.bitisan.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.MemberLevelEnum;
import com.bitisan.constant.PromotionLevel;
import com.bitisan.constant.RealNameStatus;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.*;
import com.bitisan.user.service.*;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.DateUtil;
import com.bitisan.util.GeneratorUtil;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 推广
 *
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @date 2020年03月19日
 */
@Api(tags = "推广")
@RestController
@Slf4j
@RequestMapping(value = "/promotion")
public class PromotionController extends BaseController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RewardRecordService rewardRecordService;

    @Autowired
    private CoinService coinService;

    @Autowired
    private MemberInviteStasticService memberInviteStasticService;

    @Autowired
    private PromotionCardService promotionCardService;

    @Autowired
    private PromotionCardOrderService promotionCardOrderService;

    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private MemberPromotionService memberPromotionService;

    @Autowired
    private LocaleMessageSourceService sourceService;

    private Random rand = new Random();
    /**
     * 获取推广合伙人信息
     * @param
     * @return
     */
    @ApiOperation(value = "获取推广合伙人信息")
    @PermissionOperation
    @RequestMapping(value = "/mypromotion")
    public MessageResult myPromotioin(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        MemberInviteStastic result  =  memberInviteStasticService.findByMemberId(user.getId());
        return success(result);
    }

    /**
     * 推广周榜
     * @param top
     * @return
     */
    @ApiOperation(value = "推广周榜")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "top", value = "top"),
    })
    @RequestMapping(value = "/weektoprank")
    public MessageResult topRankWeek(@RequestParam(value = "top", defaultValue = "20") Integer top) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        JSONObject result = (JSONObject) valueOperations.get(SysConstant.MEMBER_PROMOTION_TOP_RANK_WEEK + top);
        if (result != null){
            return success(result);
        } else {
            JSONObject resultObj = new JSONObject();
            // 周榜
            List<MemberInviteStasticRank> topInviteWeek = memberInviteStasticService.topInviteCountByType(1, 20);
            for(MemberInviteStasticRank item3: topInviteWeek) {
                item3.setUserIdentify(item3.getUserIdentify().substring(0, 3) + "****" + item3.getUserIdentify().substring(item3.getUserIdentify().length() - 4, item3.getUserIdentify().length()));
            }

            resultObj.put("topinviteweek", topInviteWeek);

            valueOperations.set(SysConstant.MEMBER_PROMOTION_TOP_RANK_WEEK+top, resultObj, SysConstant.MEMBER_PROMOTION_TOP_RANK_EXPIRE_TIME_WEEK, TimeUnit.SECONDS);
            return success(resultObj);
        }
    }

    /**
     * 推广月榜
     * @param top
     * @return
     */
    @ApiOperation(value = "推广月榜")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "top", value = "top"),
    })
    @RequestMapping(value = "/monthtoprank")
    public MessageResult topRankMonth(@RequestParam(value = "top", defaultValue = "20") Integer top) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        JSONObject result = (JSONObject) valueOperations.get(SysConstant.MEMBER_PROMOTION_TOP_RANK_MONTH + top);
        if (result != null){
            return success(result);
        } else {
            JSONObject resultObj = new JSONObject();
            // 月榜
            List<MemberInviteStasticRank> topInviteMonth = memberInviteStasticService.topInviteCountByType(2, 20);
            for(MemberInviteStasticRank item4: topInviteMonth) {
                item4.setUserIdentify(item4.getUserIdentify().substring(0, 3) + "****" + item4.getUserIdentify().substring(item4.getUserIdentify().length() - 4, item4.getUserIdentify().length()));
            }
            resultObj.put("topinvitemonth", topInviteMonth);

            valueOperations.set(SysConstant.MEMBER_PROMOTION_TOP_RANK_MONTH+top, resultObj, SysConstant.MEMBER_PROMOTION_TOP_RANK_EXPIRE_TIME_MONTH, TimeUnit.SECONDS);
            return success(resultObj);
        }
    }
    /**
     * 获取排名前top名返佣金额 & 前top名邀请人数
     * @param
     * @param top
     * @return
     */
    @ApiOperation(value = "获取排名前top名返佣金额 & 前top名邀请人数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "top", value = "top"),
    })
    @RequestMapping(value = "/toprank")
    public MessageResult topRank(@RequestParam(value = "top", defaultValue = "20") Integer top) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        JSONObject result = (JSONObject) valueOperations.get(SysConstant.MEMBER_PROMOTION_TOP_RANK + top);
        if (result != null){
            return success(result);
        } else {
            JSONObject resultObj = new JSONObject();
            List<MemberInviteStastic> topReward = memberInviteStasticService.topRewardAmount(top);

            for(MemberInviteStastic item1 : topReward) {
                if(!StringUtils.isEmpty(item1.getUserIdentify())) {
                    item1.setUserIdentify(item1.getUserIdentify().substring(0, 3) + "****" + item1.getUserIdentify().substring(item1.getUserIdentify().length() - 4, item1.getUserIdentify().length()));
                }
                item1.setMemberId(item1.getMemberId() * (item1.getMemberId() % 100)); // 仅仅为了隐藏真实ID
            }

            List<MemberInviteStastic> topInvite = memberInviteStasticService.topInviteCount(top);
            for(MemberInviteStastic item2 : topInvite) {
                if(!StringUtils.isEmpty(item2.getUserIdentify())) {
                    item2.setUserIdentify(item2.getUserIdentify().substring(0, 3) + "****" + item2.getUserIdentify().substring(item2.getUserIdentify().length() - 4, item2.getUserIdentify().length()));
                }
                item2.setMemberId(item2.getMemberId() * (item2.getMemberId() % 100));
            }
            resultObj.put("topreward", topReward);
            resultObj.put("topinvite", topInvite);

            // 周榜
            List<MemberInviteStasticRank> topInviteWeek = memberInviteStasticService.topInviteCountByType(1, 20);
            for(MemberInviteStasticRank item3: topInviteWeek) {
                if(!StringUtils.isEmpty(item3.getUserIdentify())) {
                    item3.setUserIdentify(item3.getUserIdentify().substring(0, 3) + "****" + item3.getUserIdentify().substring(item3.getUserIdentify().length() - 4, item3.getUserIdentify().length()));
                }
                item3.setMemberId(item3.getMemberId() * (item3.getMemberId() % 100));
            }

            // 月榜
            List<MemberInviteStasticRank> topInviteMonth = memberInviteStasticService.topInviteCountByType(2, 20);
            for(MemberInviteStasticRank item4: topInviteMonth) {
                if(!StringUtils.isEmpty(item4.getUserIdentify())) {
                    item4.setUserIdentify(item4.getUserIdentify().substring(0, 3) + "****" + item4.getUserIdentify().substring(item4.getUserIdentify().length() - 4, item4.getUserIdentify().length()));
                }
                item4.setMemberId(item4.getMemberId() * (item4.getMemberId() % 100));
            }
            resultObj.put("topinviteweek", topInviteWeek);
            resultObj.put("topinvitemonth", topInviteMonth);

            valueOperations.set(SysConstant.MEMBER_PROMOTION_TOP_RANK+top, resultObj, SysConstant.MEMBER_PROMOTION_TOP_RANK_EXPIRE_TIME, TimeUnit.SECONDS);
            return success(resultObj);
        }
    }

//    /**
//     * 推广记录查询
//     *
//     * @param member
//     * @return
//     */
//    @RequestMapping(value = "/record")
//    public MessageResult promotionRecord(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
//        List<Member> list = memberService.findPromotionMember(member.getId());
//        List<PromotionMember> list1 = list.stream().map(x ->
//                PromotionMember.builder().createTime(x.getRegistrationTime())
//                        .level(PromotionLevel.ONE)
//                        .username(x.getUsername())
//                        .build()
//        ).collect(Collectors.toList());
//        if (list.size() > 0) {
//            list.stream().forEach(x -> {
//                if (x.getPromotionCode() != null) {
//                    list1.addAll(memberService.findPromotionMember(x.getId()).stream()
//                            .map(y ->
//                                    PromotionMember.builder().createTime(y.getRegistrationTime())
//                                            .level(PromotionLevel.TWO)
//                                            .username(y.getUsername())
//                                            .build()
//                            ).collect(Collectors.toList()));
//                }
//            });
//        }
//        MessageResult messageResult = MessageResult.success();
//        messageResult.setData(list1.stream().sorted((x, y) -> {
//            if (x.getCreateTime().after(y.getCreateTime())) {
//                return -1;
//            } else {
//                return 1;
//            }
//        }).collect(Collectors.toList()));
//        return messageResult;
//    }


    @ApiOperation(value = "推广记录查询")
    @PermissionOperation
    @RequestMapping(value = "/record")
    public MessageResult promotionRecord2(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        AuthMember member = AuthMember.toAuthMember(authMember);
        //只查找一级推荐人
        IPage<Member> pageList = memberService.findPromotionMemberPage(pageNo-1, pageSize, member.getId());
        MessageResult messageResult = MessageResult.success();
        List<Member> list = pageList.getRecords();
        List<PromotionMember> list1 = list.stream().map(x ->
                PromotionMember.builder().createTime(x.getRegistrationTime()==null?new Date():x.getRegistrationTime())
                        .level(PromotionLevel.ONE)
                        .username(x.getUsername().substring(0, 3) + "****" + x.getUsername().substring(x.getUsername().length() - 4, x.getUsername().length()))
                        .realNameStatus(RealNameStatus.creator(x.getRealNameStatus()))
                        .build()
        ).collect(Collectors.toList());

        messageResult.setData(list1.stream().sorted((x, y) -> {
            if (x.getCreateTime().after(y.getCreateTime())) {
                return -1;
            } else {
                return 1;
            }
        }).collect(Collectors.toList()));

        messageResult.setTotalPage(pageList.getPages() + "");
        messageResult.setTotalElement(pageList.getTotal() + "");
        return messageResult;
    }


//    /**
//     * 推广奖励记录
//     *
//     * @param member
//     * @return
//     */
//    @RequestMapping(value = "/reward/record")
//    public MessageResult rewardRecord(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
//        List<RewardRecord> list = rewardRecordService.queryRewardPromotionList(memberService.findOne(member.getId()));
//        MessageResult result = MessageResult.success();
//        result.setData(list.stream().map(x ->
//                PromotionRewardRecord.builder().amount(x.getAmount())
//                        .createTime(x.getCreateTime())
//                        .remark(x.getRemark())
//                        .symbol(x.getCoin().getUnit())
//                        .build()
//        ).collect(Collectors.toList()));
//        return result;
//    }


    /**
     * 只查询推荐奖励
     *
     * @param
     * @return
     */
    @ApiOperation(value = "只查询推荐奖励")
    @PermissionOperation
    @RequestMapping(value = "/reward/record")
    public MessageResult rewardRecord2(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        AuthMember member = AuthMember.toAuthMember(authMember);
        IPage<RewardRecord> pageList = rewardRecordService.queryRewardPromotionPage(pageNo, pageSize, member.getId());
        MessageResult result = MessageResult.success();
        List<RewardRecord> list = pageList.getRecords();
        result.setData(list.stream().map(x -> {
           Coin coin = coinService.findByUnit(x.getCoinId());
            return  PromotionRewardRecord.builder()
                    .amount(x.getAmount())
                    .createTime(x.getCreateTime())
                    .remark(x.getRemark())
                    .symbol(coin.getUnit())
                    .build();
                }

        ).collect(Collectors.toList()));

        result.setTotalPage(pageList.getPages() + "");
        result.setTotalElement(pageList.getTotal() + "");
        return result;
    }

    /**
     * 获取免费推广卡（BTC: 0.001)
     * @param member
     * @return
     */
    @ApiOperation(value = "获取免费推广卡")
    @PermissionOperation
    @RequestMapping(value = "/promotioncard/getfreecard")
    public MessageResult createFreeCard(@RequestHeader(SysConstant.SESSION_MEMBER) String member) {
        // 检查是否实名认证
        AuthMember member1 = AuthMember.toAuthMember(member);
        Member authMember = memberService.getById(member1.getId());
        if(authMember.getMemberLevel()== MemberLevelEnum.GENERAL.getCode()){
            return MessageResult.error(500,sourceService.getMessage("NO_REALNAME"));
        }
        // 检查是否领取过一次
        List<PromotionCard> result = promotionCardService.findAllByMemberIdAndIsFree(member1.getId(), 1);
        if(result != null && result.size() > 0) {
            return MessageResult.error(500,"请不要重复领取免费推广卡");
        }

        PromotionCard card = new PromotionCard();
        card.setCardName("合伙人推广卡");
        card.setCardNo(authMember.getPromotionCode() + GeneratorUtil.getNonceString(5).toUpperCase());
        card.setAmount(new BigDecimal(0.001));
        card.setCardDesc("");
        card.setCoinId("BTC");
        card.setCount(30);
        card.setMemberId(authMember.getId());
        card.setIsFree(1);
        card.setIsEnabled(1);
        card.setExchangeCount(0);
        card.setTotalAmount(new BigDecimal(0.03));
        card.setIsLock(0);
        card.setLockDays(0);
        card.setIsEnabled(1);
        card.setCreateTime(DateUtil.getCurrentDate());

        promotionCardService.save(card);

        return success(card);
    }

    /**
     * 获取我创建的卡券列表
     * @param authMember
     * @return
     */
    @ApiOperation(value = "获取我创建的卡券列表")
    @PermissionOperation
    @RequestMapping(value = "/promotioncard/mycard")
    public MessageResult getMyCardList(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember member = AuthMember.toAuthMember(authMember);
        List<PromotionCard> result = promotionCardService.findAllByMemberId(member.getId());
        return success(result);
    }

    /**
     * 兑换卡详情
     * @param cardId
     * @return
     */
    @ApiOperation(value = "兑换卡详情")
    @RequestMapping(value = "/promotioncard/detail")
    public MessageResult getCardDetail(@RequestParam(value = "cardId", defaultValue = "") Long cardId) {

        Assert.notNull(cardId, "无效的兑换卡！");
        PromotionCard result = promotionCardService.getById(cardId);
        Assert.notNull(result, "无效的兑换卡！");

        return success(result);
    }
    /**
     * 兑换码兑换卡券（免费领取的卡券只能兑换一次）
     * @param authMember
     * @param cardNo
     * @return
     */
    @ApiOperation(value = "兑换卡详情")
    @PermissionOperation
    @RequestMapping(value = "/promotioncard/exchangecard")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult exhcangeCard(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember,
                                       @RequestParam(value = "cardNo", defaultValue = "") String cardNo) {
        AuthMember member = AuthMember.toAuthMember(authMember);
        // 检查卡券是否存在
        Assert.notNull(cardNo, "请输入兑换卡号！");
        if(!StringUtils.hasText(cardNo)) {
            return error("请输入兑换卡号！");
        }
        PromotionCard card = promotionCardService.findPromotionCardByCardNo(cardNo);
        Assert.notNull(card, "无效的兑换卡号！");

        // 用户是否存在
        Member authMember1 = memberService.getById(member.getId());
        Assert.notNull(authMember1, "非法操作!");

        // 检查卡券是否有效
        if(card.getIsEnabled() == 0) {
            return error("推广卡无效！");
        }

        // 用户钱包是否存在
        MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(card.getCoinId(), authMember1.getId());
        Assert.notNull(memberWallet, "该资产不存在!");

        // 检查卡券数量是否足够
        if(card.getExchangeCount() >= card.getCount()) {
            return error("该推广卡已兑完！");
        }
        // 检查自己是否领取过
        List<PromotionCardOrder> order = promotionCardOrderService.findByCardIdAndMemberId(card.getId(), authMember1.getId());
        if(order != null && order.size() > 0) {
            return error("已兑换过该卡，请勿重复兑换！");
        }

        // 检查自己是否领取过免费卡（官方为每一个用户发放的免费卡，每个人只能兑换一次）
        List<PromotionCardOrder> orderFree = promotionCardOrderService.findAllByMemberIdAndIsFree(authMember1.getId(), 1);
        if(orderFree != null && orderFree.size() > 0) {
            return error("官方发行免费推广卡只能兑换一次！");
        }

        PromotionCardOrder newOrder= new PromotionCardOrder();
        newOrder.setMemberId(authMember1.getId());
        newOrder.setAmount(card.getAmount());
        newOrder.setCardId(card.getId());
        newOrder.setIsFree(card.getIsFree());
        newOrder.setIsLock(card.getIsLock());
        newOrder.setLockDays(card.getLockDays());

        newOrder.setState(1);

        newOrder.setCreateTime(DateUtil.getCurrentDate());
        promotionCardOrderService.save(newOrder);

        if(newOrder != null) {
            // 如果用户自身没有被任何邀请，则新增邀请人
            if(authMember1.getInviterId() == null) {
                if(authMember1.getId() != card.getMemberId()) {
                    Member levelOneMember = memberService.getById(card.getMemberId());
                    // 用户已通过实名认证再兑换卡时，需要考虑一级二级好友
                    // 用户未通过实名认证，则只需设置inviteID
                    authMember1.setInviterId(card.getMemberId());
                    if(authMember1.getMemberLevel() == MemberLevelEnum.REALNAME.getCode()){
                        // 一级邀请关系保存
                        MemberPromotion one = new MemberPromotion();
                        one.setInviterId(card.getMemberId());
                        one.setInviteesId(authMember1.getId());
                        one.setLevel(PromotionLevel.ONE);
                        memberPromotionService.save(one);
                        // 一级好友人数 + 1
                        levelOneMember.setFirstLevel(levelOneMember.getFirstLevel() + 1);

                        if(levelOneMember.getInviterId() != null) {
                            Member levelTwoMember = memberService.getById(levelOneMember.getInviterId());
                            // 二级邀请关系保存
                            MemberPromotion two = new MemberPromotion();
                            two.setInviterId(levelTwoMember.getId());
                            two.setInviteesId(authMember1.getId());
                            two.setLevel(PromotionLevel.TWO);
                            memberPromotionService.save(two);

                            // 二级好友人数 + 1
                            levelTwoMember.setSecondLevel(levelTwoMember.getSecondLevel() + 1);
                        }
                    }
                }
            }

            // 添加资产到用户钱包
            memberWalletService.increaseFrozen(memberWallet.getId(), newOrder.getAmount());

            // 更新主表数据
            card.setExchangeCount(card.getExchangeCount() + 1);
            promotionCardService.saveOrUpdate(card);

            return success("兑换成功！");
        }else {
            return error("兑换失败！");
        }
    }
}
