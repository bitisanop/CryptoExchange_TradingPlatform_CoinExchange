package com.bitisan.agent.controller;

import com.alibaba.fastjson.JSON;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.swap.feign.ContractRewardRecordFeign;
import com.bitisan.swap.vo.RewardSetVo;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberWeightUpper;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.MemberWeightUpperFeign;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sulinxin
 */
@Slf4j
@RestController
@RequestMapping("transactionRebateSet")
public class MemberTransactionRebateSetController extends BaseController {

    @Resource
    private MemberFeign memberFeign;
    @Resource
    private MemberWeightUpperFeign memberWeightUpperFeign;
    @Resource
    private ContractRewardRecordFeign contractRewardRecordFeign;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequestMapping(value = "query")
    @PermissionOperation
    public MessageResult query(
            @RequestHeader(SysConstant.SESSION_MEMBER) String authMember
    ) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member checkMember = memberFeign.findMemberById(user.getId());
        if(!checkMember.getSuperPartner().equals("1")) {
            return error(messageSource.getMessage("NOT_AN_AGENT"));
        }
        RewardSetVo vo = memberWeightUpperFeign.findRewardSetVoById(user.getId());
        return success(vo);
    }

    @RequestMapping(value = "clear")
    @PermissionOperation
    public MessageResult clear(
            @RequestHeader(SysConstant.SESSION_MEMBER) String authMember
    ){
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member checkMember = memberFeign.findMemberById(user.getId());
        if(!checkMember.getSuperPartner().equals("1")) {
            return error(messageSource.getMessage("NOT_AN_AGENT"));
        }
        contractRewardRecordFeign.clearRewardSetVoById(user.getId());
        return success();
    }

    @PermissionOperation
    @RequestMapping(value = "set")
    public MessageResult set(
            @RequestHeader(SysConstant.SESSION_MEMBER) String authMember,
            @RequestParam("id") Long memberId,
            @RequestParam("rate") Integer rate
            ) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        if(memberId == null){
            return error(messageSource.getMessage("USER_ID_CANNOT_BE_EMPTY"));
        }
        if(rate == null){
            return error(messageSource.getMessage("USER_PROPORTION_CANNOT_BE_EMPTY"));
        }
        if(rate.intValue() < 0 || rate.intValue() > 100){
            return error(messageSource.getMessage("USER_PROPORTION_CANNOT_BE_LESS_THAN_0_OR_MORE_THAN_100_PERCENT"));
        }
        Member checkMember = memberFeign.findMemberById(user.getId());
        if(!"1".equals(checkMember.getSuperPartner())) {
            return error(messageSource.getMessage("NOT_AN_AGENT"));
        }
        Member setMember = memberFeign.findMemberById(memberId);
//        if("1".equals(setMember.getSuperPartner())){
//            return error("您不设置下级代理商！");
//        }
        MemberWeightUpper upperMemberId = memberWeightUpperFeign.findMemberWeightUpperByMemberId(setMember.getInviterId());
        Member upperMember = memberFeign.findMemberById(setMember.getInviterId());
        if("1".equals(upperMember.getSuperPartner())){
            if (rate.intValue() >= 100) {
                return error(messageSource.getMessage("PROPORTION_SHOULD_BE_LESS_THAN_THE_SUPERIOR_REFEREE"));
            }
        }else {
            if (rate.intValue() >= upperMemberId.getRate().intValue()) {
                return error(messageSource.getMessage("PROPORTION_SHOULD_BE_LESS_THAN_THE_SUPERIOR_REFEREE"));
            }
        }
        List<Member> promotionMember = memberFeign.findPromotionMember(setMember.getId());
        if(promotionMember!=null && !promotionMember.isEmpty()){
            String idString = promotionMember.stream().map(e-> e.getId().toString()).collect(Collectors.joining(","));
            List<MemberWeightUpper> uppers = memberWeightUpperFeign.findAllByUpperIds(idString);
            for (MemberWeightUpper memberWeightUpper:uppers) {
                if(rate.intValue() <= memberWeightUpper.getRate().intValue()){
                    return error(messageSource.getMessage("PROPORTION_SHOULD_BE_GREATER_THAN_THE_DIRECT_REFEREE"));
                }
            }
        }
        log.info("setMember====>{}",JSON.toJSONString(setMember));
        MemberWeightUpper memberWeightUpper = memberWeightUpperFeign.saveMemberWeightUpper(setMember);
        log.info("memberWeightUpper====>{}",JSON.toJSONString(memberWeightUpper));
//        if(memberWeightUpper.getUpper() != null){
//            MessageResult<List<Member>> allByIds = memberFeign.findSuperPartnerMembersByIds(memberWeightUpper.getUpper());
//            log.info("allByIds====>{}", JSON.toJSONString(allByIds));
//            if(allByIds!=null){
//                Optional<Member> firstSuperPartner = allByIds.getData().stream().filter(e -> "1".equals(e.getSuperPartner())).findFirst();
//                if(firstSuperPartner.isPresent()){
//                    if (!firstSuperPartner.get().getId().equals(checkMember.getId())){
//                        return error("不能设置下级代理商的直推用户！");
//                    }
//                }
//            }
//        }
        memberWeightUpper.setRate(rate);
        memberWeightUpperFeign.modifyMemberWeightUpper(memberWeightUpper);
        contractRewardRecordFeign.clearRewardSetVoById(user.getId());
        return success();
    }
}
