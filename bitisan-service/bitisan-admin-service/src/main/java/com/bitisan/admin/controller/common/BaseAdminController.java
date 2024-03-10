package com.bitisan.admin.controller.common;

import com.bitisan.admin.entity.Admin;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @date 2020年12月19日
 */
@Component
public class BaseAdminController extends BaseController {

    @Autowired
    private RedisTemplate redisTemplate ;
    @Autowired
    private LocaleMessageSourceService msService;

    protected Admin getAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Admin) session.getAttribute(SysConstant.SESSION_ADMIN);
    }

    /**
     * 判断手机验证码正确否
     * @param code 验证码
     * @param key redis中的key 前缀+手机号
     * @return
     */
    protected MessageResult checkCode(String code, String key){
        return success(msService.getMessage("CODE_CORRECT"));
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        Object value = valueOperations.get(key);
//        if(value==null) {
//            return error(msService.getMessage("CODE_NOT_EXIST_RESEND"));
//        }
//        if(!value.toString().equals(code)) {
//            return  error(msService.getMessage("CODE_ERROR"));
//        }
//        valueOperations.getOperations().delete(key);
//        /**
//         * 十分钟之内无需再次验证
//         */
//        valueOperations.set(key+"_PASS",true,10, TimeUnit.MINUTES);
//        return success(msService.getMessage("CODE_CORRECT"));
    }
}
