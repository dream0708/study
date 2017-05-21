package com.sohu.bp.elite.sna.util;

import com.sohu.bp.elite.sna.SessionMap;



/**
 * create time: 2013-5-31 下午5:41:42
 * @author: dexingyang
 */
public class SessionUtil {
	/** 用来存储当前请求线程中的SessionMap数据，以便数据在filter和interceptor中共享*/
	private static final ThreadLocal<SessionMap> sessionLocal = new ThreadLocal<SessionMap>();
	
	public static boolean isSessionSaved(){
		SessionMap session = getSessionMap();
		if(session == null)
			return true;
		else
			return false;
	}
	
	public static void addSessionMap(SessionMap session){
		sessionLocal.set(session);
	}
	
	public static SessionMap getSessionMap(){
		return sessionLocal.get();
	}
	
	public static String getId(){
		return sessionLocal.get().getSessionId();
	}
	
	public static void clear(){
		sessionLocal.remove();
	}
}
