package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 会员秒合约钱包
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "会员秒合约钱包")
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberSecondWallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 币种余额
     */
    @ApiModelProperty(value = "币种余额")
    private BigDecimal balance;

    /**
     * 冻结余额
     */
    @ApiModelProperty(value = "冻结余额")
    private BigDecimal frozenBalance;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private Long memberId;

    /**
     * `币种id
     */
    @ApiModelProperty(value = "币种id")
    private String coinId;




}
