package com.bitisan.exchange.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.config.SnowflakeConfig;
import com.bitisan.constant.*;
import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.entity.ExchangeOrderDetail;
import com.bitisan.exchange.entity.OrderDetailAggregation;
import com.bitisan.exchange.mapper.ExchangeOrderMapper;
import com.bitisan.exchange.repository.ExchangeOrderDetailRepository;
import com.bitisan.exchange.repository.OrderDetailAggregationRepository;
import com.bitisan.exchange.service.ExchangeCoinService;
import com.bitisan.exchange.service.ExchangeOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitisan.exchange.util.OrderUtils;
import com.bitisan.pojo.ExchangeTrade;
import com.bitisan.screen.ExchangeOrderScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.*;
import com.bitisan.user.service.*;
import com.bitisan.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 币币交易订单表 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2022-02-07
 */
@Service
@Slf4j
public class ExchangeOrderServiceImpl extends ServiceImpl<ExchangeOrderMapper, ExchangeOrder> implements ExchangeOrderService {

    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private ExchangeOrderDetailRepository exchangeOrderDetailRepository;
    @Autowired
    private ExchangeCoinService exchangeCoinService;
    @Autowired
    private OrderDetailAggregationRepository orderDetailAggregationRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberTransactionService memberTransactionService;
    @Autowired
    private RewardPromotionSettingService rewardPromotionSettingService;
    @Autowired
    private RewardRecordService rewardRecordService;
    @Autowired
    private ExchangeOrderDetailServiceImpl exchangeOrderDetailService;
    @Value("${channel.enable:false}")
    private Boolean channelEnable;
    @Value("${channel.exchange-rate:0.00}")
    private BigDecimal channelExchangeRate;
    @Autowired
    private SnowflakeConfig snowflakeConfig;
    @Autowired
    private LocaleMessageSourceService msService;


    /**
     * 添加委托订单
     *
     * @param memberId
     * @param order
     * @return
     */
    @Transactional
    public MessageResult addOrder(Long memberId, ExchangeOrder order) {
        order.setTime(Calendar.getInstance().getTimeInMillis());
        order.setStatus(ExchangeOrderStatus.TRADING);
        order.setTradedAmount(BigDecimal.ZERO);
        order.setOrderId(snowflakeConfig.getOrderId("E"));
        log.info("add order:{}", order);
        if (order.getDirection() == ExchangeOrderDirection.BUY) {
            MemberWallet wallet = memberWalletService.findByCoinUnitAndMemberId(order.getBaseSymbol(), memberId);
            if(wallet.getIsLock().equals(BooleanEnum.IS_TRUE)){
                return MessageResult.error(500,msService.getMessage("WALLET_LOCKED"));
            }
            BigDecimal turnover;
            if (order.getType() == ExchangeOrderType.MARKET_PRICE) {
                turnover = order.getAmount();
            } else {
                turnover = order.getAmount().multiply(order.getPrice());
            }
            if (wallet.getBalance().compareTo(turnover) < 0) {
                return MessageResult.error(500, msService.getMessage("BALANCE_NOT_ENOUGH"));
            } else {
                memberWalletService.freezeBalance(wallet.getId(), turnover);
            }
        } else if (order.getDirection() == ExchangeOrderDirection.SELL) {
            MemberWallet wallet = memberWalletService.findByCoinUnitAndMemberId(order.getCoinSymbol(), memberId);
            if(wallet.getIsLock().equals(BooleanEnum.IS_TRUE)){
                return MessageResult.error(500,msService.getMessage("WALLET_LOCKED"));
            }
            if (wallet.getBalance().compareTo(order.getAmount()) < 0) {
                return MessageResult.error(500, msService.getMessage("INSUFFICIENT_COIN") + order.getCoinSymbol());
            } else {
                memberWalletService.freezeBalance(wallet.getId(), order.getAmount());
            }
        }
        if (this.save(order)) {
            return MessageResult.success(msService.getMessage("EX_CORE_SUCCESS"));
        } else {
            return MessageResult.error(500, msService.getMessage("EX_CORE_ERROR"));
        }
    }

    /**
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    public IPage<ExchangeOrder> findHistory(Long uid, String symbol, int pageNo, int pageSize) {
        IPage<ExchangeOrder> page = new Page<>(pageNo,pageSize);
        QueryWrapper<ExchangeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(symbol),"symbol",symbol);
        queryWrapper.eq(uid!=null,"member_id",uid);
        queryWrapper.ne("status",ExchangeOrderStatus.TRADING.getCode());
        queryWrapper.orderByDesc("time");
        return this.page(page,queryWrapper);
    }

    /**
     * 查询固定时间前的可删除订单
     * @param beforeTime
     * @return
     */
    public List<ExchangeOrder> queryHistoryDelete(long beforeTime, int limit){
        return this.baseMapper.queryHistoryDeleteList(beforeTime,limit);
    }

