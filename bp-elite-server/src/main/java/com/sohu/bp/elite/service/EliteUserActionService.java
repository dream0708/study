package com.sohu.bp.elite.service;

/**
 * web端用户行为逻辑封装
 * @author zhijungou
 * 2017年3月28日
 */
public interface EliteUserActionService {
    /**
     * 点赞回答
     * @param answerId
     * @param bpId
     * @param ip
     * @param port
     * @param time
     * @param messageFlag 是否发消息
     */
    boolean likeAnswer(Long answerId, Long bpId, Long ip, Integer port, Long time, boolean messageFlag, boolean feedFlag);
    /**
     * 取消点赞回答
     * @param answerId
     * @param bpId
     * @return
     */
    boolean unlikeAnswer(Long answerId, Long bpId);
    /**
     * 踩回答
     * @param answerId
     * @param bpId
     * @param ip
     * @param port
     * @param time
     * @return
     */
    boolean treadAnswer(Long answerId, Long bpId, Long ip, Integer port, Long time);
    /**
     * 取消踩
     * @param answerId
     * @param bpId
     * @return
     */
    boolean unTreadAnswer(Long answerId, Long bpId);
    /**
     * 收藏回答
     * @param answerId
     * @param bpId
     * @param ip
     * @param port
     * @param time
     * @param messageFlag
     * @return
     */
    boolean favoriteAnswer(Long answerId, Long bpId, Long ip, Integer port, Long time, boolean messageFlag, boolean feedFlag);
    /**
     * 取消收藏
     * @param answerId
     * @param bpId
     * @return
     */
    boolean unfavoriteAnswer(Long answerId, Long bpId);
    /**
     * 收藏目标
     * @param targetId
     * @param targetType
     * @param bpId
     * @param ip
     * @param port
     * @param time
     * @return
     */
    boolean favoriteTarget(Long targetId, Integer targetType, Long bpId, Long ip, Integer port, Long time);
    /**
     * 取消收藏目标
     * @param targetId
     * @param targetType
     * @param bpId
     * @return
     */
    boolean unfavoriteTarget(Long targetId, Integer targetType, Long bpId);
    /**
     * 关注问题
     * @param questionId
     * @param bpId
     * @param ip
     * @param port
     * @param time
     * @param messageFlag
     * @return
     */
    boolean followQuestion(Long questionId, Long bpId, Long ip, Integer port, Long time, boolean messageFlag, boolean feedFlag);
    /**
     * 取消关注问题
     * @param questionId
     * @param bpId
     * @param ip
     * @param port
     * @param time
     * @return
     */
    boolean unfollowQuestion(Long questionId, Long bpId, Long ip, Integer port, Long time);
    /**
     * 关注用户
     * @param followedId 被关注用户的id
     * @param followId 关注用户的id
     * @param ip
     * @param port
     * @param time
     * @param messageFlag
     * @return
     */
    boolean followPeople(Long followedId, Long followId, Long ip, Integer port, Long time, boolean messageFlag);
    /**
     * 取消关注用户
     * @param followedId
     * @param followId
     * @param ip
     * @param port
     * @param time
     * @return
     */
    boolean unfollowPeople(Long followedId, Long followId, Long ip, Integer port, Long time);
    /**
     * 
     * @param answerId
     * @param bpId
     * @param content
     * @param ip
     * @param port
     * @param time
     * @return
     */
    boolean commentAnswer(Long answerId, Long bpId, String content, Long ip, Integer port, Long time, boolean messageFlag, boolean feedFlag);
}
