package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "货币汇率表")
@Data
@EqualsAndHashCode(callSuper = false)
public class Currency {

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "全称")
    private String fullName;

    @ApiModelProperty(value = "符号")
    private String symbol;

    @ApiModelProperty(value = "汇率")
    private BigDecimal rate;

    @ApiModelProperty(value = "图片url")
    private String imageUrl;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态 0 禁用 1 启用")
    private Integer status;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
