package com.bitisan.user.feign;

import com.bitisan.swap.vo.RewardSetVo;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberWeightUpper;
import com.bitisan.util.MessageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "memberWeightUpperFeign")
public interface MemberWeightUpperFeign {

    @PostMapping(value = "/memberWeightUpperFeign/findAllByUpperIds")
    List<MemberWeightUpper> findAllByUpperIds(@RequestParam("upper") String upper);

    @PostMapping(value = "/memberWeightUpperFeign/findMemberWeightUpperByMemberId")
    MemberWeightUpper findMemberWeightUpperByMemberId(@RequestParam("memberId") Long memberId);

    @PostMapping(value = "/memberWeightUpperFeign/saveMemberWeightUpper")
    MemberWeightUpper saveMemberWeightUpper(@RequestBody Member member);

    @PostMapping(value = "/memberWeightUpperFeign/modifyMemberWeightUpper")
    Boolean modifyMemberWeightUpper(@RequestBody MemberWeightUpper memberWeightUpper);

    @PostMapping(value = "/memberWeightUpperFeign/findRewardSetVoById")
    RewardSetVo findRewardSetVoById(@RequestParam("memberId") Long memberId);
}
