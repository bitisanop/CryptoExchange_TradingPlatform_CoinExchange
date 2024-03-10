package com.bitisan.user.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author markchao
 * @since 2022-04-06
 */
@ApiModel(value = "提现申请")
@Data
@EqualsAndHashCode(callSuper = false)
public class Withdraw implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户
     */
    @ApiModelProperty(value = "用户")
    private Integer memberId;

    @ApiModelProperty(value = "添加时间")
    private Long addTime;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种ID")
    private Integer coinId;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种名称")
    private String coinName;

    /**
     * 提现地址
     */
    @ApiModelProperty(value = "提现地址")
    private String address;

    /**
     * 申请金额
     */
    @ApiModelProperty(value = "申请金额")
    private BigDecimal money;

    /**
     * 提现手续费
     */
    @ApiModelProperty(value = "提现手续费")
    private BigDecimal fee;

    /**
     * 真实到账数量
     */
    @ApiModelProperty(value = "真实到账数量")
    private BigDecimal realMoney;

    /**
     * *处理模式，0区块处理，1外部处理
     */
    @ApiModelProperty(value = "处理模式，0区块处理，1外部处理")
    private Integer processMold;

    /**
     * 提现哈希
     */
    @ApiModelProperty(value = "提现哈希")
    private String hash;

    /**
     * *状态，-1,驳回,0待处理,1处理中,2已处理 3 失败
     */
    @ApiModelProperty(value = "状态，-1,驳回,0待处理,1处理中,2已处理 3 失败")
    private Integer status;

    /**
     * 后台处理时间
     */
    @ApiModelProperty(value = "后台处理时间")
    private Long processTime;

    /**
     * 提现失败原因（后台驳回理由可填写）
     */
    @ApiModelProperty(value = "提现失败原因")
    private String withdrawInfo;

    /**
     * 用户申请提现备注（）
     */
    @ApiModelProperty(value = "用户申请提现备注")
    private String remark;

    /**
     * 协议
     */
    @ApiModelProperty(value = "协议")
    private Integer protocol;

    /**
     * 协议
     */
    @ApiModelProperty(value = "协议名称")
    private String protocolName;


}
