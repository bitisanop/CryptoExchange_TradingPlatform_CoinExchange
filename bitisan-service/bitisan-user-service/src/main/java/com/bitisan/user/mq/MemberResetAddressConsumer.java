package com.bitisan.user.mq;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bitisan.constant.BooleanEnum;
import com.bitisan.rpc.feign.RpcFeign;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.service.CoinService;
import com.bitisan.user.service.MemberWalletService;
import com.bitisan.util.GeneratorUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RocketMQMessageListener(topic = "reset-member-address", consumerGroup = "user-member-reset-address")
public class MemberResetAddressConsumer implements RocketMQListener<String> {

    @Autowired
    private CoinService coinService;
    @Autowired
    private RpcFeign rpcFeign;
    @Autowired
    private MemberWalletService memberWalletService;

    @Override
    public void onMessage(String content) {
        log.info("handle member-register={}", content);
        if (StringUtils.isEmpty(content)) {
            return;
        }
        JSONObject json = JSON.parseObject(content);
        if (json == null) {
            return;
        }
//        Long memberId = json.getLong("uid");
//        String unit = json.getString("unit");
        //获取所有支持的币种
//        Coin coin = coinService.findByUnit(unit);
//        Assert.notNull(coin,"coin null");
//        if(coin.getEnableRpc()== BooleanEnum.IS_TRUE){
//        MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(unit,memberId);
//            Assert.notNull(memberWallet,"wallet null");

//            memberWalletService.save(memberWallet);
        }

//    }
}
