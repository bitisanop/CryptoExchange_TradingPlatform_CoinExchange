package com.bitisan.market.repository;

import com.bitisan.pojo.ExchangeTrade;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TradeRepository extends MongoRepository<ExchangeTrade,Long>{
}
