package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bitisan.constant.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Embedded;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 会员用户
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "会员用户")
@Data
@EqualsAndHashCode(callSuper = false)
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 胜诉次数
     */
    @ApiModelProperty(value = "胜诉次数")
    private Integer appealSuccessTimes = 0;

    /**
     * 申诉次数
     */
    @ApiModelProperty(value = "申诉次数")
    private Integer appealTimes = 0;

    /**
     * 实名认证通过时间
     */
    @ApiModelProperty(value = "实名认证通过时间")
    private Date applicationTime;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**
     * 认证商家申请时间
     */
    @ApiModelProperty(value = "认证商家申请时间")
    private Date certifiedBusinessApplyTime;

    /**
     * 商家审核通过时间
     */
    @ApiModelProperty(value = "商家审核通过时间")
    private Date certifiedBusinessCheckTime;

    /**
     * 认证商家状态
     */
    @ApiModelProperty(value = "认证商家状态")
    private CertifiedBusinessStatus certifiedBusinessStatus = CertifiedBusinessStatus.NOT_CERTIFIED;

    /**
     * 通道id
     */
    @ApiModelProperty(value = "通道id")
    private Integer channelId;

    /**
     * email
     */
    @ApiModelProperty(value = "email")
    private String email;

    /**
     * 一级推广人数
     */
    @ApiModelProperty(value = "一级推广人数")
    private Integer firstLevel = 0;

    /**
     * 谷歌认证时间
     */
    @ApiModelProperty(value = "谷歌认证时间")
    private Date googleDate;

    /**
     * 谷歌key
     */
    @ApiModelProperty(value = "谷歌key")
    private String googleKey;

    /**
     * 谷歌认证状态 0未开启 1开起
     */
    @ApiModelProperty(value = "谷歌认证状态 0未开启 1开起")
    private Integer googleState;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    private String idNumber;

    /**
     * 推广者id
     */
    @ApiModelProperty(value = "推广者id")
    private Long inviterId;

    /**
     * 是否为渠道
     */
    @ApiModelProperty(value = "是否为渠道")
    private Integer isChannel;

    /**
     * 交易密码
     */
    @ApiModelProperty(value = "交易密码")
    private String jyPassword;

    /**
     * 最后登陆时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "最后登陆时间")
    private Date lastLoginTime;

    /**
     * 城市
     */
    @ApiModelProperty(value = "城市")
    private String city;

    /**
     * 登陆次数
     */
    @ApiModelProperty(value = "登陆次数")
    private Integer loginCount = 0;

    /**
     * 登陆锁
     */
    @ApiModelProperty(value = "登陆锁")
    private Integer loginLock;

    /**
     * 用户级别
     */
    @ApiModelProperty(value = "用户级别")
    private Integer memberLevel;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String mobilePhone;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 推广码
     */
    @ApiModelProperty(value = "推广码")
    private String promotionCode;

    /**
     * 是否允许发布广告  1表示可以发布
     */
    @ApiModelProperty(value = "是否允许发布广告  1表示可以发布")
    private Integer publishAdvertise = BooleanEnum.IS_TRUE.getCode();

    /**
     * 真实名称
     */
    @ApiModelProperty(value = "真实名称")
    private String realName;

    /**
     * 实名状态
     */
    @ApiModelProperty(value = "实名状态")
    private Integer realNameStatus = RealNameStatus.NOT_CERTIFIED.getCode();

    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "注册时间")
    private Date registrationTime = new Date();

    /**
     * 盐值
     */
    @ApiModelProperty(value = "盐值")
    private String salt;

    /**
     * 二级推广人数
     */
    @ApiModelProperty(value = "二级推广人数")
    private Integer secondLevel = 0;

    /**
     * 签到能力
     */
    @ApiModelProperty(value = "签到能力")
    private Boolean signInAbility = true;

    /**
     * 用户状态
     */
    @ApiModelProperty(value = "用户状态")
    private Integer status = CommonStatus.NORMAL.getCode();

    /**
     * 三级推广人数
     */
    @ApiModelProperty(value = "三级推广人数")
    private Integer thirdLevel = 0;

    /**
     * 交易状态
     */
    @ApiModelProperty(value = "交易状态")
    private Integer transactionStatus = BooleanEnum.IS_TRUE.getCode();

    /**
     * 首次交易时间
     */
    @ApiModelProperty(value = "首次交易时间")
    private Date transactionTime;

    /**
     * 交易次数
     */
    @ApiModelProperty(value = "交易次数")
    private Integer transactions = 0;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 国家
     */
    @ApiModelProperty(value = "国家")
    private String local;

    /**
     * 等级id
     */
    @ApiModelProperty(value = "等级id")
    private Long memberGradeId;

    /**
     * kyc等级
     */
    @ApiModelProperty(value = "kyc等级")
    private Integer kycStatus;

    /**
     * 注册赠送积分
     */
    @ApiModelProperty(value = "注册赠送积分")
    private Long generalizeTotal;

    /**
     * 邀请者父级id
     */
    @ApiModelProperty(value = "邀请者父级id")
    private Long inviterParentId;

    /**
     * 国家
     */
    @ApiModelProperty(value = "国家")
    private String country;

    /**
     * 街道
     */
    @ApiModelProperty(value = "街道")
    private String district;

    /**
     * 省份
     */
    @ApiModelProperty(value = "省份")
    private String province;

    /**
     * 老用户是否修改密码 0未修改 1 已修改
     */
    @ApiModelProperty(value = "老用户是否修改密码 0未修改 1 已修改")
    private String margin;

    @ApiModelProperty(value = "超级合伙人标识 0 普通    1 超级合伙人")
    private String superPartner = "0";


}
