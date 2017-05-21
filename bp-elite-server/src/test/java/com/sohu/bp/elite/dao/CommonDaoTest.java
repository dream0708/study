package com.sohu.bp.elite.dao;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:bpEliteServer/*.xml"})
public class CommonDaoTest {
	@Resource
	private EliteColumnDao eliteColumnDao;
	@Resource
	private EliteFollowDao eliteFollowDao;
	@Resource
	private EliteFragmentDao eliteFragmentDao;
	@Resource
	private EliteSubjectDao eliteSubjectDao;
	@Resource
	private EliteTopicDao eliteTopicDao;
	
	@Test
	public void testCommonDao() {
		System.out.println("start to remove cache");
		eliteColumnDao.removeCache();
		eliteFollowDao.removeCache();
		eliteFragmentDao.removeCache();
		eliteSubjectDao.removeCache();
		eliteTopicDao.removeCache();
	}
	
}
