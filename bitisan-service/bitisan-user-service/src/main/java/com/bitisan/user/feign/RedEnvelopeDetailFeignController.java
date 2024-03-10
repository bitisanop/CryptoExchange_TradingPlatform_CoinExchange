package com.bitisan.user.feign;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.user.entity.RedEnvelopeDetail;
import com.bitisan.user.service.RedEnvelopeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 会员用户 前端控制器
 * </p>
 *
 * @author markchao
 * @since 2021-06-14
 */
@RestController
@RequestMapping("/redEnvelopeDetailFeignController")
public class RedEnvelopeDetailFeignController extends BaseController {

    @Autowired
    private RedEnvelopeDetailService redEnvelopeDetailService;

    @PostMapping("/findByEnvelope")
    Page<RedEnvelopeDetail> findByEnvelope(@RequestParam("envelopeId") Long envelopeId,
                                           @RequestParam("pageNo")Integer pageNo,
                                           @RequestParam("pageSize")Integer pageSize){
        return redEnvelopeDetailService.findByEnvelope(envelopeId,pageNo,pageSize);
    }


}

