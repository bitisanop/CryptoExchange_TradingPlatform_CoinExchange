package com.bitisan.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.screen.MemberInviteStasticScreen;
import com.bitisan.user.entity.MemberInviteStastic;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "memberInviteStasticFeign")
public interface MemberInviteStasticFeign {


    @RequestMapping(value = "/memberInviteStasticFeign/queryRankList")
    Page<MemberInviteStastic> queryRankList(@RequestBody MemberInviteStasticScreen screen);
}
