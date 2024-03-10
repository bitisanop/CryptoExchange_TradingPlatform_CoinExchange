package com.bitisan.exchange.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 交易优先符号
 * </p>
 *
 * @author markchao
 * @since 2022-02-07
 */
@ApiModel(value = "交易优先符号")
@Data
@EqualsAndHashCode(callSuper = false)
public class ExchangeFavorSymbol implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "添加时间")
    private String addTime;

    @ApiModelProperty(value = "用户id")
    private Long memberId;

    @ApiModelProperty(value = "交易对符号")
    private String symbol;


}
