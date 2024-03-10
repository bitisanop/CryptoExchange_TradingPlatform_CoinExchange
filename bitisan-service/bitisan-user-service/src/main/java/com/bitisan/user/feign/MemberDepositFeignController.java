package com.bitisan.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.screen.MemberApplicationScreen;
import com.bitisan.screen.MemberDepositScreen;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberDeposit;
import com.bitisan.user.service.MemberDepositService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "会员充值数字币记录")
@RestController
@RequestMapping("/memberDepositFeign")
public class MemberDepositFeignController {

    @Autowired
    private MemberDepositService memberDepositService;

//    @ApiOperation(value = "查找全部")
    @PostMapping("/findAll")
    public Page<MemberDeposit> findAll(@RequestBody MemberDepositScreen screen) {
        return memberDepositService.findAll(screen);
    }

    @PostMapping("getDepositStatistics")
    public List<MemberDeposit> getDepositStatistics(@RequestParam("dateStr")String dateStr){
        return memberDepositService.getDepositStatistics(dateStr);
    }
}
