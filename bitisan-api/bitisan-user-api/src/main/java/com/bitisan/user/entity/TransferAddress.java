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
 * 转账地址
 * </p>
 *
 * @author markchao
 * @since 2021-09-21
 */
@ApiModel(value = "转账地址")
@Data
@EqualsAndHashCode(callSuper = false)
public class TransferAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "地址")
    private String address;

    /**
     * 最低转账数目
     */
    @ApiModelProperty(value = "最低转账数目")
    private BigDecimal minAmount;

    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * 转账手续费率
     */
    @ApiModelProperty(value = "转账手续费率")
    private BigDecimal transferFee;

    @ApiModelProperty(value = "币种id")
    private String coinId;


}
