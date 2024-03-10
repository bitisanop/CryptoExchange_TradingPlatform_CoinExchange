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
 * 快速兑换币种
 * </p>
 *
 * @author markchao
 * @since 2022-07-12
 */
@ApiModel(value = "快速兑换币种")
@Data
@EqualsAndHashCode(callSuper = false)
public class ConvertCoin implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 最大兑换数量
     */
    @ApiModelProperty(value = "最大兑换数量")
    private BigDecimal maxAmount;

    /**
     * 最小兑换数量
     */
    @ApiModelProperty(value = "最小兑换数量")
    private BigDecimal minAmount;

    /**
     * 币种unit
     */
    @ApiModelProperty(value = "币种unit")
    private String coinUnit;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;

    /**
     * 状态0不可用 1可用
     */
    @ApiModelProperty(value = "状态0不可用 1可用")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
