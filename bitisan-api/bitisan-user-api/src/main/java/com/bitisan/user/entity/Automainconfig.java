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
 *
 * </p>
 *
 * @author markchao
 * @since 2022-03-20
 */
@ApiModel(value = "归集配置")
@Data
@EqualsAndHashCode(callSuper = false)
public class Automainconfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private Integer coinId;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String coinName;

    /**
     * 最低归集数量
     */
    @ApiModelProperty(value = "最低归集数量")
    private BigDecimal minNum;

    /**
     * 币种协议
     */
    @ApiModelProperty(value = "币种协议")
    private Integer protocol;

    /**
     * 归集地址（前端需要加密）
     */
    @ApiModelProperty(value = "归集地址")
    private String address;


}
