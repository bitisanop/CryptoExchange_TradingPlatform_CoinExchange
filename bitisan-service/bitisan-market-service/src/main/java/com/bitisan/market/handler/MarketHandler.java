package com.bitisan.market.handler;


import com.bitisan.pojo.CoinThumb;
import com.bitisan.pojo.ExchangeTrade;
import com.bitisan.pojo.KLine;

public interface MarketHandler {

    /**
     * 存储交易信息
     * @param exchangeTrade
     */
    void handleTrade(String symbol, ExchangeTrade exchangeTrade, CoinThumb thumb);


    /**
     * 存储K线信息
     *
     * @param kLine
     */
    void handleKLine(String symbol, KLine kLine);
}
