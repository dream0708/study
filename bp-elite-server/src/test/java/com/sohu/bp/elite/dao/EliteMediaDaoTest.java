//package com.sohu.bp.elite.dao;
//
//import javax.annotation.Resource;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.sohu.bp.elite.enums.EliteMediaType;
//import com.sohu.bp.elite.persistence.EliteMedia;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:spring/applicationContext-*.xml","classpath:springmvc-servlet.xml"})
//public class EliteMediaDaoTest
//{
//	@Resource
//	private EliteMediaDao eliteMediaDao;
//	
//	@Test
//	public void testInsert()
//	{
//		EliteMedia eliteMedia = new EliteMedia();
//		eliteMedia.setMediaGivenId(123L);
//		eliteMedia.setQuestionId(123L);
//		eliteMedia.setUploadHost(123L);
//		eliteMedia.setUploadPort(123);
//		eliteMedia.setType(EliteMediaType.VIDEO.getValue());
//		
//		Long id = eliteMediaDao.save(eliteMedia);
//		
//		Assert.assertTrue(id > 0);
//	}
//}