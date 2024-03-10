package com.bitisan.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitisan.admin.entity.SysHelp;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bitisan.constant.SysHelpClassification;

import java.util.List;

/**
 * <p>
 * 系统帮助 服务类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface SysHelpService extends IService<SysHelp> {

    List<SysHelp> findBySysHelpClassification(SysHelpClassification sysHelpClassification);

    IPage<SysHelp> findByCondition(int pageNo, int pageSize, SysHelpClassification help, String lang);

    List<SysHelp> getCateTops(String cate, String lang);

    int getMaxSort();
}
