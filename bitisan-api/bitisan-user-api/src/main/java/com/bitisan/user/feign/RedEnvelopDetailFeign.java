package com.bitisan.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.user.entity.RedEnvelopeDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bitisan-user",contextId = "redEnvelopDetailFeign")
public interface RedEnvelopDetailFeign {

    @PostMapping("/redEnvelopeDetailFeignController/findByEnvelope")
    Page<RedEnvelopeDetail> findByEnvelope(@RequestParam("envelopeId") Long envelopeId,
                                           @RequestParam("pageNo")Integer pageNo,
                                           @RequestParam("pageNo")Integer pageSize);
}
