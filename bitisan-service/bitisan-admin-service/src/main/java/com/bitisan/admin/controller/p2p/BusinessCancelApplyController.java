package com.bitisan.admin.controller.p2p;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.constant.BooleanEnum;
import com.bitisan.constant.CertifiedBusinessStatus;
import com.bitisan.constant.DepositStatusEnum;
import com.bitisan.constant.MemberLevelEnum;
import com.bitisan.p2p.entity.*;
import com.bitisan.p2p.feign.BusinessAuthFeign;
import com.bitisan.screen.CancelApplyScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.MemberWalletFeign;
import com.bitisan.util.DateUtil;
import com.bitisan.util.MessageResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;


@RestController
@RequestMapping("business/cancel-apply")
public class BusinessCancelApplyController extends BaseAdminController {
    private static Logger logger = LoggerFactory.getLogger(BusinessCancelApplyController.class);
    @Autowired
    private BusinessAuthFeign businessAuthFeign;
    @Autowired
    private MemberWalletFeign memberWalletService;
    @Autowired
    private MemberFeign memberService;
    @Autowired
    private LocaleMessageSourceService msService;
    @Autowired
    private CoinFeign coinFeign;

    @PostMapping("page-query")
    @RequiresPermissions("business:cancel-apply:page-query")
    public MessageResult pageQuery(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "account", required = false) String account,
            @RequestParam(value = "status", required = false) CertifiedBusinessStatus status,
            @RequestParam(value = "startDate", required = false)@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false)@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd") Date endDate) {
        CancelApplyScreen screen = new CancelApplyScreen();
        screen.setPageNo(pageNo);
        screen.setPageSize(pageSize);
        screen.setAccount(account);
        screen.setStatus(status);
        screen.setStartDate(startDate);
        screen.setEndDate(endDate);
        Page<BusinessCancelApply> page = businessAuthFeign.findAllCancelApply(screen);
        return success(IPage2Page(page));
    }

    /**
     * 退保审核接口
     *
     * @param id
     * @param success 通过 : IS_TRUE
     * @param reason  审核不通过的理由
     * @return
     */
    @RequiresPermissions("business:cancel-apply:check")
    @PostMapping("check")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult pass(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "success") BooleanEnum success,
            @RequestParam(value = "reason", defaultValue = "") String reason) {
        BusinessCancelApply businessCancelApply = businessAuthFeign.findCancelApplyById(id);
        Member member = memberService.findMemberById(businessCancelApply.getMemberId());
        List<BusinessAuthApply> businessAuthApplyList = businessAuthFeign.findByMemberAndCertifiedBusinessStatus(member.getId(), CertifiedBusinessStatus.VERIFIED);
        if (businessAuthApplyList == null || businessAuthApplyList.size() < 1) {
            return error("data exception,businessAuthApply not exist。。。。");
        }
        BusinessAuthApply businessAuthApply = businessAuthApplyList.get(0);
        /**
         * 处理 取消申请 日志
         */
        businessCancelApply.setHandleTime(DateUtil.getCurrentDate());
        businessCancelApply.setDepositRecordId(businessAuthApply.getDepositRecordId());
        businessCancelApply.setDetail(reason);

        if (success == BooleanEnum.IS_TRUE) {

            businessCancelApply.setStatus(CertifiedBusinessStatus.RETURN_SUCCESS.getCode());
            businessAuthFeign.updateCancelApply(businessCancelApply);

            //取消商家认证 审核通过
            //member.setCertifiedBusinessStatus(RETURN_SUCCESS);//未认证
            member.setCertifiedBusinessStatus(CertifiedBusinessStatus.NOT_CERTIFIED);
            member.setMemberLevel(MemberLevelEnum.REALNAME.getCode());
            memberService.save(member);

            List<DepositRecord> depositRecordList = businessAuthFeign.findDepositByMemberAndStatus(member.getId(), DepositStatusEnum.PAY);
            if (depositRecordList != null && depositRecordList.size() > 0) {
                BigDecimal deposit = BigDecimal.ZERO;

                /**
                 * 更改保证金记录
                 */
                for (DepositRecord depositRecord : depositRecordList) {
                    depositRecord.setStatus(DepositStatusEnum.GET_BACK);
                    deposit = deposit.add(depositRecord.getAmount());
                    businessAuthFeign.updateDepositRecord(depositRecord);
                }

                /**
                 * 退回保证金
                 */
                if (businessAuthApplyList != null && businessAuthApplyList.size() > 0) {
                    BusinessAuthDeposit businessAuthDeposit =  businessAuthFeign.findDepositById(businessAuthApply.getBusinessAuthDepositId());
                    MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(businessAuthDeposit.getCoinId(), member.getId());
                    memberWallet.setBalance(memberWallet.getBalance().add(deposit));
                    // memberWallet.setFrozenBalance(memberWallet.getFrozenBalance().subtract(deposit));
                    memberWalletService.save(memberWallet);
                }
            }
            /**
             * 更改认证申请状态
             */
            return MessageResult.success(msService.getMessage("PASS_THE_AUDIT"), reason);
        } else {
            //审核不通过，商家 维持已认证状态
            //member.setCertifiedBusinessStatus(RETURN_FAILED);
            member.setCertifiedBusinessStatus(CertifiedBusinessStatus.VERIFIED);
            member.setMemberLevel(MemberLevelEnum.IDENTIFICATION.getCode());
            memberService.save(member);

            businessCancelApply.setStatus(CertifiedBusinessStatus.RETURN_FAILED.getCode());
            businessAuthFeign.updateCancelApply(businessCancelApply);

            return MessageResult.success(msService.getMessage("AUDIT_DOES_NOT_PASS"), reason);
        }
    }

    /**
     * @param id:businessCancelApply id
     * @return
     */

    @PostMapping("detail")
    @RequiresPermissions("business:cancel-apply:detail")
    public MessageResult detail(@RequestParam(value = "id") Long id) {
        BusinessCancelApply businessCancelApply = businessAuthFeign.findCancelApplyById(id);
        DepositRecord depositRecord = businessAuthFeign.findDepositRecordById(businessCancelApply.getDepositRecordId());
        Map<String, Object> map1 = businessAuthFeign.getBusinessOrderStatistics(businessCancelApply.getMemberId());
        logger.info("会员订单信息:{}", map1);
        Map<String, Object> map2 = businessAuthFeign.getBusinessAppealStatistics(businessCancelApply.getMemberId());
        logger.info("会员申诉信息:{}", map2);
        Long advertiseNum = businessAuthFeign.getAdvertiserNum(businessCancelApply.getMemberId());
        logger.info("会员广告信息:{}", advertiseNum);
        Map<String, Object> map = new HashMap<>();
        map.putAll(map1);
        map.putAll(map2);
        map.put("advertiseNum", advertiseNum);
        map.put("businessCancelApply", businessCancelApply);
        map.put("depositRecord", depositRecord);
        logger.info("会员退保相关信息:{}", map);
        return success(map);
    }

    @PostMapping("get-search-status")
    public MessageResult getSearchStatus() {
        CertifiedBusinessStatus[] statuses = CertifiedBusinessStatus.values();
        List<Map> list = new ArrayList<>();
        for (CertifiedBusinessStatus status : statuses) {
            if (status.getCode() < CertifiedBusinessStatus.CANCEL_AUTH.getCode()) {
                continue;
            }
            Map map = new HashMap();
            map.put("name", status.getDescription());
            map.put("value", status.getCode());
            list.add(map);
        }
        return success(list);
    }
}
