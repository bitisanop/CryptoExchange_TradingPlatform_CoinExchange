package com.bitisan.admin.controller.system;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.DataDictionary;
import com.bitisan.admin.service.DataDictionaryService;
import com.bitisan.admin.vo.DataDictionaryCreate;
import com.bitisan.admin.vo.DataDictionaryUpdate;
import com.bitisan.screen.PageParam;
import com.bitisan.util.BindingResultUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Date;


/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1214:21
 */
@Slf4j
@RestController
@RequestMapping("system/data-dictionary")
public class DataDictionaryController extends BaseAdminController {
    @Autowired
    private DataDictionaryService service;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @PostMapping
    public MessageResult post(@Valid DataDictionaryCreate model, BindingResult bindingResult) {
        log.info(">>>>>>新增键值对>>>key>>"+model.getBond()+">>>>value>>"+model.getValue());
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        DataDictionary data = service.findByBond(model.getBond());
        if (data != null) {
            return error("bond already existed!");
        }
        DataDictionary dictionary = model.transformation();
        dictionary.setCreationTime(new Date());
        dictionary.setUpdateTime(new Date());
        service.saveOrUpdate(dictionary);
        rocketMQTemplate.convertAndSend("data-dictionary-save-update", JSON.toJSONString(model.transformation()));
        log.info(">>>>>新增结束>>>>");
        return success();
    }

    @GetMapping
    public MessageResult page(PageParam pageParam) {
        IPage<DataDictionary> page = new Page<>(pageParam.getPageNo(),pageParam.getPageSize());
        IPage<DataDictionary> all = service.page(page);
        return success(IPage2Page(all));
    }
    @RequiresPermissions("system:data-dictionary:update")
    @PutMapping("/{bond}")
    public MessageResult put(@PathVariable("bond") String bond, DataDictionaryUpdate model) {
        log.info(">>>>>修改键值对>>>key>>"+bond+">>>value>>"+model.getValue());
        DataDictionary dataDictionary = service.findByBond(bond);
        Assert.notNull(dataDictionary, "validate bond");
        dataDictionary = model.transformation(dataDictionary);
        dataDictionary.setUpdateTime(new Date());
        service.updateById(dataDictionary);
        dataDictionary.setValue(model.getValue());
        dataDictionary.setComment(model.getComment());
        rocketMQTemplate.convertAndSend("data-dictionary-save-update",JSON.toJSONString(dataDictionary));
        return success();
    }


}
