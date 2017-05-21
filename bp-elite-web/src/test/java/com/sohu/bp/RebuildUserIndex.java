package com.sohu.bp;

import java.util.Date;
import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.enums.FeedActionType;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.service.web.FeedService;
import com.sohu.bp.elite.service.web.FirstFollowService;
import com.sohu.bp.elite.task.TaskEvent;
import com.sohu.bp.elite.task.TaskEventCacheQueue;
/**
 * 用于从ssdb中读取所有用户并调用接口重建所有用户索引
 * @author zhijungou
 * 2016年10月31日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"classpath:bpEliteWeb/applicationContext.xml", "classpath:bpEliteWeb-servlet.xml"})
public class RebuildUserIndex {
	   private static final Logger log = LoggerFactory.getLogger(RebuildUserIndex.class);

	   private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	   
	   @Resource
	   private FeedService feedService;
	   @Resource
	   private FirstFollowService firstFollowService;
	 
	   @Test
	   public void rebuildUserIndex(){
		   firstFollowService.rebuildUserIndex();
	   }
}
