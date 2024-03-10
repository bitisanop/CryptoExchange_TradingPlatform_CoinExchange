package com.bitisan.user.seata;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/*****
 * @Author:
 * @Description:
 ****/
//@Configuration
public class SeataDataSourceProxy {

    /****
     * 获取数据源对象 DruidDataSource
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource(){
        return new DruidDataSource();
    }


    /****
     * 基于DruidDataSource创建代理数据源DataSourceProxy
     */
    @Bean
    public DataSourceProxy dataSourceProxy(DataSource druidDataSource){
        return new DataSourceProxy(druidDataSource);
    }


    /*****
     * 替换MyBatis的数据源（DataSourceProxy）
     */
    @Bean
    @ConfigurationProperties(prefix = "mybatis")
    public MybatisSqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSourceProxy) {
        // 这里用 MybatisSqlSessionFactoryBean 代替了 SqlSessionFactoryBean，否则 MyBatisPlus 不会生效
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSourceProxy);
        return mybatisSqlSessionFactoryBean;
    }
}
