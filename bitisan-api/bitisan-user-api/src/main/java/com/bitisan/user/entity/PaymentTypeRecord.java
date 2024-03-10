package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Map;

/**
 * 用户支付方式绑定记录表
 */
@ApiModel(value = "用户支付方式绑定记录表")
@Data
@Entity
public class PaymentTypeRecord {

    @ApiModelProperty(value = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long memberId;
    /**
     * 支付方式id
     */
    @ApiModelProperty(value = "支付方式id")
    private Long type;

    @ApiModelProperty(value = "字段1")
    @TableField(value = "field_1")
    private String field_1;

    @ApiModelProperty(value = "字段2")
    @TableField(value = "field_2")
    private String field_2;

    @ApiModelProperty(value = "字段3")
    @TableField(value = "field_3")
    private String field_3;

    @ApiModelProperty(value = "字段4")
    @TableField(value = "field_4")
    private String field_4;

    @ApiModelProperty(value = "字段5")
    @TableField(value = "field_5")
    private String field_5;

    @ApiModelProperty(value = "字段6")
    @TableField(value = "field_6")
    private String field_6;

    @ApiModelProperty(value = "字段7")
    @TableField(value = "field_7")
    private String field_7;

    @ApiModelProperty(value = "支付方式名称")
    @TableField(exist = false)
    private String typeName;

    @ApiModelProperty(value = "字段类型")
    @TableField(exist = false)
    private Map<String,String> fieldType;

    @ApiModelProperty(value = "字段名称")
    @TableField(exist = false)
    private Map<String,String> fieldName;

    @ApiModelProperty(value = "颜色")
    @TableField(exist = false)
    private String color;
}
