package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bitisan.constant.BooleanEnum;
import com.bitisan.constant.CommonStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 币种信息表
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "币种信息表")
@Data
@EqualsAndHashCode(callSuper = false)
public class Coin implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 货币名称
     */
    @ApiModelProperty(value = "货币名称")
    @TableId
    private String name;

    /**
     * 是否能自动提币 0-否 1-是
     */
    @ApiModelProperty(value = "是否能自动提币 0-否 1-是")
    private Integer canAutoWithdraw=BooleanEnum.IS_FALSE.getCode();

    /**
     * 是否能充币 0-否 1-是
     */
    @ApiModelProperty(value = "是否能充币 0-否 1-是")
    private Integer canRecharge=BooleanEnum.IS_FALSE.getCode();

    /**
     * 是否能转账 0-否 1-是
     */
    @ApiModelProperty(value = "是否能转账 0-否 1-是")
    private Integer canTransfer=BooleanEnum.IS_FALSE.getCode();

    /**
     * 是否能提币 0-否 1-是
     */
    @ApiModelProperty(value = "是否能提币 0-否 1-是")
    private Integer canWithdraw=BooleanEnum.IS_FALSE.getCode();

    /**
     * 人民币汇率
     */
    @ApiModelProperty(value = "人民币汇率")
    private BigDecimal cnyRate;

    /**
     * 是否是法币 0-否 1-是
     */
    @ApiModelProperty(value = "是否是法币 0-否 1-是")
    private Boolean hasLegal;

    /**
     * 是否为平台币
     */
    @ApiModelProperty(value = "是否为平台币")
    private Integer isPlatformCoin;

    /**
     * 最大提币手续费
     */
    @ApiModelProperty(value = "最大提币手续费")
    private BigDecimal maxTxFee;

    /**
     * 最大提币数量
     */
    @ApiModelProperty(value = "最大提币数量")
    private BigDecimal maxWithdrawAmount;

    /**
     * 最小充币数量
     */
    @ApiModelProperty(value = "最小充币数量")
    private BigDecimal minRechargeAmount;

    /**
     * 最小提币手续费
     */
    @ApiModelProperty(value = "最小提币手续费")
    private BigDecimal minTxFee;

    /**
     * 最小提币数量
     */
    @ApiModelProperty(value = "最小提币数量")
    private BigDecimal minWithdrawAmount;

    /**
     * 矿工费
     */
    @ApiModelProperty(value = "矿工费")
    private BigDecimal minerFee;

    /**
     * 中文名称
     */
    @ApiModelProperty(value = "中文名称")
    private String nameCn;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;

    /**
     * 状态 0-正常 1-非法
     */
    @ApiModelProperty(value = "状态 0-正常 1-非法")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private CommonStatus status=CommonStatus.NORMAL;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 美元汇率
     */
    @ApiModelProperty(value = "美元汇率")
    private BigDecimal usdRate;

    /**
     * 提币精度
     */
    @ApiModelProperty(value = "提币精度")
    private Integer withdrawScale;

    /**
     * 自动提现阈值
     */
    @ApiModelProperty(value = "自动提现阈值")
    private BigDecimal withdrawThreshold;

    @ApiModelProperty(value = "币种简介")
    private String information;

    @ApiModelProperty(value = "币种资料链接")
    private String infolink;

    @ApiModelProperty(value = "币种资料链接")
    private String iconUrl;

    @ApiModelProperty(value = "总币个数")
    @TableField(exist = false)
    private BigDecimal allBalance;

}
