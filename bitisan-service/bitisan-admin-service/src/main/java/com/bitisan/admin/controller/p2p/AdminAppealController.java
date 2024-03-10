package com.bitisan.admin.controller.p2p;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.*;
import com.bitisan.exception.InformationExpiredException;
import com.bitisan.p2p.entity.Appeal;
import com.bitisan.p2p.entity.OtcCoin;
import com.bitisan.p2p.entity.OtcOrder;
import com.bitisan.p2p.feign.AdvertiseFeign;
import com.bitisan.p2p.feign.AppealFeign;
import com.bitisan.p2p.feign.OtcCoinFeign;
import com.bitisan.p2p.feign.OtcOrderFeign;
import com.bitisan.p2p.vo.AppealVo;
import com.bitisan.screen.AppealScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberTransaction;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.MemberTransactionFeign;
import com.bitisan.user.feign.MemberWalletFeign;
import com.bitisan.util.BigDecimalUtils;
import com.bitisan.util.DateUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.baomidou.mybatisplus.core.toolkit.Assert.isTrue;
import static com.baomidou.mybatisplus.core.toolkit.Assert.notNull;
import static com.bitisan.util.BigDecimalUtils.add;


/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 后台申诉管理
 * @date 2019/1/23 9:26
 */
@Slf4j
@RestController
@RequestMapping("/otc/appeal")
public class AdminAppealController extends BaseAdminController {

    @Autowired
    private AppealFeign appealService;

    @Autowired
    private OtcOrderFeign orderService;

    @Autowired
    private AdvertiseFeign advertiseService;

    @Autowired
    private MemberWalletFeign memberWalletService;

    @Autowired
    private MemberFeign memberService;

    @Autowired
    private OtcCoinFeign coinService;

    @Autowired
    private LocaleMessageSourceService msService;

    @Autowired
    private MemberTransactionFeign memberTransactionService;
//    @Autowired
//    private ESUtils esUtils;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("otc:appeal:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.OTC, operation = "分页查找后台申诉Appeal")
    public MessageResult pageQuery(
            AppealScreen screen) {

        Page page = appealService.appealQuery(screen);
        return success(messageSource.getMessage("GET_SUCCESS"),IPage2Page(page));
    }

