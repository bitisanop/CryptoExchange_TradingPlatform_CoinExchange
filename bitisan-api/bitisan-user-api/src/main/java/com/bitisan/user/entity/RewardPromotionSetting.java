package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.bitisan.constant.PromotionRewardType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 推广奖励设置
 * </p>
 *
 * @author markchao
 * @since 2021-08-04
 */
@ApiModel(value = "推广奖励设置")
@Data
@EqualsAndHashCode(callSuper = false)
public class RewardPromotionSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 生效时间，从注册之日算起。单位天
     */
    @ApiModelProperty(value = "生效时间，从注册之日算起。单位天")
    private Integer effectiveTime;

    @ApiModelProperty(value = "信息")
    private String info;

    /**
     * 0禁用 1启用
     */
    @ApiModelProperty(value = "状态 0禁用 1启用")
    private Integer status;

    /**
     * 0推广注册 1法币推广交易 2币币交易
     */
    @ApiModelProperty(value = "0推广注册 1法币推广交易 2币币交易")
    private PromotionRewardType type;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "管理员id")
    private Long adminId;

    /**
     * 奖励币种
     */
    @ApiModelProperty(value = "奖励币种")
    private String coinId;


}
