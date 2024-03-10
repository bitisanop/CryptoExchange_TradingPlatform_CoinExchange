package com.bitisan.admin.controller.convert;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.screen.ConvertOrderScreen;
import com.bitisan.user.entity.ConvertOrder;
import com.bitisan.user.feign.ConvertFeign;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/convert/order")
public class ConvertOrderController extends BaseAdminController {
    @Autowired
    private ConvertFeign convertFeign;


    @RequiresPermissions(value = {"convert:order:page-query"})
    @RequestMapping("/page-query")
    @AccessLog(module = AdminModule.FINANCE, operation = "闪兑订单列表")
    public MessageResult pageQuery(
            ConvertOrderScreen screen) {
        IPage<ConvertOrder> pageListMapResult = convertFeign.findOrderAll(screen);
        return success(IPage2Page(pageListMapResult));
    }
}
