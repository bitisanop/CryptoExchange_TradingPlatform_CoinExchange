package com.bitisan.user.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author markchao
 * @since 2023-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RedEnvelopeDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 领取数额
     */
    private BigDecimal amount;

    private Integer cate;

    private Date createTime;

    private Long envelopeId;

    private Long memberId;

    private String userIdentify;

    @TableField(exist = false)
    private String promotionCode;


}
