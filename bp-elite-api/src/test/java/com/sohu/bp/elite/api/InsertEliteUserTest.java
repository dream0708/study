package com.sohu.bp.elite.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.ssdb.SsdbCache;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.auth.AuthenticationCenter;
import com.sohu.bp.elite.api.constants.CacheConstants;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.api.util.HttpUtil;
import com.sohu.bp.elite.model.TEliteUser;

import net.sf.json.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/*", "classpath:applicationContext.xml"})
public class InsertEliteUserTest {
	private static final Logger log = LoggerFactory.getLogger(InsertEliteUserTest.class);
	private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	@Resource
	private CacheManager ssdbCacheManager;
	
	private SsdbCache ssdbCache;
	
	private static String rebuildUserUri = "/elite/innerapi/user/insert";
	private static String appkey = "bp-decoration-search";
	private static String sec = "mdQeoF3qAogwocjoz8EKZgeDSH3yRv9i";
		
	@PostConstruct
	public void init(){
		ssdbCache = (SsdbCache) ssdbCacheManager.getCache(CacheConstants.CACHE_FIRST_FOLLOW);
	}
	@Test
	public void getUserIds(){
		Long num = ssdbCache.zCount(Constants.ELITE_FOLLOW_USER_LOGIN);
		List<String> idList = ssdbCache.zRange(Constants.ELITE_FOLLOW_USER_LOGIN, 0, num.intValue());
//		String ids = "";
//		for(String id : idList){
//			if(StringUtils.isBlank(ids)) ids = id;
//			else ids += ";" + id.toString();
//		}
//		long time = System.currentTimeMillis();
//		String method = "POST";
//		Map<String, String> headers = new HashMap<>();
//		headers.put("Timestamp", String.valueOf(time));
//		headers.put("Auth", appkey + "|" + AuthenticationCenter.generateSignature(rebuildUserUri, method, time, sec));
//		Map<String, String> params = new HashMap<>();
//		params.put("ids",ids);
//		//需要将host改成线上环境。"http://10.10.3.146:8012"
//		String res = HttpUtil.post("http://10.10.24.207:8012" + rebuildUserUri, params, headers);
////		String res = HttpUtil.post("http://10.10.3.146:8012" + rebuildUserUri, params, headers);
//		JSONObject resJSON = JSONObject.fromObject(res);
//		Assert.isTrue(resJSON.getInt("code") == 0);
		for(String idString :idList){
			Long id = Long.valueOf(idString);
			try{
	    		TEliteUser userElite = new TEliteUser();
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    		userElite.setFirstLoginTime(sdf.parse("2016-10-01").getTime());
	    		userElite.setLastLoginTime(new Date().getTime());
	    		userElite.setBpId(id);
	    		userElite.setStatus(1);
	    		userElite.setFirstLogin(0);
	    		Long userId = eliteAdapter.insertUser(userElite);
	    		if(null != userId && userId >0){
	    			log.info("insert user bpId = {} suceed!", new Object[]{userId});
	    		}
			} catch (Exception e){
				continue;
			}
		} 
	
	}
}
