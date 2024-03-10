package com.bitisan.user.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 合伙人推广卡订单
 * </p>
 *
 * @author markchao
 * @since 2023-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PromotionCardOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 兑换金额
     */
    private BigDecimal amount;

    private Date createTime;

    private Integer isFree;

    private Integer isLock;

    private Integer lockDays;

    private Long memberId;

    private Integer state;

    private Long cardId;


}
