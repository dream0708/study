package com.sohu.bp.elite.dao;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.persistence.EliteExpertTeam;
import com.sohu.bp.elite.persistence.EliteUser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-*.xml","classpath:springmvc-servlet.xml"})
public class EliteExpertTeamDaoTest {
    @Resource
    private EliteExpertTeamDao expertTeamDao;
    @Resource
    private CacheManager redisCacheManager;
    @Resource
    private EliteUserDao userDao;
    
    private RedisCache redisCache;
    
    @PostConstruct
    public void init() {
        redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_EXPERT_TEAM);
    }
    
//    @Test
    public void testSave() {
        EliteExpertTeam expert = new EliteExpertTeam().setId(7298258L).setPushNum(0).setAnsweredNum(0).setLastAnsweredTime(new Date())
                .setLastPushTime(new Date()).setScore(0).setIdentity(EliteUserIdentity.EXPERT.getValue()).setUnansweredId("yy").setAnsweredId("xx");
        Long resVal = expertTeamDao.save(expert);
        Assert.isTrue(resVal > 0);
    }
    
//    @Test
    public void testGet() {
        EliteExpertTeam expert = expertTeamDao.get(7298258L);
        Assert.notNull(expert);
    }
    
//    @Test
    public void testUpdate() {
        EliteExpertTeam expert = expertTeamDao.get(7298258L);
        expert.setLastPushTime(new Date());
        boolean resVal = expertTeamDao.update(expert);
        Assert.isTrue(resVal);
    }
    
//    @Test
    public void removeCache() {
        ListResult listResult = userDao.getExperts(0, Integer.MAX_VALUE);
        List<EliteUser> users = (List<EliteUser>) listResult.getEntities();
        for(EliteUser user : users) {
            redisCache.remove(user.getId().toString());
        }
        listResult = userDao.getAuditingExperts(0, Integer.MAX_VALUE);
        users = (List<EliteUser>) listResult.getEntities();
        for(EliteUser user : users) {
            redisCache.remove(user.getId().toString());
        }
    }
    @Test
    public void getExpertsBySortField() {
        List<EliteExpertTeam> expertsPushNum = expertTeamDao.getExpertTeamByCondition(0, 10, "pushNum");
        List<EliteExpertTeam> expertsAnswerNum = expertTeamDao.getExpertTeamByCondition(0, 10, "answeredNum");
        List<EliteExpertTeam> expertScore = expertTeamDao.getExpertTeamByCondition(0, 10, "score");
        List<EliteExpertTeam> expertLastPush = expertTeamDao.getExpertTeamByCondition(0, 10, "lastPushTime");
        List<EliteExpertTeam> expertLastAnswer = expertTeamDao.getExpertTeamByCondition(0, 10, "lastAnsweredTime");
        System.out.println("result push num : " + expertsPushNum);
        System.out.println("result answer num : " + expertsAnswerNum);
        System.out.println("result expert score : " + expertScore);
        System.out.println("result expert last push time : " + expertLastPush);
        System.out.println("result expert last answer time : " + expertLastAnswer);
    }
}
