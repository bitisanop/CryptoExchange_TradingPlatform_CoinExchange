package com.bitisan.market.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bitisan.constant.SysConstant;
import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.exchange.feign.ExchangeCoinFeign;
import com.bitisan.exchange.feign.ExchangeTradeFeign;
import com.bitisan.exchange.feign.MonitorFeign;
import com.bitisan.market.component.CoinExchangeRate;
import com.bitisan.market.processor.CoinProcessor;
import com.bitisan.market.processor.CoinProcessorFactory;
import com.bitisan.market.service.MarketService;
import com.bitisan.pojo.CoinThumb;
import com.bitisan.pojo.ExchangeTrade;
import com.bitisan.pojo.KLine;
import com.bitisan.pojo.TradePlateItem;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Api(tags = "交易")
@Slf4j
@RestController
public class MarketController {
    @Autowired
    private MarketService marketService;
    @Autowired
    private ExchangeCoinFeign exchangeCoinFeign;
    @Autowired
    private CoinFeign coinFeign;
    @Autowired
    private CoinProcessorFactory coinProcessorFactory;
    @Autowired
    private ExchangeTradeFeign exchangeTradeFeign;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MonitorFeign monitorFeign;

    @Autowired
    private CoinExchangeRate coinExchangeRate;

    /**
     * 获取支持的交易币种
     * @return
     */
    @ApiOperation(value = "获取支持的交易币种")
    @RequestMapping("symbol")
    public List<ExchangeCoin> findAllSymbol(){
        List<ExchangeCoin> coins = exchangeCoinFeign.findAllVisible();
        return coins;
    }

    @ApiOperation(value = "概述")
    @RequestMapping("overview")
    public Map<String,List<CoinThumb>> overview(){
        log.info("/market/overview");
        Map<String,List<CoinThumb>> result = new HashMap<>();
        List<ExchangeCoin> recommendCoin = exchangeCoinFeign.findAllByFlag(1);
        List<CoinThumb> recommendThumbs = new ArrayList<>();
        for(ExchangeCoin coin:recommendCoin){
            CoinProcessor processor = coinProcessorFactory.getProcessor(coin.getSymbol());
            if(processor!= null) {
                CoinThumb thumb = processor.getThumb();
                recommendThumbs.add(thumb);
            }
        }
        result.put("recommend",recommendThumbs);
        List<CoinThumb> allThumbs = findSymbolThumb();
        Collections.sort(allThumbs, (o1, o2) -> o2.getChg().compareTo(o1.getChg()));
        int limit = allThumbs.size() > 5 ? 5 : allThumbs.size();
        result.put("changeRank",allThumbs.subList(0,limit));
        return result;
    }

    @ApiOperation(value = "引擎")
    @RequestMapping("engines")
	public Map<String, Integer> engines() {
		Map<String, CoinProcessor> processorList = coinProcessorFactory.getProcessorMap();
		Map<String, Integer> symbols = new HashMap<String, Integer>();
		processorList.forEach((key, processor) -> {
			if(processor.isStopKline()) {
				symbols.put(key, 2);
			}else {
				symbols.put(key, 1);
			}
		});
		return symbols;
	}

