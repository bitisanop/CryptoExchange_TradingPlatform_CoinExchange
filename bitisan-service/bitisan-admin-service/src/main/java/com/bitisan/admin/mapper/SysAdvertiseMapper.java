package com.bitisan.admin.mapper;

import com.bitisan.admin.entity.SysAdvertise;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 轮播图 Mapper 接口
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface SysAdvertiseMapper extends BaseMapper<SysAdvertise> {

    @Select("select max(s.sort) from sys_advertise s")
    int getMaxSort();

    @Select("SELECT * FROM sys_advertise s WHERE s.sort>= #{sort} AND s.status=0 AND s.sys_advertise_location= #{cate} ORDER BY sort ASC")
    List<SysAdvertise> querySysAdvertise(@Param("sort") int sort, @Param("cate") int cate);
}
