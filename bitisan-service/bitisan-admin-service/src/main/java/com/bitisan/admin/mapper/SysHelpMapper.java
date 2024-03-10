package com.bitisan.admin.mapper;

import com.bitisan.admin.entity.SysHelp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 系统帮助 Mapper 接口
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface SysHelpMapper extends BaseMapper<SysHelp> {

    @Select("select * from sys_help WHERE sys_help_classification=#{cate}  and lang=#{lang}  order by sort desc limit 10")
    List<SysHelp> getCateTops(@Param("cate") String cate, @Param("lang") String lang);

    @Select("select max(s.sort) from sys_help s")
    int getMaxSort();
}
