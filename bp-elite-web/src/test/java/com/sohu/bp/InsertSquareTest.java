//package com.sohu.bp;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//
//import javax.annotation.Resource;
//import javax.annotation.Resources;
//
//import org.apache.commons.lang3.StringUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
//import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
//import com.sohu.bp.elite.enums.FeedType;
//import com.sohu.bp.elite.service.web.SquareService;
//
///**
// * 用户问答广场运营的批量插入
// * @author zhijungou
// * 2016年11月2日
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:bpEliteWeb/*.xml", "classpath:bpEliteWeb-servlet.xml"})
//public class InsertSquareTest {
//	private static final Logger log = LoggerFactory.getLogger(InsertSquareTest.class);
//	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
//	private static final String file = "D:/opt/logs/elite-square/square1103am新增问答.txt";
//	
//	@Resource
//	private SquareService squareService;
//	
//	@Test
//	public void SquareTest(){
//		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)), "UTF-8"))){
//			String line = null;
//			for(line = reader.readLine(); null != line; line = reader.readLine()){
//				if(StringUtils.isBlank(line)) continue;
//				String[] blocks = line.split("\t");
//				Long ObjectId = Long.valueOf(blocks[0]);
//				FeedType feedType = FeedType.getFeedTypeByValue(Integer.valueOf(blocks[1]));
//				Long time = Long.valueOf(blocks[2]);
//				squareService.insertSquare(ObjectId, feedType, false, time);
//				System.out.println("ObjectId = " + ObjectId + " FeedType = " + feedType.getDesc() + " time = " + time);
//			}
//		} catch (Exception e) {
//			log.error("", e);
//		}
//	}
//	
//	public static void main(String[] args) {
//		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)), "UTF-8"))){
//			String line = null;
//			for(line = reader.readLine(); null != line; line = reader.readLine()){
//				if(StringUtils.isBlank(line)) continue;
//				String[] blocks = line.split("\t");
//				Long ObjectId = Long.valueOf(blocks[0]);
//				FeedType feedType = FeedType.getFeedTypeByValue(Integer.valueOf(blocks[1]));
//				Long time = Long.valueOf(blocks[2]);
//				System.out.println("ObjectId = " + ObjectId + " FeedType = " + feedType.getDesc() + " time = " + time);
//			}
//		} catch (Exception e) {
//			log.error("", e);
//		}
//	}
//
//}


