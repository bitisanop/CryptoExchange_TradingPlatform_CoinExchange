package com.bitisan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitisan.admin.entity.DataDictionary;
import com.bitisan.admin.mapper.DataDictionaryMapper;
import com.bitisan.admin.service.DataDictionaryService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 字典信息表 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
@Service
public class DataDictionaryServiceImpl extends ServiceImpl<DataDictionaryMapper, DataDictionary> implements DataDictionaryService {

    @Override
    public DataDictionary findByBond(String bond) {
        QueryWrapper<DataDictionary> query = new QueryWrapper<>();
        query.eq("bond",bond);
        return this.getOne(query);
    }
}
