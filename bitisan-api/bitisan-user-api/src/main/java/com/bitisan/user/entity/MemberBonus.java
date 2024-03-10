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
 * 会员分红表
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "会员分红表")
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberBonus implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "币种")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 到账时间
     */
    @ApiModelProperty(value = "币种")
    private String arriveTime;

    /**
     * 币种id
     */
    @ApiModelProperty(value = "币种")
    private String coinId;

    /**
     * 持币时间
     */
    @ApiModelProperty(value = "币种")
    private String haveTime;

    /**
     * 分红数量
     */
    @ApiModelProperty(value = "币种")
    private BigDecimal memBouns;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "币种")
    private Long memberId;

    /**
     * 当天总手续费
     */
    @ApiModelProperty(value = "币种")
    private BigDecimal total;


}
