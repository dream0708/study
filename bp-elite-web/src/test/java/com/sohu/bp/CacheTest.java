//package com.sohu.bp;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//
//import javax.annotation.Resource;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.sohu.bp.elite.action.FeatureAction;
//import com.sohu.bp.elite.service.external.UserInfoService;
//import com.sohu.bp.elite.service.web.FeatureService;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:bpEliteWeb/applicationContext.xml","classpath:*.xml"})
//@WebAppConfiguration
//public class CacheTest {
//	
//	
////	private  RedisCache redisCache;
//	
//    private MockMvc mockMvc;
//    
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//    
//
//    @Resource
//    private FeatureService featureService;
//    
//    @Resource
//    private UserInfoService userInfoService;
//
//    private FeatureAction featureAction; 
//
//
//  
//    
////	@PostConstruct
////	public void init()
////	{
////		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_BP_ELITE_FEATURE);
////	}
////	
//	@Before
//	public void setup()
//	{	
//		featureAction = new FeatureAction(featureService, userInfoService);
//		mockMvc = MockMvcBuilders.standaloneSetup(featureAction).build();
////		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//	}
//	
////	@Test
////	public void testUpdateCache() throws Exception
////	{
//////		MvcResult result =  mockMvc.perform(post("/feature/tagSquare").param("tagId", "2888").accept(MediaType.APPLICATION_JSON))
//////			.andReturn();
////		MvcResult result =  mockMvc.perform(post("/feature/subject/0baafa6bfcf19c64b682e4f06f2bb2d9").accept(MediaType.APPLICATION_JSON)).andDo(print())
////				.andReturn();
////		String response = result.getResponse().getContentAsString();
////		System.out.println("result is "+response.toString());
////	}
////	
//	
//	@Test
//	public void testGetFeatured() throws Exception
//	{
//		MvcResult result =  mockMvc.perform(post("/feature/relevant/questions").param("questionId", "e8be6922cc291079a4731c9c0e10cff9").accept(MediaType.APPLICATION_JSON)).andDo(print())
//				.andReturn();
//		String response = result.getResponse().getContentAsString();
//		System.out.println("result is "+response.toString());
//	}
//	
//	@Test
//	public void testGetRelevantTags() throws Exception
//	{
//		MvcResult result =  mockMvc.perform(post("/feature/relevant/tags").param("questionId", "e8be6922cc291079a4731c9c0e10cff9").accept(MediaType.APPLICATION_JSON)).andDo(print())
//				.andReturn();
//		String response = result.getResponse().getContentAsString();
//		System.out.println("result is "+response.toString());
//	}
//	
//	@Test
//	public void testGetRecomUsers() throws Exception
//	{
//		MvcResult result =  mockMvc.perform(post("/feature/relevant/users").accept(MediaType.APPLICATION_JSON)).andDo(print())
//				.andReturn();
//		String response = result.getResponse().getContentAsString();
//		System.out.println("result is "+response.toString());
//	}
//	
//
//	
//}
