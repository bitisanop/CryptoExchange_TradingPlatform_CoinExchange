package com.bitisan.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.entity.Announcement;
import com.bitisan.admin.service.AnnouncementService;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.pagination.PageResult;
import com.bitisan.util.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author Bitisan  E-mail:xunibidev@gmail.com
 * @description
 * @date 2019/3/5 15:25
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController extends BaseController {
    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private RedisTemplate redisTemplate;


    /*@ApiOperation("全部")
    @GetMapping
    public MessageResult all() {
        List<Announcement> announcementList = announcementService.findAll();
        return success(announcementList);
    }*/

    @PostMapping("page")
    public MessageResult page(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestHeader(value = "lang") String lang
    ) {
        IPage<Announcement> page = new Page<>(pageNo,pageSize);
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_show",true).eq("lang",lang);
        queryWrapper.orderByDesc("create_time");
        IPage<Announcement> announcementIpage = announcementService.page(page,queryWrapper);
        //查
        PageResult<Announcement> pageResult = new PageResult<Announcement>(announcementIpage.getRecords(), pageNo, pageSize, announcementIpage.getTotal());;
        List<Announcement> rlist = pageResult.getContent();
        for(int i = 0; i < rlist.size(); i++) {
        	rlist.get(i).setContent(null);
        }
        pageResult.setContent(rlist);
        return success(pageResult);
    }

    @GetMapping("{id}")
    public MessageResult detail(@PathVariable("id") Long id) {
        Announcement announcement = announcementService.getById(id);
        Assert.notNull(announcement, "validate id!");
        return success(announcement);
    }

    /**
     * 根据ID获取当前公告及上一条和下一条
     * @param id
     * @return
     */
    @RequestMapping(value = "more",method = RequestMethod.POST)
    public MessageResult moreDetail(@RequestParam("id")Long id,@RequestHeader(value = "lang") String lang){
//        ValueOperations redisOperations = redisTemplate.opsForValue();
//        JSONObject result  = (JSONObject) redisOperations.get(SysConstant.NOTICE_DETAIL+id);
//        if ( result != null){
//            return success(result);
//        }else {
            JSONObject resultObj = new JSONObject();
            Announcement announcement = announcementService.getById(id);
            Assert.notNull(announcement, "validate id!");
            resultObj.put("info",announcement);
            resultObj.put("back",announcementService.getBack(id, lang));
            resultObj.put("next",announcementService.getNext(id, lang));
//            redisOperations.set(SysConstant.NOTICE_DETAIL+id,resultObj,SysConstant.NOTICE_DETAIL_EXPIRE_TIME, TimeUnit.SECONDS);
            return success(resultObj);
//        }
    }



}
