package com.bitisan.user.config;

import com.bitisan.user.entity.Coin;
import com.bitisan.user.service.CoinService;
import com.bitisan.user.system.CoinExchangeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CoinExchangeFactoryConfig {
    @Autowired
    private CoinService coinService;

    @Bean
    public CoinExchangeFactory createCoinExchangeFactory() {
        List<Coin> coinList = coinService.list();
        CoinExchangeFactory factory = new CoinExchangeFactory();
        coinList.forEach(coin ->
                factory.set(coin.getUnit(), coin.getUsdRate(), coin.getCnyRate())
        );
        return factory;
    }
}
