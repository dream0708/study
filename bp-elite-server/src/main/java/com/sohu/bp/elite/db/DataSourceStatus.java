package com.sohu.bp.elite.db;

/**
 * 保存当前使用的数据源key
 * 
 * create time: 2015年11月5日 下午4:24:04
 * @auther dexingyang
 */ 
public class DataSourceStatus {  
  
    private static final ThreadLocal<String> currentDsKey = new ThreadLocal<String>();  
  
    public static void setCurrentDsKey(String dsKey) {  
        currentDsKey.set(dsKey);  
    }  
  
    public static String getCurrentDsKey() {  
        return currentDsKey.get();  
    }  
}  
