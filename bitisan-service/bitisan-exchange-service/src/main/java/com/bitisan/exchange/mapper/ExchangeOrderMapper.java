package com.bitisan.exchange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitisan.exchange.entity.ExchangeOrder;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 币币挂单 Mapper 接口
 * </p>
 *
 * @author markchao
 * @since 2022-02-07
 */
public interface ExchangeOrderMapper extends BaseMapper<ExchangeOrder> {

    ExchangeOrder findByOrderId(@Param("orderId")String orderId);

    @Update("update exchange_order set traded_amount = traded_amount + #{amount}  where order_id = #{orderId}")
    int increaseTradeAmount(@Param("amount") BigDecimal amount, @Param("orderId")String orderId);


    @Update("update exchange_order set status = #{status} where order_id = :orderId")
    int updateStatus(@Param("orderId") String orderId, @Param("status") Integer status);

    @Select("select coin_symbol,FROM_UNIXTIME(completed_time/1000, '%Y-%m-%d') as completed_time,sum(traded_amount) as traded_amount from exchange_order where FROM_UNIXTIME(completed_time/1000, '%Y-%m-%d') = #{date} and direction = 1 and status = 1 group by coin_symbol")
    List<ExchangeOrder> getExchangeTurnoverCoin(@Param("date") String date);

    @Select("select base_symbol,FROM_UNIXTIME(completed_time/1000, '%Y-%m-%d') as completed_time,sum(turnover) turnover from exchange_order where FROM_UNIXTIME(completed_time/1000, '%Y-%m-%d') = #{date} and direction = 1 and status = 1 group by base_symbol")
    List<ExchangeOrder> getExchangeTurnoverBase(@Param("date") String date);

    @Select("select base_symbol,coin_symbol,FROM_UNIXTIME(completed_time/1000, '%Y-%m-%d') as completed_time,sum(traded_amount) as traded_amount,sum(turnover) as turnover from exchange_order where FROM_UNIXTIME(completed_time/1000, '%Y-%m-%d') = #{date} and direction = 1 and status = 1 group by base_symbol,coin_symbol")
    List<ExchangeOrder> getExchangeTurnoverSymbol(@Param("date") String date) ;

    @Select("select exchange.* from exchange_order exchange where exchange.time < #{cancelTime} and exchange.status=0 and exchange.member_id in (76895,119284)")
    List<ExchangeOrder> queryExchangeOrderByTime(@Param("cancelTime") long cancelTime);

    @Select("select exchange.* from exchange_order exchange where exchange.time< #{cancelTime} and exchange.status=0 and exchange.member_id in (#{sellMemberId},#{buyMemberId})")
    List<ExchangeOrder> queryExchangeOrderByTimeById(@Param("cancelTime") long cancelTime,@Param("sellMemberId") long sellMemberId,@Param("buyMemberId") long buyMemberId);

    @Select("select exchange.* from exchange_order exchange where exchange.time< #{cancelTime} and exchange.status=0 and exchange.order_resource=0")
    List<ExchangeOrder> queryExchangeOrderByTimeById(@Param("cancelTime") long cancelTime);

    @Select("select exchange.* from exchange_order exchange where exchange.member_id=1 and exchange.time < #{beforeTime} and exchange.status<>0 ORDER BY exchange.order_id asc limit 0,#{num}")
    List<ExchangeOrder> queryHistoryDeleteList(@Param("beforeTime") long beforeTime,@Param("num")Integer num);


    @Delete("delete from exchange_order exchange where exchange.member_id = 1 and exchange.status <> 0 and exchange.time < #{beforeTime}")
    int deleteHistory(@Param("beforeTime") long beforeTime);

//    @Select("select id from member_wallet where member_id = #{memberId} for update")
//    void selectByMemberIdForUpdate(@Param("memberId") Long memberId);
}
