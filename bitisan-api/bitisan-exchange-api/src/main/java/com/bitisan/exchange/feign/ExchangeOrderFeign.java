package com.bitisan.exchange.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.ExchangeOrderDirection;
import com.bitisan.constant.ExchangeOrderStatus;
import com.bitisan.constant.ExchangeOrderType;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.entity.ExchangeOrderDetail;
import com.bitisan.pojo.ExchangeTrade;
import com.bitisan.screen.ExchangeOrderScreen;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-exchange",contextId = "exchangeOrderFeign")
public interface ExchangeOrderFeign {

    @GetMapping(value = "/orderFeign/findOne")
    ExchangeOrder findOne(@RequestParam("id") String id);

    @PostMapping(value = "/orderFeign/processExchangeTrade")
    MessageResult processExchangeTrade(@RequestBody ExchangeTrade trade, @RequestParam("secondReferrerAward")boolean secondReferrerAward);

    @PostMapping("/orderFeign/tradeCompleted")
    MessageResult tradeCompleted(@RequestParam("orderId") String orderId,
                                 @RequestParam("tradedAmount") BigDecimal tradedAmount,
                                 @RequestParam("turnover") BigDecimal turnover);
    @PostMapping("/orderFeign/cancelOrder")
    MessageResult cancelOrder(@RequestParam("orderId")String orderId,
                              @RequestParam("tradedAmount")BigDecimal tradedAmount,
                              @RequestParam("turnover")BigDecimal turnover);

    @PostMapping("/orderFeign/findAll")
    Page<ExchangeOrder> findAll(@RequestBody ExchangeOrderScreen screen);

    @PostMapping("/orderFeign/findAllDetailByOrderId")
    List<ExchangeOrderDetail> findAllDetailByOrderId(@RequestParam("orderId")String orderId);

    @PostMapping(value = "/orderFeign/findAllTradingOrderBySymbol")
    List<ExchangeOrder> findAllTradingOrderBySymbol(@RequestParam("symbol")String symbol);

    @PostMapping(value = "/orderFeign/forceCancelOrder")
    MessageResult forceCancelOrder(@RequestBody ExchangeOrder order);

    /**
     * 行情机器人专用：当前委托
     * @param uid
     * @param sign
     * @return
     */
    @RequestMapping("/orderFeign/mockcurrentydhdnskd")
    Page<ExchangeOrder> currentOrderMock(
            @RequestParam("uid") Long uid,
            @RequestParam("sign") String sign,
            @RequestParam("symbol") String symbol,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("pageSize") int pageSize);
    /**
     * 行情机器人专用：交易取消委托
     * @param uid
     * @param orderId
     * @return
     */
    @RequestMapping("/orderFeign/mockcancelydhdnskd")
    MessageResult cancelOrdermock(@RequestParam("uid") Long uid, @RequestParam("sign")String sign, @RequestParam("orderId")String orderId);

    /**
     * 行情机器人专用：添加委托订单
     * @param uid
     * @param sign
     * @param direction
     * @param symbol
     * @param price
     * @param amount
     * @param type
     * @return
     */
    @RequestMapping("/orderFeign/mockaddydhdnskd")
    MessageResult addOrderMock(
            @RequestParam("uid") Long uid,
            @RequestParam("sign")String sign,
            @RequestParam("direction") ExchangeOrderDirection direction,
            @RequestParam("symbol")String symbol,
            @RequestParam("price")BigDecimal price,
            @RequestParam("amount")BigDecimal amount,
            @RequestParam("type") ExchangeOrderType type);

    @RequestMapping("/orderFeign/getExchangeTurnoverBase")
    List<ExchangeOrder> getExchangeTurnoverBase(@RequestParam("dateStr")String dateStr);

    @RequestMapping("/orderFeign/getExchangeTurnoverCoin")
    List<ExchangeOrder> getExchangeTurnoverCoin(@RequestParam("dateStr")String dateStr);

    @RequestMapping("/orderFeign/getExchangeTurnoverSymbol")
    List<ExchangeOrder> getExchangeTurnoverSymbol(@RequestParam("dateStr")String dateStr);

    @RequestMapping("/orderFeign/addOrder")
    public MessageResult addOrder(@RequestParam("memberId") Long memberId,
                                  @RequestParam("direction")Integer directionCode,
                                  @RequestParam("symbol")String symbol,
                                  @RequestParam("price")BigDecimal price,
                                  @RequestParam("amount")BigDecimal amount,
                                  @RequestParam("type")Integer typeCode);

    @RequestMapping("/orderFeign/personal/current")
    public Page<ExchangeOrder> personalCurrentOrder(@RequestParam("memberId") Long memberId,
                                                    @RequestParam(value = "symbol",required = false) String symbol,
                                                    @RequestParam(value = "type",required = false) Integer type,
                                                    @RequestParam(value = "startTime",required = false) String startTime,
                                                    @RequestParam(value = "endTime",required = false) String endTime,
                                                    @RequestParam(value = "direction",required = false) Integer direction,
                                                    @RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
                                                    @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) ;

    @RequestMapping("/orderFeign/cancelOrder4API")
    public MessageResult cancelOrder4API(@RequestParam("memberId") Long memberId,@RequestParam("orderId") String orderId);

    @RequestMapping("/orderFeign/personal/history")
    public Page<ExchangeOrder> personalHistoryOrder(
            @RequestParam("memberId") Long memberId,
            @RequestParam(value = "symbol" ,required = false) String symbol,
            @RequestParam(value = "type",required = false) Integer type,
            @RequestParam(value = "status" ,required = false) Integer status,
            @RequestParam(value = "startTime",required = false) String startTime,
            @RequestParam(value = "endTime",required = false) String endTime,
            @RequestParam(value = "direction",required = false) Integer direction,
            @RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize",defaultValue = "10") int pageSize);
}
