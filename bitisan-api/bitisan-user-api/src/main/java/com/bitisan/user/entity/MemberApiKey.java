package com.bitisan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 会员OpenKey
 * </p>
 *
 * @author markchao
 * @since 2023-12-15
 */
@ApiModel(value = "会员OpenKey")
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberApiKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String apiKey;

    private String apiName;

    private String bindIp;

    private Date createTime;

    private Date expireTime;

    private Long memberId;

    private String remark;

    private String secretKey;

    @TableField(exist = false)
    private String code ;

}
