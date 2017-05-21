package com.sohu.bp.elite.data.statistic.service;

/**
 * create time: 2016年7月4日 上午9:54:51
 * @auther dexingyang
 */
public interface EmailService {

	boolean send(String from, String[] to, String subject, String text);
}
