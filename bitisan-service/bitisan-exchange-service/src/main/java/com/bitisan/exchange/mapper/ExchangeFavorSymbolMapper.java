package com.bitisan.exchange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitisan.exchange.entity.ExchangeFavorSymbol;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 交易优先符号 Mapper 接口
 * </p>
 *
 * @author markchao
 * @since 2022-02-07
 */
public interface ExchangeFavorSymbolMapper extends BaseMapper<ExchangeFavorSymbol> {

    ExchangeFavorSymbol findByMemberIdAndSymbol(@Param("memberId") Long memberId, @Param("symbol")String symbol);

    List<ExchangeFavorSymbol> findAllByMemberId(@Param("memberId")Long memberId);
}
