package com.bitisan.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitisan.admin.entity.AdminAccessLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bitisan.admin.vo.AdminAccessLogVo;
import com.bitisan.screen.AccessLogScreen;

/**
 * <p>
 * 管理员访问日志 服务类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface AdminAccessLogService extends IService<AdminAccessLog> {

    IPage<AdminAccessLogVo> pageQuery(AccessLogScreen accessLogScreen);
}
