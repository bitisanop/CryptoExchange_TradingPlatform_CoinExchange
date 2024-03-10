package com.bitisan.user.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bitisan.constant.SysConstant;
import com.bitisan.user.entity.Currency;
import com.bitisan.user.service.CurrencyService;
import com.bitisan.util.DateUtil;
import com.bitisan.vo.CurrencyResponse;
import com.bitisan.vo.CurrencyVO;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class CurrencyTask {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @XxlJob("currencySyncTask")
    public void currencySyncTask() {
//        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        String url = "https://p2p.binance.com/bapi/asset/v1/public/asset-service/product/currency";
        RestTemplate restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//        requestFactory.setProxy(ProxyUtil.getProxy());
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(120000);
        restTemplate.setRequestFactory(requestFactory);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCodeValue() == HttpStatus.OK.value()) {
            ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
            CurrencyResponse currencyResponse = JSON.parseObject(response.getBody(), CurrencyResponse.class);
            if (currencyResponse != null) {
                List<CurrencyVO> CurrencyVOList = currencyResponse.getData();
                if (CurrencyVOList != null && !CurrencyVOList.isEmpty()) {

                    LambdaQueryWrapper<Currency> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.ne(Currency::getFullName, "USD");
                    List<Currency> currencyList = currencyService.list(lambdaQueryWrapper);

                    if (currencyList != null && !currencyList.isEmpty()) {
                        log.info("法币汇率更新中...");
                        currencyList.forEach(currency -> {
                            CurrencyVOList.stream().
                                    filter(currencyVO -> !StringUtils.isEmpty(currencyVO.getPair()) && currencyVO.getPair().startsWith(currency.getFullName())).
                                    findFirst().
                                    ifPresent(currencyByName -> {
                                        currency.setRate(currencyByName.getRate());
                                        currency.setUpdateTime(DateUtil.getCurrentDate());
                                    });
                        });
                        currencyService.saveOrUpdateBatch(currencyList);
                        redisTemplate.delete(SysConstant.CURRENCY);
                        opsForValue.set(SysConstant.CURRENCY, JSON.toJSONString(currencyService.list()), SysConstant.CURRENCY_HALF_HOUR, TimeUnit.SECONDS);
                    }
                } else {
                    //TODO 拿到空列表
                    log.info("获取法币汇率失败，列表为空");
                }
            }
        } else {
            //TODO 接口坏了
            log.info("获取法币汇率失败，外部接口错误");
        }
    }
}
