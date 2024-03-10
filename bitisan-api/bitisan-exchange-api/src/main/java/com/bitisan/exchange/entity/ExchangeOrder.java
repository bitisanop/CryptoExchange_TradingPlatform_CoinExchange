package com.bitisan.exchange.entity;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bitisan.constant.ExchangeOrderDirection;
import com.bitisan.constant.ExchangeOrderStatus;
import com.bitisan.constant.ExchangeOrderType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 币币挂单
 * </p>
 *
 * @author markchao
 * @since 2022-02-07
 */
@ApiModel(value = "币币挂单")
@Data
@EqualsAndHashCode(callSuper = false)
public class ExchangeOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId("order_id")
    private String orderId;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "基币/结算币")
    private String baseSymbol;

    @ApiModelProperty(value = "取消时间")
    private Long canceledTime;

    @ApiModelProperty(value = " 币种符号")
    private String coinSymbol;

    @ApiModelProperty(value = "完成时间")
    private Long completedTime;

    @ApiModelProperty(value = "方向")
    private ExchangeOrderDirection direction;

    @ApiModelProperty(value = "会员id")
    private Long memberId;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "交易状态")
    private ExchangeOrderStatus status;

    @ApiModelProperty(value = "交易对符号")
    private String symbol;

    @ApiModelProperty(value = "时间")
    private Long time;

    @ApiModelProperty(value = "已售数量")
    private BigDecimal tradedAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "成交额")
    private BigDecimal turnover = BigDecimal.ZERO;

    @ApiModelProperty(value = "订单类型")
    private ExchangeOrderType type;

    @ApiModelProperty(value = "使用折扣（例：0）")
    private String useDiscount;

    @ApiModelProperty(value = "订单来源")
    private Integer orderResource;

    @TableField(exist = false)
    private List<ExchangeOrderDetail> detail;

    @TableField(exist = false)
    private BigDecimal fee;

}
