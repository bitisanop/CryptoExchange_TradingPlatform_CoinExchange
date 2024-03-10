package com.bitisan.exchange.config;

import com.bitisan.constant.ExchangeCoinPublishType;
import com.bitisan.exchange.Trader.CoinTrader;
import com.bitisan.exchange.Trader.CoinTraderFactory;
import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.exchange.service.ExchangeCoinService;
import com.bitisan.exchange.service.ExchangeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
public class CoinTraderConfig {

    /**
     * 配置交易处理类
     * @param exchangeCoinService
     * @return
     */
    @Bean
    public CoinTraderFactory getCoinTrader(ExchangeCoinService exchangeCoinService, RocketMQTemplate rocketMQTemplate, ExchangeOrderService exchangeOrderService){
        CoinTraderFactory factory = new CoinTraderFactory();
        List<ExchangeCoin> coins = exchangeCoinService.findAllEnabled();
        for(ExchangeCoin coin:coins) {
            log.info("init trader,symbol={}",coin.getSymbol());
            CoinTrader trader = new CoinTrader(coin.getSymbol());
            trader.setRocketMQTemplate(rocketMQTemplate);
            trader.setBaseCoinScale(coin.getBaseCoinScale());
            trader.setCoinScale(coin.getCoinScale());
            trader.setPublishType(ExchangeCoinPublishType.creator(coin.getPublishType()));
            trader.setClearTime(coin.getClearTime());
            trader.stopTrading();
            factory.addTrader(coin.getSymbol(),trader);
        }
        return factory;
    }

}
