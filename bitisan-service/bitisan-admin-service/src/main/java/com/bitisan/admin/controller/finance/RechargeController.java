package com.bitisan.admin.controller.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.dto.CoinprotocolDTO;
import com.bitisan.screen.RechargeScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.Recharge;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.user.feign.CoinprotocolFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.RechargeFeign;
import com.bitisan.user.vo.RechargeExcelVO;
import com.bitisan.user.vo.RechargeVO;
import com.bitisan.util.ExcelUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 充值管理
 */
@Slf4j
@RestController
@RequestMapping("/finance/recharge")
public class RechargeController extends BaseAdminController {

    @Autowired
    private CoinFeign coinService;

    @Autowired
    private CoinprotocolFeign coinprotocolService;

    @Autowired
    private RechargeFeign rechargeService;

    @Autowired
    private MemberFeign memberService;

    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("finance:recharge:coin-list")
    @GetMapping("/coin-list")
    @AccessLog(module = AdminModule.FINANCE, operation = "充值记录里获取币种列表")
    public MessageResult coinList() {

        List<Coin> list = coinService.getAllCoinNameAndUnit();
        return success(list);
    }

    @RequiresPermissions("finance:recharge:protocol-list")
    @GetMapping("/protocol-list")
    @AccessLog(module = AdminModule.FINANCE, operation = "充值记录里获取币种协议列表")
    public MessageResult protocolList() {

        List<CoinprotocolDTO> list = coinprotocolService.list();

        return success(list);
    }

    @RequiresPermissions("finance:recharge:page-query")
    @PostMapping("/page-query")
    @AccessLog(module = AdminModule.FINANCE, operation = "获取充值记录")
    public MessageResult pageQuery(RechargeScreen rechargeScreen,
                                   HttpServletResponse response) throws IOException {
        // 导出
        if (rechargeScreen.getIsOut() == 1) {
            List<Recharge> allOut = rechargeService.findAllOut(rechargeScreen);
            Set<Long> memberSet = new HashSet<>();
            allOut.forEach(v -> {
                memberSet.add(Long.valueOf(v.getMemberId()));
            });
            Map<Long, Member> memberMap = memberService.mapByMemberIds(new ArrayList<>(memberSet));

            List<RechargeExcelVO> voList = new ArrayList<>();

            allOut.forEach(v -> {
                RechargeExcelVO vo = new RechargeExcelVO();
                BeanUtils.copyProperties(v, vo);

                vo.setMemberId(Long.valueOf(v.getMemberId()));

                vo.setMoney(String.valueOf(v.getMoney()));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (v.getAddTime() != null && v.getAddTime() > 0) {
                    vo.setAddtime(sdf.format(new Date(v.getAddTime())));
                } else {
                    vo.setAddtime("--");
                }

                Integer status = v.getStatus();
                String statusStr = "";
                if (status == 0) {
                    statusStr = messageSource.getMessage("NOT_RECEIVED");
                } else if (status == 1) {
                    statusStr = messageSource.getMessage("RECEIVED");
                } else {
                    statusStr = messageSource.getMessage("FAILURE");
                }
                vo.setStatus(statusStr);

                vo.setConfirms(v.getConfirms() + "/" + v.getNConfirms());

                Long memberId = vo.getMemberId();
                if (memberMap.containsKey(memberId)) {
                    Member member = memberMap.get(memberId);
                    vo.setEmail(member.getEmail());
                    vo.setMobilePhone(member.getMobilePhone());
                }
                voList.add(vo);

            });

            ExcelUtil.listToExcel(voList, RechargeExcelVO.class.getDeclaredFields(), response.getOutputStream());

            return null;
        }


        Page<Recharge> all = rechargeService.findAll(rechargeScreen);

        List<Long> memberIds = all.getRecords().stream().map(v -> (long) v.getMemberId()).collect(Collectors.toList());
        Map<Long, Member> memberMap = memberService.mapByMemberIds(memberIds);


        Page<RechargeVO> page = new Page<>();
        BeanUtils.copyProperties(all, page);

        List<RechargeVO> list = new ArrayList<>();
        for (Recharge v : all.getRecords()) {
            RechargeVO rechargeVO = new RechargeVO();
            BeanUtils.copyProperties(v, rechargeVO);
            Long memberid = (long) rechargeVO.getMemberId();
            if (memberMap.containsKey(memberid)) {
                rechargeVO.setUsername(memberMap.get(memberid).getUsername());
            }
            list.add(rechargeVO);
        }

        return success(IPage2Page(page));
    }

}
