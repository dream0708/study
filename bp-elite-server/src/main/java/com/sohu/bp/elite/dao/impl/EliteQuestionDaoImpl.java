package com.sohu.bp.elite.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.cache.ssdb.SsdbCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SsdbConstants;
import com.sohu.bp.elite.dao.EliteOptionsDao;
import com.sohu.bp.elite.dao.EliteQuestionBySpecialDao;
import com.sohu.bp.elite.dao.EliteQuestionBybpIdDao;
import com.sohu.bp.elite.dao.EliteQuestionDao;
import com.sohu.bp.elite.db.DbPartitionHelper;
import com.sohu.bp.elite.db.Sequence;
import com.sohu.bp.elite.db.TablePartitioner;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteQuestion;
import com.sohu.bp.elite.persistence.EliteQuestionBybpid;
import com.sohu.bp.elite.persistence.EliteQuestionByspecial;
import com.sohu.bp.elite.util.EliteFeatureUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangzhihao
 *         2016/7/8
 */
public class EliteQuestionDaoImpl extends JdbcDaoImpl implements EliteQuestionDao {
    private Logger log = LoggerFactory.getLogger(EliteQuestionDaoImpl.class);

    private TablePartitioner partitioner;
    private Sequence sequence;
    private CacheManager redisCacheManager;
    private CacheManager ssdbCacheManager;
    private RedisCache questionRedisCache;
    private SsdbCache auditingQuestionListCache;
    private EliteQuestionBybpIdDao questionBybpIdDao;
    private EliteQuestionBySpecialDao questionBySpecialDao;
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

    public void setSsdbCacheManager(CacheManager ssdbCacheManager) {
        this.ssdbCacheManager = ssdbCacheManager;
    }

    public void setQuestionBybpIdDao(EliteQuestionBybpIdDao questionBybpIdDao) {
        this.questionBybpIdDao = questionBybpIdDao;
    }

	public void setQuestionBySpecialDao(EliteQuestionBySpecialDao questionBySpecialDao) {
		this.questionBySpecialDao = questionBySpecialDao;
	}
	
	public void setOptionsDao(EliteOptionsDao optionsDao) {
        this.optionsDao = optionsDao;
    }

