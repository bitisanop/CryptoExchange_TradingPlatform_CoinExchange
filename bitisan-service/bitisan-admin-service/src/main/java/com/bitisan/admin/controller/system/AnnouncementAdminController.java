package com.bitisan.admin.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.entity.Announcement;
import com.bitisan.admin.service.AnnouncementService;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.AnnouncementClassification;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.PageParam;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.MessageResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


/**
 * @author Bitisan  E-mail:bizzanhevin@gmail.com
 * @description 公告
 * @date 2019/3/5 15:25
 */
@RestController
@RequestMapping("system/announcement")
public class AnnouncementAdminController extends BaseController {
    @Autowired
    private AnnouncementService announcementService;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("system:announcement:create")
    @PostMapping("create")
    public MessageResult create(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String lang,
            @RequestParam AnnouncementClassification announcementClassification,
            @RequestParam("isShow") Boolean isShow,
            @RequestParam(value = "imgUrl", required = false) String imgUrl) {
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setIsShow(isShow);
        announcement.setLang(lang);
        announcement.setAnnouncementClassification(announcementClassification);
        announcement.setImgUrl(imgUrl);
        announcementService.save(announcement);
        return success(messageSource.getMessage("SUCCESS"));
    }

    @RequiresPermissions("system:announcement:top")
    @PostMapping("top")
    @AccessLog(module = AdminModule.CMS, operation = "公告置顶")
    public MessageResult toTop(@RequestParam("id")long id){
        Announcement announcement = announcementService.getById(id);
        int a = announcementService.getMaxSort();
        announcement.setSort(a+1);
        announcement.setIsTop("0");
        announcementService.save(announcement);
        return success(messageSource.getMessage("SUCCESS"));
    }


    /**
     * 取消公告置顶
     * @param id
     * @return
     */
    @RequiresPermissions("system:announcement:dwon")
    @PostMapping("down")
    @AccessLog(module = AdminModule.CMS, operation = "公告取消置顶")
    public MessageResult toDown(@RequestParam("id")long id){
        Announcement announcement = announcementService.getById(id);
        announcement.setIsTop("1");
        announcementService.save(announcement);
        return success();
    }

    @RequiresPermissions("system:announcement:page-query")
    @GetMapping("page-query")
    public MessageResult page(
            PageParam pageParam,
            @RequestParam(required = false) Boolean isShow) {
        //条件
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        if (isShow != null) {
            queryWrapper.eq(Announcement::getIsShow,isShow);
        }
        IPage<Announcement> page = new Page<>(pageParam.getPageNo(),pageParam.getPageSize());
        IPage<Announcement> all = announcementService.page(page,queryWrapper);
        return success(IPage2Page(all));
    }

    @RequiresPermissions("system:announcement:deletes")
    @PatchMapping("deletes")
    public MessageResult deleteOne(@RequestParam("ids") Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        announcementService.removeByIds(idList);
        return success();
    }

    @RequiresPermissions("system:announcement:detail")
    @GetMapping("{id}/detail")
    public MessageResult detail(
            @PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        Assert.notNull(announcement, "validate id!");
        return success(announcement);
    }


    @RequiresPermissions("system:announcement:update")
    @PutMapping("{id}/update")
    public MessageResult update(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Boolean isShow,
            @RequestParam String lang,
            @RequestParam AnnouncementClassification announcementClassification,
            @RequestParam(value = "imgUrl", required = false) String imgUrl) {
        Announcement announcement = announcementService.getById(id);
        Assert.notNull(announcement, "validate id!");
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setIsShow(isShow);
        announcement.setLang(lang);
        announcement.setAnnouncementClassification(announcementClassification);
        announcement.setImgUrl(imgUrl);
        announcementService.updateById(announcement);
        return success();
    }

    @RequiresPermissions("system:announcement:turn-off")
    @PatchMapping("{id}/turn-off")
    public MessageResult turnOff(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        Assert.notNull(announcement, "validate id!");
        announcement.setIsShow(false);
        announcementService.updateById(announcement);
        return success();
    }

    @RequiresPermissions("system:announcement:turn-on")
    @PatchMapping("{id}/turn-on")
    public MessageResult turnOn(@PathVariable("id") Long id) {
        Announcement announcement = announcementService.getById(id);
        Assert.notNull(announcement, "validate id!");
        announcement.setIsShow(true);
        announcementService.updateById(announcement);
        return success();
    }

}
