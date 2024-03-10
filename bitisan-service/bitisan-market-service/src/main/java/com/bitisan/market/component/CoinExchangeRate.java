package com.bitisan.market.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bitisan.exchange.feign.ExchangeCoinFeign;
import com.bitisan.market.processor.CoinProcessor;
import com.bitisan.market.processor.CoinProcessorFactory;
import com.bitisan.pojo.CoinThumb;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.Currency;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.user.feign.CurrencyFeign;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 币种汇率管理
 */
@Component
@Slf4j
@ToString
public class CoinExchangeRate {
    @Getter
    @Setter
    private BigDecimal usdCnyRate = new BigDecimal("6.45");

    @Getter
    @Setter
    private BigDecimal usdtCnyRate = new BigDecimal("6.98");

    @Getter
    @Setter
    private BigDecimal usdJpyRate = new BigDecimal("110.02");
    @Getter
    @Setter
    private BigDecimal usdHkdRate = new BigDecimal("7.8491");
    @Getter
    @Setter
    private BigDecimal sgdCnyRate = new BigDecimal("5.08");
    @Setter
    private CoinProcessorFactory coinProcessorFactory;

    private Map<String,BigDecimal> ratesMap = new HashMap<String,BigDecimal>(){{
        put("CNY",new BigDecimal("6.36"));
        put("TWD",new BigDecimal("6.40"));
        put("USD",new BigDecimal("1.00"));
        put("EUR",new BigDecimal("0.91"));
        put("HKD",new BigDecimal("7.81"));
        put("SGD",new BigDecimal("1.36"));
    }};

    @Autowired
    private CoinFeign coinService;
    @Autowired
    private ExchangeCoinFeign exCoinService;

    @Autowired
    private CurrencyFeign currencyFeign;


    public BigDecimal getUsdRate(String symbol) {
        log.info("CoinExchangeRate getUsdRate unit = " + symbol);
        if ("USDT".equalsIgnoreCase(symbol)) {
            log.info("CoinExchangeRate getUsdRate unit = USDT  ,result = ONE");
            return BigDecimal.ONE;
        } else if ("CNY".equalsIgnoreCase(symbol)) {
            log.info("CoinExchangeRate getUsdRate unit = CNY  ,result : 1 divide {}", this.usdtCnyRate);
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdtCnyRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        }else if ("BITCNY".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdCnyRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        } else if ("ET".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdCnyRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        } else if ("JPY".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdJpyRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        }else if ("HKD".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdHkdRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        }
        String usdtSymbol = symbol.toUpperCase() + "/USDT";
        String btcSymbol = symbol.toUpperCase() + "/BTC";
        String ethSymbol = symbol.toUpperCase() + "/ETH";

        if (coinProcessorFactory != null) {
            if (coinProcessorFactory.containsProcessor(usdtSymbol)) {
                log.info("Support exchange coin = {}", usdtSymbol);
                CoinProcessor processor = coinProcessorFactory.getProcessor(usdtSymbol);
                if(processor == null) {
                	return BigDecimal.ZERO;
                }
                CoinThumb thumb = processor.getThumb();
                if(thumb == null) {
                	log.info("Support exchange coin thumb is null", thumb);
                	return BigDecimal.ZERO;
                }
                return thumb.getUsdRate();
            } else if (coinProcessorFactory.containsProcessor(btcSymbol)) {
                log.info("Support exchange coin = {}/BTC", btcSymbol);
                CoinProcessor processor = coinProcessorFactory.getProcessor(btcSymbol);
                if(processor == null) {
                	return BigDecimal.ZERO;
                }
                CoinThumb thumb = processor.getThumb();
                if(thumb == null) {
                	log.info("Support exchange coin thumb is null", thumb);
                	return BigDecimal.ZERO;
                }
                return thumb.getUsdRate();
            } else if (coinProcessorFactory.containsProcessor(ethSymbol)) {
                log.info("Support exchange coin = {}/ETH", ethSymbol);
                CoinProcessor processor = coinProcessorFactory.getProcessor(ethSymbol);
                if(processor == null) {
                	return BigDecimal.ZERO;
                }
                CoinThumb thumb = processor.getThumb();
                if(thumb == null) {
                	log.info("Support exchange coin thumb is null", thumb);
                	return BigDecimal.ZERO;
                }
                return thumb.getUsdRate();
            } else {
                return getDefaultUsdRate(symbol);
            }
        } else {
            return getDefaultUsdRate(symbol);
        }
    }

