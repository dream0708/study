//package com.sohu.bp.elite.dao;
//
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.util.Assert;
//
//import com.sohu.bp.elite.persistence.EliteFragment;
//import com.sohu.bp.elite.persistence.EliteTopic;
//
//
//
///**
// * 
// * @author zhijungou
// * 2016/8/16
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:spring/applicationContext-*.xml","classpath:springmvc-servlet.xml"})
//public class EliteTopicDaoTest {
//	@Resource
//	private EliteTopicDao eliteTopicDao;
//	@Resource
//	private EliteFragmentDao eliteFragmentDao;
//	@Test
//	public void testInsert()
//	{
//		EliteTopic topic = new EliteTopic();
//		topic.setBrief("装修怎么装");
//		topic.setCover("url:1234");
//		topic.setCreateHost(1l);
//		topic.setCreateTime(new Date());
//		topic.setEndTime(new Date());
//		topic.setQuestionId(1l);
//		topic.setStartTime(new Date());
//		topic.setStatus(1);
//		topic.setTitle("装修大全");
//		topic.setType(1);
//		topic.setUpdateHost(1l);
//		topic.setUpdateTime(new Date());
//		topic.setUserId(1l);
//		
//		Long id = eliteTopicDao.insert(topic);
//		System.out.println("\n insert topic result ="+id);
//		Assert.isTrue(id > 0);
//	}
//	@Test
//	public void testHistory()
//	{
//		Long total = eliteTopicDao.getTopicCount();
//		Assert.isTrue(total > 0);
//		List<EliteTopic> resultList = eliteTopicDao.getTopicHistory(0, total.intValue());
//		Assert.notNull(resultList);
//		Assert.isTrue(resultList.size() > 0);	
//	}
//	@Test
//	public void testUpdate()
//	{
//		EliteTopic eliteTopic = eliteTopicDao.getTopicById(1l);
//		Assert.notNull(eliteTopic);
//		if(eliteTopic.getStatus() == 1) eliteTopic.setStatus(2);
//		else eliteTopic.setStatus(1);
//		eliteTopic.setTitle("厨房");
//		boolean result = eliteTopicDao.update(eliteTopic);
//		Assert.isTrue(result);
//	}
//	@Test
//	public void testHistoryByStatus()
//	{
//		Long total = eliteTopicDao.getTopicCountByStatus(1);
//		Assert.isTrue(total > 0);
//		List<EliteTopic> resultList = eliteTopicDao.getTopicHistoryByStatus(1, 0, total.intValue());
//		Assert.notNull(resultList);
//		Assert.isTrue(resultList.size() > 0);
//	}
//	@Test
//	public void testHistory()
//	{
//		List<EliteTopic> eliteTopics = eliteTopicDao.getTopicHistory(0, 10);
//		List<EliteFragment> eliteFragments = eliteFragmentDao.getAllFragment();
//		Assert.isTrue(eliteTopics.size() > 0);
//	}
//}
