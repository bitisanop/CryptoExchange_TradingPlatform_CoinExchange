package com.bitisan.open.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.entity.ExchangeOrderDetail;
import com.bitisan.exchange.feign.ExchangeOrderFeign;
import com.bitisan.open.util.RedisUtil;
import com.bitisan.user.entity.MemberApiKey;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.feign.MemberApiKeyFeign;
import com.bitisan.user.feign.MemberWalletFeign;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 个人信息相关
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class OpenApiController extends BaseController {
    @Autowired
    private MemberApiKeyFeign apiKeyService ;
    @Autowired
    private MemberWalletFeign memberWalletFeign;
    @Autowired
    private ExchangeOrderFeign exchangeOrderFeign;



    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping(value = "get/account",method = RequestMethod.GET)
    public MessageResult getUserId(HttpServletRequest request){
        String ac = request.getParameter("accessKeyId");
        MemberApiKey memberApiKey = apiKeyService.findMemberApiKeyByApiKey(ac);
        JSONObject re = new JSONObject();
        re.put("memberId",memberApiKey.getMemberId());
        return success("SUCCESS",re);
    }

    /**
     *  查询个人账户信息
     */
    @RequestMapping(value = "/account",method = RequestMethod.GET)
    public MessageResult getUserAccountInfo(HttpServletRequest request){
        String ac = request.getParameter("accessKeyId");
        MemberApiKey memberApiKey = apiKeyService.findMemberApiKeyByApiKey(ac);
        List<MemberWallet> list = memberWalletFeign.findAllByMemberId(memberApiKey.getMemberId());
        return success(list);
    }

    /**
     * 添加委托订单
     * @param memberId
     * @param direction
     * @param symbol
     * @param price
     * @param amount
     * @param type
     *          usedisCount 暂时不用
     * @return
     */
    @ApiOperation(value = "添加委托订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "direction", value = "方向 0：买  1：卖"),
            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
            @ApiImplicitParam(name = "amount", value = "金额"),
            @ApiImplicitParam(name = "price", value = "价格"),
            @ApiImplicitParam(name = "type", value = "0 市价 1 限价"),
    })
    @RequestMapping("/order/add")
    public MessageResult addOrder(@RequestParam("memberId") Long memberId,
                                  @RequestParam("direction")Integer direction,
                                  @RequestParam("symbol")String symbol,
                                  @RequestParam("price") BigDecimal price,
                                  @RequestParam("amount")BigDecimal amount,
                                  @RequestParam("type")Integer type,HttpServletRequest request){
        String ac = request.getParameter("accessKeyId");
        MemberApiKey memberApiKey = apiKeyService.findMemberApiKeyByApiKey(ac);
        return exchangeOrderFeign.addOrder(memberApiKey.getMemberId(),direction,symbol,price,amount,type);
    }


    /**
     * 根据订单Id查询订单详情
     * @param orderId
     * @return
     */
    @GetMapping("query/order_detail")
    public MessageResult queryOrderDetailByOrderId(HttpServletRequest request,@RequestParam("orderId")String orderId){
        try {
            if(StringUtils.isEmpty(orderId)|| !orderId.startsWith("E")){
                return MessageResult.error(500,"订单号非法，请核实订单号");
            }
            //根据订单号查询订单
            ExchangeOrder order = exchangeOrderFeign.findOne(orderId);
            String ac = request.getParameter("accessKeyId");
            MemberApiKey memberApiKey = apiKeyService.findMemberApiKeyByApiKey(ac);
            if(!memberApiKey.getMemberId().equals(order.getMemberId())){
                return error("账户id有误");
            }
            order.setDetail(exchangeOrderFeign.findAllDetailByOrderId(order.getOrderId()));
            if(order==null){
                return  MessageResult.error(500,"订单不存在，请核实订单号");
            }
           return MessageResult.success("success",order);

        } catch (Exception e) {
            log.info(">>>>>>查询订单详情出错>>>>>",e);
            return  MessageResult.error(500,"查询订单详情出错");
        }
    }

    @ApiOperation(value = "当前委托")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
            @ApiImplicitParam(name = "type", value = "0 市价 1 限价"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
            @ApiImplicitParam(name = "direction", value = "方向 0：买  1：卖"),

    })
    @PostMapping("query/orderCurrent")
    public MessageResult queryOrderByMemberIdAndSymbol(@RequestParam(value = "symbol",required = false) String symbol,
                                                       @RequestParam(value = "type",required = false) Integer type,
                                                       @RequestParam(value = "startTime",required = false) String startTime,
                                                       @RequestParam(value = "endTime",required = false) String endTime,
                                                       @RequestParam(value = "direction",required = false) Integer direction,
                                                       @RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
                                                       @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                                       HttpServletRequest request){
        try {
            String ac = request.getParameter("accessKeyId");
            MemberApiKey memberApiKey = apiKeyService.findMemberApiKeyByApiKey(ac);

            Page<ExchangeOrder> page = exchangeOrderFeign.personalCurrentOrder(memberApiKey.getMemberId(),symbol,type,startTime,endTime,direction,pageNo,pageSize);
            page.getRecords().forEach(exchangeOrder -> {
                //获取交易成交详情
                BigDecimal tradedAmount = BigDecimal.ZERO;
                List<ExchangeOrderDetail> details = exchangeOrderFeign.findAllDetailByOrderId(exchangeOrder.getOrderId());
                exchangeOrder.setDetail(details);
                for (ExchangeOrderDetail trade : details) {
                    tradedAmount = tradedAmount.add(trade.getAmount());
                }
                exchangeOrder.setTradedAmount(tradedAmount);
            });
            return MessageResult.success("success",page);
        }catch (Exception e){
            log.info(">>>>>>>>查询用户当前委托出错>>>",e);
            return  MessageResult.error(500,"查询当前委托出错");
        }
    }

    /**
     * 根据订单号取消订单Id
     * @param orderId
     * @return
     */
    @RequestMapping(value = "cancel_order",method = RequestMethod.GET)
    public MessageResult cancelOrderByOrderId(@RequestParam("orderId")String orderId,
                                              @RequestParam("memberId")Long memberId,HttpServletRequest request){
        try {
            String ac = request.getParameter("accessKeyId");
            MemberApiKey memberApiKey = apiKeyService.findMemberApiKeyByApiKey(ac);
            if(!memberApiKey.getMemberId().equals(memberId)){
                return error("账户id有误");
            }
            return exchangeOrderFeign.cancelOrder4API(memberId, orderId);
        }catch (Exception e){
            log.info(">>>>>取消订单出错>>>>",e);
            return  MessageResult.error(500,"取消订单出错");
        }
    }


    /**
     * 查询用户历史委托
     * @param memberId
     * @param symbol
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "history",method = RequestMethod.POST)
    public MessageResult queryHistoryOrder(
            @RequestParam("memberId") Long memberId,
            @RequestParam(value = "symbol" ,required = false) String symbol,
            @RequestParam(value = "type",required = false) Integer type,
            @RequestParam(value = "status" ,required = false) Integer status,
            @RequestParam(value = "startTime",required = false) String startTime,
            @RequestParam(value = "endTime",required = false) String endTime,
            @RequestParam(value = "direction",required = false) Integer direction,
            @RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
            HttpServletRequest request){
        MessageResult result = new MessageResult();
        try {
            String ac = request.getParameter("accessKeyId");
            MemberApiKey memberApiKey = apiKeyService.findMemberApiKeyByApiKey(ac);
            if(!memberApiKey.getMemberId().equals(memberId)){
                return error("账户id有误");
            }
            Page<ExchangeOrder> page = exchangeOrderFeign.personalHistoryOrder(memberId, symbol, type,status,startTime,endTime,direction,pageNo, pageSize);
            page.getRecords().forEach(exchangeOrder -> {
                //获取交易成交详情,
                exchangeOrder.setDetail(exchangeOrderFeign.findAllDetailByOrderId(exchangeOrder.getOrderId()));
            });
            return MessageResult.success("success",page);
        }catch (Exception e){
            log.info(">>>>>>>查询历史委托订单异常>>>>",e);
            return  MessageResult.error(500,"查询历史委托订单异常");
        }
    }

}