    /**
     * 删除可删除订单
     * @param beforeTime
     * @return
     */
    public int deleteHistory(long beforeTime) {
        return this.baseMapper.deleteHistory(beforeTime);
    }

    /**
     * 个人中心历史委托
     * @param uid
     * @param symbol
     * @param type
     * @param status
     * @param startTime
     * @param endTime
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<ExchangeOrder> findPersonalHistory(Long uid, String symbol, ExchangeOrderType type, ExchangeOrderStatus status, String startTime, String endTime, ExchangeOrderDirection direction, int pageNo, int pageSize) {
        Page<ExchangeOrder> page = new Page<>(pageNo,pageSize);
        QueryWrapper<ExchangeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(symbol),"symbol",symbol);
        if(type!=null){
            queryWrapper.eq("type",type.getCode());
        }
        if(direction!=null){
            queryWrapper.eq("direction",direction.getCode());
        }
        queryWrapper.eq(uid!=null,"member_id",uid);

        if (StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime)) {
            queryWrapper.ge(startTime!=null,"time",Long.valueOf(startTime));
            queryWrapper.le(endTime!=null,"time",Long.valueOf(endTime));
        }
        if (status == null) {
            queryWrapper.ne("status",ExchangeOrderStatus.TRADING.getCode());
        } else {
            queryWrapper.eq("status",status.getCode());
        }
        queryWrapper.orderByDesc("time");
        return this.page(page,queryWrapper);

    }


    /**
     * 个人中心当前委托
     *
     * @param uid
     * @param symbol
     * @param type
     * @param startTime
     * @param endTime
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<ExchangeOrder> findPersonalCurrent(Long uid, String symbol, ExchangeOrderType type, String startTime, String endTime, ExchangeOrderDirection direction, int pageNo, int pageSize) {
        Page<ExchangeOrder> page = new Page<>(pageNo,pageSize);
        QueryWrapper<ExchangeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(symbol),"symbol",symbol);
        if(type!=null){
            queryWrapper.eq("type",type.getCode());
        }
        if(direction!=null){
            queryWrapper.eq("direction",direction.getCode());
        }
        queryWrapper.eq(uid!=null,"member_id",uid);

        if (StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime)) {
            queryWrapper.ge("time",Long.valueOf(startTime));
            queryWrapper.le("time",Long.valueOf(endTime));
        }
        queryWrapper.eq("status",ExchangeOrderStatus.TRADING.getCode());
        queryWrapper.orderByDesc("time");
        return this.page(page,queryWrapper);

    }

    /**
     * 查询当前交易中的委托
     *
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<ExchangeOrder> findCurrent(Long uid, String symbol, int pageNo, int pageSize) {
        Page<ExchangeOrder> page = new Page<>(pageNo,pageSize);
        QueryWrapper<ExchangeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(symbol),"symbol",symbol);
        queryWrapper.eq(uid!=null,"member_id",uid);
        queryWrapper.eq("status",ExchangeOrderStatus.TRADING.getCode());
        queryWrapper.orderByDesc("time");
        return this.page(page,queryWrapper);
    }


    /**
     * 处理交易匹配
     *
     * @param trade
     * @param secondReferrerAward 二级推荐人是否返回佣金 true 返回佣金
     * @return
     * @throws Exception
     */
    @Transactional
    public MessageResult processExchangeTrade(ExchangeTrade trade, boolean secondReferrerAward) throws Exception {
        log.info("processExchangeTrade,trade = {}", trade);
        if (trade == null || trade.getBuyOrderId() == null || trade.getSellOrderId() == null) {
            return MessageResult.error(500, "trade is null");
        }
        ExchangeOrder buyOrder = this.baseMapper.findByOrderId(trade.getBuyOrderId());
        ExchangeOrder sellOrder = this.baseMapper.findByOrderId(trade.getSellOrderId());
        if (buyOrder == null || sellOrder == null) {
            log.error("order not found");
            return MessageResult.error(500, "order not found");
        }
        //获取手续费率
        ExchangeCoin coin = exchangeCoinService.findBySymbol(buyOrder.getSymbol());
        if (coin == null) {
            log.error("invalid trade symbol {}", buyOrder.getSymbol());
            return MessageResult.error(500, "invalid trade symbol {}" + buyOrder.getSymbol());
        }
        // 根据memberId锁表，防止死锁
//        this.baseMapper.selectByMemberIdForUpdate(buyOrder.getMemberId());
//        if(!buyOrder.getMemberId().equals(sellOrder.getMemberId())) {
//            this.baseMapper.selectByMemberIdForUpdate(sellOrder.getMemberId());
//        }
        //处理买入订单 手续费 是交易币  交易币对usdtRat
        processOrder(buyOrder, trade, coin, secondReferrerAward);
        //处理卖出订单 手续费是基准币 基准币对usdtRat
        processOrder(sellOrder, trade, coin, secondReferrerAward);
        return MessageResult.success("process success");
    }

