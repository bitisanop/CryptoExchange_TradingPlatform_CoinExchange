package com.bitisan.user.feign;

import com.bitisan.user.entity.RewardPromotionSetting;
import com.bitisan.util.MessageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bitisan-user",contextId = "rewardPromotionSettingFeign")
public interface RewardPromotionSettingFeign {

    @GetMapping("/rewardPromotionSettingFeign/findByType")
    RewardPromotionSetting findByType(@RequestParam("type") Integer type);

}
