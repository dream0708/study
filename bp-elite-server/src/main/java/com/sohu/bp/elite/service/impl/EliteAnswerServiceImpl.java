package com.sohu.bp.elite.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sohu.bp.decoration.adapter.BpDecorationEtcServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.elite.util.ContentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.dao.EliteAnswerByBpIdDao;
import com.sohu.bp.elite.dao.EliteAnswerByQuestionIdDao;
import com.sohu.bp.elite.dao.EliteAnswerDao;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteNotifyType;
import com.sohu.bp.elite.persistence.EliteAnswer;
import com.sohu.bp.elite.service.EliteAnswerService;
import com.sohu.bp.elite.service.EliteSearchService;
import com.sohu.bp.elite.service.NotifyService;

import cn.focus.rec.collector.RecLogHelper;
import cn.focus.rec.collector.RecLogger;
import cn.focus.rec.enums.BehaviorType;
import cn.focus.rec.log.BehaviorLog;
import cn.focus.rec.model.ItemType;

/**
 * @author zhangzhihao
 *         2016/7/11
 */
public class EliteAnswerServiceImpl implements EliteAnswerService {

    private Logger log = LoggerFactory.getLogger(EliteAnswerServiceImpl.class);
    private static final BpDecorationEtcServiceAdapter etcServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationEtcServiceAdapter();

    private EliteAnswerDao answerDao;

    private EliteAnswerByBpIdDao answerByBpIdDao;

    private EliteAnswerByQuestionIdDao answerByQuestionIdDao;

    private EliteSearchService searchService;
    
    private NotifyService notifyService;

    public void setAnswerDao(EliteAnswerDao answerDao) {
        this.answerDao = answerDao;
    }

    public void setAnswerByBpIdDao(EliteAnswerByBpIdDao answerByBpIdDao) {
        this.answerByBpIdDao = answerByBpIdDao;
    }

    public void setAnswerByQuestionIdDao(EliteAnswerByQuestionIdDao answerByQuestionIdDao) {
        this.answerByQuestionIdDao = answerByQuestionIdDao;
    }

    public void setSearchService(EliteSearchService searchService) {
        this.searchService = searchService;
    }

    public NotifyService getNotifyService() {
		return notifyService;
	}

	public void setNotifyService(NotifyService notifyService) {
		this.notifyService = notifyService;
	}

	@Override
    public Long insert(EliteAnswer answer, boolean securityFlag) {
        try {
            String checkContent = ContentUtil.getPlainText(answer.getContent());
            if (securityFlag && !etcServiceAdapter.filterComment(checkContent)) {
                log.info("insert answer rejected by filter.content={}", new String[]{answer.getContent()});
                return -1L;
            }
        } catch (Exception e) {
            log.error("", e);
            return -1l;
        }
        if(answer.getCreateTime() == null)
            answer.setCreateTime(new Date());
        if(answer.getUpdateTime() == null)
            answer.setUpdateTime(new Date());
        if(answer.getPublishTime() == null)
            answer.setPublishTime(new Date());
        if (EliteAnswerStatus.PASSED.getValue() != answer.getStatus() && EliteAnswerStatus.CHOOSE.getValue() != answer.getStatus()){
        answer.setStatus(EliteAnswerStatus.PUBLISHED.getValue());
        }
        Long id = answerDao.save(answer);
        if(id != null && id > 0) {
//            searchService.indexData(id, EliteSearchType.Answer.getValue(), null, false);
            answer.setId(id);
            notify(answer, null);
            //推荐到推荐系统
            if (answer.getStatus() == EliteAnswerStatus.PASSED.getValue()) {
                RecLogger.log(RecLogHelper.createItemLog(ItemType.ANSWER, id));
            }
        }
        return id;
    }