    /**
     * 对发生交易的委托处理相应的钱包
     *
     * @param order               委托订单
     * @param trade               交易详情
     * @param coin                交易币种信息，包括手续费 交易币种信息等等
     * @param secondReferrerAward 二级推荐人是否返佣
     * @return
     */
    public void processOrder(ExchangeOrder order, ExchangeTrade trade, ExchangeCoin coin,boolean secondReferrerAward) {
        try {
            Long time = Calendar.getInstance().getTimeInMillis();
            //添加成交详情
            ExchangeOrderDetail orderDetail = new ExchangeOrderDetail();
            orderDetail.setOrderId(order.getOrderId());
            orderDetail.setTime(time);
            orderDetail.setPrice(trade.getPrice());
            orderDetail.setAmount(trade.getAmount());

            BigDecimal incomeCoinAmount, turnover, outcomeCoinAmount;
            if (order.getDirection() == ExchangeOrderDirection.BUY) {
                turnover = trade.getBuyTurnover();
            } else {
                turnover = trade.getSellTurnover();
            }
            orderDetail.setTurnover(turnover);
            //手续费，买入订单收取coin,卖出订单收取baseCoin
            BigDecimal fee;
            if (order.getDirection() == ExchangeOrderDirection.BUY) {
                fee = trade.getAmount().multiply(coin.getFee());
            } else {
                fee = turnover.multiply(coin.getFee());
            }
            // ID为1的用户默认为机器人，此处当订单用户ID为机器人时，不收取手续费
            // ID为10001的用户默认为超级管理员，此处当订单用户ID为机器人时，不收取手续费
            if(order.getMemberId() == 1 || order.getMemberId() == 10001) {
                fee = BigDecimal.ZERO;
            }
            orderDetail.setFee(fee);
            exchangeOrderDetailRepository.save(orderDetail);

            /**
             * 聚合币币交易订单手续费明细存入mongodb
             */
            OrderDetailAggregation aggregation = new OrderDetailAggregation();
            aggregation.setType(OrderTypeEnum.EXCHANGE);
            aggregation.setAmount(order.getAmount().doubleValue());
            aggregation.setFee(orderDetail.getFee().doubleValue());
            aggregation.setTime(orderDetail.getTime());
            aggregation.setDirection(order.getDirection());
            aggregation.setOrderId(order.getOrderId());
            if (order.getDirection() == ExchangeOrderDirection.BUY) {
                aggregation.setUnit(order.getBaseSymbol());
            } else {
                aggregation.setUnit(order.getCoinSymbol());
            }
            Member member = memberService.getById(order.getMemberId());
            if (member != null) {
                aggregation.setMemberId(member.getId());
                aggregation.setUsername(member.getUsername());
                aggregation.setRealName(member.getRealName());
            }
            orderDetailAggregationRepository.save(aggregation);

            //增加回报的可用的币,处理账户增加的币种，买入的时候获得交易币，卖出的时候获得基币
            if (order.getDirection() == ExchangeOrderDirection.BUY) {
                incomeCoinAmount = trade.getAmount().subtract(fee);
            } else {
                incomeCoinAmount = turnover.subtract(fee);
            }
            String incomeSymbol = order.getDirection() == ExchangeOrderDirection.BUY ? order.getCoinSymbol() : order.getBaseSymbol();
            MemberWallet incomeWallet = memberWalletService.findByCoinUnitAndMemberId(incomeSymbol, order.getMemberId());

            memberWalletService.increaseBalance(incomeWallet.getId(), incomeCoinAmount);
            //扣除付出的币，买入的时候算成交额，卖出的算成交量
            String outcomeSymbol = order.getDirection() == ExchangeOrderDirection.BUY ? order.getBaseSymbol() : order.getCoinSymbol();
            if (order.getDirection() == ExchangeOrderDirection.BUY) {
                outcomeCoinAmount = turnover;
            } else {
                outcomeCoinAmount = trade.getAmount();
            }
            MemberWallet outcomeWallet = memberWalletService.findByCoinUnitAndMemberId(outcomeSymbol, order.getMemberId());
            memberWalletService.decreaseFrozen(outcomeWallet.getId(), outcomeCoinAmount);

            //增加入资金记录
            MemberTransaction transaction = new MemberTransaction();
            transaction.setAmount(incomeCoinAmount);
            transaction.setSymbol(incomeSymbol);
            transaction.setAddress("");
            transaction.setMemberId(incomeWallet.getMemberId());
            transaction.setType(TransactionType.EXCHANGE.getCode());
            transaction.setCreateTime(new Date());
            //原手续费
            transaction.setFee(fee);
            //折扣手续费
            transaction.setDiscountFee("0");
            //实收手续费
            transaction.setRealFee(fee.toString());
            memberTransactionService.save(transaction);

            //增加出资金记录
            MemberTransaction transaction2 = new MemberTransaction();
            transaction2.setAmount(outcomeCoinAmount.negate());
            transaction2.setSymbol(outcomeSymbol);
            transaction2.setAddress("");
            transaction2.setMemberId(incomeWallet.getMemberId());
            transaction2.setType(TransactionType.EXCHANGE.getCode());
            transaction2.setFee(BigDecimal.ZERO);
            transaction2.setRealFee("0");
            transaction2.setDiscountFee("0");
            transaction2.setCreateTime(new Date());
            memberTransactionService.save(transaction2);
            try {
                // 只对基础币手续费进行返佣
                if (order.getDirection() == ExchangeOrderDirection.SELL) {
                    promoteReward(fee, member, incomeSymbol, secondReferrerAward);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("发放币币交易推广手续费佣金出错", e);
            }
        } catch (Exception e) {
            log.info(">>>>>处理交易明细出错>>>>>>>>>{}",e);
            e.printStackTrace();
        }
    }

    public List<ExchangeOrderDetail> getAggregation(String orderId) {
        return exchangeOrderDetailService.findAllByOrderId(orderId);
    }


    /**
     * 渠道币币交易返佣  新版本 方案
     * @param member
     * @param symbol
     * @param fee
     */
//    public void processChannelReward(Member member,String symbol,BigDecimal fee){
//        MemberWallet channelWallet =  memberWalletService.findByCoinUnitAndMemberId(symbol,member.getChannelId());
//        if(channelWallet != null && fee.compareTo(BigDecimal.ZERO) > 0 ){
//            BigDecimal amount = fee.multiply(channelExchangeRate);
//            memberWalletService.increaseBalance(channelWallet.getId(),amount);
//            MemberTransaction memberTransaction = new MemberTransaction();
//            memberTransaction.setAmount(amount);
//            memberTransaction.setFee(BigDecimal.ZERO);
//            memberTransaction.setMemberId(member.getChannelId());
//            memberTransaction.setSymbol(symbol);
//            memberTransaction.setType(TransactionType.CHANNEL_AWARD);
//            transactionService.save(memberTransaction);
//        }
//    }

    /**
     * 交易手续费返佣金
     *
     * @param fee                 手续费
     * @param member              订单拥有者
     * @param incomeSymbol        币种
     * @param secondReferrerAward 二级推荐人是否返佣控制
     */
    public void promoteReward(BigDecimal fee, Member member, String incomeSymbol, boolean secondReferrerAward) {
        RewardPromotionSetting rewardPromotionSetting = rewardPromotionSettingService.findByType(PromotionRewardType.EXCHANGE_TRANSACTION);
        if (rewardPromotionSetting != null && member.getInviterId() != null) {
            if (!(DateUtil.diffDays(new Date(), member.getRegistrationTime()) > rewardPromotionSetting.getEffectiveTime())) {
                Member member1 = memberService.getById(member.getInviterId());
                MemberWallet memberWallet =  memberWalletService.findByCoinUnitAndMemberId(incomeSymbol, member.getInviterId());
                JSONObject jsonObject = JSONObject.parseObject(rewardPromotionSetting.getInfo());
                BigDecimal reward = BigDecimalUtils.mulRound(fee, BigDecimalUtils.getRate(jsonObject.getBigDecimal("one")), 8);
                if (reward.compareTo(BigDecimal.ZERO) > 0) {
                    memberWalletService.increaseBalance(memberWallet.getId(), reward);
                    MemberTransaction memberTransaction = new MemberTransaction();
                    memberTransaction.setAmount(reward);
                    memberTransaction.setFee(BigDecimal.ZERO);
                    memberTransaction.setMemberId(member1.getId());
                    memberTransaction.setSymbol(incomeSymbol);
                    memberTransaction.setType(TransactionType.PROMOTION_AWARD.getCode());
                    memberTransaction.setDiscountFee("0");
                    memberTransaction.setRealFee("0");
                    memberTransaction.setCreateTime(new Date());
                    memberTransactionService.save(memberTransaction);
                    RewardRecord rewardRecord1 = new RewardRecord();
                    rewardRecord1.setAmount(reward);
                    rewardRecord1.setCoinId(memberWallet.getCoinId());
                    rewardRecord1.setMemberId(member1.getId());
                    rewardRecord1.setRemark(rewardPromotionSetting.getType().getDescription());
                    rewardRecord1.setType(RewardRecordType.PROMOTION);
                    rewardRecordService.save(rewardRecord1);
                }

                // 控制推荐人推荐是否返佣 等于false是二级推荐人不返佣
                if (secondReferrerAward == false) {
                    log.info("控制字段 : secondReferrerAward ={} , 跳过二级推荐人返佣", secondReferrerAward);
                    return;
                }
                if (member1.getInviterId() != null && !(DateUtil.diffDays(new Date(), member1.getRegistrationTime()) > rewardPromotionSetting.getEffectiveTime())) {
                    Member member2 = memberService.getById(member1.getInviterId());
                    MemberWallet memberWallet1 = memberWalletService.findByCoinUnitAndMemberId(incomeSymbol, member2.getId());

                    BigDecimal reward1 = BigDecimalUtils.mulRound(fee, BigDecimalUtils.getRate(jsonObject.getBigDecimal("two")), 8);
                    if (reward1.compareTo(BigDecimal.ZERO) > 0) {
                        //memberWallet1.setBalance(BigDecimalUtils.add(memberWallet1.getBalance(), reward));
                        memberWalletService.increaseBalance(memberWallet1.getId(), reward);
                        MemberTransaction memberTransaction = new MemberTransaction();
                        memberTransaction.setAmount(reward1);
                        memberTransaction.setFee(BigDecimal.ZERO);
                        memberTransaction.setMemberId(member2.getId());
                        memberTransaction.setSymbol(incomeSymbol);
                        memberTransaction.setType(TransactionType.PROMOTION_AWARD.getCode());
                        memberTransaction.setCreateTime(new Date());
                        memberTransactionService.save(memberTransaction);

                        RewardRecord rewardRecord1 = new RewardRecord();
                        rewardRecord1.setAmount(reward1);
                        rewardRecord1.setCoinId(memberWallet1.getCoinId());
                        rewardRecord1.setMemberId(member2.getId());
                        rewardRecord1.setRemark(rewardPromotionSetting.getType().getDescription());
                        rewardRecord1.setType(RewardRecordType.PROMOTION);
                        rewardRecordService.save(rewardRecord1);
                    }
                }
            }
        }
    }

    /**
     * 查询所有未完成的挂单
     *
     * @param symbol 交易对符号
     * @return
     */
    public List<ExchangeOrder> findAllTradingOrderBySymbol(String symbol) {
        QueryWrapper<ExchangeOrder> query = new QueryWrapper<>();
        query.eq("symbol",symbol);
        query.eq("status",ExchangeOrderStatus.TRADING.getCode());
        return this.list(query);
    }


//    @Transactional(readOnly = true)
//    public PageResult<ExchangeOrder> queryWhereOrPage(List<Predicate> predicates, Integer pageNo, Integer pageSize) {
//        List<ExchangeOrder> list;
//        JPAQuery<ExchangeOrder> jpaQuery = queryFactory.selectFrom(QExchangeOrder.exchangeOrder);
//        if (predicates != null) {
//            jpaQuery.where(predicates.toArray(new BooleanExpression[predicates.size()]));
//        }
//        jpaQuery.orderBy(QExchangeOrder.exchangeOrder.time.desc());
//        if (pageNo != null && pageSize != null) {
//            list = jpaQuery.offset((pageNo - 1) * pageSize).limit(pageSize).fetch();
//        } else {
//            list = jpaQuery.fetch();
//        }
//        PageResult<ExchangeOrder> result = new PageResult<>(list, jpaQuery.fetchCount());
//        result.setNumber(pageNo);
//        result.setSize(pageSize);
//        return result;
//    }

    /**
     * 订单交易完成
     *
     * @param orderId
     * @return
     */
    @Transactional
    public MessageResult tradeCompleted(String orderId, BigDecimal tradedAmount, BigDecimal turnover) {
        ExchangeOrder order = this.baseMapper.findByOrderId(orderId);
        if(order==null){
            log.error("order:(" + orderId + "),不存在");
            return MessageResult.error(500, "order:(" + orderId + "),不存在");
        }
        if (order.getStatus()!=null && order.getStatus() != ExchangeOrderStatus.TRADING) {
            return MessageResult.error(500, "invalid order(" + orderId + "),not trading status");
        }
        order.setTradedAmount(tradedAmount);
        order.setTurnover(turnover);
        order.setStatus(ExchangeOrderStatus.COMPLETED);
        order.setCompletedTime(Calendar.getInstance().getTimeInMillis());
        this.updateById(order);

        //处理用户钱包,对冻结作处理，剩余成交额退回
        orderRefund(order, tradedAmount, turnover);

        return MessageResult.success("tradeCompleted success");
    }

    /**
     * 委托退款，如果取消订单或成交完成有剩余
     *
     * @param order
     * @param tradedAmount
     * @param turnover
     */
    public void orderRefund(ExchangeOrder order, BigDecimal tradedAmount, BigDecimal turnover) {
        //下单时候冻结的币，实际成交应扣的币
        BigDecimal frozenBalance, dealBalance;
        if (order.getDirection() == ExchangeOrderDirection.BUY) {
            if (order.getType() == ExchangeOrderType.LIMIT_PRICE) {
                frozenBalance = order.getAmount().multiply(order.getPrice());
            } else {
                frozenBalance = order.getAmount();
            }
            dealBalance = turnover;
        } else {
            frozenBalance = order.getAmount();
            dealBalance = tradedAmount;
        }
        String coinSymbol = order.getDirection() == ExchangeOrderDirection.BUY ? order.getBaseSymbol() : order.getCoinSymbol();
        //减少付出的冻结的币
        BigDecimal refundAmount = frozenBalance.subtract(dealBalance);
        System.out.println("退币：" + refundAmount);
        log.info("===cancel==退币：" + refundAmount);
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(coinSymbol,order.getMemberId());
            if(memberWallet==null){
                log.error("===cancel==退币：钱包不存在::"+coinSymbol+" memberId:" + order.getMemberId());
                return ;
            }
            memberWalletService.thawBalance(memberWallet.getId(), refundAmount);
        }
    }