    /**
     * 获取币种详情
     * @param unit
     * @return
     */
    @ApiOperation(value = "概获取币种详情述")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "unit", value = "币种")
    })
    @RequestMapping("coin-info")
    public Coin findCoin(String unit){
        Coin coin = coinFeign.findByUnit(unit);
//        coin.setColdWalletAddress("");//隐藏冷钱包地址
        return coin;
    }

    /**
     * 获取C2C中USDT兑换人民币价格
     * @return
     */
    @ApiOperation(value = "获取C2C中USDT兑换人民币价格")
    @RequestMapping("ctc-usdt")
    public MessageResult ctcUsdt(){
    	MessageResult mr = new MessageResult(0,"success");
        BigDecimal latestPrice = coinExchangeRate.getUsdtCnyRate();
        JSONObject obj = new JSONObject();
        obj.put("buy", latestPrice);
        // 0.015为1.5%的买卖差价
        obj.put("sell", latestPrice.subtract(latestPrice.multiply(new BigDecimal(0.011)).setScale(2, BigDecimal.ROUND_DOWN)));
        mr.setData(obj);
        return mr;
    }

    /**
     * 获取某交易对详情
     * @param symbol
     * @return
     */
    @ApiOperation(value = "获取某交易对详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对符号")
    })
    @RequestMapping("symbol-info")
    public ExchangeCoin findSymbol(String symbol){
        ExchangeCoin coin = exchangeCoinFeign.findBySymbol(symbol);
        coin.setCurrentTime(Calendar.getInstance().getTimeInMillis());
        return coin;
    }

    /**
     * 获取币种缩略行情
     * @return
     */
    @ApiOperation(value = "获取币种缩略行情")
    @RequestMapping("symbol-thumb")
    public List<CoinThumb> findSymbolThumb(){
        List<ExchangeCoin> coins = exchangeCoinFeign.findAllVisible();
        List<CoinThumb> thumbs = new ArrayList<>();
        for(ExchangeCoin coin:coins){
            CoinProcessor processor = coinProcessorFactory.getProcessor(coin.getSymbol());
            CoinThumb thumb = processor.getThumb();
            if(thumb==null){
                log.info("thumb is null Symbol:{}",coin.getSymbol());
                continue;
            }
            thumb.setZone(coin.getZone());
            thumbs.add(thumb);
        }
        return thumbs;
    }


    @ApiOperation(value = "获取币种缩略趋势")
    @RequestMapping("symbol-thumb-trend")
    public JSONArray findSymbolThumbWithTrend(){
        List<ExchangeCoin> coins = exchangeCoinFeign.findAllVisible();
        //List<CoinThumb> thumbs = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        //将秒、微秒字段置为0
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.MINUTE,0);
        long nowTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR_OF_DAY,-24);
        JSONArray array = new JSONArray();
        long firstTimeOfToday = calendar.getTimeInMillis();
        for(ExchangeCoin coin:coins){
            CoinProcessor processor = coinProcessorFactory.getProcessor(coin.getSymbol());
            CoinThumb thumb = processor.getThumb();
            JSONObject json = (JSONObject) JSON.toJSON(thumb);
            json.put("zone",coin.getZone());
            List<KLine> lines = marketService.findAllKLine(thumb.getSymbol(),firstTimeOfToday,nowTime,"1hour");
            JSONArray trend = new JSONArray();
            for(KLine line:lines){
                trend.add(line.getClosePrice());
            }
            json.put("trend",trend);
            array.add(json);
        }
        return array;
    }

    /**
     * 获取币种历史K线
     * @param symbol
     * @param from
     * @param to
     * @param resolution
     * @return
     */
    @ApiOperation(value = "获取币种历史K线")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
            @ApiImplicitParam(name = "from", value = "开始时间"),
            @ApiImplicitParam(name = "to", value = "结束时间"),
            @ApiImplicitParam(name = "resolution", value = "时间格式"),
    })
    @RequestMapping("history")
    public JSONArray findKHistory(String symbol,long from,long to,String resolution){
        String period = "";
        if(resolution.endsWith("H") || resolution.endsWith("h")){
            period = resolution.substring(0,resolution.length()-1) + "hour";
        }
        else if(resolution.endsWith("D") || resolution.endsWith("d")){
            period = resolution.substring(0,resolution.length()-1) + "day";
        }
        else if(resolution.endsWith("W") || resolution.endsWith("w")){
            period = resolution.substring(0,resolution.length()-1) + "week";
        }
        else if(resolution.endsWith("M") || resolution.endsWith("m")){
            period = resolution.substring(0,resolution.length()-1) + "month";
        }
        else{
            Integer val = Integer.parseInt(resolution);
            if(val < 60) {
                period = resolution + "min";
            }
            else {
                period = (val/60) + "hour";
            }
        }
        List<KLine> list = marketService.findAllKLine(symbol,from,to,period);
        JSONArray array = new JSONArray();
        boolean startFlag = false;
        KLine temKline = null;
        BigDecimal lastPrice = null;
        //获取下最新价格
        CoinProcessor processor = coinProcessorFactory.getProcessor(symbol);
        if(processor!=null){
            CoinThumb thumb = processor.getThumb();
            if(thumb!=null){
                lastPrice = thumb.getClose();
            }
        }

        for (int i = 0; i < list.size(); i++) {
            KLine item = list.get(i);
            // 此段处理是过滤币种开头出现0开盘/收盘的K线
            if(!startFlag && item.getOpenPrice().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }else {
                startFlag = true;
            }
            // 中间段如果出现为0的现象，需要处理一下
            if(item.getOpenPrice().compareTo(BigDecimal.ZERO) == 0) {
                item.setOpenPrice(temKline.getClosePrice());
                item.setClosePrice(temKline.getClosePrice());
                item.setHighestPrice(temKline.getClosePrice());
                item.setLowestPrice(temKline.getClosePrice());
            }
            //最后一个
            if(i==list.size()-1){
                Date date = new Date();
                List<ExchangeTrade> exchangeTrades = marketService.findTradeByTimeRange(symbol, temKline.getTime(), date.getTime());
                if(exchangeTrades!=null && exchangeTrades.size()>0){
                    for (ExchangeTrade exchangeTrade : exchangeTrades) {
                        processTrade(item, exchangeTrade);
                    }
                }
            }
//            //设置最新价格
            if(i==list.size()-1 && lastPrice!=null){
                item.setClosePrice(lastPrice);
            }
            JSONArray group = new JSONArray();
            group.add(0,item.getTime());
            group.add(1,item.getOpenPrice());
            group.add(2,item.getHighestPrice());
            group.add(3,item.getLowestPrice());
            group.add(4,item.getClosePrice());
            group.add(5,item.getVolume());
            array.add(group);

            temKline = item;
        }
        return array;
    }

    public void processTrade(KLine kLine, ExchangeTrade exchangeTrade) {
        if (kLine.getClosePrice().compareTo(BigDecimal.ZERO) == 0) {
            //第一次设置K线值
            kLine.setOpenPrice(exchangeTrade.getPrice());
            kLine.setHighestPrice(exchangeTrade.getPrice());
            kLine.setLowestPrice(exchangeTrade.getPrice());
            kLine.setClosePrice(exchangeTrade.getPrice());
        } else {
            kLine.setHighestPrice(exchangeTrade.getPrice().max(kLine.getHighestPrice()));
            kLine.setLowestPrice(exchangeTrade.getPrice().min(kLine.getLowestPrice()));
            kLine.setClosePrice(exchangeTrade.getPrice());
        }
        kLine.setCount(kLine.getCount() + 1);
        kLine.setVolume(kLine.getVolume().add(exchangeTrade.getAmount()));
        BigDecimal turnover = exchangeTrade.getPrice().multiply(exchangeTrade.getAmount());
        kLine.setTurnover(kLine.getTurnover().add(turnover));
    }

    /**
     * 查询最近成交记录
     * @param symbol 交易对符号
     * @param size 返回记录最大数量
     * @return
     */
    @ApiOperation(value = "查询最近成交记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
            @ApiImplicitParam(name = "size", value = "返回记录最大数量"),
    })
    @RequestMapping("latest-trade")
    public List<ExchangeTrade> latestTrade(String symbol, int size){
        return exchangeTradeFeign.findLatest(symbol,size);
    }

    @ApiOperation(value = "获取某交易对详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
    })
    @RequestMapping("exchange-plate")
    public Map<String,List<TradePlateItem>> findTradePlate(String symbol){
        //远程RPC服务URL,后缀为币种单位
        Map<String, List<TradePlateItem>> stringListMap = monitorFeign.traderPlate(symbol);
        return stringListMap;
    }


    @ApiOperation(value = "获取某交易对详情mini")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
    })
    @RequestMapping("exchange-plate-mini")
    public Map<String,JSONObject> findTradePlateMini(String symbol){
        //远程RPC服务URL,后缀为币种单位
        Map<String, JSONObject> traderPlateMini = monitorFeign.traderPlateMini(symbol);
        return traderPlateMini;
    }

    @ApiOperation(value = "获取某交易对详情full")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
    })
    @RequestMapping("exchange-plate-full")
    public Map<String,JSONObject> findTradePlateFull(String symbol){
        //远程RPC服务URL,后缀为币种单位
        Map<String, JSONObject> traderPlateFull = monitorFeign.traderPlateFull(symbol);
        return traderPlateFull;
    }

    @ApiOperation(value = "BTC/USDT趋势线")
    @GetMapping("add_dcitionary/{bond}/{value}")
    public MessageResult addDcitionaryForAdmin(@PathVariable("bond") String bond,@PathVariable("value") String value){
        log.info(">>>>字典表数据已修改>>>修改缓存中的数据>>>>>bond>"+bond+">>>>>value>>"+value);
        String key = SysConstant.DATA_DICTIONARY_BOUND_KEY+bond;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object bondvalue =valueOperations.get(key );
        if(bondvalue==null){
            log.info(">>>>>>缓存中无数据>>>>>");
            valueOperations.set(key,value);
        }else{
           log.info(">>>>缓存中有数据>>>");
           valueOperations.getOperations().delete(key);
           valueOperations.set(key,value);
        }
        MessageResult re = new MessageResult();
        re.setCode(0);
        re.setMessage("success");
        return re;
    }

    /**
     * BTC/USDT趋势线
     * @return
     */
    @ApiOperation(value = "BTC/USDT趋势线")
    @RequestMapping("/btc/trend")
    public MessageResult btcTrend() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.MINUTE,0);
        long nowTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR_OF_DAY,-24);

        JSONArray array = new JSONArray();
        long firstTimeOfToday = calendar.getTimeInMillis();

        List<KLine> lines = marketService.findAllKLine("BTC/USDT",firstTimeOfToday,nowTime,"5min");
        JSONArray trend = new JSONArray();
        for(KLine line:lines){
            trend.add(line.getClosePrice());
        }
        MessageResult re = new MessageResult();
        re.setCode(0);
        re.setData(trend);
        re.setMessage("success");
        return re;
    }

    /**
     * 币币时分图
     * @return
     */
    @ApiOperation(value = "币币时分图")
    @RequestMapping("/symbol-trend")
    public MessageResult symbolTrend(@RequestParam("symbol") String symbol) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.MINUTE,0);
        long nowTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR_OF_DAY,-24);

        JSONArray array = new JSONArray();
        long firstTimeOfToday = calendar.getTimeInMillis();

        List<KLine> lines = marketService.findAllKLine(symbol,firstTimeOfToday,nowTime,"1min");
        List<BigDecimal> amount = new ArrayList<>();
        List<Long> time = new ArrayList<>();
        for(KLine line:lines){
            amount.add(line.getClosePrice());
            time.add(line.getTime());
        }

        Map<String,List> resultMap = new HashMap<>();
        resultMap.put("amount",amount);
        resultMap.put("time",time);

        MessageResult re = new MessageResult();
        re.setCode(0);
        re.setData(resultMap);
        re.setMessage("success");
        return re;
    }

}
