package com.bitisan.admin.mapper;

import com.bitisan.admin.entity.AdminPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 后台菜单 Mapper 接口
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface AdminPermissionMapper extends BaseMapper<AdminPermission> {

    List<AdminPermission> getPermissionsByRid(@Param("rid") Long rid);
}
