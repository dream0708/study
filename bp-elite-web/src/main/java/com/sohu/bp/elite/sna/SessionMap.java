package com.sohu.bp.elite.sna;

import java.util.HashMap;
import java.util.Map;

/**
 * create time: 2013-5-29 下午2:52:49
 * @author: dexingyang
 */
public class SessionMap extends HashMap {

	private static final long serialVersionUID = 1L;
	
	private boolean isModified = false;
	private boolean isClear = false;
	private boolean isNew = false;
	private long lastAccessedTime = 0;
	private String sessionId = null;
	
	public SessionMap(){
		isNew = true;
		lastAccessedTime = System.currentTimeMillis();
	}
	
	public boolean isClear() {
		return isClear;
	}

	public boolean isModified() {
		return isModified;
	}

	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}

	public void clear() {
		super.clear();
		isClear = true;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void clearStatus(){
		isModified = false;
		isClear = false;
		isNew = false;
	}

	public Object put(Object key, Object value) {
		setModified(true);
		return super.put(key, value);
	}

	public void putAll(Map values) {
		super.putAll(values);
		setModified(true);
	}

	public Object remove(Object key) {
		setModified(true);
		return super.remove(key);
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}
}
