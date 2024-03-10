package com.bitisan.admin.controller.member;

import com.alibaba.fastjson.JSONObject;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.Admin;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.SysConstant;
import com.bitisan.constant.TransactionType;
import com.bitisan.screen.MemberWalletScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.dto.MemberWalletDTO;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberTransaction;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.MemberTransactionFeign;
import com.bitisan.user.feign.MemberWalletFeign;
import com.bitisan.util.DateUtil;
import com.bitisan.util.MessageResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("member/member-wallet")
@Slf4j
public class MemberWalletController extends BaseAdminController {

    @Autowired
    private MemberWalletFeign memberWalletService;
    @Autowired
    private MemberFeign memberFeign;
    @Autowired
    private CoinFeign coinService;
    @Autowired
    private MemberTransactionFeign memberTransactionService;

    @Autowired
    private LocaleMessageSourceService messageSource;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${spark.system.admins}")
    private String admins;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;
    @Value("${spark.system.host}")
    private String host;
    @Value("${spark.system.name}")
    private String company;

    @RequiresPermissions("member:member-wallet:recharge")
    @PostMapping("recharge")
    @AccessLog(module = AdminModule.MEMBER, operation = "充币管理")
    public MessageResult recharge(
    		@SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin,
            @RequestParam("unit") String unit,
            @RequestParam("uid") Long uid,
            @RequestParam("amount") BigDecimal amount) {
        //前端传的是name
        Coin coin = coinService.findByUnit(unit);
        if (coin == null) {
            return error(messageSource.getMessage("CURRENCY_NOT_FOUND"));
        }
        MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(coin.getUnit(), uid);
        Assert.notNull(memberWallet, "wallet null");
        memberWalletService.increaseBalance(memberWallet.getId(),amount);
        MemberTransaction memberTransaction = new MemberTransaction();
        memberTransaction.setFee(BigDecimal.ZERO);
        memberTransaction.setAmount(amount);
        memberTransaction.setMemberId(memberWallet.getMemberId());
        memberTransaction.setSymbol(coin.getUnit());
        memberTransaction.setType(TransactionType.ADMIN_RECHARGE.getCode());
        memberTransaction.setCreateTime(DateUtil.getCurrentDate());
        memberTransaction.setRealFee("0");
        memberTransaction.setDiscountFee("0");
        memberTransactionService.save(memberTransaction);

        String[] adminList = admins.split(",");
        for(int i = 0; i < adminList.length; i++) {
			sendEmailMsg(adminList[i], "管理员人工充值(用户ID: " + uid + ", 币种: " + unit + ", 数量: " + amount + "); 操作者：" +admin.getUsername() + "/" + admin.getMobilePhone(), "人工充值通知");
		}

        return success(messageSource.getMessage("SUCCESS"));
    }

    /**
     * 发送邮件
     * @param email
     * @param msg
     * @param subject
     * @throws MessagingException
     * @throws IOException
     * @throws TemplateException
     */
    @Async
    public void sendEmailMsg(String email, String msg, String subject){
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject(company + "-" + subject);
            Map<String, Object> model = new HashMap<>(16);
            model.put("msg", msg);
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
            cfg.setClassForTemplateLoading(this.getClass(), "/templates");
            Template template = cfg.getTemplate("simpleMessage.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            helper.setText(html, true);

            //发送邮件
            javaMailSender.send(mimeMessage);
            log.info("send email for {},content:{}", email, html);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    @RequiresPermissions("member:member-wallet:balance")
    @PostMapping("balance")
    @AccessLog(module = AdminModule.MEMBER, operation = "余额管理")
    public MessageResult getBalance(
            MemberWalletScreen screen) {
        Page<MemberWalletDTO> page = memberWalletService.getBalance(screen);
        return success(messageSource.getMessage("SUCCESS"), IPage2Page(page));
    }



    @RequiresPermissions("member:member-wallet:reset-address")
    @PostMapping("reset-address")
    @AccessLog(module = AdminModule.MEMBER, operation = "重置钱包地址")
    public MessageResult resetAddress(String unit, long uid) {
        Member member = memberFeign.findMemberById(uid);
        Assert.notNull(member, "member null");
        try {
            JSONObject json = new JSONObject();
            json.put("uid", member.getId());
            json.put("unit", unit);
            log.info("rocketMQTemplate send : topic = {reset-member-address} , json = {}", unit, json);
            rocketMQTemplate.convertAndSend("reset-member-address", json.toJSONString());
            return MessageResult.success(messageSource.getMessage("SUCCESS"));
        } catch (Exception e) {
            return MessageResult.error(messageSource.getMessage("REQUEST_FAILED"));
        }
    }

    @RequiresPermissions("member:member-wallet:lock-wallet")
    @PostMapping("lock-wallet")
    @AccessLog(module = AdminModule.MEMBER, operation = "锁定钱包")
    public MessageResult lockWallet(Long uid, String unit) {
        if (memberWalletService.lockWallet(uid, unit)) {
            return success(messageSource.getMessage("SUCCESS"));
        } else {
            return error(500, messageSource.getMessage("REQUEST_FAILED"));
        }
    }

    @RequiresPermissions("member:member-wallet:unlock-wallet")
    @PostMapping("unlock-wallet")
    @AccessLog(module = AdminModule.MEMBER, operation = "解锁钱包")
    public MessageResult unlockWallet(Long uid, String unit) {
        memberWalletService.unlockWallet(uid, unit);
        return success(messageSource.getMessage("SUCCESS"));
    }

}
