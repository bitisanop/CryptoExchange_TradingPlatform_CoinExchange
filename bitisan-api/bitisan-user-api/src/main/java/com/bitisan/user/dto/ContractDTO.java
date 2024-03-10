package com.bitisan.user.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Bitisan E-Mali:bitisanop@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2021/4/289:45
 */
@Data
public class ContractDTO {
    private String name;
    private String address;
    private int decimals;
    private BigDecimal minCollectAmount;
}
