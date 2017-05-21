package com.sohu.bp.elite.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sohu.bp.decoration.adapter.BpDecorationEtcServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.elite.util.ContentUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.dao.EliteAnswerByQuestionIdDao;
import com.sohu.bp.elite.dao.EliteOptionsDao;
import com.sohu.bp.elite.dao.EliteQuestionBySpecialDao;
import com.sohu.bp.elite.dao.EliteQuestionBybpIdDao;
import com.sohu.bp.elite.dao.EliteQuestionDao;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteNotifyType;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.persistence.EliteQuestion;
import com.sohu.bp.elite.service.EliteQuestionService;
import com.sohu.bp.elite.service.EliteSearchService;
import com.sohu.bp.elite.service.NotifyService;
import com.sohu.bp.elite.util.EliteFeatureUtil;

import cn.focus.rec.collector.RecLogHelper;
import cn.focus.rec.collector.RecLogger;
import cn.focus.rec.model.ItemType;

/**
 * @author zhangzhihao
 *         2016/7/11
 */
public class EliteQuestionServiceImpl implements EliteQuestionService {
    private Logger log = LoggerFactory.getLogger(EliteAnswerServiceImpl.class);
    private static final BpDecorationEtcServiceAdapter etcServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationEtcServiceAdapter();

    private EliteQuestionDao questionDao;

    private EliteQuestionBybpIdDao questionBybpIdDao;

    private EliteAnswerByQuestionIdDao answerByQuestionIdDao;

    private EliteSearchService searchService;
    
    private NotifyService notifyService;
    
    private EliteQuestionBySpecialDao questionBySpecialDao;
    
    private EliteOptionsDao optionsDao;

