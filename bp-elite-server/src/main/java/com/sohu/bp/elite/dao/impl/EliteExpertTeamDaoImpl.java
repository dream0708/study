package com.sohu.bp.elite.dao.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.google.common.base.Objects;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.dao.EliteExpertTeamDao;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteExpertTeam;
import com.sohu.bp.elite.util.ExpertScoreUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class EliteExpertTeamDaoImpl extends JdbcDaoImpl implements EliteExpertTeamDao {
    private static final Logger log = LoggerFactory.getLogger(EliteExpertTeamDaoImpl.class);
    private CacheManager redisCacheManager;
    private RedisCache redisCache;
    
    public void setRedisCacheManager(CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }
    
    public void init() {
        redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_EXPERT_TEAM);
    }
    
    @Override
    public Long save(EliteExpertTeam expert) {
        Long id = -1L;
        if (null == expert || null == expert.getId() || expert.getId() <= 0) return id;
        if (super.save(expert) > 0) {
            id = expert.getId(); 
            updateCache(expert);
        } else {
            log.info("save expert error, bpId = {}", expert.getId());
        }
        return id;
    }

    @Override
    public boolean update(EliteExpertTeam expert) {
        if (null == expert || null == expert.getId()) return false;
        EliteExpertTeam originExpert = get(expert.getId());
        if (null == originExpert) return false;
        if (super.update(expert) > 0) {
            updateCache(expert);
        } else {
            log.error("update expert error, bpId = {}", expert.getId());
            return false;
        }
        return true;
    }

    @Override
    public EliteExpertTeam get(Long bpId) {
        if (null == bpId || bpId <= 0) return null;
        EliteExpertTeam expert = null;
        try{
            expert = (EliteExpertTeam)redisCache.get(bpId); 
        } catch (Exception e) {
            log.error("elite expert team struct has changed; bpId = {}", bpId);
            redisCache.remove(bpId.toString());
        }
        if (null == expert) {
            expert = super.get(EliteExpertTeam.class, bpId);
            if (null != expert) updateCache(expert);
        }
        return expert;
    }
    
    private void updateCache(EliteExpertTeam expert) {
        redisCache.put(expert.getId().toString(), expert);
    }

    @Override
    public boolean addNewMessage(Long bpId, Long questionId) {
        EliteExpertTeam expertTeam = get(bpId);
        if (null != expertTeam) {
//            String unansweredId = expertTeam.getUnansweredId();
//            Pattern pattern = Pattern.compile("(?<!\\d)" + questionId + Constants.TAG_IDS_SEPARATOR);
//            if (!pattern.matcher(unansweredId).find()) {
//                expertTeam.setLastPushTime(new Date());
//                expertTeam.setPushNum(expertTeam.getPushNum() + 1);
//                expertTeam.setScore(expertTeam.getScore() - 1);
//                unansweredId = expertTeam.getUnansweredId() + questionId + Constants.TAG_IDS_SEPARATOR;
//                unansweredId = truncateText(unansweredId, Constants.DB_TEXT_LENGTH);
//                expertTeam.setUnansweredId(unansweredId);
//                return update(expertTeam);
//            } else {
//                return true;
//            }
            String cacheKey = getUniteCacheKey(bpId);
            if (!redisCache.zExist(cacheKey, questionId.toString())) {
                String pushIds = expertTeam.getUnansweredId();
                JSONArray array = truncateText(pushIds, Constants.DB_TEXT_LENGTH);
                if (!redisCache.exist(cacheKey) || isTruncate(pushIds, Constants.DB_TEXT_LENGTH)) {
                    redisCache.remove(cacheKey);
                    for (int i = 0; i < array.size(); i ++) {
                        Long id = array.getJSONObject(i).getLong("id");
                        Long time = array.getJSONObject(i).getLong("time");
                        redisCache.zAdd(cacheKey, time, id.toString());
                    }
                }
                if (!redisCache.zExist(cacheKey, questionId.toString())) {
                    Date date = new Date();
                    JSONObject data = new JSONObject();
                    Long time = new Date().getTime();
                    data.put("id", questionId);
                    data.put("time", time);
                    array.add(data);
                    redisCache.zAdd(cacheKey, time, questionId.toString());
                    expertTeam.setLastPushTime(date).setPushNum(expertTeam.getPushNum() + 1).setUnansweredId(array.toString()).setScore(expertTeam.getScore() - 1);
                    return update(expertTeam);
                }
            }
        }
        return false;
    }

    @Override
    public boolean addNewAnswered(Long bpId, Long questionId) {
        EliteExpertTeam expertTeam = get(bpId);
        if (null != expertTeam) {
//            String unansweredId = expertTeam.getUnansweredId();
//            String newUnansweredId = unansweredId.replaceAll("(?<!\\d)" + questionId + Constants.TAG_IDS_SEPARATOR, "");
//            if (!Objects.equal(unansweredId, newUnansweredId)) {
//                expertTeam.setLastAnsweredTime(new Date());
//                expertTeam.setAnsweredNum(expertTeam.getAnsweredNum() + 1);
//                expertTeam.setScore(expertTeam.getScore() + 2);
//                expertTeam.setUnansweredId(newUnansweredId);
//                String answeredId = expertTeam.getAnsweredId() + questionId + Constants.TAG_IDS_SEPARATOR;
//                answeredId = truncateText(answeredId, Constants.DB_TEXT_LENGTH);
//                expertTeam.setAnsweredId(answeredId);
//            return update(expertTeam);
//            }
//            return true;
            String cacheKey = getUniteCacheKey(bpId);
            if (!redisCache.exist(cacheKey)) {
                String pushIds = expertTeam.getUnansweredId();
                JSONArray pushIdArray = JSONArray.fromObject(pushIds);
                for (int i = 0; i < pushIdArray.size(); i++) {
                    JSONObject data = pushIdArray.getJSONObject(i);
                    redisCache.zAdd(cacheKey, data.getLong("time"), data.getString("id"));
                }
            }
            if (redisCache.zExist(cacheKey, questionId.toString())) {
                Date date = new Date();
                String answerIds = expertTeam.getAnsweredId();
                JSONArray array = truncateText(answerIds, Constants.DB_TEXT_LENGTH);
                JSONObject data = new JSONObject();
                data.put("id", questionId);
                data.put("time", date.getTime());
                array.add(data);
                Long pushTime = redisCache.zScore(cacheKey, questionId.toString());
                Long answerTime = date.getTime();
                int score = ExpertScoreUtil.getScore(pushTime, answerTime);
                expertTeam.setAnsweredNum(expertTeam.getAnsweredNum() + 1).setLastAnsweredTime(date)
                .setAnsweredId(array.toString()).setScore(expertTeam.getScore() + score);
                return update(expertTeam);
            }  
        }
        return false;
    }
    @Deprecated
    @Override
    public List<EliteExpertTeam> getExpertTeamByCondition(Integer start, Integer count, String sortField) {
        Criteria criteria = Criteria.create(EliteExpertTeam.class).and("identity", new Integer[]{EliteUserIdentity.EXPERT.getValue()}).desc(sortField).limit(start, count);
        List<EliteExpertTeam> list = super.queryList(criteria);
        if (null == list) list = new ArrayList<EliteExpertTeam>();
        return list;
    }
    
    //用于截取超出数据库text的字段，直接取一半进行截取
