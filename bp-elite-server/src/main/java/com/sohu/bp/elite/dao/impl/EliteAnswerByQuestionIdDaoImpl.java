package com.sohu.bp.elite.dao.impl;

import com.sohu.bp.elite.dao.EliteAnswerByQuestionIdDao;
import com.sohu.bp.elite.db.DbPartitionHelper;
import com.sohu.bp.elite.db.TablePartitioner;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteAnswerByquestionid;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhihao
 *         2016/7/11
 */
public class EliteAnswerByQuestionIdDaoImpl  extends JdbcDaoImpl implements EliteAnswerByQuestionIdDao {

    private TablePartitioner partitioner;

    public void setPartitioner(TablePartitioner partitioner) {
        this.partitioner = partitioner;
    }

    public String getTableName(Long questionId){
        return DbPartitionHelper.getTableName(partitioner, questionId);
    }

    @Override
    public Long save(EliteAnswerByquestionid answerByquestionid) {
        Long id = -1L;
        if(answerByquestionid == null || answerByquestionid.getAnswerId() == null || answerByquestionid.getQuestionId() == null)
            return id;
        return super.insert(answerByquestionid, getTableName(answerByquestionid.getQuestionId()));
    }

    @Override
    public EliteAnswerByquestionid get(Long questionId, Long answerId) {
        if(questionId == null || answerId == null)
            return null;
        Criteria criteria = Criteria.create(EliteAnswerByquestionid.class).where("questionId", new Object[]{questionId}).and("answerId", new Object[]{answerId});
        return super.querySingleResult(criteria, getTableName(questionId));
    }

    @Override
    public boolean update(EliteAnswerByquestionid answerByquestionid) {
        if(answerByquestionid == null || answerByquestionid.getId() == null)
            return false;
        return super.update(answerByquestionid, getTableName(answerByquestionid.getQuestionId())) > 0;
    }

    @Override
    public List<Long> getAnswerIds(Long questionId) {
        if(questionId == null || questionId <= 0)
            return null;
        Criteria criteria = Criteria.create(EliteAnswerByquestionid.class).where("questionId", new Object[]{questionId}).and("status", "not in", new Object[]{EliteQuestionStatus.DEL.getValue(), EliteQuestionStatus.SYSDEL.getValue()});

        List<EliteAnswerByquestionid> answerByquestionidList = super.queryList(criteria, getTableName(questionId));
        List<Long> answerIds = null;
        if(answerByquestionidList != null && answerByquestionidList.size() > 0){
            answerIds = new ArrayList<>();
            for(EliteAnswerByquestionid obj : answerByquestionidList){
                answerIds.add(obj.getAnswerId());
            }
        }
        return answerIds;
    }

    @Override
    public List<Long> getQuestionAnswersByStatus(Long questionId, List<Integer> statusList) {
        Criteria criteria = Criteria.create(EliteAnswerByquestionid.class).where("questionId", new Object[]{questionId}).and("status", "in", statusList.toArray());
        List<EliteAnswerByquestionid> answerByQuestionIdList = super.queryList(criteria, getTableName(questionId));
        List<Long> answerIds = new ArrayList<>();
        if(null != answerByQuestionIdList) {
            answerByQuestionIdList.forEach(answerByQuestionId -> answerIds.add(answerByQuestionId.getAnswerId()));
        }
        return answerIds;
    }

    @Override
    public int getQuestionAnswerNumByStatus(Long questionId, List<Integer> statusList) {
        Criteria criteria = Criteria.create(EliteAnswerByquestionid.class).where("questionId", new Object[]{questionId}).and("status", "in", statusList.toArray());
        List<EliteAnswerByquestionid> answerByQuestionIdList = super.queryList(criteria, getTableName(questionId));
        if (null == answerByQuestionIdList) {
            return 0;
        }
        return answerByQuestionIdList.size();
    }


}
