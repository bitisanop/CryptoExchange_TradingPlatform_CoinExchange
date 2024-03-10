package com.bitisan.exchange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitisan.exchange.entity.ExchangeCoin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 币币交易对 Mapper 接口
 * </p>
 *
 * @author markchao
 * @since 2022-02-07
 */
public interface ExchangeCoinMapper extends BaseMapper<ExchangeCoin> {

    @Select("select distinct base_symbol from exchange_coin where enable = 1 ")
    List<String> findBaseSymbol();

    @Select("select distinct coin_symbol from exchange_coin where enable = 1 and base_symbol = #{baseSymbol} ")
    List<String> findCoinSymbol(@Param("baseSymbol") String baseSymbol);

    @Select("select distinct coin_symbol from exchange_coin where enable = 1 ")
    List<String> findAllCoinSymbol();
}