//    private String truncateText(String originText, int byteLength) {
//        String truncateText = "";
//        try {
//            int num = originText.getBytes("utf-8").length;
//            if (num < byteLength) return originText;
//            StringBuilder tempText = new StringBuilder(originText);
//            tempText.delete(0, byteLength / 2);
//            int index = tempText.indexOf(Constants.TAG_IDS_SEPARATOR);
//            truncateText = tempText.substring(index + 1);
//        } catch (Exception e) {
//            log.error("", e);
//        }
//        return truncateText;
//    }
    
    private JSONArray truncateText(String originText, int byteLength) {
        if (StringUtils.isBlank(originText)) return new JSONArray();
        JSONArray array = JSONArray.fromObject(originText);
        try{
            int num = originText.getBytes("utf-8").length;
            if (num < byteLength) return array;
            JSONArray resultArray = new JSONArray();
            for (int i = 0; i <= array.size() / 2; i++) resultArray.add(array.getJSONObject(i));
            return resultArray;
        } catch (Exception e) {
            log.error("", e);
        }
        return array;
    }
    
    private boolean isTruncate(String originText, int byteLength) {
        boolean res = false;
        try {
             res = !(originText.getBytes("utf-8").length < byteLength);
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }
        return res;
    }
    
    private String getUniteCacheKey(Long bpId) {
        return CacheConstants.ELITE_EXPERT_TEAM_PUSHID_SET + "_" + bpId.toString();
    }

    @Override
    public boolean addExpertTag(Integer tagId) {
        log.info("add expert team tag, id = {}", new Object[]{tagId});
        return redisCache.zAdd(CacheConstants.ELITE_EXPERT_TEAM_TAG_SET, new Date().getTime(), tagId.toString());
    }

    @Override
    public boolean removeExpertTag(Integer tagId) {
        log.info("remove expert team tag, id = {}", new Object[]{tagId});
        return redisCache.zRem(CacheConstants.ELITE_EXPERT_TEAM_TAG_SET, tagId.toString());
    }

    @Override
    public List<Integer> getExpertTagIds() {
        Set<String> set = redisCache.zRevRange(CacheConstants.ELITE_EXPERT_TEAM_TAG_SET, 0L, -1L);
        if (null == set || set.size() <= 0) return new ArrayList<Integer>();
        return set.stream().map(Integer::valueOf).collect(Collectors.toList());
    }
}
