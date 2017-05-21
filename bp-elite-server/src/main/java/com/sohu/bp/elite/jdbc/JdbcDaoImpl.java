package com.sohu.bp.elite.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.CollectionUtils;

import com.sohu.bp.elite.jdbc.namehandler.DefaultNameHandler;
import com.sohu.bp.elite.util.ClassUtils;
import com.sohu.bp.elite.util.NameUtils;

/**
 * jdbc操作dao
 *
 * 
 */
@SuppressWarnings("unchecked")
public class JdbcDaoImpl implements JdbcDao {

    /** spring jdbcTemplate 对象 */
    protected JdbcTemplate jdbcTemplate;

    /** 名称处理器，为空按默认执行 */
    protected NameHandler    nameHandler;

    /** rowMapper，为空按默认执行 */
    protected String         rowMapperClass;

    /** 数据库方言 */
    protected String         dialect;

    /**
     * 插入数据
     *
     * @param entity the entity
     * @param criteria the criteria
     * @param tableName partition table name
     * @return long long
     */
    private Long insert(Object entity, Criteria criteria, String tableName) {
        Class<?> entityClass = SqlUtils.getEntityClass(entity, criteria);
        NameHandler handler = this.getNameHandler();
        String pkValue = handler.getPKValue(entityClass, this.dialect);
        if (StringUtils.isNotBlank(pkValue)) {
            String primaryName = handler.getPKName(entityClass);
            if (criteria == null) {
                criteria = Criteria.create(entityClass);
            }
            criteria.setPKValueName(NameUtils.getCamelName(primaryName), pkValue);
        }
        final BoundSql boundSql = SqlUtils.buildInsertSql(entity, criteria, this.getNameHandler(), tableName);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(boundSql.getSql(),
                    new String[] { boundSql.getPrimaryKey() });
                int index = 0;
                for (Object param : boundSql.getParams()) {
                    index++;
                    ps.setObject(index, param);
                }
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public Long insert(Object entity) {
        return this.insert(entity, null, null);
    }
    
    public Long insert(Object entity, String tableName) {
        return this.insert(entity, null, tableName);
    }

    public Long insert(Criteria criteria) {
        return this.insert(null, criteria, null);
    }
    
    public Long insert(Criteria criteria, String tableName) {
        return this.insert(null, criteria, tableName);
    }

    public int save(Object entity) {
        final BoundSql boundSql = SqlUtils.buildInsertSql(entity, null, this.getNameHandler(), null);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }
    
    public int save(Object entity, String tableName) {
        final BoundSql boundSql = SqlUtils.buildInsertSql(entity, null, this.getNameHandler(), tableName);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }

    public int save(Criteria criteria) {
        final BoundSql boundSql = SqlUtils.buildInsertSql(null, criteria, this.getNameHandler(), null);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }
    
    public int save(Criteria criteria, String tableName) {
        final BoundSql boundSql = SqlUtils.buildInsertSql(null, criteria, this.getNameHandler(), tableName);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }

    public int update(Criteria criteria) {
        BoundSql boundSql = SqlUtils.buildUpdateSql(null, criteria, this.getNameHandler(), null);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }
    
    public int update(Criteria criteria, String tableName) {
        BoundSql boundSql = SqlUtils.buildUpdateSql(null, criteria, this.getNameHandler(), tableName);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }

    public int update(Object entity) {
        BoundSql boundSql = SqlUtils.buildUpdateSql(entity, null, this.getNameHandler(), null);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }
    
    public int update(Object entity, String tableName) {
        BoundSql boundSql = SqlUtils.buildUpdateSql(entity, null, this.getNameHandler(), tableName);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }

    public int delete(Criteria criteria) {
        BoundSql boundSql = SqlUtils.buildDeleteSql(null, criteria, this.getNameHandler(), null);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }
    
    public int delete(Criteria criteria, String tableName) {
        BoundSql boundSql = SqlUtils.buildDeleteSql(null, criteria, this.getNameHandler(), tableName);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }

    public int delete(Object entity) {
        BoundSql boundSql = SqlUtils.buildDeleteSql(entity, null, this.getNameHandler(), null);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }
    
    public int delete(Object entity, String tableName) {
        BoundSql boundSql = SqlUtils.buildDeleteSql(entity, null, this.getNameHandler(), tableName);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }

    public int delete(Class<?> clazz, Long id) {
        BoundSql boundSql = SqlUtils.buildDeleteSql(clazz, id, this.getNameHandler(), null);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }
    
    public int delete(Class<?> clazz, Long id, String tableName) {
        BoundSql boundSql = SqlUtils.buildDeleteSql(clazz, id, this.getNameHandler(), tableName);
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams().toArray());
    }

