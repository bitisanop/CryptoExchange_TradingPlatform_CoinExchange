package com.bitisan.exchange.service;

import com.bitisan.exchange.entity.ExchangeFavorSymbol;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bitisan.util.DateUtil;

import java.util.List;

/**
 * <p>
 * 币币交易收藏表 服务类
 * </p>
 *
 * @author markchao
 * @since 2022-02-07
 */
public interface ExchangeFavorSymbolService extends IService<ExchangeFavorSymbol> {
    /**
     * 添加自选
     * @param memberId
     * @param symbol
     * @return
     */
    public ExchangeFavorSymbol add(Long memberId,String symbol);

    /**
     * 删除自选
     * @param memberId
     * @param symbol
     */
    public void delete(Long memberId,String symbol);

    /**
     * 查询会员所有的自选
     * @param memberId
     * @return
     */
    public List<ExchangeFavorSymbol> findByMemberId(Long memberId);
    /**
     * 查询某个自选
     * @param memberId
     * @param symbol
     * @return
     */
    public ExchangeFavorSymbol findByMemberIdAndSymbol(Long memberId,String symbol);
}
