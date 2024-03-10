package com.bitisan.user.feign;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.MemberInviteStasticScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.MemberInviteStastic;
import com.bitisan.user.service.MemberInviteStasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 会员邀请统计 前端控制器
 * </p>
 *
 * @author markchao
 * @since 2021-06-14
 */
@RestController
@RequestMapping("/memberInviteStasticFeign")
public class MemberInviteStasticFeignController extends BaseController {

    @Autowired
    private LocaleMessageSourceService messageSourceService;

    @Autowired
    private MemberInviteStasticService memberInviteStasticService;



//    @ApiOperation(value = "获取等级列表")
    @RequestMapping(value = "queryRankList")
    public Page<MemberInviteStastic> queryRankList(@RequestBody MemberInviteStasticScreen screen){
        Page<MemberInviteStastic> page = memberInviteStasticService.queryRankList(screen);
        return page;
    }


}

