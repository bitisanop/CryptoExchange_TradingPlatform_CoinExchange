package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bitisan.constant.PromotionLevel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户推广
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "用户推广")
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberPromotion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id自增
     */
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 邀请者Id
     */
    @ApiModelProperty(value = "邀请者Id")
    private Long inviteesId;

    /**
     * 受邀者Id
     */
    @ApiModelProperty(value = "受邀者Id")
    private Long inviterId;

    /**
     * 等级0一级1二级2三级
     */
    @ApiModelProperty(value = "等级0一级1二级2三级")
    private PromotionLevel level;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
