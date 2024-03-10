package com.bitisan.exchange.repository;

import com.bitisan.pojo.ExchangeTrade;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExchangeTradeRepository extends MongoRepository<ExchangeTrade,String> {
}
