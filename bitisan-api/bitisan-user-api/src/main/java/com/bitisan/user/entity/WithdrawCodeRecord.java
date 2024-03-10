package com.bitisan.user.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author markchao
 * @since 2023-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WithdrawCodeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dealTime;

    private Long memberId;

    private String remark;

    private Long rmemberId;

    private Integer status = 0;

    /**
     * 申请总数量
     */
    private BigDecimal withdrawAmount;

    private String withdrawCode;

    private String coinId;


}
