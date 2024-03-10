package com.bitisan.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.screen.WithdrawScreen;
import com.bitisan.user.entity.Withdraw;
import com.bitisan.user.vo.WithdrawVO;
import com.bitisan.util.MessageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "withdrawFeign")
public interface WithdrawFeign {

    @PostMapping("/withdrawFeign/findAllOut")
    List<Withdraw> findAllOut(@RequestBody WithdrawScreen withdrawScreen);

    @PostMapping("/withdrawFeign/findAll")
    Page<Withdraw> findAll(@RequestBody WithdrawScreen withdrawScreen);

    @PostMapping("/withdrawFeign/findOne")
    Withdraw findOne(@RequestParam("id") Long id);

    @PostMapping("/withdrawFeign/updateById")
    MessageResult updateById(@RequestBody Withdraw update);

    @PostMapping("/withdrawFeign/joinFind")
    Page<Withdraw> joinFind(@RequestBody WithdrawScreen screen);

    @PostMapping("/withdrawFeign/getWithdrawStatistics")
    List<WithdrawVO> getWithdrawStatistics(@RequestParam("dateStr")String dateStr);

    @PostMapping("/withdrawFeign/countAuditing")
    Integer countAuditing();

    @PostMapping("/withdrawFeign/withdrawSuccess")
    void withdrawSuccess(@RequestParam("withdrawId")Long withdrawId, @RequestParam("txid")String txid);

    @PostMapping("/withdrawFeign/withdrawFail")
    void withdrawFail(@RequestParam("withdrawId")Long withdrawId);

    @PostMapping("/withdrawFeign/findWithdrawByStatus")
    List<Withdraw> findWithdrawByStatus(@RequestParam("status") Integer status);

    @PostMapping("/withdrawFeign/updateWithdrawStatus")
    void updateWithdrawStatus(@RequestParam("id")Long id, @RequestParam("status") Integer status);
}
