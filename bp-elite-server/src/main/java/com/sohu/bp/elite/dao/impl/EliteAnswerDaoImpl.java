package com.sohu.bp.elite.dao.impl;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.cache.ssdb.SsdbCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SsdbConstants;
import com.sohu.bp.elite.dao.EliteAnswerByBpIdDao;
import com.sohu.bp.elite.dao.EliteAnswerByQuestionIdDao;
import com.sohu.bp.elite.dao.EliteAnswerDao;
import com.sohu.bp.elite.dao.EliteOptionsDao;
import com.sohu.bp.elite.db.DbPartitionHelper;
import com.sohu.bp.elite.db.Sequence;
import com.sohu.bp.elite.db.TablePartitioner;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.persistence.EliteAnswer;
import com.sohu.bp.elite.persistence.EliteAnswerBybpid;
import com.sohu.bp.elite.persistence.EliteAnswerByquestionid;
import com.sohu.bp.elite.util.EliteFeatureUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangzhihao
 *         2016/7/11
 */
public class EliteAnswerDaoImpl extends JdbcDaoImpl implements EliteAnswerDao {
    private Logger log = LoggerFactory.getLogger(EliteAnswerDaoImpl.class);
    private TablePartitioner partitioner;
    private Sequence sequence;
    private CacheManager redisCacheManager;
    private CacheManager ssdbCacheManager;
    private RedisCache answerRedisCache;
    private SsdbCache auditingAnswersCache;
    private EliteAnswerByBpIdDao answerByBpIdDao;
    private EliteAnswerByQuestionIdDao answerByQustionIdDao;
    private EliteOptionsDao optionsDao;

