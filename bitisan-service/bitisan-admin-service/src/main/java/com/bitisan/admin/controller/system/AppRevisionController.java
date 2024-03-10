package com.bitisan.admin.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.AppRevision;
import com.bitisan.admin.entity.DataDictionary;
import com.bitisan.admin.service.AppRevisionService;
import com.bitisan.admin.vo.AppRevisionCreate;
import com.bitisan.admin.vo.AppRevisionUpdate;
import com.bitisan.screen.PageParam;
import com.bitisan.util.BindingResultUtil;
import com.bitisan.util.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/2416:31
 */
@RestController
@RequestMapping("system/app-revision")
public class AppRevisionController extends BaseAdminController {
    @Autowired
    private AppRevisionService service;

    //新增
    @PostMapping
    public MessageResult create(@Valid AppRevisionCreate model, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        service.save(model.transformation());
        return success();
    }

    //更新
    @PutMapping("{id}")
    public MessageResult put(@PathVariable("id") Long id, AppRevisionUpdate model) {
        AppRevision appRevision = service.getById(id);
        Assert.notNull(appRevision, "validate appRevision id!");
        appRevision = model.transformation(appRevision);
        service.updateById(appRevision);
        return success();
    }

    //详情
    @GetMapping("{id}")
    public MessageResult get(@PathVariable("id") Long id) {
        AppRevision appRevision = service.getById(id);
        Assert.notNull(appRevision, "validate appRevision id!");
        return success(appRevision);
    }

    //分页
    @GetMapping("page-query")
    public MessageResult get(PageParam pageParam) {
        IPage<AppRevision> page = new Page<>(pageParam.getPageNo(),pageParam.getPageSize());
        IPage<AppRevision> all = service.page(page);
        return success(IPage2Page(all));
    }
}
