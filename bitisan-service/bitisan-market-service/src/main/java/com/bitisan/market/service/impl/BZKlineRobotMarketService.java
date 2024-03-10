package com.bitisan.market.service.impl;

import com.bitisan.market.handler.NettyHandler;
import com.bitisan.market.handler.WebsocketMarketHandler;
import com.bitisan.market.service.KlineRobotMarketService;
import com.bitisan.pojo.KLine;
import com.bitisan.pojo.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BZKlineRobotMarketService implements KlineRobotMarketService {
    @Autowired
    private MongoTemplate mongoTemplate;

    private Logger logger = LoggerFactory.getLogger(BZKlineRobotMarketService.class);
    @Autowired
    private NettyHandler nettyHandler;
    @Autowired
    private WebsocketMarketHandler websocketMarketHandler;

    @Override
    public void saveKLine(String symbol, KLine kLine){
        String period = kLine.getPeriod();
        if(period.equals("60min")){
            period = "1hour";
        }else if(period.equals("1mon")){
            period = "1month";
        }
        kLine.setPeriod(period);
        long timeStamp = findMaxTimestamp(symbol, period);
        if(kLine.getTime() == timeStamp){
            return;
        }

        logger.info("保存K线(" + symbol + "): " + period + "/" + kLine.getTime() + "----maxTime: " + timeStamp);
        mongoTemplate.insert(kLine,"exchange_kline_"+symbol+"_"+period);
        nettyHandler.handleKLine(symbol,kLine);
        websocketMarketHandler.handleKLine(symbol,kLine);
    }

    /**
     * 获取K最近一条K线的时间
     * @param symbol
     * @param period
     * @return
     */
    @Override
    public long findMaxTimestamp(String symbol, String period) {
        Sort sort = Sort.by(Sort.Direction.DESC,"time");
        Query query = new Query().with(sort).limit(1);
        if(period.equals("60min")){
            period = "1hour";
        }else if(period.equals("1mon")){
            period = "1month";
        }
        List<KLine> result = mongoTemplate.find(query,KLine.class,"exchange_kline_"+symbol+"_"+period);

        if (result != null && result.size() > 0) {
            return result.get(0).getTime();
        } else {
            return 0;
        }
    }

    @Override
    public List<Symbol> findAllSymbol() {
       return mongoTemplate.findAll(Symbol.class,"robot_symbol");
    }

    @Override
    public void addSymbol(Symbol symbol) {
        mongoTemplate.insert(symbol,"robot_symbol");
    }

    @Override
    public void deleteAll(String symbol) {
        mongoTemplate.dropCollection("robot_symbol");
        mongoTemplate.dropCollection("exchange_kline_"+symbol+"_1min");
        mongoTemplate.dropCollection("exchange_kline_"+symbol+"_5min");
        mongoTemplate.dropCollection("exchange_kline_"+symbol+"_10min");
        mongoTemplate.dropCollection("exchange_kline_"+symbol+"_15min");
        mongoTemplate.dropCollection("exchange_kline_"+symbol+"_30min");
        mongoTemplate.dropCollection("exchange_kline_"+symbol+"_1hour");
        mongoTemplate.dropCollection("exchange_kline_"+symbol+"_4hour");
        mongoTemplate.dropCollection("exchange_kline_"+symbol+"_1day");
        mongoTemplate.dropCollection("exchange_kline_"+symbol+"_1week");
        mongoTemplate.dropCollection("exchange_kline_"+symbol+"_1month");
    }
}
