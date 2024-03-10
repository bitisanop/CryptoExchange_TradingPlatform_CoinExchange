package com.bitisan.admin.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.dto.CoinprotocolDTO;
import com.bitisan.screen.WithdrawScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.Withdraw;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.user.feign.CoinprotocolFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.WithdrawFeign;
import com.bitisan.user.vo.WithdrawExcelVO;
import com.bitisan.user.vo.WithdrawVO;
import com.bitisan.util.BindingResultUtil;
import com.bitisan.util.ExcelUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 提币管理
 */
@Slf4j
@RestController
@RequestMapping("/finance/withdraw")
public class WithdrawController extends BaseAdminController {

    @Autowired
    private CoinFeign coinService;

    @Autowired
    private CoinprotocolFeign coinprotocolService;

    @Autowired
    private WithdrawFeign withdrawService;

    @Autowired
    private MemberFeign memberService;

    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("finance:withdraw:coin-list")
    @GetMapping("/coin-list")
    @AccessLog(module = AdminModule.FINANCE, operation = "提币审核里获取币种列表")
    public MessageResult coinList() {

        List<Coin> list = coinService.getAllCoinNameAndUnit();
        return success(list);
    }

    @RequiresPermissions("finance:withdraw:protocol-list")
    @GetMapping("/protocol-list")
    @AccessLog(module = AdminModule.FINANCE, operation = "提币审核里获取币种协议列表")
    public MessageResult protocolList() {

        List<CoinprotocolDTO> list = coinprotocolService.list();

        return success(list);
    }

    @RequiresPermissions("finance:withdraw:page-query")
    @PostMapping("/page-query")
    @AccessLog(module = AdminModule.FINANCE, operation = "获取提币审核列表")
    public MessageResult pageQuery(WithdrawScreen withdrawScreen,
                                   HttpServletResponse response) throws IOException {


        // 导出
        if (withdrawScreen.getIsOut() == 1) {

            List<Withdraw> allOut = withdrawService.findAllOut(withdrawScreen);
            Set<Long> memberSet = new HashSet<>();
            allOut.forEach(v -> {
                memberSet.add(Long.valueOf(v.getMemberId()));
            });
            Map<Long, Member> memberMap = memberService.mapByMemberIds(new ArrayList<>(memberSet));

            List<WithdrawExcelVO> voList = new ArrayList<>();


            allOut.forEach(v -> {
                WithdrawExcelVO vo = new WithdrawExcelVO();
                BeanUtils.copyProperties(v, vo);

                vo.setMemberId(Long.valueOf(v.getMemberId()));

                vo.setMoney(String.valueOf(v.getMoney()));

                vo.setFee(String.valueOf(v.getFee()));

                vo.setReal_money(String.valueOf(v.getRealMoney()));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                vo.setAddtime(sdf.format(new Date(v.getAddTime())));
                if (v.getProcessTime() != null && v.getProcessTime() > 0) {
                    vo.setProcesstime(sdf.format(new Date(v.getProcessTime())));
                } else {
                    vo.setProcesstime("--");
                }

                Integer statusD = v.getStatus();
                String statusStr = "";
                if (statusD == -1) {
                    statusStr = messageSource.getMessage("REJECTED");
                } else if (statusD == 0) {
                    statusStr = messageSource.getMessage("PENDING");
                } else if (statusD == 1) {
                    statusStr = messageSource.getMessage("PROCESSING");
                } else if (statusD == 2) {
                    statusStr = messageSource.getMessage("PROCESSED");
                } else {
                    statusStr = messageSource.getMessage("FAILURE");
                }
                vo.setStatus(statusStr);

                Long memberId = vo.getMemberId();
                if (memberMap.containsKey(memberId)) {
                    Member member = memberMap.get(memberId);
                    vo.setEmail(member.getEmail());
                    vo.setMobilePhone(member.getMobilePhone());
                }
                voList.add(vo);

            });

            ExcelUtil.listToExcel(voList, WithdrawExcelVO.class.getDeclaredFields(), response.getOutputStream());

            return null;

        }

        Page<Withdraw> all = withdrawService.findAll(withdrawScreen);
        List<Long> memberIds = all.getRecords().stream().map(v -> (long) v.getMemberId()).collect(Collectors.toList());
        Map<Long, Member> memberMap = memberService.mapByMemberIds(memberIds);

        Page<WithdrawVO> page = new Page<>();
        BeanUtils.copyProperties(all, page);

        List<WithdrawVO> list = new ArrayList<>();
        for (Withdraw v : all.getRecords()) {
            WithdrawVO withdrawVO = new WithdrawVO();
            BeanUtils.copyProperties(v, withdrawVO);
            Long memberid = (long) withdrawVO.getMemberId();
            if (memberMap.containsKey(memberid)) {
                withdrawVO.setUsername(memberMap.get(memberid).getUsername());
                withdrawVO.setEmail(memberMap.get(memberid).getEmail());
            }
            list.add(withdrawVO);
        }
        page.setRecords(list);
        return success(IPage2Page(page));
    }


    @RequiresPermissions("finance:withdraw:merge")
    @PostMapping("/merge")
    @AccessLog(module = AdminModule.FINANCE, operation = "审核/驳回提币")
    public MessageResult merge(@Valid Withdraw withdraw, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }

        if (withdraw.getId() == null || withdraw.getId() <= 0) {
            result = error(messageSource.getMessage("PLEASE_SELECT_RECORD_FOR_REVIEW"));
            return result;
        }

        Withdraw one = withdrawService.findOne(withdraw.getId());
        if (one == null) {
            result = error(messageSource.getMessage("PLEASE_SELECT_RECORD_FOR_REVIEW"));
            return result;
        }

        Withdraw update = new Withdraw();
        update.setId(withdraw.getId());
        update.setStatus(withdraw.getStatus());
        if (withdraw.getStatus() == -1) {
            update.setWithdrawInfo(withdraw.getWithdrawInfo());
        }

        if (StringUtils.isBlank(withdraw.getWithdrawInfo())) {
            update.setWithdrawInfo("");
        }
        update.setProcessTime(new Date().getTime());
        update.setCoinName(one.getCoinName());
        update.setMemberId(one.getMemberId());
        update.setMoney(one.getMoney());
        withdrawService.updateById(update);

        result = success(messageSource.getMessage("OPERATION_SUCCESS"));
        result.setData(withdraw);
        return result;
    }

}
