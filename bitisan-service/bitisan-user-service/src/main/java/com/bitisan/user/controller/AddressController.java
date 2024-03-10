package com.bitisan.user.controller;


import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.rpc.feign.RpcFeign;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.udun.feign.UdunFeign;
import com.bitisan.user.entity.Addressext;
import com.bitisan.user.entity.Coinprotocol;
import com.bitisan.user.service.AddressextService;
import com.bitisan.user.service.CoinprotocolService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.MessageResult;
import com.uduncloud.sdk.domain.Address;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


/**
 * @author Bitisan E-Mali:bitisanop@gmail.com
 * @Description: coin
 * @date 2021/4/214:20
 */
@Api(tags = "地址")
@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController extends BaseController {

    @Autowired
    private AddressextService addressextService;
    @Autowired
    private CoinprotocolService coinprotocolService;
    @Autowired
    private LocaleMessageSourceService messageSource;
    @Autowired
    private RpcFeign rpcFeign;
    @Autowired
    private UdunFeign udunFeign;
    @Value("${wallet.type}")
    private String walletType;


    // 读取地址
    @ApiOperation(value = "读取地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coinprotocol", value = "币种协议"),
    })
    @PermissionOperation
    @GetMapping("read")
    public MessageResult read(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember,
                              @RequestParam(value = "coinprotocol") Integer coinprotocol) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Integer memberid = (int) user.getId();
        Addressext read = addressextService.read(memberid, coinprotocol);

        return success(read);
    }


    // 创建地址
    @ApiOperation(value = "创建地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coinprotocol", value = "币种协议"),
    })
    @PermissionOperation
    @PostMapping("create")
    public MessageResult create(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember,
                                @RequestParam(value = "coinprotocol") Integer coinprotocol) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Integer memberid = (int) user.getId();
        Addressext read = addressextService.read(memberid, coinprotocol);

        if (read != null) {
            return success(read);
        }
        //获取地址
        Coinprotocol protocol = coinprotocolService.findByProtocol(coinprotocol);
        String account = "U" + memberid;
        String address = null;
        if("udun".equalsIgnoreCase(walletType)){
            //U盾
            Address coinAddress = udunFeign.createCoinAddress(protocol.getSymbol());
            if(coinAddress!=null){
                address = coinAddress.getAddress();
            }
        }else {
            //远程RPC服务URL,后缀为币种单位
            String serviceName = protocol.getSymbol().toLowerCase();
            MessageResult mr = rpcFeign.getNewAddress(serviceName, account);
            log.info("remote call:service={},result={}", serviceName, mr);
            if (mr != null && mr.getCode() == 0) {
                address = mr.getData().toString();
            }
        }
        if(!StringUtils.isEmpty(address)){
            Addressext addressext = new Addressext();
            addressext.setMemberId(memberid);
            addressext.setAddress(address);
            addressext.setCoinProtocol(coinprotocol);
            addressext.setStatus(1);
            addressextService.saveAndFlush(addressext);
            return success(addressext);
        }else {
            return error(messageSource.getMessage("system_opt_fail"));
        }

    }


}
