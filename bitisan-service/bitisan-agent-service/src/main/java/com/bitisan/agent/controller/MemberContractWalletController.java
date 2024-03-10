package com.bitisan.agent.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.coinswap.feign.ContractCoinMarketFeign;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.pojo.CoinThumb;
import com.bitisan.screen.MemberContractWalletScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.swap.entity.ContractCoin;
import com.bitisan.swap.entity.MemberContractWallet;
import com.bitisan.swap.feign.ContractCoinFeign;
import com.bitisan.swap.feign.MemberContractWalletFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 持仓管理
 */
@RestController
@RequestMapping("/swap/position")
@Slf4j
public class MemberContractWalletController extends BaseController {
    @Autowired
    private MemberContractWalletFeign memberContractWalletFeign;
    @Autowired
    private MemberFeign memberFeign;
    @Autowired
    private ContractCoinMarketFeign contractCoinMarketFeign;
    @Autowired
    private ContractCoinFeign contractCoinFeign;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @PermissionOperation
    @PostMapping("page-query")
    public MessageResult detail(
            MemberContractWalletScreen screen, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
//        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
        ArrayList<Sort.Direction> directions = new ArrayList<>();
        directions.add(Sort.Direction.DESC);
        screen.setDirection(directions);
        List<String> property = new ArrayList<>();
        property.add("usdtBalance"); // 默认金额排序
        screen.setProperty(property);
        Page<MemberContractWallet> all = memberContractWalletFeign.findAll(screen);

        // 获取最新价格
//        String serviceName = "contract-swap-api";
//        String marketUrl = "http://" + serviceName + "/swap/symbol-thumb";
//        ResponseEntity<List> thumbsResult = restTemplate.getForEntity(marketUrl, List.class);
//        List<CoinThumb> thumbList = (List<CoinThumb>)thumbsResult.getBody();

//        ParameterizedTypeReference<List<CoinThumb>> typeRef = new ParameterizedTypeReference<List<CoinThumb>>() {};
//        ResponseEntity<List<CoinThumb>> responseEntity = restTemplate.exchange(marketUrl, HttpMethod.POST, new HttpEntity<>(null), typeRef);
        List<CoinThumb> thumbList =contractCoinMarketFeign.findSymbolThumb4Feign();
        List<MemberContractWallet> list = all.getRecords();
        for(MemberContractWallet wallet :list) {
            for(int i = 0; i < thumbList.size(); i++) {
                CoinThumb thumb = thumbList.get(i);
                if(wallet.getSymbol().equals(thumb.getSymbol())) {
                    wallet.setCurrentPrice(thumb.getClose());
                }
            }
            ContractCoin coin = new ContractCoin();
            coin.setSymbol(wallet.getSymbol());
            wallet.setContractCoin(coin);
            // 设置CNY / USDT汇率
            wallet.setCnyRate(BigDecimal.valueOf(6.98));
        }
        return success(IPage2Page(all));
    }

    @PostMapping("force-close")
    public MessageResult forceClose(Long walletId) {
        MemberContractWallet wallet = memberContractWalletFeign.findOne(walletId);
        if(wallet == null) {
            return MessageResult.error(messageSource.getMessage("CANCEL_ORDER_FAILED"));
        }
        return MessageResult.success(messageSource.getMessage("OPERATION_SUCCESS"));
    }

}
