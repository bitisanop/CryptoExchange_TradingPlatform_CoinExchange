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
 * 会员邀请记录
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "会员邀请记录")
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberInviteStastic implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 返佣总额（BTC）
     */
    @ApiModelProperty(value = "返佣总额（BTC）")
    private BigDecimal btcReward;

    /**
     * 折合返佣总额（估算折合USDT，用户总额排名比较）
     */
    @ApiModelProperty(value = "折合返佣总额")
    private BigDecimal estimatedReward;

    /**
     * 返佣总额（BTC）
     */
    @ApiModelProperty(value = "返佣总额（BTC）")
    private BigDecimal ethReward;

    /**
     * 额外奖励（折合USDT）
     */
    @ApiModelProperty(value = "额外奖励（折合USDT）")
    private BigDecimal extraReward;

    /**
     * 是否机器人（0：否，1：是）
     */
    @ApiModelProperty(value = "是否机器人（0：否，1：是）")
    private Integer isRobot;

    /**
     * 邀请一级好友数量
     */
    @ApiModelProperty(value = "邀请一级好友数量")
    private Integer levelOne;

    /**
     * 邀请二级好友数量
     */
    @ApiModelProperty(value = "邀请二级好友数量")
    private Integer levelTwo;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private Long memberId;

    /**
     * 其他返佣（json格式{"LTC":123, "EOS":22.3})
     */
    @ApiModelProperty(value = "其他返佣")
    private String otherReward;

    /**
     * 统计日期
     */
    @ApiModelProperty(value = "统计日期")
    private String stasticDate;

    /**
     * 返佣总额（USDT）
     */
    @ApiModelProperty(value = "返佣总额（USDT）")
    private BigDecimal usdtReward;

    /**
     * 用户账户标识，手机或邮箱
     */
    @ApiModelProperty(value = "用户账户标识，手机或邮箱")
    private String userIdentify;


}
