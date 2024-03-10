package com.bitisan.admin.service;

import com.bitisan.admin.entity.Announcement;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 公告表 服务类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface AnnouncementService extends IService<Announcement> {

    Announcement getBack(long id, String lang);

    Announcement getNext(long id, String lang);

    int getMaxSort();
}
