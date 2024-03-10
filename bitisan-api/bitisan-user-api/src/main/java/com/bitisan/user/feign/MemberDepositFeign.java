package com.bitisan.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.screen.MemberApplicationScreen;
import com.bitisan.screen.MemberDepositScreen;
import com.bitisan.user.entity.MemberDeposit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(value = "bitisan-user",contextId = "memberDepositFeign")
public interface MemberDepositFeign {
    @PostMapping("/memberDepositFeign/findAll")
    Page<MemberDeposit> findAll(@RequestBody MemberDepositScreen screen);

    @PostMapping("/memberDepositFeign/getDepositStatistics")
    List<MemberDeposit> getDepositStatistics(@RequestParam("dateStr")String dateStr);
}
