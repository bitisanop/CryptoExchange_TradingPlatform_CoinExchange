package com.bitisan.exchange.service.impl;

import com.bitisan.exchange.entity.ExchangeFavorSymbol;
import com.bitisan.exchange.mapper.ExchangeFavorSymbolMapper;
import com.bitisan.exchange.service.ExchangeFavorSymbolService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitisan.util.DateUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 币币交易收藏表 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2022-02-07
 */
@Service
public class ExchangeFavorSymbolServiceImpl extends ServiceImpl<ExchangeFavorSymbolMapper, ExchangeFavorSymbol> implements ExchangeFavorSymbolService {

    /**
     * 添加自选
     * @param memberId
     * @param symbol
     * @return
     */
    @Override
    public ExchangeFavorSymbol add(Long memberId,String symbol){
        ExchangeFavorSymbol favor = new ExchangeFavorSymbol();
        favor.setMemberId(memberId);
        favor.setAddTime(DateUtil.getDateTime());
        favor.setSymbol(symbol);
        this.save(favor);
        return favor;
    }

    /**
     * 删除自选
     * @param memberId
     * @param symbol
     */
    @Override
    public void delete(Long memberId,String symbol){
        ExchangeFavorSymbol favor = this.baseMapper.findByMemberIdAndSymbol(memberId,symbol);
        if(favor != null){
            this.removeById(favor.getId());
        }
    }

    /**
     * 查询会员所有的自选
     * @param memberId
     * @return
     */
    @Override
    public List<ExchangeFavorSymbol> findByMemberId(Long memberId){
        return this.baseMapper.findAllByMemberId(memberId);
    }

    /**
     * 查询某个自选
     * @param memberId
     * @param symbol
     * @return
     */
    @Override
    public ExchangeFavorSymbol findByMemberIdAndSymbol(Long memberId,String symbol){
        return this.baseMapper.findByMemberIdAndSymbol(memberId,symbol);
    }
}