    @RequiresPermissions("otc:appeal:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.OTC, operation = "后台申诉Appeal详情")
    public MessageResult detail(
            @RequestParam(value = "id") Long id) {
        AppealVo one = appealService.findOneAppealVO(id);
        if (one == null) {
            return error("Data is empty!You should check parameter (id)!");
        }
        return success(one);
    }

    /**
     * 申诉已处理  取消订单
     *
     * @param orderSn
     * @return
     * @throws InformationExpiredException
     */
    @RequiresPermissions("otc:appeal:cancel-order")
    @RequestMapping(value = "cancel-order")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult cancelOrder(long appealId, String orderSn, @RequestParam(value = "banned", defaultValue = "false") boolean banned) throws InformationExpiredException {
        Appeal appeal = appealService.findOne(appealId);
        Assert.notNull(appeal, messageSource.getMessage("APPEAL_NOT_FOUND"));
        Long initiatorId = appeal.getInitiatorId();
        Long associateId = appeal.getAssociateId();
        OtcOrder order = orderService.findOneByOrderSn(orderSn);
        notNull(order, msService.getMessage("ORDER_NOT_EXISTS"));
        int ret = getRet(order, initiatorId, associateId);
        isTrue(ret != 0, msService.getMessage("REQUEST_ILLEGAL"));
        isTrue(order.getStatus().equals(OrderStatus.NONPAYMENT) || order.getStatus().equals(OrderStatus.PAID) || order.getStatus().equals(OrderStatus.APPEAL), msService.getMessage("ORDER_NOT_ALLOW_CANCEL"));
        //取消订单
        if (!(orderService.cancelOrder(order.getOrderSn()) > 0)) {
            throw new InformationExpiredException("Information Expired");
        }
        MessageResult result = success("");
        if (ret == 1) {
            //banned为true 禁用账户
            Member member1 = memberService.findMemberById(initiatorId);
            if (member1.getStatus() == CommonStatus.NORMAL.getCode() && banned) {
                member1.setStatus(CommonStatus.ILLEGAL.getCode());
                memberService.save(member1);
            }

            result = cancel(order, order.getNumber(), associateId);
        } else if (ret == 2) {
            Member member1 = memberService.findMemberById(initiatorId);
            if (member1.getStatus() == CommonStatus.NORMAL.getCode() && banned) {
                member1.setStatus(CommonStatus.ILLEGAL.getCode());
                memberService.save(member1);
            }
            result = cancel(order, add(order.getNumber(), order.getCommission()), associateId);
        } else if (ret == 3) {
            Member member1 = memberService.findMemberById(associateId);
            if (member1.getStatus() == CommonStatus.NORMAL.getCode() && banned) {
                member1.setStatus(CommonStatus.ILLEGAL.getCode());
                memberService.save(member1);
            }
            result = cancel(order, add(order.getNumber(), order.getCommission()), initiatorId);
        } else if (ret == 4) {
            Member member1 = memberService.findMemberById(associateId);
            if (member1.getStatus() == CommonStatus.NORMAL.getCode() && banned) {
                member1.setStatus(CommonStatus.ILLEGAL.getCode());
                memberService.save(member1);
            }
            result = cancel(order, order.getNumber(), initiatorId);
        } else {
            throw new InformationExpiredException("Information Expired");
        }
        appeal.setDealWithTime(DateUtil.getCurrentDate());
        appeal.setIsSuccess(BooleanEnum.IS_FALSE);
        appeal.setStatus(AppealStatus.PROCESSED);
        appealService.updateById(appeal);
        return result;
    }


    private MessageResult cancel(OtcOrder order , BigDecimal amount , Long memberId)  throws InformationExpiredException{
        MemberWallet memberWallet  ;
        //更改广告
        if (!advertiseService.updateAdvertiseAmountForCancel(order.getAdvertiseId(), amount)) {
            throw new InformationExpiredException("Information Expired");
        }
        OtcCoin otcCoin = coinService.findOne(order.getCoinId());
        memberWallet = memberWalletService.findByCoinUnitAndMemberId(otcCoin.getUnit(), memberId);
        log.info("======memberWallet===="+memberWallet.toString());
        MessageResult result = memberWalletService.thawBalance(memberWallet.getCoinId(),memberWallet.getMemberId(),amount);
        if (result.getCode() == 0) {
            return MessageResult.success(messageSource.getMessage("CANCEL_ORDER_SUCCESS"));
        } else {
            throw new InformationExpiredException("Information Expired");
        }
    }

    private int getRet(OtcOrder order, Long initiatorId, Long associateId) {
        int ret = 0;
        if (order.getAdvertiseType().equals(AdvertiseType.BUY) && order.getMemberId().equals(initiatorId)) {
            //代表该申诉者是广告发布者，并且是付款者   卖家associateId
            ret = 1;
        } else if (order.getAdvertiseType().equals(AdvertiseType.SELL) && order.getCustomerId().equals(initiatorId)) {
            //代表该申诉者不是广告发布者，但是是付款者   卖家associateId
            ret = 2;
        } else if (order.getAdvertiseType().equals(AdvertiseType.SELL) && order.getCustomerId().equals(associateId)) {
            //代表该申诉者是广告发布者，但不是付款者   卖家initiatorId
            ret = 3;
        } else if (order.getAdvertiseType().equals(AdvertiseType.BUY) && order.getMemberId().equals(associateId)) {
            //代表该申诉者不是广告发布者，但不是付款者  卖家initiatorId
            ret = 4;
        }
        return ret;
    }


    /**
     * 申诉处理 订单放行（放币）
     *
     * @param orderSn
     * @return
     */
    @RequiresPermissions("otc:appeal:release-coin")
    @RequestMapping(value = "release-coin")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult confirmRelease(long appealId, String orderSn, @RequestParam(value = "banned", defaultValue = "false") boolean banned) throws Exception {
        Appeal appeal = appealService.findOne(appealId);
        Assert.notNull(appeal, messageSource.getMessage("APPEAL_NOTO_FUND"));
        Long initiatorId = appeal.getInitiatorId();
        Long associateId = appeal.getAssociateId();
        // Assert.hasText(jyPassword, msService.getMessage("MISSING_JYPASSWORD"));
//        Member member = memberService.findMemberById(initiatorId);
       /* String mbPassword = member.getJyPassword();
        Assert.hasText(mbPassword, msService.getMessage("NO_SET_JYPASSWORD"));
        Assert.isTrue(Md5.md5Digest(jyPassword + member.getSalt()).toLowerCase().equals(mbPassword), msService.getMessage("ERROR_JYPASSWORD"));*/
        OtcOrder order = orderService.findOneByOrderSn(orderSn);
        notNull(order, msService.getMessage("ORDER_NOT_EXISTS"));
        int ret = getRet(order, initiatorId, associateId);
        isTrue(ret != 0, msService.getMessage("REQUEST_ILLEGAL"));
        isTrue(order.getStatus().equals(OrderStatus.PAID) || order.getStatus().equals(OrderStatus.APPEAL), msService.getMessage("ORDER_STATUS_EXPIRED"));
        if (ret == 1 || ret == 4) {
            //更改广告
            if (!advertiseService.updateAdvertiseAmountForRelease(order.getAdvertiseId(), order.getNumber())) {
                throw new InformationExpiredException("Information Expired");
            }
        } else if ((ret == 2 || ret == 3)) {
            //更改广告
            if (!advertiseService.updateAdvertiseAmountForRelease(order.getAdvertiseId(), add(order.getNumber(), order.getCommission()))) {
                throw new InformationExpiredException("Information Expired");
            }
        } else {
            throw new InformationExpiredException("Information Expired");
        }
        //放行订单
        if (!(orderService.releaseOrder(order.getOrderSn()) > 0)) {
            throw new InformationExpiredException("Information Expired");
        }
        OtcCoin otcCoin = coinService.findOne(order.getCoinId());
        //后台处理申诉结果为放行---更改买卖双方钱包
        this.transferAdmin(otcCoin,order, ret);

        if (ret == 1) {

            generateMemberTransaction(otcCoin,order, TransactionType.OTC_SELL, associateId, BigDecimal.ZERO);

            generateMemberTransaction(otcCoin,order, TransactionType.OTC_BUY, initiatorId, order.getCommission());

        } else if (ret == 2) {

            generateMemberTransaction(otcCoin,order, TransactionType.OTC_SELL, associateId, order.getCommission());

            generateMemberTransaction(otcCoin,order, TransactionType.OTC_BUY, initiatorId, BigDecimal.ZERO);

        } else if (ret == 3) {

            generateMemberTransaction(otcCoin,order, TransactionType.OTC_BUY, associateId, BigDecimal.ZERO);

            generateMemberTransaction(otcCoin,order, TransactionType.OTC_SELL, initiatorId, order.getCommission());
        } else {

            generateMemberTransaction(otcCoin,order, TransactionType.OTC_BUY, associateId, order.getCommission());

            generateMemberTransaction(otcCoin,order, TransactionType.OTC_SELL, initiatorId, BigDecimal.ZERO);
        }
        orderService.onOrderCompleted(order);

        //banned为true 禁用账户
        if (ret == 1 || ret == 2) {
            Member member1 = memberService.findMemberById(associateId);
            if (member1.getStatus() == CommonStatus.NORMAL.getCode() && banned) {
                member1.setStatus(CommonStatus.ILLEGAL.getCode());
                memberService.save(member1);
            }

        } else {
            Member member1 = memberService.findMemberById(initiatorId);
            if (member1.getStatus() == CommonStatus.NORMAL.getCode() && banned) {
                member1.setStatus(CommonStatus.ILLEGAL.getCode());
                memberService.save(member1);
            }
        }
        appeal.setDealWithTime(DateUtil.getCurrentDate());
        appeal.setIsSuccess(BooleanEnum.IS_TRUE);
        appeal.setStatus(AppealStatus.PROCESSED);
        appealService.updateById(appeal);
        return MessageResult.success(msService.getMessage("SUCCESS"));
    }


    public void transferAdmin(OtcCoin otcCoin,OtcOrder order, int ret) throws InformationExpiredException {
        if (ret == 1 || ret == 4) {
            trancerDetail(otcCoin,order, order.getCustomerId(), order.getMemberId());
        } else {
            trancerDetail(otcCoin,order, order.getMemberId(), order.getCustomerId());
        }
    }

    private void trancerDetail(OtcCoin otcCoin,OtcOrder order, long sellerId, long buyerId) throws InformationExpiredException {
        MemberWallet customerWallet = memberWalletService.findByCoinUnitAndMemberId(otcCoin.getUnit(), sellerId);
        //卖币者，买币者要处理的金额
        BigDecimal sellerAmount, buyerAmount;
        if (order.getMemberId() == sellerId) {
            sellerAmount = BigDecimalUtils.add(order.getNumber(), order.getCommission());
            buyerAmount = order.getNumber();
        } else {
            sellerAmount = order.getNumber();
            buyerAmount = order.getNumber().subtract(order.getCommission());
        }
        MessageResult is = memberWalletService.decreaseFrozen(customerWallet.getId(), sellerAmount);
        if (is.getCode() == 0) {
            MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(otcCoin.getUnit(), buyerId);
            MessageResult a = memberWalletService.increaseBalance(memberWallet.getId(), buyerAmount);
            if (a.getCode() != 0) {
                throw new InformationExpiredException("Information Expired");
            }
        } else {
            throw new InformationExpiredException("Information Expired");
        }
    }

    private void generateMemberTransaction(OtcCoin coin,OtcOrder order, TransactionType type, long memberId, BigDecimal fee) {

        MemberTransaction memberTransaction = new MemberTransaction();
        memberTransaction.setSymbol(coin.getUnit());
        memberTransaction.setType(type.getCode());
        memberTransaction.setFee(fee);
        memberTransaction.setMemberId(memberId);
        memberTransaction.setAmount(order.getNumber());
        memberTransaction.setDiscountFee("0");
        memberTransaction.setRealFee(fee+"");
        memberTransaction.setCreateTime(new Date());
        memberTransactionService.save(memberTransaction);

    }
}
