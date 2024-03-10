package com.bitisan.market.handler;

import com.bitisan.pojo.CoinThumb;
import com.bitisan.pojo.ExchangeTrade;
import com.bitisan.pojo.KLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoMarketHandler implements MarketHandler {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void handleTrade(String symbol, ExchangeTrade exchangeTrade, CoinThumb thumb) {
        mongoTemplate.insert(exchangeTrade, "exchange_trade_" + symbol);
    }

    @Override
    public void handleKLine(String symbol, KLine kLine) {
        mongoTemplate.insert(kLine,"exchange_kline_"+symbol+"_"+kLine.getPeriod());
    }
}
