package com.sohu.bp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.enums.EliteUserStatus;
import com.sohu.bp.elite.model.SortType;
import com.sohu.bp.elite.model.TEliteExpertTeam;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.model.TSearchUserCondition;
import com.sohu.bp.elite.model.TUserIdListResult;
import com.sohu.bp.elite.model.TUserListResult;
import com.sohu.bp.elite.strategy.EliteExpertTeamScoreStrategy;
import com.sohu.bp.elite.strategy.EliteExpertTeamStrategy;
import com.sohu.bp.elite.task.EliteExpertTeamAsyncTask;
import com.sohu.bp.elite.task.EliteExpertTeamAsyncTaskPool;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;

import net.sf.json.JSONObject;
import scala.collection.mutable.StringBuilder;

@RunWith(SpringJUnit4ClassRunner.class )
@ContextConfiguration({"classpath:bpEliteWeb/*.xml","classpath:*.xml"})
public class ExpertTeamTest {
    private static final Logger log = LoggerFactory.getLogger(ExpertTeamTest.class);
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final BpDecorationServiceAdapter decorationAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
    private static final BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
    private static Long[] ids = {7298258L, 7996725L, 7996758L};
    private EliteExpertTeamStrategy strategy;
    
//    @Test
    public void testExpertTeamAsynTask() {
//        EliteExpertTeamAsyncTaskPool.addTask(new EliteExpertTeamAsyncTask(IDUtil.decodeId("cntnu"), IDUtil.decodeId("5ca0alt"), "5632;4430", "http"));
    }
    
//    @Test
    public void testSelectedExpertTeam() {
        strategy = new EliteExpertTeamScoreStrategy();
        Set<Long> expertIds = new HashSet<Long>();
        Map<Long, TEliteExpertTeam> expertMap = new HashMap<Long, TEliteExpertTeam>();
        Map<Long, String> tagMap = new HashMap<Long, String>();
        try{
            TUserListResult listResult =  eliteAdapter.getExperts(0, Integer.MAX_VALUE);
            List<TEliteUser> users = listResult.getUsers();
            List<Long> bpIds  = new ArrayList<Long>();
            users.forEach(user -> bpIds.add(user.getBpId()));
            expertIds = new HashSet<Long>(bpIds);
            List<TEliteExpertTeam> expertTeams = eliteAdapter.getBatchExpertTeams(bpIds);
            if (null != expertTeams && expertTeams.size() > 0) {
                Random rand = new Random();
                for (TEliteExpertTeam expert : expertTeams) {
                    expertMap.put(expert.getBpId(), expert);
                    int border = rand.nextInt(4) + 1;
                    int i = 0;
                    StringBuilder tags = new StringBuilder();
                    while (i < border) {
                        tags.append("1;");
                        i ++;
                    }
                    tagMap.put(expert.getBpId(),tags.toString());
                }     
            }              
        } catch (Exception e) {
            log.error("", e);
        }
        List<Long> result = strategy.getSelectedExpertTeams(expertIds, tagMap, expertMap);
        log.info("*********result***********");
        log.info("selected ids : " + result.toString());
        for (Long bpId : result) {
            try {
                eliteAdapter.addExpertNewPush(bpId, IDUtil.decodeId("16413"));
            } catch (TException e) {
               log.error("", e);
            }
        }     
        log.info("tag map : " + tagMap.toString());
    }
    
    @Test
    public void getPhoneTest() {
        CodeMsgData codeMsgData = bpServiceAdapter.getMobileByBpid(7996725L);
        String mobile = JSONObject.fromObject(codeMsgData.getData()).getString("mobile");
        System.out.println("moblie : " + mobile);
    }
    
//    @Test
    public void passExpertTest() {
        TSearchUserCondition condition = new TSearchUserCondition();
        condition.setStatus(EliteUserStatus.VALID.getValue()).setSortField("lastLoginTime").setSortType(SortType.DESC).setFrom(0).setCount(70);
        try {
            TUserIdListResult listResult = eliteAdapter.searchUserId(condition);
            List<Long> list = listResult.getUserIds();
            for (Long id : list) {
                eliteAdapter.passExpert(id);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
    
//    @Test
    public void searchTest() {
        TSearchUserCondition condition = new TSearchUserCondition();
        condition.setStatus(EliteUserStatus.VALID.getValue()).setIdentity(EliteUserIdentity.EXPERT.getValue()).setFrom(0).setCount(10);
        try {
            TUserListResult listResult = eliteAdapter.searchUser(condition);
            System.out.println("result : " + listResult);
        } catch (Exception e) {
            log.error("", e);
        }
        
    }
    
//    @Test
    public void rebuildTest() {
        try {
            TUserListResult listResult = eliteAdapter.getExperts(0, 70);
            List<TEliteUser> users = listResult.getUsers();
            if (null != users && users.size() > 0) {
                for (TEliteUser user : users) {
                    eliteAdapter.rebuildUser(user.getBpId());
                }
            }
        } catch (TException e) {
            log.error("", e);
        }
    }
//    @Test
    public void updateAreaCode() throws TException {
        TEliteUser user = eliteAdapter.getUserByBpId(7996725L);
        user.setAreaCode(0l);
        eliteAdapter.updateUser(user);
    }
    
//    @Test
    public void getExpertAuditing() {
        try {
            TUserListResult listResult = eliteAdapter.getAuditingExperts(0, 100);
            log.info("*******");
            log.info("num : " + listResult.getTotal());
            log.info("auditing experts : " + listResult.getUsers());
        } catch (TException e) {
            log.error("", e);
        }
    }
}
