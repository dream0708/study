package com.sohu.bp.elite.dao;

import java.util.List;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.persistence.EliteUser;

/**
 * 
 * @author zhijungou
 * 2016年10月20日
 */
public interface EliteUserDao {

    /**
     * 保存用户信息
     * @param user
     * @return
     */
    Long save(EliteUser user);

    /**
     * 更新用户信息
     * @param user
     */
    boolean update(EliteUser user);

    /**
     * 根据bp id 获取用户信息
     * @param bpId
     * @return
     */
    EliteUser get(Long bpId);
    
    /**
     * 根据登录时间来获取问答用户
     * @param start
     * @param count
     * @return
     */
    ListResult getAuditingExperts(Integer start, Integer count);
    
    /**
     * 获取专家列表
     * @param start
     * @param count
     * @return
     */
    ListResult getExperts(Integer start, Integer count);
    /**
     * 判断是bpId是否是专家
     * @param bpId
     * @return
     */
    EliteUser getExpert(Long bpId);
    /**
     * 获取专家数量
     * @return
     */
    Long getExpertsNum();
    /**
     * 清除专家缓存
     */
    void removeExpertsCache();
    /**
     * 提交专家申请
     * @param bpId
     * @return
     */
    Boolean addExpertAuditing(Long bpId);
    /**
     * 将专家添加到缓存列表中
     * @param bpId
     */
    void addExpertCache(Long bpId);
}
