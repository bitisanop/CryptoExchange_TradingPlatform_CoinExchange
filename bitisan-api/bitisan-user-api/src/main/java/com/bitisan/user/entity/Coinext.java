package com.bitisan.user.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.bitisan.constant.BooleanEnum;
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
 * @since 2022-03-20
 */
@ApiModel(value = "币种扩展")
@Data
@EqualsAndHashCode(callSuper = false)
public class Coinext implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "币种id")
    private Integer coinId;

    /**
     * 币种名称
     */
    @ApiModelProperty(value = "币种名称")
    private String coinName;

    @ApiModelProperty(value = "协议")
    private Integer protocol;

    /**
     * 协议名称
     */
    @ApiModelProperty(value = "协议名称")
    private String protocolName;

    /**
     * 提现私钥需要前端加密
     */
    @ApiModelProperty(value = "提现私钥")
    private String mainAddress;

    /**
     * 合约地址
     */
    @ApiModelProperty(value = "合约地址")
    private String ext;

    /**
     * 币种精度
     */
    @ApiModelProperty(value = "币种精度")
    private Integer decimals;

    /**
     * 0 关闭 1 启动
     */
    @ApiModelProperty(value = "状态 0 关闭 1 启动")
    private Integer status;

    /**
     * 提现手续费（1=100%）
     */
    @ApiModelProperty(value = "提现手续费（1=100%）")
    private BigDecimal withdrawFee;

    /**
     * 最低提现手续费数量
     */
    @ApiModelProperty(value = "最低提现手续费数量")
    private BigDecimal minWithdrawFee;

    /**
     * 是否开启提现
     */
    @ApiModelProperty(value = "是否开启提现")
    private BooleanEnum isWithdraw=BooleanEnum.IS_FALSE;

    /**
     * 是否开启充值
     */
    @ApiModelProperty(value = "是否开启充值")
    private BooleanEnum isRecharge=BooleanEnum.IS_FALSE;

    /**
     * 是否开启自动提现（不建议开启）
     */
    @ApiModelProperty(value = "是否开启自动提现")
    private BooleanEnum isAutoWithdraw=BooleanEnum.IS_FALSE;

    /**
     * 最低提现数量
     */
    @ApiModelProperty(value = "最低提现数量")
    private BigDecimal minWithdraw;

    /**
     * 最大提现数量
     */
    @ApiModelProperty(value = "最大提现数量")
    private BigDecimal maxWithdraw;

    /**
     * 最低充值
     */
    @ApiModelProperty(value = "最低充值")
    private BigDecimal minRecharge;

    /**
     * 确认到账次数
     */
    @ApiModelProperty(value = "确认到账次数")
    private Integer confirms;

    /**
     * 用户填写备注码充值的地址 如果不需要填空
     */
    @ApiModelProperty(value = "用户填写备注码充值的地址")
    private String memoAddress;


}
