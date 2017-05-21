package com.sohu.bp.elite.jdbc;

import java.util.List;

/**
 * jdbc操作dao
 *
 * 
 */
public interface JdbcDao {

    /**
     * 插入一条记录 自动处理主键
     *
     * @param entity
     * @return
     */
    public Long insert(Object entity);
    
    /**
     * 插入一条记录 自动处理主键,可设置分表名称
     *
     * @param entity
     * @param tableName
     * @return
     */
    public Long insert(Object entity, String tableName);

    /**
     * 插入一条记录 自动处理主键
     *
     * @param criteria the criteria
     * @return long long
     */
    public Long insert(Criteria criteria);
    
    /**
     * 插入一条记录 自动处理主键,可设置分表名称
     * @param criteria the criteria
     * @param tableName
     * @return
     */
    public Long insert(Criteria criteria, String tableName);

    /**
     * 保存一条记录，不处理主键，返回受影响的行数
     *
     * @param entity
     */
    public int save(Object entity);
    
    /**
     * 保存一条记录，不处理主键， 可设置分表名称，返回受影响的行数
     * @param entity
     * @param tableName
     */
    public int save(Object entity, String tableName);

    /**
     * 保存一条记录，不处理主键，返回受影响的行数
     *
     * @param criteria the criteria
     */
    public int save(Criteria criteria);
    
    /**
     * 保存一条记录，不处理主键， 可设置分表名称，返回受影响的行数
     * @param criteria the criteria
     * @param tableName
     */
    public int save(Criteria criteria, String tableName);

    /**
     * 根据Criteria更新，返回受影响的行数
     *
     * @param criteria the criteria
     */
    public int update(Criteria criteria);
    
    /**
     * 根据Criteria更新， 可设置分表名称，返回受影响的行数
     *
     * @param criteria the criteria
     * @param tableName
     */
    public int update(Criteria criteria, String tableName);

    /**
     * 根据实体更新，返回受影响的行数
     *
     * @param entity the entity
     */
    public int update(Object entity);
    
    /**
     * 根据实体更新，可设置分表名称，返回受影响的行数
     *
     * @param entity the entity
     * @param tableName
     */
    public int update(Object entity, String tableName);

    /**
     * 根据Criteria删除，返回受影响的行数
     *
     * @param criteria the criteria
     */
    public int delete(Criteria criteria);
    
    /**
     * 根据Criteria删除，可设置分表名称，返回受影响的行数
     *
     * @param criteria the criteria
     * @param tableName
     */
    public int delete(Criteria criteria, String tableName); 

    /**
     * 删除记录 此方法会以实体中不为空的字段为条件，返回受影响的行数
     *
     * @param entity
     */
    public int delete(Object entity);
    
    /**
     * 删除记录 此方法会以实体中不为空的字段为条件，可设置分表名称，返回受影响的行数
     *
     * @param entity
     * @param tableName
     */
    public int delete(Object entity, String tableName);

    /**
     * 删除记录，返回受影响的行数
     *
     * @param clazz the clazz
     * @param id the id
     */
    public int delete(Class<?> clazz, Long id);
    
    /**
     * 删除记录，可设置分表名称，返回受影响的行数
     *
     * @param clazz the clazz
     * @param id the id
     * @param tableName
     */
    public int delete(Class<?> clazz, Long id, String tableName);

    /**
     * 删除所有记录(TRUNCATE ddl权限)
     *
     * @param clazz the clazz
     */
    public void deleteAll(Class<?> clazz);
    
    /**
     * 删除所有记录(TRUNCATE ddl权限),可设置分表名称
     *
     * @param clazz the clazz
     * @param tableName
     */
    public void deleteAll(Class<?> clazz, String tableName);

    /**
     * 按设置的条件查询
     *
     * @param <T>  the type parameter
     * @param criteria the criteria
     * @return list
     */
    public <T> List<T> queryList(Criteria criteria);
    
    /**
     * 按设置的条件查询, 可设置分表名称
     *
     * @param <T>  the type parameter
     * @param criteria the criteria
     * @param tableName
     * @return list
     */
    public <T> List<T> queryList(Criteria criteria, String tableName);

    /**
     * 按设置的条件查询
     *
     * @param <T>  the type parameter
     * @param clazz the clazz
     * @return list
     */
    public <T> List<T> queryList(Class<?> clazz);
    
