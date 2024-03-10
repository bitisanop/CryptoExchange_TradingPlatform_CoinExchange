package com.bitisan.agent.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.AccessLog;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.ContractOrderEntrustStatus;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.ContractOrderEntrustScreen;
import com.bitisan.screen.PageParam;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.swap.entity.ContractCoin;
import com.bitisan.swap.entity.ContractOrderEntrust;
import com.bitisan.swap.feign.ContractCoinFeign;
import com.bitisan.swap.feign.ContractOrderEntrustFeign;
import com.bitisan.user.entity.Member;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.bitisan.constant.SysConstant.SESSION_MEMBER;


@RestController
@RequestMapping("/swap/order")
@Slf4j
public class ContractOrderEntrustController extends BaseController {
    @Autowired
    private ContractOrderEntrustFeign contractOrderEntrustFeign;

    @Autowired
    private MemberFeign memberFeign;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ContractCoinFeign contractCoinFeign;
    @Autowired
    private LocaleMessageSourceService messageSource;


    @PostMapping("/coin/page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约交易对 列表")
    public MessageResult list( PageParam pageParam) {

        Page<ContractCoin> coinList = contractCoinFeign.findAll( pageParam);

        return success(IPage2Page(coinList));
    }

    /**
     * 分页查询
     * @param
     * @param screen
     * @return
     */
    @PermissionOperation
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约订单 列表")
    public MessageResult pageQuery(
            PageParam pageParam,
            ContractOrderEntrustScreen screen,@RequestHeader(SysConstant.SESSION_MEMBER) String authMember
            ) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Member checkMember = memberFeign.findMemberById(user.getId());
        if(!checkMember.getSuperPartner().equals("1")) {
            return error(messageSource.getMessage("NOT_AN_AGENT"));
        }
        //获取查询条件
        if(screen.getEndTime()!=null ){
            Calendar calendar   =   new GregorianCalendar();
            calendar.setTime(screen.getEndTime());
            calendar.add(Calendar.DATE, 1);
            screen.setEndTime(calendar.getTime());
        }
        List<Member> memberList = memberFeign.findPromotionMember(checkMember.getId());
        if(memberList.isEmpty()){
            return error(messageSource.getMessage("NO_SUBORDINATES"));
        }
        Page<ContractOrderEntrust> all = contractOrderEntrustFeign.findAll4Agent(checkMember.getId(), pageParam, screen);
        return success(IPage2Page(all));
    }

    /**
     * 撤销委托
     * @param orderId
     * @return
     */
    @PostMapping("cancel")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "永续合约 撤单")
    public MessageResult cancelOrder(Long orderId) {
        ContractOrderEntrust order = contractOrderEntrustFeign.findOne(orderId);
        if(order == null) {
            return MessageResult.error(messageSource.getMessage("CANCEL_ORDER_FAILED"));
        }
        if(order.getStatus() != ContractOrderEntrustStatus.ENTRUST_ING) {
            return MessageResult.error(messageSource.getMessage("DELEGATE_STATUS_ERROR"));
        }
        // 发送消息至Exchange系统
        rocketMQTemplate.convertAndSend("swap-order-cancel", JSON.toJSONString(order));

        log.info(">>>>>>>>>>订单撤销提交完成>>>>>>>>>>");
        return MessageResult.success(messageSource.getMessage("OPERATION_SUCCESS"));
    }
}
