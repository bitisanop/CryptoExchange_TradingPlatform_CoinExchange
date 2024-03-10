package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 国家信息表
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "国家信息表")
@Data
@EqualsAndHashCode(callSuper = false)
public class Country implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 中文名称
     */
    @ApiModelProperty(value = "中文名称")
    private String zhName;

    /**
     * 英文名称
     */
    @ApiModelProperty(value = "英文名称")
    private String areaCode;

    /**
     * 区号
     */
    @ApiModelProperty(value = "区号")
    private String enName;

    /**
     * 国旗logo
     */
    @ApiModelProperty(value = "国旗logo")
    private String countryImageUrl;

    /**
     * 语言
     */
    @ApiModelProperty(value = "语言")
    private String language;

    /**
     * 当地货币缩写
     */
    @ApiModelProperty(value = "当地货币缩写")
    private String localCurrency;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;

    /**
     * 多语言处理后的名字
     */
    @ApiModelProperty(value = "多语言处理后的名字")
    @TableField(exist = false)
    private String name;
}
