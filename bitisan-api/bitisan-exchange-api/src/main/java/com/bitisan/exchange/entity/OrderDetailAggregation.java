package com.bitisan.exchange.entity;

import com.bitisan.constant.ExchangeOrderDirection;
import com.bitisan.constant.OrderTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(collection = "order_detail_aggregation")
public class OrderDetailAggregation implements Serializable{

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private String orderId ;

    /**
     * 订单类型
     * otc 或者 exchange  order
     */
    @ApiModelProperty(value = "订单类型")
    private OrderTypeEnum type ;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username ;

    /**
     * 会员真实姓名
     */
    @ApiModelProperty(value = "会员真实姓名")
    private String realName ;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID")
    private Long memberId ;

    /**
     * 此聚合信息生成时间
     */
    @ApiModelProperty(value = "此聚合信息生成时间")
    private long time ;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private double fee ;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private double amount ;

    /**
     * 币种单位
     */
    @ApiModelProperty(value = "币种单位")
    private String unit ;

    /**
     * exchange订单专有属性
     */
    @ApiModelProperty(value = "exchange订单专有属性")
    private ExchangeOrderDirection direction;

    /**
     * 交易对象id
     * otc订单专有属性
     */
    @ApiModelProperty(value = "交易对象id")
    private Long customerId;

    /**
     * 交易对象用户名
     * otc订单专有属性
     */
    @ApiModelProperty(value = "交易对象用户名")
    private String customerName;

    @ApiModelProperty(value = "交易者真实姓名")
    private String customerRealName;

}
