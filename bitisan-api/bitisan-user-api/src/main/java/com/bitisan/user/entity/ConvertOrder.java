package com.bitisan.user.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 快速兑换订单
 * </p>
 *
 * @author markchao
 * @since 2022-07-12
 */
@ApiModel(value = "快速兑换订单")
@Data
@EqualsAndHashCode(callSuper = false)
public class ConvertOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 兑换后数量
     */
    @ApiModelProperty(value = "兑换后数量")
    private BigDecimal fromAmount;

    /**
     * 兑换后数量
     */
    @ApiModelProperty(value = "兑换前数量")
    private BigDecimal toAmount;

    @ApiModelProperty(value = "源币种")
    private String fromUnit;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long memberId;

    /**
     * 价格
     */
    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "目标币种")
    private String toUnit;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}
