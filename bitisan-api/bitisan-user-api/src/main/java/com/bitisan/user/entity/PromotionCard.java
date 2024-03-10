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
 * 合伙人推广卡
 * </p>
 *
 * @author markchao
 * @since 2023-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PromotionCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 单个卡券金额
     */
    private BigDecimal amount;

    private String cardDesc;

    private String cardName;

    private String cardNo;

    private Integer count;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer exchangeCount;

    private Integer isEnabled;

    private Integer isFree;

    private Integer isLock;

    private Integer lockDays;

    private Long memberId;

    /**
     * 所有卡券总金额
     */
    private BigDecimal totalAmount;

    private String coinId;


}
