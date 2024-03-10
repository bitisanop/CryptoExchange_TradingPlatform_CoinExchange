package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bitisan.constant.RewardRecordType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 奖励记录
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "奖励记录")
@Data
@EqualsAndHashCode(callSuper = false)
public class RewardRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id自增
     */
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数目
     */
    @ApiModelProperty(value = "数目")
    private BigDecimal amount;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 类型0推广1活动2分红
     */
    @ApiModelProperty(value = "类型0推广1活动2分红")
    private RewardRecordType type;

    /**
     * 币种id
     */
    @ApiModelProperty(value = "币种id")
    private String coinId;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private Long memberId;


}
