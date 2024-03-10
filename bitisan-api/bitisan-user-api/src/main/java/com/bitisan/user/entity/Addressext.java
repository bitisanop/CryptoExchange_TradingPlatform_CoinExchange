package com.bitisan.user.entity;

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
@ApiModel(value = "地址")
@Data
@EqualsAndHashCode(callSuper = false)
public class Addressext implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 状态 0 未使用 1 使用
     */
    @ApiModelProperty(value = "状态 0 未使用 1 使用")
    private Integer status;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String address;

    /**
     * 协议 通协议 用永辉只存在一条数据
     */
    @ApiModelProperty(value = "协议")
    private Integer coinProtocol;

    /**
     * 用户
     */
    @ApiModelProperty(value = "用户")
    private Integer memberId;


}
