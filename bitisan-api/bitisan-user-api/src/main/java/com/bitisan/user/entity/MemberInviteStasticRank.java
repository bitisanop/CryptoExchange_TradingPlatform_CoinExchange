package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 会员邀请等级
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "会员邀请等级")
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberInviteStasticRank implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 统计日期
     */
    @ApiModelProperty(value = "统计日期")
    private Date stasticDate;

    /**
     * 类型：0 = 日榜，1 = 周榜， 2 = 月榜
     */
    @ApiModelProperty(value = "类型：0 = 日榜，1 = 周榜， 2 = 月榜")
    private Integer type;

    /**
     * 用户账户标识，手机或邮箱
     */
    @ApiModelProperty(value = "用户账户标识，手机或邮箱")
    private String userIdentify;


}
