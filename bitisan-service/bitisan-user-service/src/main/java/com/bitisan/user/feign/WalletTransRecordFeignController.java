package com.bitisan.user.feign;


import com.bitisan.controller.BaseController;
import com.bitisan.user.entity.WalletTransRecord;
import com.bitisan.user.service.WalletTransRecordService;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 会员用户 前端控制器
 * </p>
 *
 * @author markchao
 * @since 2021-06-14
 */
@RestController
@RequestMapping("/walletTransRecordFeign")
public class WalletTransRecordFeignController extends BaseController {

    @Autowired
    private WalletTransRecordService walletTransRecordService;

//    @ApiOperation(value = "保存")
    @PostMapping("save")
    public MessageResult save(@RequestBody WalletTransRecord walletTransRecord){
        boolean ret = walletTransRecordService.saveOrUpdate(walletTransRecord);
        if (ret) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

}

