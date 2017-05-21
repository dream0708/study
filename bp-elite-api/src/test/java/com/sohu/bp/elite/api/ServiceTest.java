package com.sohu.bp.elite.api;


import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.api.constants.CacheConstants;
import com.sohu.bp.elite.api.dao.EliteFeatureDao;
import com.sohu.bp.elite.api.dao.IdentityDao;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/*.xml","classpath:applicationContext.xml"})
public class ServiceTest {

	private static final Logger log = LoggerFactory.getLogger(ServiceTest.class);
	private static String app_key = "app_test";
	private static String app_secret = "t1i8RiTpMVLwQJK1iJuXPSuOlMYe4lvZ";
	private static String host="http://10.10.24.207:8012";
	
	@Resource 
	private EliteFeatureDao eliteFeatureDao;

	@Resource
	private IdentityDao identityDao;
	@Resource
	private CacheManager redisCacheManager;
	private RedisCache redisCache;
	
	@PostConstruct
	public void init(){
		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_ELITE_SQUARE);
	}
	
//	@Test
//	public void testEliteFeature(){
//		String allUser = eliteFeatureDao.getAllUser();
//		List<String> userIdList = Arrays.asList(allUser.split(";"));
//		log.info("get all user" + userIdList.toString());
//		Assert.isTrue(userIdList.size() > 0);
//	}
//	
//	@Test
//	public void testEliteFeatureApi(){
//		Map<String, String> header = new HashMap<String, String>();
//		Map<String, String> params = new HashMap<String, String>();
//		
//		Long timestamp = System.currentTimeMillis();
//		String method = "POST";
//		String uri = "/elite/innerapi/feature/getAllUser";
//		String sig = AuthenticationCenter.generateSignature(uri, method, timestamp, app_secret);
//		header.put("Timestamp", timestamp.toString());
//		header.put("Auth", app_key + "|" + sig);
//		String response = HttpUtil.get(host + uri, params, header);
//		System.out.println(response);
//	}
	
	@Test
	public void testRedis(){
		List<Long> inviteList = eliteFeatureDao.getInvitedList();
		List<Long> identityList = identityDao.getExpertsList(0, 9);
		Set<String> squareSet = redisCache.zRevRange(CacheConstants.SQUARE_SET_KEY, 0, 29);
		System.out.println("invite list : " + inviteList);
		System.out.println("identity list : " + identityList);
		System.out.println("square list : " + squareSet);
	}
}
