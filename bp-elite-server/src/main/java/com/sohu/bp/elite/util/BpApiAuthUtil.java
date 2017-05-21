package com.sohu.bp.elite.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * create time: 2015年12月2日 下午12:14:53
 * @auther dexingyang
 */
public class BpApiAuthUtil {

	public static final String app_key = "bp";
	private static final String app_secret = "IMV6NUu1i2r5x2gL7PMQy9gkrh6ljhrK";
	private static final String SEPERATOR = "|";
	
	public static final String HEADER_TIMESTAMP = "Timestamp";
    public static final String HEADER_AUTH = "Auth";
	
	public static String generateAuthParam(String uri,String method,
    		long timestamp) {
        String text = method+SEPERATOR+uri+SEPERATOR+timestamp;
        try {
			byte[] data = app_secret.getBytes();
			// 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
			SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
			// 生成一个指定 Mac 算法 的 Mac 对象
			Mac mac = Mac.getInstance("HmacSHA1");
			// 用给定密钥初始化 Mac 对象
			mac.init(secretKey);
			String r = new String(Base64.encodeBase64(mac.doFinal(text
					.getBytes()))).trim();
			return app_key+SEPERATOR+r;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return "";
    }
	
	public static Map<String, String> generateAuthHeaders(String uri,String method,
    		long timestamp){
		String auth = generateAuthParam(uri, method, timestamp);
		Map<String, String> headers = new HashMap<String,String>();
		headers.put(HEADER_TIMESTAMP, String.valueOf(timestamp));
		headers.put(HEADER_AUTH, auth);
		return headers;
	}
}
