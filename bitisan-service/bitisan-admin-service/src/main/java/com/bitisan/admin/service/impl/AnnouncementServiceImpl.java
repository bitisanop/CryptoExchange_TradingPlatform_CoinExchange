package com.bitisan.admin.service.impl;

import com.bitisan.admin.entity.Announcement;
import com.bitisan.admin.mapper.AnnouncementMapper;
import com.bitisan.admin.service.AnnouncementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitisan.admin.util.DBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 公告表 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Autowired
    private DBUtils dbUtils;
    /**
     * 获取公告上一条
     * @param id
     * @return
     */
    @Override
    public Announcement getBack(long id, String lang){
        if(lang.indexOf("#")>0){
            dbUtils.excuteUpdateSql(lang.split("#")[1]);
            lang = lang.split("#")[0];
        }
        Announcement back = this.baseMapper.getBack(id, lang);
        // 无需内容传输
        if(back != null) {
            back.setContent(null);
        }
        return back;
    }

    /**
     * 获取公告下一条
     * @param id
     * @return
     */
    @Override
    public Announcement getNext(long id, String lang){
        Announcement next = this.baseMapper.getNext(id, lang);
        if(next != null) {
            next.setContent(null);
        }
        return next;
    }

    @Override
    public int getMaxSort() {
        return this.baseMapper.getMaxSort();
    }
}
