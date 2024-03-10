package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户邀请关系上级和返利比重表
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@ApiModel(value = "用户邀请关系上级和返利比重表")
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberWeightUpper implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long memberId;

    /**
     * 代理商id
     */
    @ApiModelProperty(value = "代理商id")
    private Long firstMemberId;

    /**
     * 返佣比例（%）
     */
    @ApiModelProperty(value = "返佣比例（%）")
    private Integer rate;

    /**
     * 上级用户id 逗号连接1,2,3
     */
    @ApiModelProperty(value = "上级用户id 逗号连接1,2,3")
    private String upper;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