    /**
     * 获取币种设置里的默认价格
     *
     * @param symbol
     * @return
     */
    public BigDecimal getDefaultUsdRate(String symbol) {
        Coin coin = coinService.findByUnit(symbol);
        if (coin != null) {
            return coin.getUsdRate();
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getCnyRate(String symbol) {
        if ("CNY".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        } else if("ET".equalsIgnoreCase(symbol)){
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdtCnyRate).setScale(2, RoundingMode.DOWN);
    }

    public BigDecimal getJpyRate(String symbol) {
        if ("JPY".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdJpyRate).setScale(2, RoundingMode.DOWN);
    }

    public BigDecimal getHkdRate(String symbol) {
        if ("HKD".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdHkdRate).setScale(2, RoundingMode.DOWN);
    }

    /**
     * 同步汇率从uc
     *
     * @throws UnirestException
     */

//    @Scheduled(cron = "0 */15 * * * *")
    @XxlJob("syncCurrencyMap")
    public void syncCurrencyMap() {

        List<Currency> allCurrency = currencyFeign.findAllCurrency();
        for (Currency currency : allCurrency) {
            ratesMap.put(currency.getFullName(),currency.getRate());
            if("CNY".equals(currency.getFullName().toUpperCase())){
                setUsdCnyRate(currency.getRate());
                setUsdtCnyRate(currency.getRate());
            }else if("JPY".equals(currency.getFullName().toUpperCase())){
                setUsdJpyRate(currency.getRate());
            }else if("HKD".equals(currency.getFullName().toUpperCase())){
                setUsdHkdRate(currency.getRate());
            }
        }


//        Set<String> currencies = ratesMap.keySet();
//        for (String currency : currencies) {
            // okex接口
            String urlOk="https://www.okex.com/v3/c2c/otc-ticker?&baseCurrency=USDT&quoteCurrency=CNY";
            try {
                HttpResponse<JsonNode> resp = Unirest.get(urlOk).asJson();
                if(resp.getStatus() == 200) { //正确返回
                    JSONObject ret = JSON.parseObject(resp.getBody().toString());
                    if(ret.getIntValue("code") == 0) {
                        double doubleValue = ret.getJSONObject("data").getDoubleValue("otcTicker");
                        setUsdtCnyRate(new BigDecimal(doubleValue).setScale(2, RoundingMode.HALF_UP));
                    }
                }
            } catch (UnirestException e) {
                log.info("开始同步OTC报错");
                log.error(e.toString());
                e.printStackTrace();
            }
//        }

    }

//    /**
//     * 每30分钟同步一次价格
//     *
//     * @throws UnirestException
//     */
//
//    @Scheduled(cron = "0 */30 * * * *")
//    public void syncPrice() throws UnirestException {

//        String url = "http://web.juhe.cn:8080/finance/exchange/frate?key=0330f6e51631ee1c0c4696a49201cb94";
//        //如有报错 请自行官网申请获取汇率 或者默认写死
//        HttpResponse<JsonNode> resp = Unirest.get(url)
//                .queryString("key", "0330f6e51631ee1c0c4696a49201cb94")
//                .asJson();
//        log.info("forex result:{}", resp.getBody());
//        JSONObject ret = JSON.parseObject(resp.getBody().toString());
//
//        if(ret.getIntValue("resultcode") == 200) {
//	        JSONArray result = ret.getJSONArray("result");
//	        result.forEach(json -> {
//	            JSONObject obj = (JSONObject) json;
//	            if ("USDCNY".equals(obj.getString("code"))) {
//	                setUsdCnyRate(new BigDecimal(obj.getDouble("price")).setScale(2, RoundingMode.DOWN));
//	                log.info(obj.toString());
//	            } else if ("USDJPY".equals(obj.getString("code"))) {
//	                setUsdJpyRate(new BigDecimal(obj.getDouble("price")).setScale(2, RoundingMode.DOWN));
//	                log.info(obj.toString());
//	            }
//	            else if ("USDHKD".equals(obj.getString("code"))) {
//	                setUsdHkdRate(new BigDecimal(obj.getDouble("price")).setScale(2, RoundingMode.DOWN));
//	                log.info(obj.toString());
//	            }
//	            /*
//	            else if("SGDCNH".equals(obj.getString("code"))){
//	                setSgdCnyRate(new BigDecimal(obj.getDouble("price")).setScale(2,RoundingMode.DOWN));
//	                log.info(obj.toString());
//	            }
//	            */
//
//	        });
//        }
//    }

    public HashMap<String, BigDecimal> getAllRate(String symbol) {
        HashMap<String,BigDecimal> result = new HashMap<>();
        Set<String> keySet = ratesMap.keySet();
        for (String currency : keySet) {
            if ("CNY".equalsIgnoreCase(symbol)) {
                result.put(currency,BigDecimal.ONE);
                continue;
            } else if("ET".equalsIgnoreCase(symbol)){
                result.put(currency,BigDecimal.ONE);
                continue;
            }
            BigDecimal usdtRate = ratesMap.get(currency);
            BigDecimal rate = getUsdRate(symbol).multiply(usdtRate).setScale(2, RoundingMode.DOWN);
            result.put(currency,rate);
        }
        return result;
    }
}
