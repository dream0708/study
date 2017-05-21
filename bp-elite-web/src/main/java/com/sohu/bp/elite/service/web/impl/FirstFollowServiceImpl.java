package com.sohu.bp.elite.service.web.impl;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.cache.ssdb.SsdbCache;
import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SsdbConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteNotifyType;
import com.sohu.bp.elite.model.TEliteFollow;
import com.sohu.bp.elite.model.TEliteFollowType;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.service.web.FirstFollowService;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.HttpUtil;
import com.sohu.bp.elite.util.RequestUtil;
import com.sohu.bp.elite.util.SpringUtil;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapter;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapterFactory;
import com.sohu.bp.thallo.enums.ActionType;
import com.sohu.bp.thallo.model.RelationAction;
import com.sohu.bp.utils.http.UserAgentUtil;

import net.sf.json.JSONObject;
/**
 * 
 * @author zhijungou
 * 2016年9月9日
 * 用户初次登录默认关注操作
 */
public class FirstFollowServiceImpl implements FirstFollowService {
	
	private static final Logger log = LoggerFactory.getLogger(FirstFollowServiceImpl.class);
	private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private BpThalloServiceAdapter thalloAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();
	private CacheManager ssdbCacheManager;
	private CacheManager redisCacheManager;
	private SsdbCache ssdbCache;
	private RedisCache redisCache;
	private String host;
	private static final String SEPERATOR = "|";

	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setSsdbCacheManager(CacheManager ssdbCacheManager) {
		this.ssdbCacheManager = ssdbCacheManager;
	}
	
	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}


	public void init(){
		ssdbCache = (SsdbCache) ssdbCacheManager.getCache(SsdbConstants.CACHE_FIRST_FOLLOW);
		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_FOLLOW);
	}
	
	@Override
	public Boolean isFirstLogin(Long bpId) {
		Boolean retVal = false;
		Long num = ssdbCache.zCount(Constants.ELITE_FOLLOW_USER_LOGIN);
		List<String> ids = ssdbCache.zRange(Constants.ELITE_FOLLOW_USER_LOGIN, 0, Integer.valueOf(num.intValue()));
		if(null != ids){
			if(!ids.contains(bpId.toString())){
				ssdbCache.zAdd(Constants.ELITE_FOLLOW_USER_LOGIN, Constants.ELITE_FOLLOW_USER_LOGIN_SCORE, bpId.toString());
				retVal = true;
			}
		}
		else{
			ssdbCache.zAdd(Constants.ELITE_FOLLOW_USER_LOGIN, Constants.ELITE_FOLLOW_USER_LOGIN_SCORE, bpId.toString());
			retVal = true;
		}
		return retVal;
	}

	@Override
	public void userFollow(Long bpId, Long actIp, Integer actPort) {
		try{
			List<TEliteFollow> tEliteFollows = (List<TEliteFollow>) redisCache.get(Constants.ELITE_FOLLOW_USER_KEY);
			//TODO 加入缓存，在server进行清除缓存的操作，当缓存为0时即重新加载。
			if(null == tEliteFollows || tEliteFollows.size() <= 0){
					Integer userNum = eliteAdapter.getEliteFollowCountByType(TEliteFollowType.ELITE_USER);
					tEliteFollows = eliteAdapter.getEliteFollowByType(TEliteFollowType.ELITE_USER, 0, userNum);
					redisCache.put(Constants.ELITE_FOLLOW_USER_KEY, tEliteFollows);					
			}		
			log.info("invoke first login follow default user userId = {}, default bpId={}", new Object[]{bpId.toString(), tEliteFollows.toString()});
					for(TEliteFollow tEliteFollow : tEliteFollows){
						RelationAction relationAction = new RelationAction();
						relationAction.setUserId(bpId)
									  .setUserType(BpType.Elite_User.getValue())
									  .setType(ActionType.TYPE_FOLLOW.getValue())
									  .setObjectId(tEliteFollow.getBpId())
									  .setObjectType(BpType.Elite_User.getValue())
									  .setActTime(new Date().getTime())
									  .setActIp(actIp)
									  .setActPort(actPort);
						Boolean result = thalloAdapter.doFollow(relationAction);
						log.info("first login follow bpId = " + tEliteFollow.getBpId() +"succeed");
				}
	
			} catch (Exception e){
				log.error("", e);
			}
	}



	@Override
	public void tagFollow(Long bpId, Long actIp, Integer actPort) {
		try{
			Integer tagNum = eliteAdapter.getEliteFollowCountByType(TEliteFollowType.ELITE_TAG);
			List<TEliteFollow> tEliteFollows = (List<TEliteFollow>) redisCache.get(Constants.ELITE_FOLLOW_TAG_KEY);
			if(null == tEliteFollows || tEliteFollows.size() <= 0){
			tEliteFollows = eliteAdapter.getEliteFollowByType(TEliteFollowType.ELITE_TAG, 0, tagNum);
			redisCache.put(Constants.ELITE_FOLLOW_TAG_KEY, tEliteFollows);
			}
			log.info("invoke first login follow default userId userId = {}, default tagId = {}", new Object[]{bpId.toString(), tEliteFollows.toString()});
			for(TEliteFollow tEliteFollow : tEliteFollows){
				RelationAction relationAction = new RelationAction();
				relationAction.setUserId(bpId)
							  .setUserType(BpType.Elite_User.getValue())
							  .setType(ActionType.TYPE_FOLLOW.getValue())
							  .setObjectId(tEliteFollow.getBpId())
							  .setObjectType(BpType.Tag.getValue())
							  .setActTime(new Date().getTime())
							  .setActIp(actIp)
							  .setActPort(actPort);
				Boolean result = thalloAdapter.doFollow(relationAction);
				log.info("first login follow tagId = " + tEliteFollow.getBpId() + "succeed");
			}
		} catch (Exception e){
			log.error("" ,e);
		}
		
	}



	@Override
	public void firstLoginFollow(final Long bpId, final HttpServletRequest request) {
		Boolean isFirstLogin = isFirstLogin(bpId);
		final Long actIp = RequestUtil.getClientIPLong(request);
		final Integer actPort = RequestUtil.getClientPort(request);
		final AgentSource source = AgentUtil.getSource(request);
		if(isFirstLogin){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					userFollow(bpId, actIp, actPort);
					tagFollow(bpId, actIp, actPort);
					TEliteUser user = new TEliteUser();
					user.setBpId(bpId).setFirstLoginTime(new Date().getTime()).setLastLoginTime(new Date().getTime()).setStatus(1).setFirstLogin(source.getValue());
					try {
						Long bpId = eliteAdapter.insertUser(user);
						if(null != bpId && bpId > 0 ) log.info("insert elite user succeed! user bpId = {}", new Object[]{bpId});
//						log.info("build index for user <bpId={}>", bpId);
//						eliteAdapter.rebuildUser(bpId);
//						log.info("build index succeeded");
					} catch (TException e) {
						log.error("", e);
					}
				}
			}).start();
		} else {
			new Thread(new Runnable() {				
				@Override
				public void run() {
					try {
						TEliteUser user = eliteAdapter.getUserByBpId(bpId);
						user.setLastLoginTime(new Date().getTime());
						eliteAdapter.updateUser(user);
					} catch (TException e) {
						log.error("", e);
					}
					
				}
			}).start();
		}
		
	}
	
	//重建所用用户索引
	@Override
	public void rebuildUserIndex(){
		Long num = ssdbCache.zCount(Constants.ELITE_FOLLOW_USER_LOGIN);
		log.info("the num of elite production is " + num );
		List<String> ids = ssdbCache.zRange(Constants.ELITE_FOLLOW_USER_LOGIN, 0, Integer.valueOf(num.intValue()));
		
		String rebuildUserUri = "/elite/innerapi/rebuild-index/user";
		long time = System.currentTimeMillis();
		String method = "POST";
        String sec = "mdQeoF3qAogwocjoz8EKZgeDSH3yRv9i";
        String appkey = "bp-decoration-search";
        Map<String, String> headers = new HashMap<>();
        headers.put("Timestamp", String.valueOf(time));
        headers.put("Auth", appkey + "|" + generateSignature(rebuildUserUri, method, time, sec));
        Map<String, String> params = new HashMap<>();
		if(null != ids){
			for(String id : ids){
				params.put("bpId", id);
//				String res = HttpUtil.post("http://10.10.3.146:8012" + rebuildUserUri, params, headers);
				String res = HttpUtil.post(host + rebuildUserUri, params, headers);
	            log.info("rebuild user bpid = {}, result= {}" , new Object[]{id, res});
			}
		}
	}
	//再测试端重建用户索引
	@Override
	public void rebuildUserIndexTest(){
		BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
		Configuration configuration = (Configuration) SpringUtil.getBean("configuration");
		String topic = configuration.get("index.kafka.topic");
		Long num = ssdbCache.zCount(Constants.ELITE_FOLLOW_USER_LOGIN);
		log.info("the num of elite production is " + num );
		List<String> ids = ssdbCache.zRange(Constants.ELITE_FOLLOW_USER_LOGIN, 0, Integer.valueOf(num.intValue()));
		
		if(null != ids){
			for(String id : ids){
				JSONObject msg = new JSONObject();
				msg.put("objectId", id);
				msg.put("objectType", BpType.Elite_User.getValue());
				msg.put("notifyType", EliteNotifyType.ELITE_NOTIFY_UPDATE.getValue());
				kafkaProducer.send(topic, msg.toString());
				log.info("rebuild user detail : " + msg.toString());
			}
		}
	}
	
    public static String generateSignature(String uri, String method, long timestamp, String secret) {
		String text = method + SEPERATOR + uri + SEPERATOR + timestamp;
		try {
			byte[] data = secret.getBytes();
			// 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
			SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
			// 生成一个指定 Mac 算法 的 Mac 对象
			Mac mac = Mac.getInstance("HmacSHA1");
			// 用给定密钥初始化 Mac 对象
			mac.init(secretKey);
			String r = new String(Base64.encodeBase64(mac.doFinal(text
			.getBytes()))).trim();
			return r;
		} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
		} catch (InvalidKeyException e) {
		e.printStackTrace();
		} catch (IllegalStateException e) {
		e.printStackTrace();
		}
	return "";
    }

}
