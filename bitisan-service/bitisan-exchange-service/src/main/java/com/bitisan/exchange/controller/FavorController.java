package com.bitisan.exchange.controller;

import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.exchange.entity.ExchangeFavorSymbol;
import com.bitisan.exchange.service.ExchangeFavorSymbolService;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;


@Api(tags = "交易优先符号处理")
@Slf4j
@RestController
@RequestMapping("/favor")
public class FavorController extends BaseController {
    @Autowired
    private ExchangeFavorSymbolService favorSymbolService;

    @Autowired
    private LocaleMessageSourceService msService;

    /**
     * 添加自选
     * @param authMember
     * @param symbol
     * @return
     */
    @ApiOperation(value = "添加自选")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
    })
    @PermissionOperation
    @RequestMapping("add")
    public MessageResult addFavor(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, String symbol){
        AuthMember member = AuthMember.toAuthMember(authMember);
        if(StringUtils.isEmpty(symbol)){
            return MessageResult.error(500,msService.getMessage("SYMBOL_CANNOT_BE_EMPTY"));
        }
        ExchangeFavorSymbol favorSymbol = favorSymbolService.findByMemberIdAndSymbol(member.getId(),symbol);
        if(favorSymbol != null){
            return MessageResult.error(500,msService.getMessage("SYMBOL_ALREADY_FAVORED"));
        }
        ExchangeFavorSymbol favor =  favorSymbolService.add(member.getId(),symbol);
        if(favor!= null){
            return MessageResult.success(msService.getMessage("EXAPI_SUCCESS"));
        }
        return MessageResult.error(msService.getMessage("EXAPI_ERROR"));
    }

    /**
     * 查询当前用户自选
     * @param authMember
     * @return
     */
    @ApiOperation(value = "查询当前用户自选")
    @PermissionOperation
    @RequestMapping("find")
    public MessageResult findFavor(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember){
        AuthMember member = AuthMember.toAuthMember(authMember);
        List<ExchangeFavorSymbol> list =  favorSymbolService.findByMemberId(member.getId());
        return this.success(list);
    }

    /**
     * 删除自选
     * @param authMember
     * @param symbol
     * @return
     */
    @ApiOperation(value = "删除自选")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
    })
    @PermissionOperation
    @RequestMapping("delete")
    public MessageResult deleteFavor(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember,String symbol){
        if(StringUtils.isEmpty(symbol)){
            return MessageResult.error(msService.getMessage("SYMBOL_CANNOT_BE_EMPTY"));
        }
        AuthMember member = AuthMember.toAuthMember(authMember);
        ExchangeFavorSymbol favorSymbol = favorSymbolService.findByMemberIdAndSymbol(member.getId(),symbol);
        if(favorSymbol == null){
            return MessageResult.error(msService.getMessage("FAVOR_NOT_EXISTS"));
        }
        favorSymbolService.delete(member.getId(),symbol);
        return MessageResult.success(msService.getMessage("EXAPI_SUCCESS"));
    }
}
