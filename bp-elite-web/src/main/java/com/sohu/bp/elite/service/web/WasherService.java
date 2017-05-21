package com.sohu.bp.elite.service.web;

import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;

/**
 * 
 * @author nicholastang
 * 2016-10-12 15:34:27
 * TODO 干脏活的类
 */
public interface WasherService
{
	/**
	 * 另存外域图片
	 * @param question & answer
	 */
	public void resaveImage(Long id, BpType bpType);
	
	
	/**
	 * 另存外域图片
	 * @param htmlBuilder
	 * @return
	 */
	public boolean resaveImage(StringBuilder htmlBuilder);


	/**
	 * 对getResaveContent的封装
	 * @param id
	 * @param bpType
	 */
	public boolean resaveContent(Long id, BpType bpType, String ip);

	/**
	 * 存储外域图片，获取@内容
	 * @param htmlBuilder
	 * @return
	 */
	public boolean resaveContent(StringBuilder htmlBuilder, long id, BpType bpType);
}