package com.bitisan.user.feign;

import com.bitisan.user.entity.RewardRecord;
import com.bitisan.util.MessageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "bitisan-user",contextId = "rewardRecordFeign")
public interface RewardRecordFeign {

    @PostMapping("/rewardRecordFeign/save")
    MessageResult save(@RequestBody RewardRecord rewardRecord);

}
