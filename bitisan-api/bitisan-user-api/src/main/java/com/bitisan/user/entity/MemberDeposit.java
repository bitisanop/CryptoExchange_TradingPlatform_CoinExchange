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
 * 充币记录表
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "充币记录表")
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberDeposit implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String address;

    /**
     * 金额
     */
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 会员Id
     */
    @ApiModelProperty(value = "会员Id")
    private Long memberId;

    /**
     * 交易号
     */
    @ApiModelProperty(value = "交易号")
    private String txid;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String unit;


}
