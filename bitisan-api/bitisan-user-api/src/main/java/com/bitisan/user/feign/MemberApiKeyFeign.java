package com.bitisan.user.feign;

import com.bitisan.user.entity.Addressext;
import com.bitisan.user.entity.MemberApiKey;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author Bitisan E-Mali:bitisanop@gmail.com
 * @Description: coin
 * @date 2021/4/214:20
 */
@FeignClient(value = "bitisan-user",contextId = "memberApiKeyFeign")
public interface MemberApiKeyFeign {

    @PostMapping(value = "/memberApiKeyFeign/findMemberApiKeyByApiKey")
    MemberApiKey findMemberApiKeyByApiKey(@RequestParam("apiKey")String apiKey);

}