    @Override
    public boolean update(EliteAnswer answer) {
        try {
            String checkContent = ContentUtil.getPlainText(answer.getContent());
            if (!etcServiceAdapter.filterComment(checkContent)) {
                log.info("update answer rejected by filter.content={}", new String[]{answer.getContent()});
                return false;
            }
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
        //answer.setUpdateTime(new Date());
        EliteAnswer oriAnswer = this.getById(answer.getId());
        boolean result = answerDao.update(answer);
        if(result){
//            searchService.indexData(answer.getId(), EliteSearchType.Answer.getValue(), null, false);
            notify(answer, oriAnswer);
            //推荐到推荐系统
            if (answer.getStatus() == EliteAnswerStatus.PASSED.getValue() || answer.getStatus() == EliteAnswerStatus.REJECTED.getValue()
                    || answer.getStatus() == EliteAnswerStatus.DEL.getValue() || answer.getStatus() == EliteAnswerStatus.SYSDEL.getValue()) {
                RecLogger.log(RecLogHelper.createItemLog(ItemType.ANSWER, answer.getId()));
            }
        }
        return result;
    }

    @Override
    public EliteAnswer getById(Long answerId) {
        return answerDao.getById(answerId);
    }

    @Override
    public List<EliteAnswer> getAnswersByQuestionId(Long questionId) {
        return getAnswers(answerByQuestionIdDao.getAnswerIds(questionId));
    }

    @Override
    public List<EliteAnswer> getQuestionAnswersByStatus(Long questionId, List<Integer> statusList) {
        if (null == questionId || questionId.longValue() <= 0 || null == statusList || statusList.size() ==0) {
           return new ArrayList<EliteAnswer>();
        }
        return getAnswers(answerByQuestionIdDao.getQuestionAnswersByStatus(questionId, statusList));
    }

    @Override
    public int getQuestionAnswerNumByStatus(Long questionId, List<Integer> statusList) {
        if (null == questionId || questionId.longValue() <= 0 || null == statusList || statusList.size() ==0) {
            return 0;
        }
        return answerByQuestionIdDao.getQuestionAnswerNumByStatus(questionId, statusList);
    }

    @Override
    public List<EliteAnswer> getUserAnswersByStatus(Long bpId, List<Integer> statusList) {
        if (null == bpId || bpId.longValue() <= 0 || null == statusList || statusList.size() ==0) {
            return new ArrayList<EliteAnswer>();
        }
        return getAnswers(answerByBpIdDao.getUserAnswersByStatus(bpId, statusList));
    }

    @Override
    public int getUserAnswerNumByStatus(Long bpId, List<Integer> statusList) {
        if (null == bpId || bpId.longValue() <= 0 || null == statusList || statusList.size() ==0) {
            return 0;
        }
        return answerByBpIdDao.getUserAnswerNumByStatus(bpId, statusList);
    }

    @Override
    public List<EliteAnswer> getAnswersByBpId(Long bpId) {
        return getAnswers(answerByBpIdDao.getAnswerIds(bpId));
    }

    @Override
    public ListResult getAuditingAnswers(Integer start, Integer count) {
        return answerDao.getAuditingAnswers(start, count);
    }

    @Override
    public boolean batchAudit(List<Long> passAnswerIds, List<Long> rejectedAnswerIds) {
        EliteAnswer answer;
        boolean result = true;
        if (passAnswerIds != null && passAnswerIds.size() > 0) {
            for (Long id : passAnswerIds) {
                answer = getById(id);
                if(answer != null) {
                    answer.setStatus(EliteAnswerStatus.PASSED.getValue());
                    if(!update(answer)){
                        result = false;
                        log.error("audit pass, update answer error, id=" + answer.getId());
                    }
                }
            }
        }

        if (rejectedAnswerIds != null && rejectedAnswerIds.size() > 0) {
            for (Long id : rejectedAnswerIds) {
                answer = getById(id);
                if(answer != null) {
                    answer.setStatus(EliteAnswerStatus.REJECTED.getValue());
                    if(!update(answer)){
                        result = false;
                        log.error("audit reject, update answer error, id=" + answer.getId());
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean pass(Long answerId) {
        List<Long> passAnswerIds = new ArrayList<>();
        if(answerId != null)
            passAnswerIds.add(answerId);
        return batchAudit(passAnswerIds, null);
    }

    @Override
    public boolean reject(Long answerId) {
        List<Long> rejectAnswerIds = new ArrayList<>();
        if(answerId != null)
            rejectAnswerIds.add(answerId);
        return batchAudit(null, rejectAnswerIds);
    }

    @Override
    public boolean userDelete(Long answerId) {
        List<Long> answerIds = new ArrayList<>();
        if(answerId != null)
            answerIds.add(answerId);
        return delete(answerIds, EliteAnswerStatus.DEL.getValue());
    }

    @Override
    public boolean userDelete(List<Long> answerIds) {
        return delete(answerIds, EliteAnswerStatus.DEL.getValue());
    }

    @Override
    public boolean sysDelete(Long answerId) {
        List<Long> answerIds = new ArrayList<>();
        if(answerId != null)
            answerIds.add(answerId);
        return delete(answerIds, EliteAnswerStatus.SYSDEL.getValue());
    }

    @Override
    public boolean sysDelete(List<Long> answerIds) {
        return delete(answerIds, EliteAnswerStatus.SYSDEL.getValue());
    }

    public boolean delete(List<Long> answerIds, Integer deleteType){
        boolean result = true;
        EliteAnswer answer;
        if(answerIds != null && answerIds.size() > 0 && deleteType != null){
            for(Long id : answerIds){
                answer = getById(id);
                if(answer != null){
                    answer.setStatus(deleteType);
                    if(!update(answer)){
                        result = false;
                        log.error("audit delete, update answer error, id=" + answer.getId());
                    }
                }
            }
        }
        return result;
    }

    public List<EliteAnswer> getAnswers(List<Long> answerIds){
        if(answerIds != null && answerIds.size() > 0){
            List<EliteAnswer> answers = new ArrayList<>();
            for(Long answerId : answerIds){
                answers.add(getById(answerId));
            }
            return answers;
        }
        return null;
    }
    
    public void notify(EliteAnswer answer, EliteAnswer oriAnswer)
    {
    	if(null != answer && answer.getId() != null)
    	{
    		long id = answer.getId();
            int type = EliteNotifyType.ELITE_NOTIFY_INSERT.getValue();
            if(notifyService.notify2Statistic(answer.getId(), BpType.Answer.getValue(), type))
                log.info("notify to statistic sucess");
    		if (null == oriAnswer || oriAnswer.getStatus() != answer.getStatus()) {
                long questionId = answer.getQuestionId();
                long bpId = answer.getBpId();
                notifyService.notify2Statistic(questionId, BpType.Question.getValue(), type);
                notifyService.notify2Statistic(bpId, BpType.Elite_User.getValue(), type);
            }
    	}
    	
    }

}
