package com.bitisan.exchange.service;

import com.bitisan.exchange.entity.ExchangeOrderDetail;

import java.util.List;

public interface ExchangeOrderDetailService {

    public List<ExchangeOrderDetail> findAllByOrderId(String orderId);

    public ExchangeOrderDetail save(ExchangeOrderDetail detail);
}
