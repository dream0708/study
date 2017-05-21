//
//package com.sohu.bp.elite.dao;
//
//import java.util.Date;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.antlr.v4.runtime.misc.NotNull;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.util.Assert;
//
//import com.sohu.bp.elite.persistence.EliteSubject;
//
//
//
///**
// * 
// * @author zhijungou
// * 2016/8/12
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:spring/applicationContext-*.xml", "classpath:springmvc-servlet.xml"})
//public class EliteSubjectDaoTest {
//	
//	@Resource
//	private EliteSubjectDao subjectDao;	
//	
//	@Test
//	public void testUpdate()
//	{
//		EliteSubject subject = subjectDao.getHistoryById(41l);
//		Assert.notNull(subject);
//		if(subject.getStatus() == 1) subject.setStatus(2);
//		else subject.setStatus(1);
//		subject.setName("测试2");
//		subjectDao.update(subject);
//	}
//	
//	@Test
//	public void testHistory()
//	{
//		Long total = subjectDao.getAllHistoryCount();
//		Assert.isTrue(total > 0);
//		List<EliteSubject> resultList = subjectDao.getAllHistory(0, total.intValue());
//		Assert.notNull(resultList);
//		Assert.isTrue(resultList.size() > 0);
//		
//		
//	}
//	
//	@Test
//	public void testHistoryByStatus()
//	{
//		Long total = subjectDao.getHistoryCountByStatus(1);
//		Assert.isTrue(total > 0);
//		List<EliteSubject> resultList = subjectDao.getHistoryByStatus(1, 0, total.intValue());
//		Assert.notNull(resultList);
//		Assert.isTrue(resultList.size() > 0);
//
//	}
//		
//	@Test
//	public void testInsert()
//	{
//		EliteSubject subject = new EliteSubject();
//		subject.setAreaCode(0l);
//		subject.setName("马桶");
//		subject.setBrief("马桶不错");
//		subject.setStatus(1);
//		subject.setDetail("{\"测试2详情\":\"1 3 5\"}");
//		subject.setCreateHost(1234l);
//		subject.setUpdateHost(1234l);
//		subject.setCreatePort(1234);
//		subject.setUpdatePort(1234);
//		subject.setUserId(1234l);
//		subject.setCreateTime(new Date());
//		subject.setUpdateTime(new Date());
//		Long id = subjectDao.insert(subject);
//		System.out.println("\n insert subject result="+ id);
//		Assert.isTrue(id > 0);
//	}
//	
//	@Test
//	public void testgetAllHistory()
//	{
//		List<EliteSubject> eliteSubjects = subjectDao.getHistoryByStatus(1, 0, 1);
//		Assert.isTrue(eliteSubjects.size() > 0);
//	}
//}

