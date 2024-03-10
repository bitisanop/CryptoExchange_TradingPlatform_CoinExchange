package com.bitisan.admin.service;

import com.bitisan.admin.entity.AppRevision;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bitisan.constant.Platform;

/**
 * <p>
 * app修订版本表 服务类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface AppRevisionService extends IService<AppRevision> {

    AppRevision findRecentVersion(Platform platform);
}
