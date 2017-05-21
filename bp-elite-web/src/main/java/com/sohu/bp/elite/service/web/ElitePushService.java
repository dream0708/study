package com.sohu.bp.elite.service.web;

/**
 * 采用socketio来向前端推送消息的service类
 * @author zhijungou
 * 2017年1月11日
 */
public interface ElitePushService {
	/**
	 * 推送广场未读消息
	 */
	void squareUnreadPush();
}
