package com.sohu.bp.elite.service.web;

import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;

/**
 * 
 * @author nicholastang
 * 2016-08-17 17:55:54
 * TODO 和bp user相关的service
 */
public interface BpUserService
{
	/**
	 * 获取 bpuser主页
	 * @param bpid
	 * @return
	 */
	public String getUserHomeUrl(Long bpid);

	/**
	 * 根据bpId获取用户详情信息
	 * @param bpId
	 * @return
     */
	UserDetailDisplayBean getUserDetailByBpId(long bpid, Long viewerId);
	/**
	 * 根据bpId获取用户详情信息
	 * @param bpid
	 * @param viewerId
	 * @param needTag
	 * @return
	 */
	UserDetailDisplayBean getUserDetailByBpId(long bpid, Long viewerId, boolean needTag);

	/**
	 *
	 * @param bpId
	 * @param quesitonId
	 * @return true  已经回答了该问题
	 * 		   false 没有回答该问题
     */
	boolean checkHasAnswered(Long bpId, Long questionId);
	/**
	 * 
	 * @param bpId
	 * @param questionId
	 * @return
	 */
	boolean checkHasChoosed(Long bpId, Long questionId);
	   /**
     * 
     * @param bpId
     * @param questionId
     * @return
     */
    Integer getChoosedOption(Long bpId, Long questionId);

	/**
	 * 查看该用户是否有手机号
	 * @param bpId
	 * @return true  有手机号
	 * 		   false 没有手机号
     */
	boolean checkHasPhoneNo(Long bpId);
	
	/**
	 * 根据bpId获取用户简单的信息
	 * @param bpId
	 * @return
	 */
	UserDetailDisplayBean getUserSimpleByBpId(Long bpid);
	/**
	 * 根据bpId获取用户最基本信息，用于app，加速
	 * @param bpid
	 * @return
	 */
	UserDetailDisplayBean getUserBaseByBpId(Long bpid);
	/**
	 * 根据bpid来判断用户是否进入黑名单
	 * @param bpid
	 * @return
	 */
	boolean checkNotInBlackList(Long bpid);
}