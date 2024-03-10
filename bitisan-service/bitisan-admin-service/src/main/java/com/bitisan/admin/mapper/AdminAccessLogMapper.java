package com.bitisan.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitisan.admin.entity.AdminAccessLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bitisan.admin.vo.AdminAccessLogVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 管理员访问日志 Mapper 接口
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface AdminAccessLogMapper extends BaseMapper<AdminAccessLog> {

    IPage<AdminAccessLogVo> pageQuery(IPage<AdminAccessLogVo> page, @Param("adminName") String adminName, @Param("module")Integer module);
}
