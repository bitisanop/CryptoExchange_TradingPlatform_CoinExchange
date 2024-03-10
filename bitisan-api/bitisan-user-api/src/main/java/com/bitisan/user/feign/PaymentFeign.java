package com.bitisan.user.feign;

import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.PaymentType;
import com.bitisan.user.entity.PaymentTypeRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "paymentFeign")
public interface PaymentFeign {

    @PostMapping(value = "/paymentFeign/findPaymentTypeById")
    PaymentType findPaymentTypeById(@RequestParam("id") Long id);


    @PostMapping(value = "/paymentFeign/getRecordsByUserId")
    List<PaymentTypeRecord> getRecordsByUserId(@RequestParam("memberId") Long memberId);
}
