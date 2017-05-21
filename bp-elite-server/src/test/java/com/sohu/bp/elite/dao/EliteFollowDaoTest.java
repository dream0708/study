//package com.sohu.bp.elite.dao;
//
//import java.util.Date;
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
//import com.sohu.bp.elite.enums.EliteFollowType;
//import com.sohu.bp.elite.persistence.EliteFollow;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:spring/applicationContext-*.xml","classpath:springmvc-servlet.xml"})
//public class EliteFollowDaoTest {
//	@Resource
//	private EliteFollowDao eliteFollowDao;
//	
//	@Test
//	public void test(){
//		EliteFollow eliteFollow = new EliteFollow();
//		eliteFollow.setBpId(1l);
//		eliteFollow.setCreateHost(1l);
//		eliteFollow.setCreateTime(new Date());
//		eliteFollow.setCreatePort(1);
//		eliteFollow.setUserId(1l);
//		eliteFollow.setStatus(1);
//		eliteFollow.setType(1);
//		Long id = eliteFollowDao.setEliteFollow(eliteFollow);
//		Assert.isTrue(id > 0);
//		Integer num = eliteFollowDao.getEliteFollowCountByType(EliteFollowType.ELITE_USER);
//		Assert.isTrue(num > 0);
//		List<EliteFollow> eliteFollows = eliteFollowDao.getEliteFollowByType(EliteFollowType.ELITE_USER, 0, 10);
//		Assert.notNull(eliteFollows);
//		eliteFollow.setBpId(3l);
//		eliteFollow.setId(id);
//		Boolean res = eliteFollowDao.updateEliteFollow(eliteFollow);
//		Assert.isTrue(res);
//		
//	}
//	
//}
