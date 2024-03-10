package com.bitisan.market.consumer;

import com.alibaba.fastjson.JSON;
import com.bitisan.admin.entity.DataDictionary;
import com.bitisan.constant.SysConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(topic = "data-dictionary-save-update", consumerGroup = "market-data-dictionary-save-update")
public class DataDictionarySaveUpdateConsumer implements RocketMQListener<String> {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onMessage(String s) {
        DataDictionary record = JSON.parseObject(s, DataDictionary.class);
        String bond = record.getBond();
        String value = record.getValue();
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
    }
}
