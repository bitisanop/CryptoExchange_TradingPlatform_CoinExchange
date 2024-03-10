package com.bitisan.admin.controller;

import com.bitisan.admin.entity.Feedback;
import com.bitisan.admin.service.FeedbackService;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @date 2020年03月19日
 */
@RestController
@Slf4j
public class FeedbackController extends BaseController {
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private LocaleMessageSourceService msService;

    /**
     * 提交反馈意见
     *
     * @param
     * @param remark
     * @return
     */
    @PermissionOperation
    @RequestMapping("feedback")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult feedback(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, String remark) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Feedback feedback = new Feedback();
        feedback.setMemberId(user.getId());
        feedback.setRemark(remark);
        boolean save = feedbackService.save(feedback);
        if (save) {
            return MessageResult.success();
        } else {
            return MessageResult.error(msService.getMessage("SYSTEM_ERROR"));
        }
    }
}
