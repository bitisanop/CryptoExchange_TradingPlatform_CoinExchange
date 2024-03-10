package com.bitisan.admin.service;

import com.bitisan.admin.entity.SysAdvertise;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bitisan.constant.SysAdvertiseLocation;

import java.util.List;

/**
 * <p>
 * 轮播图 服务类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface SysAdvertiseService extends IService<SysAdvertise> {

    List<SysAdvertise> findAllNormal(SysAdvertiseLocation sysAdvertiseLocation, String headerLanguage);

    int getMaxSort();

    List<SysAdvertise> querySysAdvertise(int sort, int cate);
}
