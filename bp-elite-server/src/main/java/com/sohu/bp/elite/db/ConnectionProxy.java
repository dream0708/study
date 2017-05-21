package com.sohu.bp.elite.db;

import java.sql.Connection;

/**
 * Connection代理,不产生实际的connection资源 
 * 
 * create time: 2015年11月5日 下午4:25:37
 * @auther dexingyang
 */ 
public interface ConnectionProxy extends Connection {  
  
    /** 
     * 获得当前使用的Connection 
     *  
     * @return 
     * @see Connection 
     */  
    Connection getCurrentConnection();  
}  
