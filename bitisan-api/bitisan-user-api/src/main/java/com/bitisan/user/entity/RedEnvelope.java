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
 *
 * </p>
 *
 * @author markchao
 * @since 2023-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RedEnvelope implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String bgImage;

    private Integer count;

    private Date createTime;

    private String detail;

    private String envelopeNo;

    private Integer expiredHours;

    private Integer invite;

    private String logoImage;

    /**
     * 最大随机领取额
     */
    private BigDecimal maxRand;

    private Long memberId;

    private String name;

    private Integer plateform;

    /**
     * 领取总额
     */
    private BigDecimal receiveAmount;

    private Integer receiveCount;

    private Integer state;

    /**
     * 红包总额
     */
    private BigDecimal totalAmount;

    private Integer type;

    private String unit;

    private String inviteUser;

    private String inviteUserAvatar;


}
