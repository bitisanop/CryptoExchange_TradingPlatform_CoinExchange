package com.bitisan.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.MemberApplicationScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.sms.SMSProvider;
import com.bitisan.user.entity.MemberApplication;
import com.bitisan.user.service.MemberApplicationService;
import com.bitisan.user.vo.MemberApplicationVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 实名审核单
 * @date 2019/12/26 15:05
 */
@Api(tags = "实名审核单")
@RestController
@RequestMapping("memberApplicationFeign")
public class MemberApplicationFeignController extends BaseController {
    @Autowired
    private MemberApplicationService memberApplicationService;
    @Autowired
    private LocaleMessageSourceService messageSource;



//    @ApiOperation(value = "获取全部")
    @PostMapping(value = "/findAll")
    public Page<MemberApplicationVo> findAll(@RequestBody MemberApplicationScreen screen){
        return memberApplicationService.findAll(screen);
    }


//    @ApiOperation(value = "根据ID查找")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "id"),
//    })
    @GetMapping(value = "/findById")
    public MemberApplication findById(@RequestParam("id") Long id){
        return memberApplicationService.getById(id);
    }

    @PostMapping(value = "/fetch")
    public List<MemberApplication> fetch(){
        return memberApplicationService.list();
    };

//    @ApiOperation(value = "审核通过")
    @PostMapping(value = "/auditPass")
    void auditPass(@RequestBody MemberApplication application){
        memberApplicationService.auditPass(application);
    }

//    @ApiOperation(value = "审核不通过")
    @PostMapping(value = "/auditNotPass")
    void auditNotPass(@RequestBody MemberApplication application){
        memberApplicationService.auditNotPass(application);
    }

    @PostMapping(value = "/countAuditing")
    Integer countAuditing(){
        return memberApplicationService.countAuditing();
    }

}
