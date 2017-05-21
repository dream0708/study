package com.sohu.bp.elite.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * 简单功能的DataSource的包装类 
 * 
 * create time: 2015年11月5日 下午4:24:40
 * @auther dexingyang
 */ 
public class SimpleDataSourceWrapper implements DataSource {  
  
    private Map<String, DataSource> dataSourceMap;  
  
    private PrintWriter logWriter;  
  
    private int loginTimeout = 3;  
  
    public DataSource getCurrentDataSource() {  
        DataSource ds = this.dataSourceMap.get(DataSourceStatus  
                .getCurrentDsKey());  
        if (ds == null) {  
            throw new RuntimeException("no datasource [ "  
                    + DataSourceStatus.getCurrentDsKey() + " ]");  
        }  
        return ds;  
    }  
  
    public void setDataSourceMap(Map<String, DataSource> dataSourceMap) {  
        this.dataSourceMap = dataSourceMap;  
    }  
  
    @Override  
    public Connection getConnection() throws SQLException {  
        return this.getCurrentDataSource().getConnection();  
    }  
  
    @Override  
    public Connection getConnection(String username, String password)  
            throws SQLException {  
        throw new SQLException("only support getConnection()");  
    }  
  
    @Override  
    public PrintWriter getLogWriter() throws SQLException {  
        return this.logWriter;  
    }  
  
    @Override  
    public int getLoginTimeout() throws SQLException {  
        return this.loginTimeout;  
    }  
  
    @Override  
    public void setLogWriter(PrintWriter out) throws SQLException {  
        this.logWriter = out;  
    }  
  
    @Override  
    public void setLoginTimeout(int seconds) throws SQLException {  
        this.loginTimeout = seconds;  
    }  
  
    @Override  
    public boolean isWrapperFor(Class<?> iface) throws SQLException {  
        return this.getCurrentDataSource().isWrapperFor(iface);  
    }  
  
    @Override  
    public <T> T unwrap(Class<T> iface) throws SQLException {  
        return this.getCurrentDataSource().unwrap(iface);  
    }

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}  
}  
