package com.bitisan.exchange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.exchange.mapper.ExchangeCoinMapper;
import com.bitisan.exchange.service.ExchangeCoinService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.bitisan.screen.ExchangeCoinScreen;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 币币交易对表 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2022-02-07
 */
@Service
public class ExchangeCoinServiceImpl extends ServiceImpl<ExchangeCoinMapper, ExchangeCoin> implements ExchangeCoinService {

    @Override
    public List<ExchangeCoin> findAllEnabled() {
        QueryWrapper<ExchangeCoin> query = new QueryWrapper<>();
        query.eq("enable",1);
        query.orderByAsc("sort");
        return this.list(query);
    }

    //获取所有可显示币种
    @Override
    public List<ExchangeCoin> findAllVisible() {
        QueryWrapper<ExchangeCoin> query = new QueryWrapper<>();
        query.eq("enable",1);
        query.eq("visible",1);
        query.orderByAsc("sort");
        return this.list(query);
    }
    @Override
    public List<ExchangeCoin> findAllByRobotType(int robotType) {
        QueryWrapper<ExchangeCoin> query = new QueryWrapper<>();
        query.eq("enable",1);
        query.eq("visible",1);
        query.eq("robot_type",robotType);
        query.orderByAsc("sort");
        return this.list(query);
    }
    @Override
    public List<ExchangeCoin> findAllByFlag(int flag) {
        QueryWrapper<ExchangeCoin> query = new QueryWrapper<>();
        query.eq("enable",1);
        query.eq("visible",1);
        query.eq("flag",flag);
        query.orderByAsc("sort");
        return this.list(query);

    }
    @Override
    public ExchangeCoin findOne(String id) {
        return this.getById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deletes(String[] ids) {
        for (String id : ids) {
            this.removeById(id);
        }

    }

    @Override
    public IPage<ExchangeCoin> pageQuery(int pageNo, Integer pageSize) {
        IPage<ExchangeCoin> page = new Page<>(pageNo,pageSize);
        QueryWrapper<ExchangeCoin> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        return this.page(page,queryWrapper);
    }
    @Override
    public ExchangeCoin findBySymbol(String symbol) {
        return this.getById(symbol);
    }
    @Override
    public List<ExchangeCoin> findAll() {
        return this.list();
    }
    @Override
    public boolean isSupported(String symbol) {
        ExchangeCoin coin = findBySymbol(symbol);
        return coin != null && (coin.getEnable() == 1);
    }

    @Override
    public List<String> getBaseSymbol() {
        return this.baseMapper.findBaseSymbol();
    }
    @Override
    public List<String> getCoinSymbol(String baseSymbol) {
        return this.baseMapper.findCoinSymbol(baseSymbol);
    }
    @Override
    public List<String> getAllCoin(){
        return this.baseMapper.findAllCoinSymbol();
    }

    @Override
    public Page<ExchangeCoin> findAll(ExchangeCoinScreen screen) {
        Page<ExchangeCoin> page = new Page<>(screen.getPageNo(),screen.getPageSize());
        LambdaQueryWrapper<ExchangeCoin> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(screen.getCoinSymbol())) {
            queryWrapper.eq(ExchangeCoin::getCoinSymbol,screen.getCoinSymbol());
        }
        if (StringUtils.isNotBlank(screen.getBaseSymbol())) {
            queryWrapper.eq(ExchangeCoin::getBaseSymbol,screen.getBaseSymbol());
        }
        queryWrapper.orderByAsc(ExchangeCoin::getSort);
        return this.page(page,queryWrapper);
    }
}
