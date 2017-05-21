package com.sohu.bp.elite.api.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 
 * @author: yuguangyuan
 * @date 2015年12月21日 下午2:41:58
 */
public class LifeSignatureUtil {
	
	private static final String appkey = "1141";
	private static final String salt = "5cfdb867e96374c7883b31d6928cc1bp";
	
	public static String generateSig(Map<String, String> map){
		map.put("appkey", appkey);
		return generateSig(map, salt);
	}

	public static String generateSig(Map<String, String> map, String saltValue)  {
        List<String> params = new ArrayList<String>();
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
        	String key = entry.getKey();
        	String value = entry.getValue();
        	params.add(key + "=" + value);
        }
        String soSigValue = generateSignature(params, saltValue);
        return soSigValue;
    } 
	
	public static String generateSignature(List params, String secret) {
		StringBuffer buffer = new StringBuffer();
		Collections.sort(params);
		for (int i = 0; i < params.size(); i++) {
			buffer.append((String) params.get(i));
		}
		buffer.append(secret);
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			StringBuffer result = new StringBuffer();
			byte[] bs = md.digest(buffer.toString().getBytes("UTF-8"));
			for (int i = 0; i < bs.length; i++) {
				byte b = bs[i];
				result.append(Integer.toHexString((b & 0xf0) >>> 4));
				result.append(Integer.toHexString(b & 0x0f));
			}
			return result.toString();
		} catch (java.security.NoSuchAlgorithmException ex) {
			return "";
		} catch (UnsupportedEncodingException uee) {
			return "";
		}
	}
}
