package com.bitisan.user.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DBUtils {

	private static final String PREFIX_LOG = "【自定义DB工具】";

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	public List<Map<String, Object>> excuteQuerySql(String sql){
		log.info(PREFIX_LOG+"执行查询sql:"+sql);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		PreparedStatement pst = null;
		SqlSession session = getSqlSession();
		ResultSet result = null;
		try {
			pst = session.getConnection().prepareStatement(sql);
			result = pst.executeQuery();
			ResultSetMetaData md = result.getMetaData(); //获得结果集结构信息,元数据
			int columnCount = md.getColumnCount();   //获得列数
			while (result.next()) {
				Map<String,Object> rowData = new HashMap<String,Object>();
				for (int i = 1; i <= columnCount; i++) {
					rowData.put(md.getColumnName(i), result.getObject(i));
				}
				list.add(rowData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			closeSqlSession(session);
		}
		return list;
	}

	public void excuteUpdateSql(String sql){
		log.info(PREFIX_LOG+"执行sql:"+sql);
		PreparedStatement pst = null;
		SqlSession session = getSqlSession();
		try {
			pst = session.getConnection().prepareStatement(sql);
			pst.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			closeSqlSession(session);
		}
	}


	/**
	 * 获取sqlSession
	 * @return
	 */
	public SqlSession getSqlSession(){
		return SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory(),
				sqlSessionTemplate.getExecutorType(), sqlSessionTemplate.getPersistenceExceptionTranslator());
	}

	/**
	 * 关闭sqlSession
	 * @param session
	 */
	public void closeSqlSession(SqlSession session){
		SqlSessionUtils.closeSqlSession(session, sqlSessionTemplate.getSqlSessionFactory());
	}
}
