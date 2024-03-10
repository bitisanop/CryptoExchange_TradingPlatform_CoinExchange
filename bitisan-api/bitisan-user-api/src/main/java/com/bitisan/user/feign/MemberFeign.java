package com.bitisan.user.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.screen.InviteManagementScreen;
import com.bitisan.screen.MemberScreen;
import com.bitisan.user.entity.Member;
import com.bitisan.util.MessageResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "memberFeign")
public interface MemberFeign {

    /****
     * 根据分类查询分类信息
     */
    @GetMapping(value = "/memberFeign/findMemberById")
    Member findMemberById(@RequestParam("id") Long id);

    @PostMapping(value = "/memberFeign/updateMemberById")
    MessageResult updateMemberById(@RequestBody Member member);

    @PostMapping(value = "/memberFeign/findSuperPartnerMembersByIds")
    MessageResult<List<Member>> findSuperPartnerMembersByIds(@RequestParam("upper") String upper);

    @GetMapping(value = "/memberFeign/findAll")
    Page<Member> findAll(@RequestBody MemberScreen screen, @RequestParam("pageNo")Integer pageNo, @RequestParam("pageSize")Integer pageSize);

    @GetMapping(value = "/memberFeign/findAllList")
    List<Member> findAllList();

    @GetMapping(value = "/memberFeign/findAllWithCondition")
    List<Member> findAllWithCondition(@RequestBody MemberScreen screen);

    @RequestMapping(value = "/memberFeign/look")
    Page<Member> lookAll(@RequestBody InviteManagementScreen screen);

    @RequestMapping(value = "/memberFeign/queryFirstAndSecondById")
    Page<Member> queryFirstAndSecondById(@RequestBody InviteManagementScreen screen);

    @RequestMapping(value = "/memberFeign/findPromotionMember")
    List<Member> findPromotionMember(@RequestParam("id")Long id);

    @RequestMapping(value = "/memberFeign/findByPhone")
    Member findByPhone(@RequestParam("phone")String phone);

    @RequestMapping(value = "/memberFeign/findByEmail")
    Member findByEmail(@RequestParam("email")String email);

    @PostMapping(value = "/memberFeign/save")
    MessageResult save(@RequestBody Member member);

    @PostMapping(value = "/memberFeign/findMemberIdsByAccount")
    List<Long> findMemberIdsByAccount(@RequestParam("account") String account);

    @PostMapping(value = "/memberFeign/findMemberIdsByAccountAndNotCertified")
    List<Long> findMemberIdsByAccountAndNotCertified(@RequestParam("account") String account);

    @PostMapping(value = "/memberFeign/mapByMemberIds")
    Map<Long, Member> mapByMemberIds(@RequestParam("ids")List<Long> ids);

    @PostMapping(value = "/memberFeign/findByUsername")
    Member findByUsername(@RequestParam("name")String name);

    @PostMapping(value = "/memberFeign/login")
    Member login(@RequestParam("name") String name, @RequestParam("password") String password);

    @PostMapping(value = "/memberFeign/getRegistrationNum")
    int getRegistrationNum(@RequestParam("dateStr")String dateStr);
    @PostMapping(value = "/memberFeign/getBussinessNum")
    int getBussinessNum(@RequestParam("dateStr")String dateStr);
    @PostMapping(value = "/memberFeign/getApplicationNum")
    int getApplicationNum(@RequestParam("dateStr")String dateStr);

    @PostMapping(value = "/memberFeign/getStartRegistrationDate")
    Date getStartRegistrationDate();

    @PostMapping(value = "/memberFeign/setMemberInviter")
    void setMemberInviter(@RequestParam("id") Long id, @RequestParam("inviterId") Long inviterId);

    @PostMapping(value = "/memberFeign/resetPwd")
    String resetPwd(@RequestParam("memberId")Long memberId);

    @PostMapping(value = "/memberFeign/createUser")
    MessageResult createUser(@RequestParam("email")String email, @RequestParam("inviteCode")String inviteCode);
}
