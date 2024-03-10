package com.bitisan.user.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 转账记录
 * </p>
 *
 * @author markchao
 * @since 2021-09-21
 */
@ApiModel(value = "转账记录")
@Data
@EqualsAndHashCode(callSuper = false)
public class TransferRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "数目")
    private BigDecimal amount;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "会员id")
    private Long memberId;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "币种id")
    private String coinId;


}
