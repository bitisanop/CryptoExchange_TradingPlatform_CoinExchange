package com.bitisan.market.config;

import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.exchange.feign.ExchangeCoinFeign;
import com.bitisan.market.component.CoinExchangeRate;
import com.bitisan.market.handler.MongoMarketHandler;
import com.bitisan.market.handler.NettyHandler;
import com.bitisan.market.handler.WebsocketMarketHandler;
import com.bitisan.market.processor.CoinProcessor;
import com.bitisan.market.processor.CoinProcessorFactory;
import com.bitisan.market.processor.DefaultCoinProcessor;
import com.bitisan.market.service.MarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class ProcessorConfig {

    @Bean
    public CoinProcessorFactory processorFactory(MongoMarketHandler mongoMarketHandler,
                                                 WebsocketMarketHandler wsHandler,
                                                 NettyHandler nettyHandler,
                                                 MarketService marketService,
                                                 CoinExchangeRate exchangeRate,
                                                 ExchangeCoinFeign exchangeCoinFeign) {

        log.info("====initialized CoinProcessorFactory start==================================");

        CoinProcessorFactory factory = new CoinProcessorFactory();
        List<ExchangeCoin> coins = exchangeCoinFeign.findAllEnabled();
        log.info("exchange-coin result:{}",coins);

        for (ExchangeCoin coin : coins) {
            CoinProcessor processor = new DefaultCoinProcessor(coin.getSymbol(), coin.getBaseSymbol());
            processor.addHandler(mongoMarketHandler);
            processor.addHandler(wsHandler);
            processor.addHandler(nettyHandler);
            processor.setMarketService(marketService);
            processor.setExchangeRate(exchangeRate);
            processor.setIsStopKLine(true);

            factory.addProcessor(coin.getSymbol(), processor);
            log.info("new processor = ", processor);
        }

        log.info("====initialized CoinProcessorFactory completed====");
        log.info("CoinProcessorFactory = ", factory);
        exchangeRate.setCoinProcessorFactory(factory);
        return factory;
    }
}
