package com.bitisan.admin.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 热传输记录
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HotTransferRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long adminId;

    /**
     * 转账金额
     */
    private BigDecimal amount;

    /**
     * 热钱包余额
     */
    private BigDecimal balance;

    private String coldAddress;

    /**
     * 矿工费
     */
    private BigDecimal minerFee;

    private String transactionNumber;

    private LocalDateTime transferTime;

    private String unit;


}
