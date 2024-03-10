package com.bitisan.user.feign;


import com.alibaba.fastjson.JSON;
import com.bitisan.controller.BaseController;
import com.bitisan.swap.vo.RewardSetVo;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberWeightUpper;
import com.bitisan.user.service.MemberWeightUpperService;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户邀请关系 前端控制器
 * </p>
 *
 * @author markchao
 * @since 2021-06-14
 */
@Slf4j
@RestController
@RequestMapping("/memberWeightUpperFeign")
public class MemberWeightUpperFeignController extends BaseController {


    @Autowired
    private MemberWeightUpperService memberWeightUpperService;



//    @ApiOperation(value = "根据会员id列表查询用户邀请关系列表")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "upper", value = "会员id列表"),
//    })
    @PostMapping("findAllByUpperIds")
    public List<MemberWeightUpper> findAllByUpperIds(@RequestParam("upper") String upper){
        List<MemberWeightUpper> list= memberWeightUpperService.findAllByUpperIds(upper);
        return list;
    }

//    @ApiOperation(value = "根据会员id查找用户邀请关系上级")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "coin", value = "币种"),
//    })
    @PostMapping("findMemberWeightUpperByMemberId")
    public MemberWeightUpper findMemberWeightUpperByMemberId(@RequestParam("memberId") Long memberId){
        MemberWeightUpper memberWeightUpper= memberWeightUpperService.findMemberWeightUpperByMemberId(memberId);
        return memberWeightUpper;
    }

//    @ApiOperation(value = "保存邀请关系")
    @PostMapping(value = "saveMemberWeightUpper")
    public MemberWeightUpper saveMemberWeightUpper(@RequestBody Member member){
        MemberWeightUpper upper = memberWeightUpperService.saveMemberWeightUpper(member);
        log.info("MemberWeightUpper Controller upper::{}", JSON.toJSONString(upper));
        return upper;
    }

//    @ApiOperation(value = "更新用户邀请关系")
    @PostMapping(value = "modifyMemberWeightUpper")
    public Boolean modifyMemberWeightUpper(@RequestBody MemberWeightUpper memberWeightUpper){
        return memberWeightUpperService.saveOrUpdate(memberWeightUpper);
    }

//    @ApiOperation(value = "根据会员id查找返佣设置")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "memberId", value = "会员id"),
//    })
    @PostMapping("findRewardSetVoById")
    public RewardSetVo findRewardSetVoById(@RequestParam("memberId") Long memberId) {
        return memberWeightUpperService.findRewardSetVoById(memberId);
    }
}

