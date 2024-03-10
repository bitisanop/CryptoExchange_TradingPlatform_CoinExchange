package com.bitisan.user.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bitisan.constant.TransactionType;
import com.bitisan.user.entity.MemberTransaction;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberTransaction4Front implements Serializable{

    private static final long serialVersionUID = 1L;
    /**
     * 交易记录编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 地址
     */
    private String address;

    /**
     * 空投ID
     */
    private Long airdropId;

    /**
     * 充币金额
     */
    private BigDecimal amount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 折扣手续费
     */
    private String discountFee;

    /**
     * 实收手续费
     */
    private String realFee;

    /**
     * 交易手续费
     */
    private BigDecimal fee;

    /**
     * 标识位
     */
    private Integer flag;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 币种名称
     */
    private String symbol;

    /**
     * 交易类型
     */
    private Integer type;

}
