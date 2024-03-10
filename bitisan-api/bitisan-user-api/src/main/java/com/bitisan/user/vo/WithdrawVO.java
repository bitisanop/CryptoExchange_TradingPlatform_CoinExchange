package com.bitisan.user.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawVO {

    private Long id;
    private Integer memberId;
    private Long addTime;
    private Integer coinId;
    private String coinName;
    private String address;
    private BigDecimal money;
    private BigDecimal fee;
    private BigDecimal realMoney;
    private Integer processMold;
    private String hash;
    private Integer status;
    private Long processTime;
    private String withdrawInfo;
    private String remark;
    private Integer protocol;
    private String protocolName;

    private String username;
    private String email;

    private String unit;
    private BigDecimal amount;
    private BigDecimal totalFee;


}
