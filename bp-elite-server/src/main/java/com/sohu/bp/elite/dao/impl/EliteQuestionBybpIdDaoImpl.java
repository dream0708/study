package com.sohu.bp.elite.dao.impl;

import com.sohu.bp.elite.dao.EliteQuestionBybpIdDao;
import com.sohu.bp.elite.db.DbPartitionHelper;
import com.sohu.bp.elite.db.TablePartitioner;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteQuestionBybpid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhihao
 *         2016/7/8
 */
public class EliteQuestionBybpIdDaoImpl extends JdbcDaoImpl implements EliteQuestionBybpIdDao{

    private Logger log = LoggerFactory.getLogger(EliteQuestionBybpIdDaoImpl.class);

    private TablePartitioner partitioner;

    public void setPartitioner(TablePartitioner partitioner) {
        this.partitioner = partitioner;
    }

    public String getTableName(Long id){
        return DbPartitionHelper.getTableName(partitioner, id);
    }

    @Override
    public Long save(EliteQuestionBybpid questionBybpid) {
        if(questionBybpid == null || questionBybpid.getBpId() == null)
            return -1L;
        return super.insert(questionBybpid, getTableName(questionBybpid.getBpId()));
    }

    @Override
    public EliteQuestionBybpid get(Long bpId, Long questionId) {
        if(bpId == null || questionId == null)
            return null;
        Criteria criteria = Criteria.create(EliteQuestionBybpid.class).where("bpId", new Object[]{bpId}).and("questionId", new Object[]{questionId});
        return super.querySingleResult(criteria, getTableName(bpId));
    }

    @Override
    public boolean update(EliteQuestionBybpid questionBybpid) {
        if(questionBybpid == null || questionBybpid.getId() == null)
            return false;
        return super.update(questionBybpid, getTableName(questionBybpid.getBpId())) > 0;
    }

    @Override
    public List<Long> getQuestionIds(Long bpId) {
        if(bpId == null)
            return null;
        Criteria criteria = Criteria.create(EliteQuestionBybpid.class).where("bpId", new Object[]{bpId}).and("status", "not in", new Object[]{EliteQuestionStatus.DEL.getValue(), EliteQuestionStatus.SYSDEL.getValue()});

        List<Long> questionIds = null;
        List<EliteQuestionBybpid> questionBybpidList = super.queryList(criteria, getTableName(bpId));
        if(questionBybpidList != null && questionBybpidList.size() > 0){
            questionIds = new ArrayList<>();
            for(EliteQuestionBybpid obj : questionBybpidList) {
                questionIds.add(obj.getQuestionId());
            }
        }

        return questionIds;
    }

    @Override
    public List<Long> getQuestionsIdsByBpIdAndStatus(Long bpId, Integer status) {
        if(bpId == null)
            return null;
        Criteria criteria = Criteria.create(EliteQuestionBybpid.class).where("bpId", new Object[]{bpId}).and("status", new Object[]{status});
        List<Long> questionIds = null;
        List<EliteQuestionBybpid> questionBybpidList = super.queryList(criteria, getTableName(bpId));
        if(questionBybpidList != null && questionBybpidList.size() > 0){
            questionIds = new ArrayList<>();
            for(EliteQuestionBybpid obj : questionBybpidList) {
                questionIds.add(obj.getQuestionId());
            }
        }

        return questionIds;
    }

    @Override
    public List<Long> getUserQuestionsByStatus(Long bpId, List<Integer> statusList) {
        Criteria criteria = Criteria.create(EliteQuestionBybpid.class).where("bpId", new Object[]{bpId}).and("status", "in", statusList.toArray());
        List<Long> questionIds = new ArrayList<>();
        List<EliteQuestionBybpid> questionByBpIdList = super.queryList(criteria, getTableName(bpId));
        if(questionByBpIdList != null){
            questionByBpIdList.forEach(questionByBpId -> questionIds.add(questionByBpId.getQuestionId()));
        }

        return questionIds;
    }

    @Override
    public int getUserQuestionNumByStatus(Long bpId, List<Integer> statusList) {
        Criteria criteria = Criteria.create(EliteQuestionBybpid.class).where("bpId", new Object[]{bpId}).and("status", "in", statusList.toArray());
        List<EliteQuestionBybpid> questionByBpIdList = super.queryList(criteria, getTableName(bpId));
        if (null == questionByBpIdList) {
            return 0;
        }
        return questionByBpIdList.size();
    }

}
