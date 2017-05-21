package com.sohu.bp.elite.web;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.achelous.model.Feed;
import com.sohu.achelous.timeline.AchelousTimeline;
import com.sohu.achelous.timeline.service.TimelineService;
import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.service.web.FeedService;
import com.sohu.bp.elite.service.web.SquareService;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;

/**
 * 用以测试整个工程各个模块的速度，进行优化的测试类
 * @author zhijungou
 * 2017年4月20日
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(value = {"classpath:bpEliteWeb/*"})
public class OptimizeTest {
    private static final Logger log = LoggerFactory.getLogger(OptimizeTest.class);
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
    private static final TimelineService timelineService = AchelousTimeline.getService();
    private long bpId = 154443L;
    
//    @Resource
    private SquareService squareService;
//    @Resource
    private FeedService feedService;
    
//    @Test
    public void SquareTest() {
        List<SimpleFeedItemBean> list = null;
    	log.info("********start to test square**********");
    	long start = new Date().getTime();
        list = squareService.getInitialEliteList(bpId, null);
        long end = new Date().getTime();
        log.info("[ORIGIN]square cost time = {}", new Object[]{end - start});
        start = new Date().getTime();
        list = squareService.getInitialEliteList(bpId, TEliteSourceType.WRITE_APP);
        end = new Date().getTime();
        log.info("[OPTIMIZE]square cost time = {}", new Object[]{end - start});
    }
    
//    @Test
    public void FeedTest() {
        List<SimpleFeedItemBean> list = null;
        log.info("*******start to test feed********");
        long start = new Date().getTime();
        list = feedService.getBackwardFeeds(new Date().getTime(), bpId, null);
        long end = new Date().getTime();
        log.info("[ORIGIN]feed cost time = {}", new Object[]{end -start});
        start = new Date().getTime();
        list = feedService.getBackwardFeeds(new Date().getTime(), bpId, TEliteSourceType.WRITE_APP);
        list.sort(new Comparator<SimpleFeedItemBean>() {

            @Override
            public int compare(SimpleFeedItemBean o1, SimpleFeedItemBean o2) {
               return (int) (o1.getUpdateTime() - o2.getUpdateTime());
            }
        });
        end = new Date().getTime();
        log.info("[OPTIMIZE]feed cost time = {}", new Object[]{end -start});
    }
    
//    @Test
    public void UserTest() {
        CodeMsgData codeMsgData = bpServiceAdapter.getBpUserBasic(bpId);
        long start = new Date().getTime();
        codeMsgData = bpServiceAdapter.getBpUserBasic(bpId);
        long end = new Date().getTime();
        log.info("get bp userinfo cost = {}", new Object[]{end - start});
        start = new Date().getTime();
        try {
            TEliteUser user = eliteAdapter.getUserByBpId(bpId);
        } catch (Exception e) {
            log.error("", e);
        }
        end = new Date().getTime();
        log.info("get elite userinfo cost = {}", new Object[]{end - start});
    }
    
    @Test
    public void TimelineTest() {
        long bpId = 154443L;
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        for (int i = 0; i < 5; i++) {
            long start = new Date().getTime();
            List<Feed> feeds = timelineService.backward(accountId, new Date(1493257732810L), null);
            long end = new Date().getTime();
            System.out.println("time cost : " + (end - start));
        }
    }
}
