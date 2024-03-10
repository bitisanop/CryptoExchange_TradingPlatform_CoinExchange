package com.bitisan.market.service;


import com.bitisan.pojo.KLine;
import com.bitisan.pojo.Symbol;

import java.util.List;

public interface KlineRobotMarketService {


    public void saveKLine(String symbol, KLine kLine);

    /**
     * 获取K最近一条K线的时间
     * @param symbol
     * @param period
     * @return
     */
    public long findMaxTimestamp(String symbol, String period);

    public List<Symbol> findAllSymbol();

    public void addSymbol(Symbol symbol);

    public void deleteAll(String symbol);
}
