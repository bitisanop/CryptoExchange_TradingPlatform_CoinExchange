package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 快速兑换
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "快速兑换")
@Data
@EqualsAndHashCode(callSuper = false)
public class QuickExchange implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "源兑换数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "创建日期")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "目标兑换数量")
    private BigDecimal exAmount;

    @ApiModelProperty(value = "源币种")
    private String fromUnit;

    @ApiModelProperty(value = "兑换人")
    private Long memberId;

    @ApiModelProperty(value = "兑换比例")
    private BigDecimal rate;

    @ApiModelProperty(value = "状态（0：未成交， 1：已成交， 2：用户取消， 3：管理员撤回）")
    private Integer status;

    @ApiModelProperty(value = "目标币种")
    private String toUnit;


}
