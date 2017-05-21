package com.sohu.bp.elite.sna.web;

import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.sohu.bp.elite.sna.SessionMap;

/**
 * create time: 2014年2月14日 下午8:56:07
 * @author dexingyang
 */
public class SNASession implements HttpSession {

    private SessionMap map;
    
    private String sessionId;
    
    private long creationTime;
    
    private long lastAccessedTime;
    
    private int maxInactiveIntervalSecond = -1;
    
    private boolean isNewSession;
    
    private transient ServletContext sevletContext;

    private boolean isValid;
    
    public SNASession(String sessionId,SessionMap map){
        super();
        this.sessionId = sessionId;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = creationTime;
        this.isNewSession = true;
        isValid = true;
        if(map == null){
            this.map = new SessionMap();
            this.map.setSessionId(sessionId);
            this.map.clearStatus();
        } 
        else
            this.map = map;
    }
    
    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getId() {
        return this.sessionId;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return this.sevletContext;
    }
    
    void setServletContext(ServletContext servletContext) {
        this.sevletContext = servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        if (interval < 0) {
            maxInactiveIntervalSecond = Integer.MAX_VALUE / 2;
        } else {
            maxInactiveIntervalSecond = interval;
        }
    }

    @Override
    public int getMaxInactiveInterval() {
        if (Integer.MAX_VALUE / 2 <= maxInactiveIntervalSecond){
            return -1;
        } else {
            return maxInactiveIntervalSecond;
        }
    }

    @Override
    public HttpSessionContext getSessionContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        lastAccessedTime = System.currentTimeMillis();
        map.setLastAccessedTime(lastAccessedTime);
        return map.get(name);
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        return Collections.enumeration(map.keySet());
    }

    @Override
    public String[] getValueNames() {
        lastAccessedTime = System.currentTimeMillis();
        if(map == null){
            return new String[0];
        }
        
        String[] s = new String[map.size()];
        Enumeration e = getAttributeNames();
        int count = 0;
        while(e.hasMoreElements()){
            s[count++] = (String)e.nextElement();
        }
        
        return s;
    }

    @Override
    public void setAttribute(String name, Object value) {
        lastAccessedTime = System.currentTimeMillis();
        map.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name,value);
    }

    @Override
    public void removeAttribute(String name) {
        map.remove(name);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        map.clear();
        setValid(false);
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean isValid(){
        return isValid;
    }
    
    @Override
    public boolean isNew() {
        return this.isNewSession;
    }

    void setLastAccessTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }
    
    void setOldInstance(){
        this.isNewSession = false;
    }
    
    public SessionMap getSessionMap(){
        return this.map;
    }
}
