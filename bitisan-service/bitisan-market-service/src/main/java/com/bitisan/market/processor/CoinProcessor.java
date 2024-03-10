package com.bitisan.market.processor;


import com.bitisan.market.component.CoinExchangeRate;
import com.bitisan.market.handler.MarketHandler;
import com.bitisan.market.service.MarketService;
import com.bitisan.pojo.CoinThumb;
import com.bitisan.pojo.ExchangeTrade;
import com.bitisan.pojo.KLine;

import java.util.List;

public interface CoinProcessor {

    void setIsHalt(boolean status);

    void setIsStopKLine(boolean stop);

    boolean isStopKline();
    /**
     * 处理新生成的交易信息
     * @param trades
     * @return
     */
    void process(List<ExchangeTrade> trades);

    /**
     * 添加存储器
     * @param storage
     */
    void addHandler(MarketHandler storage);

    CoinThumb getThumb();

    void setMarketService(MarketService service);

    void generateKLine(int range, int field, long time);

    void generateKLine1min(int range, int field, long time);

    KLine getKLine();

    void initializeThumb();

    void autoGenerate();

    void resetThumb();

    void setExchangeRate(CoinExchangeRate coinExchangeRate);

    void update24HVolume(long time);

    void initializeUsdRate();

    void generateKLine(long time, int minute, int hour);
}
