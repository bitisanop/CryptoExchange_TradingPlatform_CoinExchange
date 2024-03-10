package com.bitisan.user.feign;


import com.bitisan.constant.PromotionRewardType;
import com.bitisan.controller.BaseController;
import com.bitisan.user.entity.RewardPromotionSetting;
import com.bitisan.user.service.RewardPromotionSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/rewardPromotionSettingFeign")
public class RewardPromotionSettingFeignController extends BaseController {

    @Autowired
    private RewardPromotionSettingService rewardPromotionSettingService;

//    @ApiOperation(value = "根据返佣类型查询")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "type", value = "类型"),
//    })
    @GetMapping("findByType")
    public RewardPromotionSetting findByType(@RequestParam("type") Integer type) {
        PromotionRewardType pType = PromotionRewardType.creator(type);
        RewardPromotionSetting rewardPromotionSetting = rewardPromotionSettingService.findByType(pType);
        return rewardPromotionSetting;
    }


}

