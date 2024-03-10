package com.bitisan.exchange.service.impl;

import com.bitisan.exchange.entity.ExchangeOrderDetail;
import com.bitisan.exchange.repository.ExchangeOrderDetailRepository;
import com.bitisan.exchange.service.ExchangeOrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExchangeOrderDetailServiceImpl implements ExchangeOrderDetailService {
    @Autowired
    private ExchangeOrderDetailRepository orderDetailRepository;

    /**
     * 查询某订单的成交详情
     * @param orderId
     * @return
     */
    @Override
    public List<ExchangeOrderDetail> findAllByOrderId(String orderId){
        return orderDetailRepository.findAllByOrderId(orderId);
    }
    @Override
    public ExchangeOrderDetail save(ExchangeOrderDetail detail){
       return orderDetailRepository.save(detail);
    }
}