    /**
     * 按设置的条件查询,可设置分表名称
     *
     * @param <T>  the type parameter
     * @param clazz the clazz
     * @param tableName
     * @return list
     */
    public <T> List<T> queryList(Class<?> clazz, String tableName);

    /**
     * 查询列表
     *
     * @param entity the entity
     * @return the list
     */
    public <T> List<T> queryList(T entity);
    
    /**
     * 查询列表,可设置分表名称
     *
     * @param entity the entity
     * @param tableName
     * @return the list
     */
    public <T> List<T> queryList(T entity, String tableName);

    /**
     * 查询列表
     *
     * @param <T>  the type parameter
     * @param entity the entity
     * @param criteria the criteria
     * @return the list
     */
    public <T> List<T> queryList(T entity, Criteria criteria);
    
    /**
     * 查询列表,可设置分表名称
     *
     * @param <T>  the type parameter
     * @param entity the entity
     * @param criteria the criteria
     * @param tableName
     * @return the list
     */
    public <T> List<T> queryList(T entity, Criteria criteria, String tableName);

    /**
     * 查询记录数
     *
     * @param entity
     * @return
     */
    public int queryCount(Object entity);
    
    /**
     * 查询记录数,可设置分表名称
     *
     * @param entity
     * @param tableName
     * @return
     */
    public int queryCount(Object entity, String tableName);

    /**
     * 查询记录数
     *
     * @param criteria the criteria
     * @return int int
     */
    public int queryCount(Criteria criteria);
    
    /**
     * 查询记录数,可设置分表名称
     *
     * @param criteria the criteria
     * @param tableName
     * @return int int
     */
    public int queryCount(Criteria criteria, String tableName);

    /**
     * 查询记录数
     *
     * @param entity the entity
     * @param criteria the criteria
     * @return int int
     */
    public int queryCount(Object entity, Criteria criteria);
    
    /**
     * 查询记录数,可设置分表名称
     *
     * @param entity the entity
     * @param criteria the criteria
     * @param tableName
     * @return int int
     */
    public int queryCount(Object entity, Criteria criteria, String tableName);

    /**
     * 根据主键得到记录
     *
     * @param <T>  the type parameter
     * @param clazz the clazz
     * @param id the id
     * @return t
     */
    public <T> T get(Class<T> clazz, Long id);
    
    /**
     * 根据主键得到记录,可设置分表名称
     *
     * @param <T>  the type parameter
     * @param clazz the clazz
     * @param id the id
     * @param tableName
     * @return t
     */
    public <T> T get(Class<T> clazz, Long id, String tableName);

    /**
     * 根据主键得到记录
     *
     * @param <T>  the type parameter
     * @param criteria the criteria
     * @param id the id
     * @return t
     */
    public <T> T get(Criteria criteria, Long id);
    
    public <T> T get(Criteria criteria, Integer id);
    
    /**
     * 根据主键得到记录,可设置分表名称
     *
     * @param <T>  the type parameter
     * @param criteria the criteria
     * @param id the id
     * @param tableName
     * @return t
     */
    public <T> T get(Criteria criteria, Long id, String tableName);

    /**
     * 查询单个记录
     *
     * @param <T>   the type parameter
     * @param entity the entity
     * @return t t
     */
    public <T> T querySingleResult(T entity);
    
    /**
     * 查询单个记录,可设置分表名称
     *
     * @param <T>   the type parameter
     * @param entity the entity
     * @param tableName
     * @return t t
     */
    public <T> T querySingleResult(T entity, String tableName);

    /**
     * 查询单个记录
     *
     * @param <T>     the type parameter
     * @param criteria the criteria
     * @return t t
     */
    public <T> T querySingleResult(Criteria criteria);
    
    /**
     * 查询单个记录,可设置分表名称
     *
     * @param <T>     the type parameter
     * @param criteria the criteria
     * @param tableName
     * @return t t
     */
    public <T> T querySingleResult(Criteria criteria, String tableName);

    /**
     * 查询blob字段值
     *
     * @param clazz
     * @param fieldName
     * @param id
     * @return
     */
    public byte[] getBlobValue(Class<?> clazz, String fieldName, Long id);
    
    /**
     * 查询blob字段值,可设置分表名称
     *
     * @param clazz
     * @param fieldName
     * @param id
     * @param tableName
     * @return
     */
    public byte[] getBlobValue(Class<?> clazz, String fieldName, Long id, String tableName);
}
