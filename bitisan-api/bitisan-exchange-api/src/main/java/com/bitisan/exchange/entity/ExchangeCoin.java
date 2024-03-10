package com.bitisan.exchange.entity;

import java.math.BigDecimal;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bitisan.constant.BooleanEnum;
import com.bitisan.constant.ExchangeCoinPublishType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Transient;

/**
 * <p>
 * 币币交易对
 * </p>
 *
 * @author markchao
 * @since 2022-02-07
 */
@ApiModel(value = "币币交易对")
@Data
@EqualsAndHashCode(callSuper = false)
public class ExchangeCoin implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId("symbol")
    private String symbol;

    private Integer baseCoinScale;

    private String baseSymbol;

    private Integer coinScale;

    private String coinSymbol;

    private Integer enable;

    /**
     * 交易手续费
     */
    @ApiModelProperty(value = "交易手续费")
    private BigDecimal fee;

    private Integer sort;

    /**
     * 是否启用市价买
     */
    @ApiModelProperty(value = "是否启用市价买")
    private Integer enableMarketBuy;

    /**
     * 是否启用市价卖
     */
    @ApiModelProperty(value = "是否启用市价卖")
    private Integer enableMarketSell;

    /**
     * 最低挂单卖价
     */
    @ApiModelProperty(value = "最低挂单卖价")
    private BigDecimal minSellPrice;

    /**
     * 默认为0，1表示推荐
     */
    @ApiModelProperty(value = "默认为0，1表示推荐")
    private Integer flag;

    /**
     * 最大允许同时交易的订单数，0表示不限制
     */
    @ApiModelProperty(value = "最大允许同时交易的订单数，0表示不限制")
    private Integer maxTradingOrder;

    /**
     * 委托超时自动下架时间，单位为秒，0表示不过期
     */
    @ApiModelProperty(value = "委托超时自动下架时间，单位为秒，0表示不过期")
    private Integer maxTradingTime;

    /**
     * 交易类型，B2C2特有
     */
    @ApiModelProperty(value = "交易类型，B2C2特有")
    private String instrument;

    /**
     * 最小挂单成交额
     */
    @ApiModelProperty(value = "最小挂单成交额")
    private BigDecimal minTurnover;

    /**
     * 最大下单量
     */
    @ApiModelProperty(value = "最大下单量")
    private BigDecimal maxVolume;

    /**
     * 最小下单量
     */
    @ApiModelProperty(value = "最小下单量")
    private BigDecimal minVolume;

    private Integer zone;

    /**
     * 清盘时间
     */
    @ApiModelProperty(value = "清盘时间")
    private String clearTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    /**
     *  分摊发行价格
     */
    @ApiModelProperty(value = "分摊发行价格")
    private BigDecimal publishPrice;

    /**
     * 发行活动类型 1:无活动,2:抢购发行,3:分摊发行 ExchangeCoinPublishType
     */
    @ApiModelProperty(value = "发行活动类型 1:无活动,2:抢购发行,3:分摊发行")
    private Integer publishType;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     *  是否可交易
     */
    @ApiModelProperty(value = "是否可交易")
    private Integer exchangeable;

    /**
     *  活动发行数量
     */
    @ApiModelProperty(value = "活动发行数量")
    private BigDecimal publishAmount;

    /**
     *  前台可见状态
     */
    @ApiModelProperty(value = "前台可见状态")
    private Integer visible;

    /**
     * 最高买单价
     */
    @ApiModelProperty(value = "最高买单价")
    private BigDecimal maxBuyPrice;

    /**
     *
     */
    @ApiModelProperty(value = "机器人类型")
    private Integer robotType;

    /**
     * 是否允许买
     */
    @ApiModelProperty(value = "是否允许买")
    private Integer enableBuy;

    /**
     * 是否允许卖
     */
    @ApiModelProperty(value = "是否允许卖")
    private Integer enableSell;

    @TableField(exist = false)
    private Long  currentTime;


    /**
     * 交易引擎状态（0：不可用，1：可用
     */
    @ApiModelProperty(value = "交易引擎状态（0：不可用，1：可用")
    @TableField(exist = false)
    private int engineStatus = 0;

    /**
     * 行情引擎状态（0：不可用，1：可用
     */
    @ApiModelProperty(value = "行情引擎状态（0：不可用，1：可用")
    @TableField(exist = false)
    private int marketEngineStatus = 0;

    /**
     * 交易机器人状态（0：非运行中，1：运行中）
     */
    @ApiModelProperty(value = "交易机器人状态（0：非运行中，1：运行中）")
    @TableField(exist = false)
    private int exEngineStatus = 0;

}
