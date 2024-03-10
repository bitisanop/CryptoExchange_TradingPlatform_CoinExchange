package com.bitisan.user.feign;

import com.bitisan.controller.BaseController;
import com.bitisan.user.entity.Addressext;
import com.bitisan.user.entity.MemberApiKey;
import com.bitisan.user.service.AddressextService;
import com.bitisan.user.service.MemberApiKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author Bitisan E-Mali:bitisanop@gmail.com
 * @Description: coin
 * @date 2021/4/214:20
 */

@RestController
@RequestMapping("/memberApiKeyFeign")
public class MemberApiKeyFeignController extends BaseController {

    @Autowired
    private MemberApiKeyService memberApiKeyService;

    @PostMapping(value = "/findMemberApiKeyByApiKey")
    public MemberApiKey findMemberApiKeyByApiKey(@RequestParam("apiKey")String apiKey){
        return memberApiKeyService.findMemberApiKeyByApiKey(apiKey);
    }

}