    public void deleteAll(Class<?> clazz) {
        String tableName = this.getNameHandler().getTableName(clazz, null);
        String sql = "TRUNCATE TABLE " + tableName;
        jdbcTemplate.execute(sql);
    }
    
    public void deleteAll(Class<?> clazz, String tableName) {
    	String genTableName = "";
    	if(tableName != null && tableName.length() > 0)
    		genTableName = tableName;
    	else
        	genTableName = this.getNameHandler().getTableName(clazz, null);
        String sql = "TRUNCATE TABLE " + genTableName;
        jdbcTemplate.execute(sql);
    }

	public <T> List<T> queryList(Criteria criteria) {
        BoundSql boundSql = SqlUtils.buildListSql(null, criteria, this.getNameHandler(), null);
        List<?> list = jdbcTemplate.query(boundSql.getSql(), boundSql.getParams().toArray(),
            this.getRowMapper(criteria.getEntityClass()));
        return (List<T>) list;
    }
	
	public <T> List<T> queryList(Criteria criteria, String tableName) {
        BoundSql boundSql = SqlUtils.buildListSql(null, criteria, this.getNameHandler(), tableName);
        List<?> list = jdbcTemplate.query(boundSql.getSql(), boundSql.getParams().toArray(),
            this.getRowMapper(criteria.getEntityClass()));
        return (List<T>) list;
    }

    public <T> List<T> queryList(Class<?> clazz) {
        BoundSql boundSql = SqlUtils.buildListSql(null, Criteria.create(clazz),
            this.getNameHandler(), null);
        List<?> list = jdbcTemplate.query(boundSql.getSql(), boundSql.getParams().toArray(),
            this.getRowMapper(clazz));
        return (List<T>) list;
    }
    
    public <T> List<T> queryList(Class<?> clazz, String tableName) {
        BoundSql boundSql = SqlUtils.buildListSql(null, Criteria.create(clazz),
            this.getNameHandler(), tableName);
        List<?> list = jdbcTemplate.query(boundSql.getSql(), boundSql.getParams().toArray(),
            this.getRowMapper(clazz));
        return (List<T>) list;
    }

    public <T> List<T> queryList(T entity) {
        BoundSql boundSql = SqlUtils.buildListSql(entity, null, this.getNameHandler(), null);
        List<?> list = jdbcTemplate.query(boundSql.getSql(), boundSql.getParams().toArray(),
            this.getRowMapper(entity.getClass()));
        return (List<T>) list;
    }
    
    public <T> List<T> queryList(T entity, String tableName) {
        BoundSql boundSql = SqlUtils.buildListSql(entity, null, this.getNameHandler(), tableName);
        List<?> list = jdbcTemplate.query(boundSql.getSql(), boundSql.getParams().toArray(),
            this.getRowMapper(entity.getClass()));
        return (List<T>) list;
    }

    public <T> List<T> queryList(T entity, Criteria criteria) {
        BoundSql boundSql = SqlUtils.buildListSql(entity, criteria, this.getNameHandler(), null);
        List<?> list = jdbcTemplate.query(boundSql.getSql(), boundSql.getParams().toArray(),
            this.getRowMapper(entity.getClass()));
        return (List<T>) list;
    }
    
    public <T> List<T> queryList(T entity, Criteria criteria, String tableName) {
        BoundSql boundSql = SqlUtils.buildListSql(entity, criteria, this.getNameHandler(), tableName);
        List<?> list = jdbcTemplate.query(boundSql.getSql(), boundSql.getParams().toArray(),
            this.getRowMapper(entity.getClass()));
        return (List<T>) list;
    }

    public int queryCount(Object entity, Criteria criteria) {
        BoundSql boundSql = SqlUtils.buildCountSql(entity, criteria, this.getNameHandler(), null);
        //return jdbcTemplate.queryForInt(boundSql.getSql(), boundSql.getParams().toArray());
        Integer ret = jdbcTemplate.queryForObject(boundSql.getSql(), Integer.class, boundSql.getParams().toArray());
        if(ret != null)
        	return ret.intValue();
        else
        	return 0;
    }
    
    public int queryCount(Object entity, Criteria criteria, String tableName) {
        BoundSql boundSql = SqlUtils.buildCountSql(entity, criteria, this.getNameHandler(), tableName);
        //return jdbcTemplate.queryForInt(boundSql.getSql(), boundSql.getParams().toArray());
        Integer ret = jdbcTemplate.queryForObject(boundSql.getSql(), Integer.class, boundSql.getParams().toArray());
        if(ret != null)
        	return ret.intValue();
        else
        	return 0;
    }

