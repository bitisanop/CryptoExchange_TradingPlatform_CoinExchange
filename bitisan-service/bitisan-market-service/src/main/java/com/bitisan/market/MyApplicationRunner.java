package com.bitisan.market;

import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.exchange.feign.ExchangeCoinFeign;
import com.bitisan.market.processor.CoinProcessor;
import com.bitisan.market.processor.CoinProcessorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MyApplicationRunner implements ApplicationRunner {
    @Autowired
    private CoinProcessorFactory coinProcessorFactory;
    @Autowired
    private ExchangeCoinFeign coinService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("====应用初始化完成，开启CoinProcessor====");
        if(coinService==null){
            return;
        }
        List<ExchangeCoin> coins = coinService.findAllEnabled();
        coins.forEach(coin->{
            CoinProcessor processor = coinProcessorFactory.getProcessor(coin.getSymbol());
            processor.initializeThumb();
            processor.initializeUsdRate();
            processor.setIsHalt(false);
        });
    }
}
