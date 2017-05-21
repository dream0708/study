package com.sohu.bp.elite.dao.impl;

import com.sohu.bp.elite.dao.EliteAnswerByBpIdDao;
import com.sohu.bp.elite.db.DbPartitionHelper;
import com.sohu.bp.elite.db.TablePartitioner;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteAnswerBybpid;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhihao
 *         2016/7/11
 */
public class EliteAnswerByBpIdDaoImpl extends JdbcDaoImpl implements EliteAnswerByBpIdDao {

    private TablePartitioner partitioner;

    public void setPartitioner(TablePartitioner partitioner) {
        this.partitioner = partitioner;
    }

    public String getTableName(Long bpId){
        return DbPartitionHelper.getTableName(partitioner, bpId);
    }
    @Override
    public Long save(EliteAnswerBybpid answerBybpid) {
        Long id = -1L;
        if(answerBybpid == null || answerBybpid.getBpId() == null || answerBybpid.getAnswerId() == null)
            return id;
        return super.insert(answerBybpid, getTableName(answerBybpid.getBpId()));
    }

    @Override
    public EliteAnswerBybpid get(Long bpId, Long answerId) {
        if(bpId == null || answerId == null)
            return null;
        Criteria criteria = Criteria.create(EliteAnswerBybpid.class).where("bpId", new Object[]{bpId}).and("answerId", new Object[]{answerId});
        return super.querySingleResult(criteria, getTableName(bpId));
    }

    @Override
    public boolean update(EliteAnswerBybpid answerBybpid) {
        if(answerBybpid == null || answerBybpid.getId() == null)
            return false;
        return super.update(answerBybpid, getTableName(answerBybpid.getBpId())) > 0;
    }

    @Override
    public List<Long> getAnswerIds(Long bpId) {
        if(bpId == null || bpId <= 0 )
            return null;
        Criteria criteria = Criteria.create(EliteAnswerBybpid.class).where("bpId", new Object[]{bpId}).and("status", "not in", new Object[]{EliteAnswerStatus.DEL.getValue(), EliteAnswerStatus.SYSDEL.getValue()});

        List<EliteAnswerBybpid> answerBybpidList = super.queryList(criteria, getTableName(bpId));
        List<Long> answerIds = null;
        if(answerBybpidList != null && answerBybpidList.size() > 0){
            answerIds = new ArrayList<>();
            for(EliteAnswerBybpid obj : answerBybpidList){
                answerIds.add(obj.getAnswerId());
            }
        }
        return answerIds;
    }

    @Override
    public List<Long> getUserAnswersByStatus(Long bpId, List<Integer> statusList) {
        Criteria criteria = Criteria.create(EliteAnswerBybpid.class).where("bpId", new Object[]{bpId}).and("status", "in", statusList.toArray());
        List<EliteAnswerBybpid> answerByBpIdList = super.queryList(criteria, getTableName(bpId));
        List<Long> answerIds = new ArrayList<>();
        if(null != answerByBpIdList) {
            answerByBpIdList.forEach(answerByBpId -> answerIds.add(answerByBpId.getAnswerId()));
        }
        return answerIds;
    }

    @Override
    public int getUserAnswerNumByStatus(Long bpId, List<Integer> statusList) {
        Criteria criteria = Criteria.create(EliteAnswerBybpid.class).where("bpId", new Object[]{bpId}).and("status", "in", statusList.toArray());
        List<EliteAnswerBybpid> answerByBpIdList = super.queryList(criteria, getTableName(bpId));
        if (null == answerByBpIdList) {
            return  0;
        }
        return answerByBpIdList.size();
    }
}
