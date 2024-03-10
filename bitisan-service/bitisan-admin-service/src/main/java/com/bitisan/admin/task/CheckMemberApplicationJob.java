package com.bitisan.admin.task;

import com.bitisan.sms.SMSProvider;
import com.bitisan.user.feign.MemberApplicationFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.util.MessageResult;
import com.xxl.job.core.handler.annotation.XxlJob;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 检查实名认证申请用户
 * @author Bitisan  E-mail:bizzanhevin@gmail.com
 *
 */
@Component
@Slf4j
public class CheckMemberApplicationJob {
	@Autowired
	private MemberFeign memberService;

	@Autowired
    private MemberApplicationFeign memberApplicationService;

	@Autowired
    private SMSProvider smsProvider;

	@Resource
    private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
    private String from;
    @Value("${spark.system.host}")
    private String host;
    @Value("${spark.system.name}")
    private String company;

	@Value("${spark.system.admins}")
    private String admins;

    @Value("${spark.system.admin_phones}")
    private String adminPhones;

    private Long maxUserId = Long.valueOf(0);

	/**
	 * 每小时检查一次
	 */
//	@Scheduled(cron = "0 0 * * * *")
	@XxlJob("checkNewMemberApplication")
    public void checkNewMemberApplication(){
		if(isRestTime()) {
			return;
		}
		// 查询待审核数量
		Integer count = memberApplicationService.countAuditing();
		if(count > 0) {
			try {
				String[] adminList = admins.split(",");
				for(int i = 0; i < adminList.length; i++) {
					sendEmailMsg(adminList[i], "有新实名认证申请( 共" + count+ "条 )", "新实名认证审核通知");
				}
			} catch (Exception e) {
				MessageResult result;
				try {
					String[] phones = adminPhones.split(",");
					if(phones.length > 0) {
						result = smsProvider.sendSingleMessage(phones[0], "==新实名申请==");
						if(result.getCode() != 0) {
							if(phones.length > 1) {
								smsProvider.sendSingleMessage(phones[1], "==新实名申请==");
							}
						}
					}

				} catch (Exception e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	@Async
    public void sendEmailMsg(String email, String msg, String subject) throws MessagingException, IOException, TemplateException {
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
    }


	private boolean isRestTime() {
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 每日不同时间段需要不同成交量体现

        if(hour >= 0 && hour <= 6) {
        	return true;
        }
        return false;
	}
}
