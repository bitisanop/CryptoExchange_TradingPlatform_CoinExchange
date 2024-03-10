package com.bitisan.admin.mapper;

import com.bitisan.admin.entity.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * 管理员 Mapper 接口
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface AdminMapper extends BaseMapper<Admin> {

    Map findAdminDetail(@Param("id") Long id);
}
