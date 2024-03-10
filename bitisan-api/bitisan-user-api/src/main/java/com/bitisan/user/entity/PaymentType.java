package com.bitisan.user.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;


@ApiModel(value = "支付方式")
@Data
@Entity
public class PaymentType {
    /**
     * 中文名称
     */
    @ApiModelProperty(value = "id")
    @Id
    private Long id;
    /**
     * 支付方式简码
     */
    @ApiModelProperty(value = "支付方式简码")
    private String code;

    /**
     * 支付方式配置json
     */
    @ApiModelProperty(value = "支付方式配置json")
    private String configJson;
    

}
