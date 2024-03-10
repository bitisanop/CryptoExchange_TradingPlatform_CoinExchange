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
 * @since 2022-03-20
 */
@ApiModel(value = "充值")
@Data
@EqualsAndHashCode(callSuper = false)
public class Recharge implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 哈希
     */
    @ApiModelProperty(value = "哈希")
    private String hash;

    /**
     * 哈希MD5
     */
    @ApiModelProperty(value = "哈希MD5")
    private String md5;

    /**
     * 用户
     */
    @ApiModelProperty(value = "用户")
    private Integer memberId;

    @ApiModelProperty(value = "币种")
    private Long addTime;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种id")
    private Integer coinId;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种名称")
    private String coinName;

    /**
     * 金额
     */
    @ApiModelProperty(value = "金额")
    private BigDecimal money;

    /**
     * 区块
     */
    @ApiModelProperty(value = "区块")
    private Integer block;

    /**
     * 0 为正常区块记录 1 后台增加
     */
    @ApiModelProperty(value = "0 为正常区块记录 1 后台增加")
    private Integer agreen;

    /**
     * 确认次数
     */
    @ApiModelProperty(value = "确认次数")
    private Integer confirms;

    /**
     * 需要确认的次数
     */
    @ApiModelProperty(value = "需要确认的次数")
    private Integer nConfirms;

    /**
     * 0 未到账 1 已到账 -1 失败
     */
    @ApiModelProperty(value = "0 未到账 1 已到账 -1 失败")
    private Integer status;

    /**
     * 发送方地址
     */
    @ApiModelProperty(value = "发送方地址")
    private String send;

    /**
     * 接收方地址
     */
    @ApiModelProperty(value = "接收方地址")
    private String address;

    /**
     * 协议
     */
    @ApiModelProperty(value = "协议")
    private Integer protocol;

    /**
     * 协议名称
     */
    @ApiModelProperty(value = "协议名称")
    private String protocolName;


}
