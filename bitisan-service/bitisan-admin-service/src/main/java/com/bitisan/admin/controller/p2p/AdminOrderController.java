package com.bitisan.admin.controller.p2p;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.OrderStatus;
import com.bitisan.controller.BaseController;
import com.bitisan.p2p.entity.OtcOrder;
import com.bitisan.p2p.feign.OtcOrderFeign;
import com.bitisan.screen.OrderScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.ExcelUtil;
import com.bitisan.util.MessageResult;
import com.bitisan.vo.OtcOrderVO;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.baomidou.mybatisplus.core.toolkit.Assert.notNull;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 法币交易订单
 * @date 2019/1/8 15:41
 */
@RestController
@RequestMapping("/otc/order")
public class AdminOrderController extends BaseController {

    @Autowired
    private OtcOrderFeign orderService;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("otc:order:all")
    @PostMapping("all")
    @AccessLog(module = AdminModule.OTC, operation = "所有法币交易订单Order")
    public MessageResult all() {
        List<OtcOrder> exchangeOrderList = orderService.findAll();
        if (exchangeOrderList != null && exchangeOrderList.size() > 0) {
            return success(exchangeOrderList);
        }
        return error(messageSource.getMessage("NO_DATA"));
    }

    @RequiresPermissions("otc:order:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.OTC, operation = "法币交易订单Order详情")
    public MessageResult detail(Long id) {
        OtcOrder one = orderService.findOne(id);
        if (one == null) {
            return error(messageSource.getMessage("NO_DATA"));
        }
        return success(one);
    }

    //修改订单状态
    @RequiresPermissions("otc:order:alert-status")
    @PatchMapping("{id}/alert-status")
    @AccessLog(module = AdminModule.OTC, operation = "修改法币交易订单Order")
    public MessageResult status(
            @PathVariable("id") Long id,
            @RequestParam("status") OrderStatus status) {
        OtcOrder order = orderService.findOne(id);
        notNull(order, "validate order.id!");
        order.setStatus(status);
        orderService.updateById(order);
        return success();
    }


    @RequiresPermissions(value = {"otc:order:page-query","finance:otc:order:page-query"},logical = Logical.OR)
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.OTC, operation = "分页查找法币交易订单Order")
    public MessageResult page(OrderScreen screen) {
        Page<OtcOrderVO> page = orderService.outExcel(screen);
        return success(IPage2Page(page));
    }


    @RequiresPermissions("otc:order:get-order-num")
    @PostMapping("get-order-num")
    @AccessLog(module = AdminModule.OTC, operation = "后台首页订单总数接口")
    public MessageResult getOrderNum() {
        return orderService.getOrderNum();
    }

    /**
     * 参数 fileName 为导出excel 文件的文件名 格式为 .xls  定义在OutExcelInterceptor 拦截器中 ，非必须参数
     * @param
     * @param screen
     * @param response
     * @throws Exception
     */
    @RequiresPermissions("otc:order:out-excel")
    @GetMapping("out-excel")
    @AccessLog(module = AdminModule.OTC, operation = "导出法币交易订单Order Excel")
    public void outExcel(
            OrderScreen screen,
            HttpServletResponse response
            ) throws Exception {
        List<OtcOrderVO> list = orderService.outExcel(screen).getRecords();
        ExcelUtil.listToExcel(list,OtcOrderVO.class.getDeclaredFields(),response.getOutputStream());
    }


}
