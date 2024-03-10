package com.bitisan.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitisan.admin.entity.DataDictionary;

/**
 * <p>
 * 字典信息表 服务类
 * </p>
 *
 * @author markchao
 * @since 2021-06-21
 */
public interface DataDictionaryService extends IService<DataDictionary> {

    DataDictionary findByBond(String bond);
}
