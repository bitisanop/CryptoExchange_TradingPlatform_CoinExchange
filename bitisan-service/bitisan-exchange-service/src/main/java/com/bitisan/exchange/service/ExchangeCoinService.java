package com.bitisan.exchange.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.exchange.entity.ExchangeCoin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bitisan.screen.ExchangeCoinScreen;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 币币交易对表 服务类
 * </p>
 *
 * @author markchao
 * @since 2022-02-07
 */
public interface ExchangeCoinService extends IService<ExchangeCoin> {

    public List<ExchangeCoin> findAllEnabled();

    //获取所有可显示币种
    public List<ExchangeCoin> findAllVisible();

    public List<ExchangeCoin> findAllByRobotType(int robotType);

    public List<ExchangeCoin> findAllByFlag(int flag);

    public ExchangeCoin findOne(String id);

    public void deletes(String[] ids);


    public IPage<ExchangeCoin> pageQuery(int pageNo, Integer pageSize);

    public ExchangeCoin findBySymbol(String symbol) ;

    public List<ExchangeCoin> findAll() ;

    public boolean isSupported(String symbol) ;

    public List<String> getBaseSymbol() ;

    public List<String> getCoinSymbol(String baseSymbol) ;

    public List<String> getAllCoin();

    Page<ExchangeCoin> findAll(ExchangeCoinScreen screen);
}
