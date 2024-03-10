package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bitisan.constant.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 员交易记录，包括充值、提现、转账等
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "会员交易记录")
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberTransaction implements Serializable {

    public MemberTransaction() {
        this.createTime = new Date();
    }

    private static final long serialVersionUID = 1L;

    /**
     * 交易记录编号
     */
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String address;

    /**
     * 空投ID
     */
    @ApiModelProperty(value = "空投ID")
    private Long airdropId;

    /**
     * 充币金额
     */
    @ApiModelProperty(value = "充币金额")
    private BigDecimal amount;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();

    /**
     * 折扣手续费
     */
    @ApiModelProperty(value = "折扣手续费")
    private String discountFee;

    /**
     * 实收手续费
     */
    @ApiModelProperty(value = "实收手续费")
    private String realFee;

    /**
     * 交易手续费
     */
    @ApiModelProperty(value = "交易手续费")
    private BigDecimal fee;

    /**
     * 标识位
     */
    @ApiModelProperty(value = "标识位")
    private Integer flag;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private Long memberId;

    /**
     * 币种名称
     */
    @ApiModelProperty(value = "币种名称")
    private String symbol;

    /**
     * 交易类型
     */
    @ApiModelProperty(value = "交易类型")
    private Integer type;

    @ApiModelProperty("int(2) default 0 comment '是否已返佣'")
    private Integer isReward;// 是否已返佣，0：否，1：是


}
