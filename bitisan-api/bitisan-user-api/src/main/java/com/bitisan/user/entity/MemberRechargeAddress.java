package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 会员充值地址
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "会员充值地址")
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberRechargeAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long memberId;

    @ApiModelProperty(value = "地址")
    private String address;

    /**
     * 链id
     */
    @ApiModelProperty(value = "链id")
    private Long chainId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
