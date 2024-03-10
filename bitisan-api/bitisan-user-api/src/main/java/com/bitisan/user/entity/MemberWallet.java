package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import com.bitisan.constant.BooleanEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 会员钱包
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "会员钱包")
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberWallet implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 可用余额
     */
    @ApiModelProperty(value = "可用余额")
    private BigDecimal balance;

    /**
     * 冻结余额
     */
    @ApiModelProperty(value = "冻结余额")
    private BigDecimal frozenBalance;

    /**
     * 待释放余额
     */
    @ApiModelProperty(value = "待释放余额")
    private BigDecimal releaseBalance;

    /**
     * 钱包不是锁定
     */
    @ApiModelProperty(value = "钱包不是锁定")
    private BooleanEnum isLock;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long memberId;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    @Version
    private Integer version;

    /**
     * 币种id
     */
    @ApiModelProperty(value = "币种id")
    private String coinId;

}
