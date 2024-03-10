package com.bitisan.user.feign;

import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.entity.WalletTransRecord;
import com.bitisan.util.MessageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "walletTransRecordFeign")
public interface WalletTransRecordFeign {


    @PostMapping("/walletTransRecordFeign/save")
    MessageResult save(@RequestBody WalletTransRecord walletTransRecord);
}
