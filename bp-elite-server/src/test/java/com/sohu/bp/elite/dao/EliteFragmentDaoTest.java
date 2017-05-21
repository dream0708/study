//package com.sohu.bp.elite.dao;
//
//import java.util.Date;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.util.Assert;
//
//import com.sohu.bp.elite.persistence.EliteFragment;
//
//
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:spring/applicationContext-*.xml","classpath:springmvc-servlet.xml"})
//public class EliteFragmentDaoTest {
//	
//	@Resource
//	private EliteFragmentDao eliteFragmentDao;
//	
//	@Test
//	public void testInsert()
//	{	
//		EliteFragment fragment = new EliteFragment();
//		fragment.setUserId(1l);
//		fragment.setPosition(1);
//		fragment.setType(1);
//		fragment.setName("导航标签");
//		fragment.setDetail("1234");
//		fragment.setCreateTime(new Date());
//		fragment.setCreateHost(1l);
//		fragment.setCreatePort(1);
//		fragment.setUpdateTime(new Date());
//		fragment.setUpdatePort(1);
//		fragment.setUpdateHost(1l);
//		fragment.setStatus(1);
//		Long id = eliteFragmentDao.setEliteFragment(fragment);
//		System.out.println("\n insert fragment result="+ id);
//		Assert.isTrue(id > 0);
//	}
//	
//	@Test
//	public void testUpdate()
//	{
//		EliteFragment fragment = eliteFragmentDao.getFragmentById(1l);
//		Assert.notNull(fragment);
//		if(fragment.getStatus() == 1) fragment.setStatus(2);
//		else fragment.setStatus(1);
//		fragment.setName("装修");
//		eliteFragmentDao.updateEliteFragment(fragment);
//	}
//	
//	@Test
//	public void testHistory()
//	{
//		Integer total = eliteFragmentDao.getFragmentCount();
//		Assert.isTrue(total > 0);
//		Integer num = eliteFragmentDao.getFragmentCountByType(1);
//		Assert.isTrue(num > 0);
//		List<EliteFragment> resultList = eliteFragmentDao.getFragmentByType(1);
//		Assert.notNull(resultList);
//		Assert.isTrue(resultList.size() > 0);
//		EliteFragment result = eliteFragmentDao.getFragmentById(1l);
//		Assert.notNull(result);
//		Assert.isTrue(result.getId() > 0);
//	}
//	
//
//		
//	
//
//}