    public int queryCount(Object entity) {
        BoundSql boundSql = SqlUtils.buildCountSql(entity, null, this.getNameHandler(), null);
        //return jdbcTemplate.queryForInt(boundSql.getSql(), boundSql.getParams().toArray());
        Integer ret = jdbcTemplate.queryForObject(boundSql.getSql(), Integer.class, boundSql.getParams().toArray());
        if(ret != null)
        	return ret.intValue();
        else
        	return 0;
    }
    
    public int queryCount(Object entity, String tableName) {
        BoundSql boundSql = SqlUtils.buildCountSql(entity, null, this.getNameHandler(), tableName);
        //return jdbcTemplate.queryForInt(boundSql.getSql(), boundSql.getParams().toArray());
        Integer ret = jdbcTemplate.queryForObject(boundSql.getSql(), Integer.class, boundSql.getParams().toArray());
        if(ret != null)
        	return ret.intValue();
        else
        	return 0;
    }

    public int queryCount(Criteria criteria) {
        BoundSql boundSql = SqlUtils.buildCountSql(null, criteria, this.getNameHandler(), null);
        //return jdbcTemplate.queryForInt(boundSql.getSql(), boundSql.getParams().toArray());
        Integer ret = jdbcTemplate.queryForObject(boundSql.getSql(), Integer.class, boundSql.getParams().toArray());
        if(ret != null)
        	return ret.intValue();
        else
        	return 0;
    }
    
    public int queryCount(Criteria criteria, String tableName) {
        BoundSql boundSql = SqlUtils.buildCountSql(null, criteria, this.getNameHandler(), tableName);
        //return jdbcTemplate.queryForInt(boundSql.getSql(), boundSql.getParams().toArray());
        Integer ret = jdbcTemplate.queryForObject(boundSql.getSql(), Integer.class, boundSql.getParams().toArray());
        if(ret != null)
        	return ret.intValue();
        else
        	return 0;
    }

    public <T> T get(Class<T> clazz, Long id) {
        BoundSql boundSql = SqlUtils.buildByIdSql(clazz, id, null, this.getNameHandler(), null);

        //采用list方式查询，当记录不存在时返回null而不会抛出异常
        List<T> list = jdbcTemplate.query(boundSql.getSql(), this.getRowMapper(clazz), id);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.iterator().next();
    }
    
    public <T> T get(Class<T> clazz, Long id, String tableName) {
        BoundSql boundSql = SqlUtils.buildByIdSql(clazz, id, null, this.getNameHandler(), tableName);

        //采用list方式查询，当记录不存在时返回null而不会抛出异常
        List<T> list = jdbcTemplate.query(boundSql.getSql(), this.getRowMapper(clazz), id);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.iterator().next();
    }
    
    public <T> T get(Class<T> clazz, Integer id) {
        BoundSql boundSql = SqlUtils.buildByIdSql(clazz, id, null, this.getNameHandler(), null);

        //采用list方式查询，当记录不存在时返回null而不会抛出异常
        List<T> list = jdbcTemplate.query(boundSql.getSql(), this.getRowMapper(clazz), id);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.iterator().next();
    }
    
    public <T> T get(Class<T> clazz, Integer id, String tableName) {
        BoundSql boundSql = SqlUtils.buildByIdSql(clazz, id, null, this.getNameHandler(), tableName);

        //采用list方式查询，当记录不存在时返回null而不会抛出异常
        List<T> list = jdbcTemplate.query(boundSql.getSql(), this.getRowMapper(clazz), id);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.iterator().next();
    }

    public <T> T get(Criteria criteria, Long id) {
        BoundSql boundSql = SqlUtils.buildByIdSql(null, id, criteria, this.getNameHandler(), null);

        //采用list方式查询，当记录不存在时返回null而不会抛出异常
        List<T> list = (List<T>) jdbcTemplate.query(boundSql.getSql(),
            this.getRowMapper(criteria.getEntityClass()), id);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.iterator().next();
    }
    
    public <T> T get(Criteria criteria, Integer id){
    	BoundSql boundSql = SqlUtils.buildByIdSql(null, id, criteria, this.getNameHandler(), null);

        //采用list方式查询，当记录不存在时返回null而不会抛出异常
        List<T> list = (List<T>) jdbcTemplate.query(boundSql.getSql(),
            this.getRowMapper(criteria.getEntityClass()), id);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.iterator().next();
    }
    
