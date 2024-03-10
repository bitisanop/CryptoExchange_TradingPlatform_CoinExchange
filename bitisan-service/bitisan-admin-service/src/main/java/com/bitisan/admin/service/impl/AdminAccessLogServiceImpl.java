package com.bitisan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.entity.AdminAccessLog;
import com.bitisan.admin.mapper.AdminAccessLogMapper;
import com.bitisan.admin.service.AdminAccessLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitisan.admin.vo.AdminAccessLogVo;
import com.bitisan.constant.AdminModule;
import com.bitisan.screen.AccessLogScreen;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 管理员访问日志 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Service
public class AdminAccessLogServiceImpl extends ServiceImpl<AdminAccessLogMapper, AdminAccessLog> implements AdminAccessLogService {

    @Override
    public IPage<AdminAccessLogVo> pageQuery(AccessLogScreen accessLogScreen) {
        IPage<AdminAccessLogVo> page = new Page<>(accessLogScreen.getPageNo(),accessLogScreen.getPageSize());
        IPage<AdminAccessLogVo> logVoIPage = this.baseMapper.pageQuery(page, accessLogScreen.getAdminName(), accessLogScreen.getModule()==null?null:accessLogScreen.getModule().getCode());
        for (AdminAccessLogVo record : logVoIPage.getRecords()) {
            record.setModuleName(AdminModule.creator(record.getModule()).getDescription());
        }
        return logVoIPage;
    }
}
