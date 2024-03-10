package com.bitisan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bitisan.admin.entity.AppRevision;
import com.bitisan.admin.mapper.AppRevisionMapper;
import com.bitisan.admin.service.AppRevisionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitisan.constant.Platform;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * app修订版本表 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Service
public class AppRevisionServiceImpl extends ServiceImpl<AppRevisionMapper, AppRevision> implements AppRevisionService {

    @Override
    public AppRevision findRecentVersion(Platform platform) {
        QueryWrapper<AppRevision> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("platform",platform.getCode());
        queryWrapper.orderByDesc("id");
        List<AppRevision> list = this.list(queryWrapper);
        if(list!=null && list.size()>0){
            return list.get(0);
        }else {
            return null;
        }
    }
}