    public <T> T get(Criteria criteria, Long id, String tableName) {
        BoundSql boundSql = SqlUtils.buildByIdSql(null, id, criteria, this.getNameHandler(), tableName);

        //采用list方式查询，当记录不存在时返回null而不会抛出异常
        List<T> list = (List<T>) jdbcTemplate.query(boundSql.getSql(),
            this.getRowMapper(criteria.getEntityClass()), id);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.iterator().next();
    }

    public <T> T querySingleResult(Criteria criteria) {
    	if(criteria != null){
    		criteria.limit(0, 1);
    	}
        BoundSql boundSql = SqlUtils.buildListSql(null, criteria, this.getNameHandler(), null);
        //采用list方式查询，当记录不存在时返回null而不会抛出异常
        List<?> list = jdbcTemplate.query(boundSql.getSql(), boundSql.getParams().toArray(),
                this.getRowMapper(criteria.getEntityClass()));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return (T) list.iterator().next();
    }
    
    public <T> T querySingleResult(Criteria criteria, String tableName) {
    	if(criteria != null){
    		criteria.limit(0, 1);
    	}
        BoundSql boundSql = SqlUtils.buildListSql(null, criteria, this.getNameHandler(), tableName);
        //采用list方式查询，当记录不存在时返回null而不会抛出异常
        List<?> list = jdbcTemplate.query(boundSql.getSql(), boundSql.getParams().toArray(),
            this.getRowMapper(criteria.getEntityClass()));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return (T) list.iterator().next();
    }

    public <T> T querySingleResult(T entity) {
        BoundSql boundSql = SqlUtils.buildQuerySql(entity, null, this.getNameHandler(), null);

        //采用list方式查询，当记录不存在时返回null而不会抛出异常
        List<?> list = jdbcTemplate.query(boundSql.getSql(), boundSql.getParams().toArray(),
            this.getRowMapper(entity.getClass()));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return (T) list.iterator().next();
    }
    
    public <T> T querySingleResult(T entity, String tableName) {
        BoundSql boundSql = SqlUtils.buildQuerySql(entity, null, this.getNameHandler(), tableName);

        //采用list方式查询，当记录不存在时返回null而不会抛出异常
        List<?> list = jdbcTemplate.query(boundSql.getSql(), boundSql.getParams().toArray(),
            this.getRowMapper(entity.getClass()));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return (T) list.iterator().next();
    }

    public byte[] getBlobValue(Class<?> clazz, String fieldName, Long id) {
        String primaryName = nameHandler.getPKName(clazz);
        String columnName = nameHandler.getColumnName(fieldName);
        String tableName = nameHandler.getTableName(clazz, null);
        String tmp_sql = "select t.%s from %s t where t.%s = ?";
        String sql = String.format(tmp_sql, columnName, tableName, primaryName);
        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<byte[]>() {
            public byte[] extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getBytes(1);
                }
                return null;
            }
        });
    }
    
    public byte[] getBlobValue(Class<?> clazz, String fieldName, Long id, String tableName) {
        String primaryName = nameHandler.getPKName(clazz);
        String columnName = nameHandler.getColumnName(fieldName);
        String genTableName = "";
        if(tableName != null && tableName.length() > 0)
        	genTableName = tableName;
        else
        	genTableName = nameHandler.getTableName(clazz, null);
        String tmp_sql = "select t.%s from %s t where t.%s = ?";
        String sql = String.format(tmp_sql, columnName, genTableName, primaryName);
        return jdbcTemplate.query(sql, new Object[] { id }, new ResultSetExtractor<byte[]>() {
            public byte[] extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getBytes(1);
                }
                return null;
            }
        });
    }

    /**
     * 获取rowMapper对象
     *
     * @param clazz
     * @return
     */
    protected <T> RowMapper<T> getRowMapper(Class<T> clazz) {

        if (StringUtils.isBlank(rowMapperClass)) {
            return BeanPropertyRowMapper.newInstance(clazz);
        } else {
            return (RowMapper<T>) ClassUtils.newInstance(rowMapperClass);
        }
    }

    /**
     * 获取名称处理器
     *
     * @return
     */
    protected NameHandler getNameHandler() {

        if (this.nameHandler == null) {
        	synchronized(this){
        		if(this.nameHandler == null)
        			this.nameHandler = new DefaultNameHandler();
        	}
        }
        return this.nameHandler;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setNameHandler(NameHandler nameHandler) {
        this.nameHandler = nameHandler;
    }

    public void setRowMapperClass(String rowMapperClass) {
        this.rowMapperClass = rowMapperClass;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
}
