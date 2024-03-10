package com.bitisan.market.feign;


import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.exchange.feign.ExchangeCoinFeign;
import com.bitisan.market.processor.CoinProcessor;
import com.bitisan.market.processor.CoinProcessorFactory;
import com.bitisan.pojo.CoinThumb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("marketFeign")
public class MarketFeignController {
    @Autowired
    private ExchangeCoinFeign exchangeCoinFeign;
    @Autowired
    private CoinProcessorFactory coinProcessorFactory;

    @RequestMapping("engines")
    public Map<String, Integer> engines4Feign() {
        return this.engines();
    }

    /**
     * 获取币种缩略行情
     * @return
     */
    @RequestMapping("symbolThumb4Feign")
    public List<CoinThumb> findSymbolThumb4Feign(){
        List<ExchangeCoin> coins = exchangeCoinFeign.findAllVisible();
        List<CoinThumb> thumbs = new ArrayList<>();
        for(ExchangeCoin coin:coins){
            CoinProcessor processor = coinProcessorFactory.getProcessor(coin.getSymbol());
            CoinThumb thumb = processor.getThumb();
            thumb.setZone(coin.getZone());
            thumbs.add(thumb);
        }
        return thumbs;
    }

    private Map<String, Integer> engines() {
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

}