    /**
     * 取消订单
     *
     * @param orderId 订单编号
     * @return
     */
    @Transactional
    public MessageResult cancelOrder(String orderId, BigDecimal tradedAmount, BigDecimal turnover) {
        ExchangeOrder order = this.getById(orderId);
        if (order == null) {
            return MessageResult.error("order not exists");
        }
        if (order.getStatus() != ExchangeOrderStatus.TRADING) {
            return MessageResult.error(500, "order not in trading");
        }
        order.setTradedAmount(tradedAmount);
        order.setTurnover(turnover);
        order.setStatus(ExchangeOrderStatus.CANCELED);
        order.setCanceledTime(Calendar.getInstance().getTimeInMillis());
        this.updateById(order);
        //未成交的退款
        orderRefund(order, tradedAmount, turnover);
        return MessageResult.success();
    }


    /**
     * 获取某交易对当日已取消次数
     *
     * @param uid
     * @param symbol
     * @return
     */
    public long findTodayOrderCancelTimes(Long uid, String symbol) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTick = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR_OF_DAY, 24);
        long endTick = calendar.getTimeInMillis();

        QueryWrapper<ExchangeOrder> query = new QueryWrapper<>();
        query.eq("symbol",symbol);
        query.eq("member_id",uid);
        query.eq("status",ExchangeOrderStatus.CANCELED.getCode());
        query.ge("canceledTime",startTick);
        query.lt("canceledTime",endTick);
        return this.count(query);

    }

    /**
     * 查询当前正在交易的订单数量
     *
     * @param uid
     * @param symbol
     * @return
     */
    public long findCurrentTradingCount(Long uid, String symbol) {

        QueryWrapper<ExchangeOrder> query = new QueryWrapper<>();
        query.eq("symbol",symbol);
        query.eq("member_id",uid);
        query.eq("status",ExchangeOrderStatus.TRADING.getCode());
        return this.count(query);
    }

    public long findCurrentTradingCount(Long uid, String symbol, ExchangeOrderDirection direction) {

        QueryWrapper<ExchangeOrder> query = new QueryWrapper<>();
        query.eq("symbol",symbol);
        query.eq("member_id",uid);
        query.eq("direction",direction.getCode());
        query.eq("status",ExchangeOrderStatus.TRADING.getCode());
        return this.count(query);

    }

    public List<ExchangeOrder> findOvertimeOrder(String symbol, int maxTradingTime) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -maxTradingTime);
        long tickTime = calendar.getTimeInMillis();
        QueryWrapper<ExchangeOrder> query = new QueryWrapper<>();
        query.eq("symbol",symbol);
        query.eq("status",ExchangeOrderStatus.TRADING.getCode());
        query.lt("time",tickTime);
        return this.list(query);
    }

    /**
     * 查询符合状态的订单
     *
     * @param cancelTime
     * @return
     */
    public List<ExchangeOrder> queryExchangeOrderByTime(long cancelTime) {
        return this.baseMapper.queryExchangeOrderByTime(cancelTime);
    }
    public List<ExchangeOrder> queryExchangeOrderByTimeById(long cancelTime,long sellMemberId,long buyMemberId) {
        return this.baseMapper.queryExchangeOrderByTimeById(cancelTime,sellMemberId,buyMemberId);
    }

    /**
     * 查询符合状态的订单
     *
     * @param cancelTime
     * @return
     */

    public List<ExchangeOrder> queryExchangeOrderByTimeById(long cancelTime) {
        return this.baseMapper.queryExchangeOrderByTimeById(cancelTime);
    }
    /**
     * API 添加订单接口
     *
     * @param memberId
     * @param order
     * @return
     */
    @Transactional
    public String addOrderForApi(Long memberId, ExchangeOrder order) {
        order.setTime(Calendar.getInstance().getTimeInMillis());
        order.setStatus(ExchangeOrderStatus.TRADING);
        order.setTradedAmount(BigDecimal.ZERO);
        order.setOrderId(snowflakeConfig.getOrderId("E"));
        log.info("add order:{}", order);
        if (order.getDirection() == ExchangeOrderDirection.BUY) {
            MemberWallet wallet = memberWalletService.findByCoinUnitAndMemberId(order.getBaseSymbol(), memberId);
            BigDecimal turnover;
            if (order.getType() == ExchangeOrderType.MARKET_PRICE) {
                turnover = order.getAmount();
            } else {
                turnover = order.getAmount().multiply(order.getPrice());
            }
            if (wallet.getBalance().compareTo(turnover) < 0) {
                return null;
            } else {
                memberWalletService.freezeBalance(wallet.getId(), turnover);
                //wallet.setBalance(wallet.getBalance().subtract(turnover));
                //wallet.setFrozenBalance(wallet.getFrozenBalance().add(turnover));
            }
        } else if (order.getDirection() == ExchangeOrderDirection.SELL) {

            MemberWallet wallet = memberWalletService.findByCoinUnitAndMemberId(order.getCoinSymbol(), memberId);
            if (wallet.getBalance().compareTo(order.getAmount()) < 0) {
                return null;
            } else {
                memberWalletService.freezeBalance(wallet.getId(), order.getAmount());
                //wallet.setBalance(wallet.getBalance().subtract(order.getAmount()));
                //wallet.setFrozenBalance(wallet.getFrozenBalance().add(order.getAmount()));
            }
        }
        this.saveOrUpdate(order);
        return order.getOrderId();
    }

    /**
     * Api 查询订单接口
     *
     * @param memberId
     * @param symbol
     * @param direction
     * @return
     */
    public Page<ExchangeOrder> findCurrentTradingOrderForApi(long memberId, String symbol, ExchangeOrderDirection direction, int pageNo, int pageSize) {

        Page<ExchangeOrder> page = new Page<>(pageNo,pageSize);
        QueryWrapper<ExchangeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(symbol),"symbol",symbol);
        queryWrapper.eq("member_id",memberId);
        queryWrapper.eq("status",ExchangeOrderStatus.TRADING.getCode());
        queryWrapper.eq("direction",direction.getCode());
        queryWrapper.orderByDesc("time");
        return this.page(page,queryWrapper);

    }


    /**
     * 强制取消订单,在撮合中心和数据库订单不一致的情况下使用
     * @param order
     */
    @Transactional
    public void forceCancelOrder(ExchangeOrder order){
        List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(order.getOrderId());
        BigDecimal tradedAmount = BigDecimal.ZERO;
        BigDecimal turnover = BigDecimal.ZERO;
        for(ExchangeOrderDetail trade:details){
            tradedAmount = tradedAmount.add(trade.getAmount());
            turnover = turnover.add(trade.getAmount().multiply(trade.getPrice()));
        }
        order.setTradedAmount(tradedAmount);
        order.setTurnover(turnover);
        if(OrderUtils.isCompleted(order)){
            tradeCompleted(order.getOrderId(),order.getTradedAmount(),order.getTurnover());
        }
        else{
            cancelOrder(order.getOrderId(),order.getTradedAmount(),order.getTurnover());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        this.removeById(id);
    }

    @Override
    public Page<ExchangeOrder> findAll(ExchangeOrderScreen screen) {
        Page<ExchangeOrder> page = new Page<>(screen.getPageNo(),screen.getPageSize());
        LambdaQueryWrapper<ExchangeOrder> queryWrapper = new LambdaQueryWrapper<>();

        if (screen.getOrderDirection() != null) {
            queryWrapper.eq(ExchangeOrder::getDirection,screen.getOrderDirection().getCode());
        }
        if (StringUtils.isNotEmpty(screen.getOrderId())) {
            queryWrapper.eq(ExchangeOrder::getOrderId,screen.getOrderId());
        }
        if (screen.getMemberId() != null) {
            queryWrapper.eq(ExchangeOrder::getMemberId,screen.getMemberId());
        }
        if (screen.getType() != null) {
            queryWrapper.eq(ExchangeOrder::getType,screen.getType().getCode());
        }
        if (StringUtils.isNotBlank(screen.getCoinSymbol())) {
            queryWrapper.eq(ExchangeOrder::getCoinSymbol,screen.getCoinSymbol());
        }
        if (StringUtils.isNotBlank(screen.getBaseSymbol())) {
            queryWrapper.eq(ExchangeOrder::getBaseSymbol,screen.getBaseSymbol());
        }
        if (screen.getStatus() != null) {
            queryWrapper.eq(ExchangeOrder::getStatus,screen.getStatus().getCode());
        }
        if (screen.getMinPrice()!=null) {
            queryWrapper.ge(ExchangeOrder::getPrice,screen.getMinPrice());
        }
        if (screen.getMaxPrice()!=null) {
            queryWrapper.le(ExchangeOrder::getPrice,screen.getMaxPrice());
        }
        if (screen.getMinTradeAmount()!=null) {
            queryWrapper.ge(ExchangeOrder::getTradedAmount,screen.getMinTradeAmount());
        }
        if (screen.getMaxTradeAmount()!=null) {
            queryWrapper.le(ExchangeOrder::getTradedAmount,screen.getMaxTradeAmount());
        }
        if (screen.getMinTurnOver()!=null) {
            queryWrapper.ge(ExchangeOrder::getTurnover,screen.getMinTurnOver());
        }
        if (screen.getMaxTurnOver()!=null) {
            queryWrapper.le(ExchangeOrder::getTurnover,screen.getMaxTurnOver());
        }
        if (screen.getRobotOrder()!=null&&screen.getRobotOrder() == 1){
            //不看机器人（不包含机器人）
            queryWrapper.notIn(ExchangeOrder::getMemberId,1,2,10001);
//            booleanExpressions.add(qExchangeOrder.memberId.notIn(69296 , 52350));
        }
        if (screen.getRobotOrder()!=null&&screen.getRobotOrder() == 0){
            //查看机器人
            queryWrapper.in(ExchangeOrder::getMemberId,1,2,10001);
//            booleanExpressions.add(qExchangeOrder.memberId.in(69296 , 52350));

        }
        if(screen.getCompleted()!=null)
        /**
         * 委托订单
         */ {
            if(screen.getCompleted()== BooleanEnum.IS_FALSE){
                queryWrapper.isNull(ExchangeOrder::getCompletedTime);
                queryWrapper.isNull(ExchangeOrder::getCanceledTime);
                queryWrapper.eq(ExchangeOrder::getStatus,ExchangeOrderStatus.TRADING.getCode());
            }else{
                /**
                 * 历史订单
                 */
                queryWrapper.and(wrapper->wrapper.isNotNull(ExchangeOrder::getCompletedTime)
                        .or().isNotNull(ExchangeOrder::getCanceledTime)
                        .or().ne(ExchangeOrder::getStatus,ExchangeOrderStatus.TRADING.getCode())
                );
            }
        }
        queryWrapper.orderByDesc(ExchangeOrder::getTime);

        return this.page(page,queryWrapper);
    }

    @Override
    public List<ExchangeOrder> getExchangeTurnoverBase(String dateStr) {
        return this.baseMapper.getExchangeTurnoverBase(dateStr);
    }

    @Override
    public List<ExchangeOrder> getExchangeTurnoverCoin(String dateStr) {
        return this.baseMapper.getExchangeTurnoverCoin(dateStr);
    }

    @Override
    public List<ExchangeOrder> getExchangeTurnoverSymbol(String dateStr) {
        return this.baseMapper.getExchangeTurnoverSymbol(dateStr);
    }

}
