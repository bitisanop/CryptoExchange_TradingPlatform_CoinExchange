package com.bitisan.admin.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.vo.AutomainSetPassword;
import com.bitisan.admin.vo.AutomainconfigVo;
import com.bitisan.admin.vo.MessageEncrypt;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.dto.CoinDTO;
import com.bitisan.dto.CoinprotocolDTO;
import com.bitisan.rpc.feign.RpcFeign;
import com.bitisan.screen.PageParam;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Automainconfig;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.Coinprotocol;
import com.bitisan.user.feign.AutoMainConfigFeign;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.user.feign.CoinprotocolFeign;
import com.bitisan.util.AESUtils;
import com.bitisan.util.BindingResultUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 币种扩展管理
 */
@Slf4j
@RestController
@RequestMapping("/system/automainconfig")
public class AutomainconfigController extends BaseAdminController {

    @Autowired
    private CoinFeign coinFeign;
    @Autowired
    private RpcFeign rpcFeign;
    @Autowired
    private CoinprotocolFeign coinprotocolFeign;
    @Autowired
    private AutoMainConfigFeign autoMainConfigFeign;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("system:automainconfig:coin-list")
    @GetMapping("/coin-list")
    @AccessLog(module = AdminModule.SYSTEM, operation = "归集配置里获取币种列表")
    public MessageResult coinList() {

        List<Coin> list = coinFeign.getAllCoinNameAndUnit();
        return success(list);
    }

    @RequiresPermissions("system:automainconfig:protocol-list")
    @GetMapping("/protocol-list")
    @AccessLog(module = AdminModule.SYSTEM, operation = "归集配置里获取币种协议列表")
    public MessageResult protocolList() {

        List<CoinprotocolDTO> list = coinprotocolFeign.list();

        return success(list);
    }

    @RequiresPermissions("system:automainconfig:page-query")
    @PostMapping("/page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "获取归集配置列表")
    public MessageResult pageQuery(PageParam pageParam) {

        Page<Automainconfig> pageResult = autoMainConfigFeign.findAll(pageParam.getPageNo(),pageParam.getPageSize());
        return success(IPage2Page(pageResult));
    }

    @RequiresPermissions("system:automainconfig:merge")
    @PostMapping("/merge")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建/修改归集配置")
    public MessageResult merge(@Valid Automainconfig automainconfig, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }

        // 查询是否存在
        Automainconfig one = autoMainConfigFeign.findAutoMainConfigByCoinNameAndProtocol(automainconfig.getCoinName(), automainconfig.getProtocol());
        if (automainconfig.getId() != null) {
            if (one != null && !one.getId().equals(automainconfig.getId())) {
                result = error(messageSource.getMessage("CURRENCY_ALREADY_EXISTS"));
                return result;
            }
        } else if (one != null) {
            result = error(messageSource.getMessage("当前协议的币种已存在"));
            return result;
        }

        // 删除redis缓存
        redisTemplate.delete("automainconfig");

        automainconfig = autoMainConfigFeign.save(automainconfig);
        result = success(messageSource.getMessage("OPERATION_SUCCESS"));
        result.setData(automainconfig);
        return result;
    }

    @RequiresPermissions("system:automainconfig:collect-coin")
    @PostMapping("/collectCoin")
    @AccessLog(module = AdminModule.SYSTEM, operation = "手动归集")
    public MessageResult collectCoin(@Valid AutomainconfigVo automainconfig, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }

        Coinprotocol protocol = coinprotocolFeign.findByProtocol(automainconfig.getProtocol());
        //远程RPC服务URL,后缀为币种单位
        String chain =  protocol.getSymbol().toLowerCase();
        MessageResult rt = rpcFeign.transferAll(chain, automainconfig.getAddress(), automainconfig.getCoinName(), automainconfig.getPassword().trim());
        if(rt==null){
            return success(messageSource.getMessage("OPERATION_SUCCESS"));
        }
        if(rt.getCode()==0) {
            return success(messageSource.getMessage("OPERATION_SUCCESS"));
        }else {
            return error(rt.getMessage());
        }
    }

    @RequiresPermissions("system:automainconfig:set-password")
    @PostMapping("/setPassword")
    @AccessLog(module = AdminModule.SYSTEM, operation = "设置密码")
    public MessageResult setPassword(AutomainSetPassword automainconfig) throws Exception {
        if(StringUtils.isEmpty(automainconfig.getPassword())){
            return error(messageSource.getMessage("PASSWORD_CANNOT_BE_EMPTY"));
        }
        Coinprotocol protocol = coinprotocolFeign.findByProtocol(automainconfig.getProtocol());
        //远程RPC服务URL,后缀为币种单位
        String chain = protocol.getSymbol().toLowerCase();

        MessageResult mr = rpcFeign.setPassword(chain, automainconfig.getPassword().trim());
        if(mr==null){
            return error(messageSource.getMessage("OPERATION_FAILED"));
        }else {
            if(mr.getCode()==0) {
                return success(messageSource.getMessage("OPERATION_SUCCESS"));
            }else {
                return error(mr.getMessage());
            }
        }

    }
    @RequiresPermissions("system:automainconfig:update-contract")
    @PostMapping("/updateContract")
    @AccessLog(module = AdminModule.SYSTEM, operation = "同步币种")
    public MessageResult updateContract(AutomainSetPassword automainconfig) throws Exception {
        if(StringUtils.isEmpty(automainconfig.getPassword())){
            return error(messageSource.getMessage("PASSWORD_CANNOT_BE_EMPTY"));
        }
        Coinprotocol protocol = coinprotocolFeign.findByProtocol(automainconfig.getProtocol());
        //远程RPC服务URL,后缀为币种单位
        String chain = protocol.getSymbol().toLowerCase();
        MessageResult mr = rpcFeign.updateContract(chain, automainconfig.getPassword().trim());
        if(mr==null){
            return error(messageSource.getMessage("OPERATION_FAILED"));
        }else {
            if(mr.getCode()==0) {
                return success(messageSource.getMessage("OPERATION_SUCCESS"));
            }else {
                return error(mr.getMessage());
            }
        }

    }

    @RequiresPermissions("system:automainconfig:encrypt")
    @PostMapping("/encrypt")
    @AccessLog(module = AdminModule.SYSTEM, operation = "加密工具")
    public MessageResult encrypt(MessageEncrypt messageEncrypt) throws Exception {
        if(StringUtils.isEmpty(messageEncrypt.getPassword())){
            return error(messageSource.getMessage("PASSWORD_CANNOT_BE_EMPTY"));
        }
        String  encrypt = AESUtils.encrypt(messageEncrypt.getMessage(), messageEncrypt.getPassword().trim());
        return success(messageSource.getMessage("OPERATION_SUCCESS"),encrypt);
    }


}
