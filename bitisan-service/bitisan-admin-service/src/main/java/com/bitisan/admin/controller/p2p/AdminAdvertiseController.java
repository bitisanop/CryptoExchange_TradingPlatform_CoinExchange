package com.bitisan.admin.controller.p2p;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.AdvertiseControlStatus;
import com.bitisan.constant.AdvertiseType;
import com.bitisan.p2p.entity.Advertise;
import com.bitisan.p2p.feign.AdvertiseFeign;
import com.bitisan.screen.AdvertiseScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Country;
import com.bitisan.user.entity.Member;
import com.bitisan.user.feign.CountryFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.util.FileUtil;
import com.bitisan.util.MessageResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 后台广告web层
 * @date 2019/1/3 9:42
 */
@RestController
@RequestMapping("/otc/advertise")
public class AdminAdvertiseController extends BaseAdminController {

    @Autowired
    private AdvertiseFeign advertiseService;
    @Autowired
    private LocaleMessageSourceService messageSource;
    @Autowired
    private MemberFeign memberFeign;
    /**
     * 当地货币缩写
     */
    @Autowired
    private CountryFeign countryFeign;

    @RequiresPermissions("otc:advertise:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.OTC, operation = "后台广告Advertise详情")
    public MessageResult detail(Long id) {
        if (id == null) {
            return error(messageSource.getMessage("ID_REQUIRED"));
        }
        Advertise one = advertiseService.findOne(id);
        if (one == null) {
            return error(messageSource.getMessage("NO_ADVERTISEMENT_WITH_THIS_ID"));
        }
        if(StringUtils.isNotEmpty(one.getCountry())) {
          Country country = countryFeign.findByZhName(one.getCountry());
          if(country!=null){
              one.setLocalCurrency(country.getLocalCurrency());
          }
        }
        if(StringUtils.isEmpty(one.getUsername())){
            Member member = memberFeign.findMemberById(one.getMemberId());
            one.setUsername(member.getUsername());
        }
        return success(messageSource.getMessage("SUCCESS"), one);
    }

    @RequiresPermissions("otc:advertise:alter-status")
    @PostMapping("alter-status")
    @AccessLog(module = AdminModule.OTC, operation = "修改后台广告Advertise状态")
    public MessageResult statue(
            @RequestParam(value = "ids") Long[] ids,
            @RequestParam(value = "status") AdvertiseControlStatus status) {
        advertiseService.turnOffBatch(status,ids);
        return success(messageSource.getMessage("SUCCESS"));
    }

    @RequiresPermissions("otc:advertise:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.OTC, operation = "分页查找后台广告Advertise")
    public MessageResult page(AdvertiseScreen screen) {
        Page<Advertise> all = advertiseService.findAll(screen);
        return success(IPage2Page(all));
    }

    @RequiresPermissions("otc:advertise:out-excel")
    @GetMapping("out-excel")
    @AccessLog(module = AdminModule.OTC, operation = "导出后台广告Advertise Excel")
    public MessageResult outExcel(
            @RequestParam(value = "startTime", required = false) Date startTime,
            @RequestParam(value = "endTime", required = false) Date endTime,
            @RequestParam(value = "advertiseType", required = false) AdvertiseType advertiseType,
            @RequestParam(value = "realName", required = false) String realName,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<Advertise> list = advertiseService.queryAdvertise(startTime, endTime, advertiseType, realName);
        return new FileUtil().exportExcel(request, response, list, "order");
    }

}