    public void init(){
        questionRedisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_QUESTION);
        auditingQuestionListCache = (SsdbCache) ssdbCacheManager.getCache(SsdbConstants.ELITE_AUDITING_QUESTION_LIST);
    }

    public String getTableName(Long id){
        return DbPartitionHelper.getTableName(partitioner, id);
    }

    private String getKey(Long bpId, Integer status){
        return bpId.toString() + "-" + status.toString();
    }

    @Override
    public Long save(EliteQuestion question) {
        Long id = -1L;
        if(question == null || question.getBpId() == null || question.getStatus() == null)
            return id;

        id = sequence.nextSequence();
        if(id == null || id <= 0) {
            log.error("generate id for question error, id="+id);
            return id;
        }
        question.setId(id);
        log.info("saving question (bpId={}, title={}, detail={}, tagIds={}, relationType={}, relationId={}, source={}, status={}, specialType = {}, specialId = {})",new Object[]{
                question.getBpId(), question.getTitle(), question.getDetail(), question.getTagIds(), question.getRelationType(), question.getRelationId(), question.getSource(), question.getStatus(),
                question.getSpecialType(), question.getSpecialId()});
        if (super.save(question, getTableName(id)) > 0) {
            EliteQuestionBybpid questionBybpid = new EliteQuestionBybpid();
            questionBybpid.setBpId(question.getBpId());
            questionBybpid.setStatus(question.getStatus());
            questionBybpid.setQuestionId(id);
            questionBybpIdDao.save(questionBybpid);
            EliteQuestionByspecial questionBySpecial = new EliteQuestionByspecial().setQuestionId(id);
            if(null != question.getSpecialId() && null != question.getSpecialType())
            	questionBySpecial.setSpecialId(question.getSpecialId()).setSpecialType(question.getSpecialType());
            questionBySpecialDao.save(questionBySpecial);
            if (EliteFeatureUtil.isVSQuestion(question)) {
                optionsDao.createRecord(id, 2);
            }
            if (EliteFeatureUtil.isVoteQuestion(question)) {
                int num = JSONArray.fromObject(question.getOptions()).size();
                optionsDao.createRecord(id, num); 
            }
            
            updateCache(question);
            log.info("new question saved (bpId={}, title={}, detail={}, tagIds={}, relationType={}, relationId={}, source={}, status={})",new Object[]{
                    question.getBpId(), question.getTitle(), question.getDetail(), question.getTagIds(), question.getRelationType(), question.getRelationId(), question.getSource(), question.getStatus()});
        }else {
            log.error("save question error, id=" + id + ", bpId=" + question.getBpId());
            id = -1L;
        }

        return id;
    }

    @Override
    public boolean update(EliteQuestion question) {
        if(question == null || question.getId() == null || question.getBpId() == null || question.getStatus() == null)
            return false;
        EliteQuestion originQuestion = get(question.getId());
        if(originQuestion == null)
            return false;

        log.info("updating question(id={}, bpId={}, title={}, detail={}, tagIds={}, relationType={}, relationId={}, source={}, status={})",new Object[]{
                originQuestion.getId(), originQuestion.getBpId(), originQuestion.getTitle(), originQuestion.getDetail(), originQuestion.getTagIds(), originQuestion.getRelationType(), originQuestion.getRelationId(), originQuestion.getSource(), originQuestion.getStatus()});

        if (super.update(question, getTableName(question.getId())) > 0) {
            EliteQuestionBybpid questionBybpid = questionBybpIdDao.get(question.getBpId(), question.getId());
            questionBybpid.setStatus(question.getStatus());
            questionBybpIdDao.update(questionBybpid);

            updateCache(question);

            log.info("question updated to(id={}, bpId={}, title={}, detail={}, tagIds={}, relationType={}, relationId={}, source={}, status={})",new Object[]{
                    question.getId(), question.getBpId(), question.getTitle(), question.getDetail(), question.getTagIds(), question.getRelationType(), question.getRelationId(), question.getSource(), question.getStatus()});
        }else {
            log.error("update question error, id=" + question.getId());
            return false;
        }
        return true;
    }

    public void updateCache(EliteQuestion question){
        if(question == null || question.getId() == null || question.getBpId() == null || question.getStatus() == null || question.getCreateTime() == null)
            return;
        Long score = question.getCreateTime().getTime();
        if(EliteQuestionStatus.DEL.getValue() == question.getStatus() || EliteQuestionStatus.SYSDEL.getValue() == question.getStatus()){
            questionRedisCache.remove(question.getId().toString());
            //删除计数的缓存
            optionsDao.removeCache(question.getId());
            auditingQuestionListCache.zRem(Constants.Auditing_Questions_Key, question.getId().toString());
        }else {
            questionRedisCache.put(question.getId().toString(), question);
            if(EliteQuestionStatus.PUBLISHED.getValue() == question.getStatus()){
                auditingQuestionListCache.zAdd(Constants.Auditing_Questions_Key, score, question.getId().toString());
                auditingQuestionListCache.zAdd(Constants.Auditing_Questions_Key, Constants.SCORE_NEVER_USED, Constants.CACHE_OCCUPY_VALUE);
            }else {
                auditingQuestionListCache.zRem(Constants.Auditing_Questions_Key, question.getId().toString());
            }
        }
    }

    @Override
    public EliteQuestion get(Long id) {
        if(id == null || id <= 0)
            return null;
        EliteQuestion question = (EliteQuestion) questionRedisCache.get(id);
        if(question == null){
            question = super.get(EliteQuestion.class, id, getTableName(id));
            if(question != null){
                questionRedisCache.put(id, question);
            }
        }
        return question;
    }

    @Override
    public ListResult getAuditingQuestions(Integer start, Integer count) {
        long total = auditingQuestionListCache.zCount(Constants.Auditing_Questions_Key);
        ListResult listResult = new ListResult();
        if(total > 1){
            List<String> auditingQuestionIds = auditingQuestionListCache.zRange(Constants.Auditing_Questions_Key, start + 1, count);
            List<EliteQuestion> questions = new ArrayList<>();
            for(String id : auditingQuestionIds){
                questions.add(get(Long.parseLong(id)));
            }
            listResult.setTotal(total-1);
            listResult.setEntities(questions);
        }
        return listResult;
    }
}
