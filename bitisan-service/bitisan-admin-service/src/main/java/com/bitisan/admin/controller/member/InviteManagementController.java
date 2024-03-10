package com.bitisan.admin.controller.member;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.InviteManagementScreen;
import com.bitisan.screen.MemberInviteStasticScreen;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberInviteStastic;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.MemberInviteStasticFeign;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("invite/management")
public class InviteManagementController extends BaseController {

    @Autowired
    private MemberFeign memberFeign;

    @Autowired
    private MemberInviteStasticFeign memberInviteStasticFeign;

//    /**
//     * 邀请管理默认查询所有的用户
//     *
//     * @return
//     */
//    @RequiresPermissions("invite:management:query")
//    @AccessLog(module = AdminModule.CMS, operation = "邀请管理默认查询所有的用户")
//    @RequestMapping(value = "look", method = RequestMethod.POST)
//    public MessageResult lookAll(@RequestBody InviteManagementScreen screen) {
//        log.info("默认查询所有的用户 lookAll ={}", screen);
//        Page<Member> page = memberFeign.lookAll(screen);
//        return success(IPage2Page(page));
//    }

    /**
     * 条件查询
     */
    @RequiresPermissions("invite:management:query")
    @AccessLog(module = AdminModule.CMS, operation = "邀请管理多条件查询")
    @RequestMapping(value = "query", method = RequestMethod.POST)
    public MessageResult queryCondition(@RequestBody InviteManagementScreen screen) {
        log.info("默认查询所有的用户 QueryCondition ={}", screen);
        Page<Member> page = memberFeign.lookAll(screen);
        return success(page);
    }

    /**
     * 根据id查询一级二级用户
     */
    @AccessLog(module = AdminModule.CMS, operation = "根据id查询一级二级用户")
    @RequestMapping(value = "info", method = RequestMethod.POST)
    public MessageResult queryFirstAndSecondById(@RequestBody InviteManagementScreen screen) {
        log.info("根据id查询一级二级用户 queryById={}", screen.getId());
        Page<Member> page = memberFeign.queryFirstAndSecondById(screen);
        return success(page);
    }

    @RequiresPermissions("invite:management:rank")
    @AccessLog(module = AdminModule.CMS, operation = "邀请排名条件查询")
    @RequestMapping(value = "rank", method = RequestMethod.POST)
    public MessageResult queryRankList(@RequestBody MemberInviteStasticScreen screen) {
    	// type: 0 = 人数排名   // type: 1 = 佣金排名
    	Page<MemberInviteStastic> page = memberInviteStasticFeign.queryRankList(screen);
    	List<MemberInviteStastic> list= page.getRecords();
    	for(MemberInviteStastic item : list) {
    		item.setUserIdentify(item.getIsRobot() + "-" + item.getUserIdentify());
    	}
    	return success(page);
    }
//    @RequiresPermissions("invite:management:update-rank")
//    @AccessLog(module = AdminModule.CMS, operation = "更新邀请信息")
//    @PostMapping("update-rank")
//    public MessageResult updateRank(@RequestParam("id") Long id,
//    								@RequestParam("estimatedReward") BigDecimal estimatedReward,
//    								@RequestParam("extraReward") BigDecimal extraReward,
//    								@RequestParam("levelOne") Integer levelOne,
//    								@RequestParam("levelTwo") Integer levelTwo) {
//    	log.info(id.toString());
//    	MemberInviteStastic detail = memberInviteStasticService.findById(id);
//    	if(detail == null) {
//    		return error("该排名用户不存在");
//    	}
//    	if(estimatedReward != null) {
//    		detail.setEstimatedReward(estimatedReward);
//    	}
//    	if(extraReward != null) {
//    		detail.setExtraReward(extraReward);
//    	}
//    	if(levelOne != null) {
//    		detail.setLevelOne(levelOne);
//    	}
//    	if(levelTwo != null) {
//    		detail.setLevelTwo(levelTwo);
//    	}
//
//    	memberInviteStasticService.save(detail);
//
//    	return success(detail);
//    }
//    @RequiresPermissions("invite:management:detail-rank")
//    @AccessLog(module = AdminModule.CMS, operation = "邀请信息详情")
//    @RequestMapping(value = "detail-rank", method = RequestMethod.POST)
//    public MessageResult updateRank(@RequestParam(value = "id", defaultValue="0") Long id) {
//
//    	MemberInviteStastic detail = memberInviteStasticService.findById(id);
//
//    	return success(detail);
//    }
}
