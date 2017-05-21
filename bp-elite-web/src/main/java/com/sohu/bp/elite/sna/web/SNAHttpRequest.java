package com.sohu.bp.elite.sna.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import com.sohu.bp.elite.sna.SessionMap;


/**
 * create time: 2014年2月14日 下午6:56:56
 * @author dexingyang
 */
public class SNAHttpRequest extends HttpServletRequestWrapper {

    private String sessionId;
    private SNASession session;
    
    public SNAHttpRequest(HttpServletRequest originalRequest, 
            String sessionId, SessionMap map){
        super(originalRequest);
        this.sessionId = sessionId;
        session = new SNASession(sessionId,map);
        session.setServletContext(originalRequest.getSession(false).getServletContext());
    }
    
    public HttpSession getSession(){
        if(null == session){
            session = new SNASession(sessionId,null);
        } else {
            //session的过期通过缓存来实现
            //if(System.currentTimeMillis() > (session.getLastAccessedTime()+genMaxInactiveMillis(session))){
            //    session = new SNASession(sessionId,null);
            //}
            session.setOldInstance();
        }
        
        session.setLastAccessTime(System.currentTimeMillis());
        return session;
    }
    
    public HttpSession getSession(boolean create){
        return getSession();
    }
    
    public String getSessionId(){
        return sessionId;
    }
    
    public void setSessionId(String id){
        sessionId = id;
    }
    
    private long genMaxInactiveMillis(HttpSession session) {
        //half an hour for max timeout value
        long maxInactive = 30*60*1000;
        if(session.getMaxInactiveInterval() > 0) {
            maxInactive = session.getMaxInactiveInterval() * 1000;
        }
        return maxInactive;
    }
    
    public SessionMap getSessionMap(){
        return session.getSessionMap();
    }
}
