package com.bitisan.user.feign;

import com.bitisan.controller.BaseController;
import com.bitisan.user.entity.PaymentType;
import com.bitisan.user.entity.PaymentTypeRecord;
import com.bitisan.user.service.PaymentTypeRecordService;
import com.bitisan.user.service.PaymentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author Bitisan E-Mali:bitisanop@gmail.com
 * @Description: coin
 * @date 2021/4/214:20
 */
@RestController
@RequestMapping("paymentFeign")
public class PaymentFeignController extends BaseController {
    @Autowired
    private PaymentTypeService paymentTypeService;
    @Autowired
    private PaymentTypeRecordService paymentTypeRecordService;


    /**
     * 绑定支付方式
     */
//    @ApiOperation(value = "根据id获取支付方式")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "id"),
//    })
    @RequestMapping("findPaymentTypeById")
    public PaymentType findPaymentTypeById(@RequestParam("id") Long id) {
        return paymentTypeService.findPaymentTypeById(id);
    }


//    @ApiOperation(value = "根据会员id获取记录")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "memberId", value = "会员id"),
//    })
    @RequestMapping("getRecordsByUserId")
    public List<PaymentTypeRecord> getRecordsByUserId(@RequestParam("memberId") Long memberId) {
        return paymentTypeRecordService.getRecordsByUserId(memberId);
    }

}
