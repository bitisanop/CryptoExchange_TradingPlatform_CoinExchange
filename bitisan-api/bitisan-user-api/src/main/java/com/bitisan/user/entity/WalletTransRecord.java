package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bitisan.constant.WalletType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 钱包交易记录
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "钱包交易记录")
@Data
@EqualsAndHashCode(callSuper = false)
public class WalletTransRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 划转金额
     */
    @ApiModelProperty(value = "划转金额")
    private BigDecimal amount;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private Long memberId;

    /**
     * 从哪里划转
     */
    @ApiModelProperty(value = "从哪里划转")
    private WalletType source;

    /**
     * 划转到哪里去
     */
    @ApiModelProperty(value = "划转到哪里去")
    private WalletType target;

    @ApiModelProperty(value = "币种")
    private String unit;


}