    public void setPartitioner(TablePartitioner partitioner) {
        this.partitioner = partitioner;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    public void setRedisCacheManager(CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    public void setAnswerByBpIdDao(EliteAnswerByBpIdDao answerByBpIdDao) {
        this.answerByBpIdDao = answerByBpIdDao;
    }

    public void setAnswerByQustionIdDao(EliteAnswerByQuestionIdDao answerByQustionIdDao) {
        this.answerByQustionIdDao = answerByQustionIdDao;
    }

    public void setSsdbCacheManager(CacheManager ssdbCacheManager) {
        this.ssdbCacheManager = ssdbCacheManager;
    }
    
    public void setOptionsDao(EliteOptionsDao optionsDao) {
        this.optionsDao = optionsDao;
    }

    public void init(){
        answerRedisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_ANSWER);
        auditingAnswersCache = (SsdbCache) ssdbCacheManager.getCache(SsdbConstants.ELITE_AUDITING_ANSWER_LIST);
    }

    public String getTableName(Long id){
        return DbPartitionHelper.getTableName(partitioner, id);
    }

    @Override
    public Long save(EliteAnswer answer) {
        Long id = -1L;
        if(answer == null || answer.getBpId() == null || answer.getQuestionId() == null || answer.getCreateTime() == null)
            return id;

        id = sequence.nextSequence();
        if(id == null || id <= 0){
            log.error("generate id for answer error");
            return id;
        }

        answer.setId(id);
        log.info("saving answer (id={}, bpId={}, questionId={}, content={}, source={}, status={}", new Object[]{
                answer.getId(), answer.getBpId(), answer.getQuestionId(), answer.getContent(), answer.getSource(), answer.getStatus()});

        if(super.save(answer, getTableName(answer.getId())) > 0) {
            EliteAnswerBybpid answerBybpid = new EliteAnswerBybpid();
            answerBybpid.setBpId(answer.getBpId());
            answerBybpid.setAnswerId(answer.getId());
            answerBybpid.setStatus(answer.getStatus());
            answerByBpIdDao.save(answerBybpid);

            EliteAnswerByquestionid answerByquestionid = new EliteAnswerByquestionid();
            answerByquestionid.setQuestionId(answer.getQuestionId()).setAnswerId(answer.getId()).setStatus(answer.getStatus())
            .setSpecialId(answer.getSpecialId()).setSpecialType(answer.getSpecialType());
            answerByQustionIdDao.save(answerByquestionid);
            
            if (EliteFeatureUtil.isFeatureAnswer(answer)) {
            	if (null != answer.getSpecialId() && answer.getSpecialId() > 0) optionsDao.addOneVote(answer.getQuestionId(), answer.getSpecialId());
            }
            updateCache(answer);
            log.info("new answer saved (id={}, bpId={}, questionId={}, content={}, source={}, status={}, specialType = {}, specialId = {}", new Object[]{
                    answer.getId(), answer.getBpId(), answer.getQuestionId(), answer.getContent(), answer.getSource(), answer.getStatus(), answer.getSpecialType(), answer.getSpecialId()});
        }else {
            id = -1L;
            log.error("save answer error, id=" + answer.getId());
        }

        return id;
    }

    @Override
    public boolean update(EliteAnswer answer) {
        if(answer == null || answer.getId() == null || answer.getBpId() == null || answer.getCreateTime() == null)
            return false;
        EliteAnswer originAnswer = getById(answer.getId());
        if(originAnswer == null)
            return false;
        log.info("updating answer (id={}, bpId={}, questionId={}, content={}, source={}, status={}", new Object[]{
                answer.getId(), answer.getBpId(), answer.getQuestionId(), answer.getContent(), answer.getSource(), answer.getStatus()});

        if(super.update(answer, getTableName(answer.getId())) > 0){
            EliteAnswerBybpid answerBybpid = answerByBpIdDao.get(answer.getBpId(), answer.getId());
            answerBybpid.setStatus(answer.getStatus());
            answerByBpIdDao.update(answerBybpid);

            EliteAnswerByquestionid answerByquestionid = answerByQustionIdDao.get(answer.getQuestionId(), answer.getId());
            answerByquestionid.setStatus(answer.getStatus()).setSpecialId(answer.getSpecialId()).setSpecialType(answer.getSpecialType());
            answerByQustionIdDao.update(answerByquestionid);
            if (EliteFeatureUtil.isFeatureAnswer(answer)) {
            	if (!Objects.equals(originAnswer.getSpecialId(), answer.getSpecialId())) {
            		optionsDao.addOneVote(answer.getQuestionId(), answer.getSpecialId());
            	}
            }
            updateCache(answer);
            log.info("answer updated (id={}, bpId={}, questionId={}, content={}, source={}, status={}", new Object[]{
                    answer.getId(), answer.getBpId(), answer.getQuestionId(), answer.getContent(), answer.getSource(), answer.getStatus()});
        }else {
            log.error("update answer error, id=" + answer.getId());
            return false;
        }
        return true;
    }

    public void updateCache(EliteAnswer answer){
        if(answer == null || answer.getId() == null || answer.getBpId() == null || answer.getCreateTime() == null)
            return;
        if(EliteAnswerStatus.DEL.getValue() == answer.getStatus() || EliteAnswerStatus.SYSDEL.getValue() == answer.getStatus()){
            answerRedisCache.remove(answer.getId().toString());
            auditingAnswersCache.zRem(Constants.Auditing_Answers_Key, answer.getId().toString());
        }else {
            answerRedisCache.put(answer.getId().toString(), answer);
            if(EliteAnswerStatus.PUBLISHED.getValue() == answer.getStatus()){
                auditingAnswersCache.zAdd(Constants.Auditing_Answers_Key, answer.getCreateTime().getTime(), answer.getId().toString());
                auditingAnswersCache.zAdd(Constants.Auditing_Answers_Key, Constants.SCORE_NEVER_USED, Constants.CACHE_OCCUPY_VALUE);
            }else {
                auditingAnswersCache.zRem(Constants.Auditing_Answers_Key, answer.getId().toString());
            }
        }
    }

    @Override
    public EliteAnswer getById(Long id) {
        if(id == null || id <= 0)
            return null;
        EliteAnswer answer = (EliteAnswer) answerRedisCache.get(id);
        if(answer == null){
            answer = super.get(EliteAnswer.class, id, getTableName(id));
            if(answer != null){
                answerRedisCache.put(id, answer);
            }
        }
        return answer;
    }

    @Override
    public ListResult getAuditingAnswers(Integer start, Integer count) {
        ListResult listResult = new ListResult();
        long total = auditingAnswersCache.zCount(Constants.Auditing_Answers_Key);
        if(total > 1){
            List<String> answerIds = auditingAnswersCache.zRange(Constants.Auditing_Answers_Key, start + 1, count);
            if(answerIds != null && answerIds.size() > 0){
                List<EliteAnswer> answers = new ArrayList<>();
                for(String id : answerIds){
                    answers.add(getById(Long.parseLong(id)));
                }
                listResult.setTotal(total-1);
                listResult.setEntities(answers);
            }
        }
        return listResult;
    }
    
}
