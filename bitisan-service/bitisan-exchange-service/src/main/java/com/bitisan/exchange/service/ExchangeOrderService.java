package com.bitisan.exchange.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bitisan.constant.ExchangeOrderDirection;
import com.bitisan.constant.ExchangeOrderStatus;
import com.bitisan.constant.ExchangeOrderType;
import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.entity.ExchangeOrderDetail;
import com.bitisan.pojo.ExchangeTrade;
import com.bitisan.screen.ExchangeOrderScreen;
import com.bitisan.user.entity.Member;
import com.bitisan.util.MessageResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 币币交易订单表 服务类
 * </p>
 *
 * @author markchao
 * @since 2022-02-07
 */
public interface ExchangeOrderService extends IService<ExchangeOrder> {
    /**
     * 添加委托订单
     *
     * @param memberId
     * @param order
     * @return
     */
    public MessageResult addOrder(Long memberId, ExchangeOrder order);

    /**
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    public IPage<ExchangeOrder> findHistory(Long uid, String symbol, int pageNo, int pageSize) ;

    /**
     * 查询固定时间前的可删除订单
     * @param beforeTime
     * @return
     */
    public List<ExchangeOrder> queryHistoryDelete(long beforeTime, int limit);
    /**
     * 删除可删除订单
     * @param beforeTime
     * @return
     */
    public int deleteHistory(long beforeTime) ;

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
    public Page<ExchangeOrder> findPersonalHistory(Long uid, String symbol, ExchangeOrderType type, ExchangeOrderStatus status, String startTime, String endTime, ExchangeOrderDirection direction, int pageNo, int pageSize);


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
    public Page<ExchangeOrder> findPersonalCurrent(Long uid, String symbol, ExchangeOrderType type, String startTime, String endTime, ExchangeOrderDirection direction, int pageNo, int pageSize);

    /**
     * 查询当前交易中的委托
     *
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<ExchangeOrder> findCurrent(Long uid, String symbol, int pageNo, int pageSize) ;


    /**
     * 处理交易匹配
     *
     * @param trade
     * @param secondReferrerAward 二级推荐人是否返回佣金 true 返回佣金
     * @return
     * @throws Exception
     */
    public MessageResult processExchangeTrade(ExchangeTrade trade, boolean secondReferrerAward) throws Exception ;

    /**
     * 对发生交易的委托处理相应的钱包
     *
     * @param order               委托订单
     * @param trade               交易详情
     * @param coin                交易币种信息，包括手续费 交易币种信息等等
     * @param secondReferrerAward 二级推荐人是否返佣
     * @return
     */
    public void processOrder(ExchangeOrder order, ExchangeTrade trade, ExchangeCoin coin,boolean secondReferrerAward) ;

    public List<ExchangeOrderDetail> getAggregation(String orderId) ;



    /**
     * 交易手续费返佣金
     *
     * @param fee                 手续费
     * @param member              订单拥有者
     * @param incomeSymbol        币种
     * @param secondReferrerAward 二级推荐人是否返佣控制
     */
    public void promoteReward(BigDecimal fee, Member member, String incomeSymbol, boolean secondReferrerAward) ;

    /**
     * 查询所有未完成的挂单
     *
     * @param symbol 交易对符号
     * @return
     */
    public List<ExchangeOrder> findAllTradingOrderBySymbol(String symbol) ;


    /**
     * 订单交易完成
     *
     * @param orderId
     * @return
     */
    public MessageResult tradeCompleted(String orderId, BigDecimal tradedAmount, BigDecimal turnover) ;

    /**
     * 委托退款，如果取消订单或成交完成有剩余
     *
     * @param order
     * @param tradedAmount
     * @param turnover
     */
    public void orderRefund(ExchangeOrder order, BigDecimal tradedAmount, BigDecimal turnover) ;

    /**
     * 取消订单
     *
     * @param orderId 订单编号
     * @return
     */
    public MessageResult cancelOrder(String orderId, BigDecimal tradedAmount, BigDecimal turnover) ;


    /**
     * 获取某交易对当日已取消次数
     *
     * @param uid
     * @param symbol
     * @return
     */
    public long findTodayOrderCancelTimes(Long uid, String symbol) ;

    /**
     * 查询当前正在交易的订单数量
     *
     * @param uid
     * @param symbol
     * @return
     */
    public long findCurrentTradingCount(Long uid, String symbol) ;

    public long findCurrentTradingCount(Long uid, String symbol, ExchangeOrderDirection direction) ;

    public List<ExchangeOrder> findOvertimeOrder(String symbol, int maxTradingTime) ;

    /**
     * 查询符合状态的订单
     *
     * @param cancelTime
     * @return
     */
    public List<ExchangeOrder> queryExchangeOrderByTime(long cancelTime) ;

    public List<ExchangeOrder> queryExchangeOrderByTimeById(long cancelTime,long sellMemberId,long buyMemberId) ;

    /**
     * 查询符合状态的订单
     *
     * @param cancelTime
     * @return
     */

    public List<ExchangeOrder> queryExchangeOrderByTimeById(long cancelTime) ;
    /**
     * API 添加订单接口
     *
     * @param memberId
     * @param order
     * @return
     */
    public String addOrderForApi(Long memberId, ExchangeOrder order) ;

    /**
     * Api 查询订单接口
     *
     * @param memberId
     * @param symbol
     * @param direction
     * @return
     */
    public Page<ExchangeOrder> findCurrentTradingOrderForApi(long memberId, String symbol, ExchangeOrderDirection direction, int pageNo, int pageSize) ;


    /**
     * 强制取消订单,在撮合中心和数据库订单不一致的情况下使用
     * @param order
     */
    public void forceCancelOrder(ExchangeOrder order);

    public void delete(String id) ;

    public Page<ExchangeOrder> findAll(ExchangeOrderScreen screen);

    public List<ExchangeOrder> getExchangeTurnoverBase(String dateStr);

    public List<ExchangeOrder> getExchangeTurnoverCoin(String dateStr);

    public List<ExchangeOrder> getExchangeTurnoverSymbol(String dateStr);
}
