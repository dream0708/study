package com.sohu.bp.elite.service;

import java.util.List;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.persistence.EliteUser;


/**
 * @author zhangzhihao
 *         2016/7/14
 */
public interface EliteUserService {

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
     * 根据bp id 得到用户信息
     * @param bpId
     * @return
     */
    EliteUser get(Long bpId);
    /**
     * 获取待审核的专家列表
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
     * 获取专家
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
     * 清除专家团缓存并重新载入
     */
    void reloadExpertsCache();
    /**
     * 同意专家
     * @param bpId
     * @return
     */
    Boolean passExpert(Long bpId);
    /**
     * 拒绝专家
     * @param bpId
     * @return
     */
    Boolean rejectExpert(Long bpId, String reason);
    /**
     * 批量审核专家
     * @param passIds
     * @param rejectIds
     * @param reason
     * @return
     */
    Boolean batchAudit(List<Long> passIds, List<Long> rejectIds, String reason);
    /**
     * 提交专家申请
     * @param bpId
     * @return
     */
    Boolean addExpertAuditing(Long bpId);
}
