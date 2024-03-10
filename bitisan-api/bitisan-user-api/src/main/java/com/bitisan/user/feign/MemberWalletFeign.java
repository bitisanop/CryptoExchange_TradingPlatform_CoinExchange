package com.bitisan.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.screen.MemberWalletScreen;
import com.bitisan.user.dto.MemberWalletDTO;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.MemberDeposit;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.util.MessageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "memberWalletFeign")
public interface MemberWalletFeign {


    @GetMapping("/memberWalletFeign/findByCoinUnitAndMemberId")
    MemberWallet findByCoinUnitAndMemberId(@RequestParam("coinUnit")String coinUnit, @RequestParam("memberId") Long memberId);

    @PostMapping("/memberWalletFeign/freezeBalance")
    MessageResult freezeBalance(@RequestParam("id") Long id, @RequestParam("amount") BigDecimal amount);

    @PostMapping("/memberWalletFeign/deductBalance")
    MessageResult deductBalance(@RequestParam("walletId") Long walletId, @RequestParam("amount") BigDecimal amount);

    @PostMapping("/memberWalletFeign/increaseBalance")
    MessageResult increaseBalance(@RequestParam("walletId") Long walletId, @RequestParam("amount") BigDecimal amount);

    @PostMapping("/memberWalletFeign/decreaseFrozen")
    MessageResult decreaseFrozen(@RequestParam("walletId") Long walletId, @RequestParam("amount") BigDecimal amount);

    @PostMapping("/memberWalletFeign/thawBalance")
    MessageResult thawBalance(@RequestParam("coinId")String coinId, @RequestParam("memberId") Long memberId, @RequestParam("amount") BigDecimal amount);

    @GetMapping("/memberWalletFeign/findAllByMemberId")
    List<MemberWallet> findAllByMemberId(@RequestParam("memberId")Long memberId);

    @PostMapping(value = "/memberWalletFeign/save")
    Boolean save(@RequestBody MemberWallet wallet);

    @PostMapping(value = "/memberWalletFeign/getBalance")
    Page<MemberWalletDTO> getBalance(@RequestBody MemberWalletScreen screen);

    @PostMapping("/memberWalletFeign/increaseToRelease")
    MessageResult increaseToRelease(@RequestParam("walletId") Long walletId, @RequestParam("amount") BigDecimal amount);

    @PostMapping("/memberWalletFeign/lockWallet")
    boolean lockWallet(@RequestParam("uid")Long uid, @RequestParam("unit")String unit);

    @PostMapping("/memberWalletFeign/unlockWallet")
    boolean unlockWallet(@RequestParam("uid")Long uid, @RequestParam("unit")String unit);

    @PostMapping("/memberWalletFeign/findDeposit")
    MemberDeposit findDeposit(@RequestParam("address")String address, @RequestParam("txid") String txid);

    @PostMapping("/memberWalletFeign/recharge")
    MessageResult recharge(@RequestParam("unit")String unit,
                           @RequestParam("address")String address,
                           @RequestParam("amount")BigDecimal amount,
                           @RequestParam("txid")String txid,
                           @RequestParam("fromAddress")String fromAddress,
                           @RequestParam("blockHeight")Long blockHeight);
}
