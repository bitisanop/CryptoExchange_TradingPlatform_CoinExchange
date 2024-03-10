package com.bitisan.user.vo;

import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.MemberRechargeAddress;
import com.bitisan.user.entity.MemberWallet;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 会员钱包
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberWalletVo extends MemberWallet implements Serializable {

    private static final long serialVersionUID = 1L;

    private Coin coin;


}
