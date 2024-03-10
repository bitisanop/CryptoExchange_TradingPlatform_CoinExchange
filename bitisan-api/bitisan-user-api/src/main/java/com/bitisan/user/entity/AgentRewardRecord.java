package com.bitisan.user.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 返佣记录
 * @date 2019/1/2 15:28
 */
@Entity
@Data
@Table
public class AgentRewardRecord {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long memberId;
    private Long fromMemberId;
    private Long orderId;

    private Integer type;//类型

    @Column(columnDefinition = "币种unit")
    private String coinUnit;
    /**
     * 返佣数量
     */
    @Column(columnDefinition = "decimal(26,16) comment '返佣数量'")
    private BigDecimal num;

    @Column(columnDefinition = "创建时间")
    private Long createTime;


    @Transient
    private Member member;

    @Transient
    private Member fromMember;

    @Transient
    private Coin coin;
}