    public void setQuestionDao(EliteQuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    public void setQuestionBybpidDao(EliteQuestionBybpIdDao questionBybpIdDao) {
        this.questionBybpIdDao = questionBybpIdDao;
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
	
	public void setQuestionBySpecialDao(EliteQuestionBySpecialDao questionBySpecialDao) {
		this.questionBySpecialDao = questionBySpecialDao;
	}
	
	public void setOptionsDao(EliteOptionsDao optionsDao) {
        this.optionsDao = optionsDao;
    }
	
	@Override
    public Long insert(EliteQuestion question, boolean securityFlag) {
        try {
            String checkContent = ContentUtil.getPlainText(question.getTitle() + " " + question.getDetail() + " " + question.getOptions());
            if (securityFlag && !etcServiceAdapter.filterComment(checkContent)) {
                log.info("insert question rejected by filter.title={}, content={}, options={}", new String[]{question.getTitle(), question.getDetail(), question.getOptions()});
                return -1L;
            }
        } catch (Exception e) {
            log.error("", e);
            return -1l;
        }

        if(question.getCreateTime() == null)
            question.setCreateTime(new Date());
        if(question.getUpdateTime() == null)
            question.setUpdateTime(new Date());
        if(question.getPublishTime() == null)
            question.setPublishTime(new Date());
        if (EliteQuestionStatus.PASSED.getValue()!= question.getStatus() ){
        	  question.setStatus(EliteQuestionStatus.PUBLISHED.getValue());
        }
        if (EliteFeatureUtil.isVSQuestion(question) && question.getSpecialId() == 0L) question.setSpecialId(1L);
        Long id = questionDao.save(question);
        if(id != null && id > 0){
//            searchService.indexData(id, EliteSearchType.Question.getValue(), null, false);
            question.setId(id);
            notify(question, null);
            //推送到推荐系统
            if (question.getStatus() == EliteQuestionStatus.PASSED.getValue()) RecLogger.log(RecLogHelper.createItemLog(ItemType.QUESTION, id));
        }
        return id;
    }

    @Override
    public boolean update(EliteQuestion question) {
        //question.setUpdateTime(new Date());
        try {
            String checkContent = ContentUtil.getPlainText(question.getTitle() + " " + question.getDetail() + " " + question.getOptions());
            if (!etcServiceAdapter.filterComment(checkContent)) {
                log.info("update question rejected by filter.title={}, content={}, options={}", new String[]{question.getTitle(), question.getDetail(), question.getOptions()});
                return false;
            }
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
        EliteQuestion oriQuestion = this.getById(question.getId());
        boolean result =  questionDao.update(question);
        if(result){
//            searchService.indexData(question.getId(), EliteSearchType.Question.getValue(), null, false);

            //问题的tagIds改变，更新该问题所有回答的tagIds
            if(StringUtils.isNotBlank(question.getTagIds())){
                EliteQuestion originQuestion = getById(question.getId());
                if(!question.getTagIds().equals(originQuestion.getTagIds())){
                    List<Long> answerIds = answerByQuestionIdDao.getAnswerIds(question.getId());
                    if(answerIds != null && answerIds.size() > 0) {
                        for (Long answerId : answerIds) {
//                            searchService.indexData(answerId, EliteSearchType.Answer.getValue(), null, false);
                        	notifyService.notify2Statistic(answerId, BpType.Answer.getValue(), EliteNotifyType.ELITE_NOTIFY_INSERT.getValue());
                        }
                    }
                }
            }

            notify(question, oriQuestion);
            //推送到推荐系统
            if (question.getStatus() == EliteQuestionStatus.PASSED.getValue() || question.getStatus() == EliteQuestionStatus.REJECTED.getValue() 
                    || question.getStatus() == EliteQuestionStatus.DEL.getValue() || question.getStatus() == EliteQuestionStatus.SYSDEL.getValue()) {
                RecLogger.log(RecLogHelper.createItemLog(ItemType.QUESTION, question.getId()));
            }
        }
        return result;
    }

    @Override
    public EliteQuestion getById(Long questionId) {
        EliteQuestion question = questionDao.get(questionId);
        if (null != question && EliteFeatureUtil.isFeatureQuestion(question)) {
            question.setCounts(optionsDao.getOptionRecord(questionId));
        }
        return question;
    }

    @Override
    public ListResult getByIds(List<Long> questionIds) {
        ListResult listResult = new ListResult();
        if(questionIds == null || questionIds.size() <= 0)
            return listResult;
        List<EliteQuestion> questions = new ArrayList<>();
        for(Long id : questionIds){
            questions.add(getById(id));
        }
        listResult.setTotal(questionIds.size());
        listResult.setEntities(questions);
        return listResult;
    }

    @Override
    public Boolean batchAudit(List<Long> passQuestionIds, List<Long> rejectedQuestionIds) {
        EliteQuestion question;
        boolean result = true;
        if (passQuestionIds != null && passQuestionIds.size() > 0) {
            for(Long id : passQuestionIds){
                question = getById(id);
                if(question != null) {
                    question.setStatus(EliteQuestionStatus.PASSED.getValue());
                    if(!update(question)){
                        result = false;
                        log.error("audit pass, update question error, id=" + question.getId());
                    } 
                }
            }
        }

        if (rejectedQuestionIds != null && rejectedQuestionIds.size() > 0) {
            for (Long id : rejectedQuestionIds) {
                question = getById(id);
                if (question != null) {
                    question.setStatus(EliteQuestionStatus.REJECTED.getValue());
                    if(!update(question)){
                        result = false;
                        log.error("audit reject, update question error, id=" + question.getId());
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Boolean pass(Long questionId) {
        List<Long> passQuestionIds = new ArrayList<>();
        if(questionId != null)
            passQuestionIds.add(questionId);
        return batchAudit(passQuestionIds, null);
    }

    @Override
    public Boolean reject(Long questionId) {
        List<Long> rejectedQuestionIds = new ArrayList<>();
        if(questionId != null)
            rejectedQuestionIds.add(questionId);
        return batchAudit(null, rejectedQuestionIds);
    }

    @Override
    public Boolean sysDelete(List<Long> questionIds) {
        return delete(questionIds, EliteQuestionStatus.SYSDEL.getValue());
    }

    @Override
    public Boolean userDelete(List<Long> questionIds) {
        return delete(questionIds, EliteQuestionStatus.DEL.getValue());
    }

    @Override
    public Boolean sysDelete(Long questionId) {
        List<Long> questionIds = new ArrayList<>();
        if(questionId != null)
            questionIds.add(questionId);
        return delete(questionIds, EliteQuestionStatus.SYSDEL.getValue());
    }

    @Override
    public Boolean userDelete(Long questionId) {
        List<Long> questionIds = new ArrayList<>();
        if(questionId != null)
            questionIds.add(questionId);
        return delete(questionIds, EliteQuestionStatus.DEL.getValue());
    }

    @Override
    public ListResult getAuditingQuestions(Integer start, Integer count) {
       return questionDao.getAuditingQuestions(start, count);
    }

    public Boolean delete(List<Long> questionIds, Integer deleteStatus){
        EliteQuestion question;
        boolean result = true;
        if(questionIds != null && questionIds.size() > 0 && deleteStatus != null) {
            for (Long id : questionIds) {
                question = getById(id);
                if (question != null) {
                    question.setStatus(deleteStatus);
                    if (!update(question)) {
                        result = false;
                        log.error("audit delete, update question error, id=" + question.getId());
                    } 
                }
            }
        }
        return result;
    }

    @Override
    public List<EliteQuestion> getQuestionsByBpId(Long bpId) {
        List<Long> questionIds = questionBybpIdDao.getQuestionIds(bpId);
        if(questionIds != null && questionIds.size() > 0){
            List<EliteQuestion> questions = new ArrayList<>();
            for(Long questionId : questionIds){
                questions.add(getById(questionId));
            }
            return questions;
        }
        return new ArrayList<>();
    }
    
    public void notify(EliteQuestion question, EliteQuestion oriQuestion)
    {
    	if(null != question && question.getId() != null)
    	{
    		long id = question.getId();
    		
    		int type = EliteNotifyType.ELITE_NOTIFY_INSERT.getValue();

    		if(notifyService.notify2Statistic(question.getId(), BpType.Question.getValue(), type))
    			log.info("index question={} succeeded", new Object[]{question.getId()});

            if (null == oriQuestion || question.getStatus() != oriQuestion.getStatus()) {
                long bpId = question.getBpId();
                notifyService.notify2Statistic(bpId, BpType.Elite_User.getValue(), type);
            }
    	}
    	
    }

	@Override
	public List<EliteQuestion> getQuestionsBySpeical(Long specialId, Integer specialType) {	
		List<Long> questionIds = questionBySpecialDao.getQuestionsBySpecial(specialId, specialType);
		List<EliteQuestion> questions = new ArrayList<>();
		if(null != questionIds && questionIds.size() > 0){
			questionIds.forEach(questionId -> questions.add(questionDao.get(questionId)));
		}
		return questions;
	}

    @Override
    public List<EliteQuestion> getUserQuestionsByStatus(Long bpId, List<Integer> statusList) {
        List<EliteQuestion> questions = new ArrayList<>();
        if (null == bpId || bpId.longValue() <= 0 || null == statusList || statusList.size() == 0) {
            return questions;
        }
        List<Long> questionIds = questionBybpIdDao.getUserQuestionsByStatus(bpId, statusList);
        if (null == questionIds || questionIds.size() == 0) {
            return questions;
        }
        questionIds.forEach(questionId -> questions.add(questionDao.get(questionId)));
        return questions;
    }

    @Override
    public int getUserQuestionNumByStatus(Long bpId, List<Integer> statusList) {
        if (null == bpId || bpId.longValue() <=0 || null == statusList || statusList.size() == 0) {
            return 0;
        }
        return questionBybpIdDao.getUserQuestionNumByStatus(bpId, statusList);
    }
}
