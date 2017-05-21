package com.sohu.bp.elite.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.Configuration;
import com.sohu.bp.util.SpringUtil;

/**
 * 前端页面URL 加解密工具,主要加密对外暴露的 id
 * 商品 id:  http://life.sohu.com/p?pde=h2QKten07cB6H833mlQ7VQ
 * 店铺 id:  http://life.sohu.com/s?spe=h2QKten07cB6H833mlQ7VQ
 * 订单 id:  ode=h2QKten07cB6H833mlQ7VQ
 * 商品标签 id:  tge=h2QKten07cB6H833mlQ7VQ
 * 
 * create time: 2015年12月29日 下午6:18:01
 * @auther dexingyang
 */
public class FrontUrlGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(FrontUrlGenerator.class);

	public static String FRONT_DOMAIN = "http://life.sohu.com";
	
	public static String COMMODITY_URL_PREFIX = "/p?pde=";
	public static String COMMODITY_URL_PREFIX2 = "/p/";
	
	public static String SHOP_URL_PREFIX = "/s?spe=";
	
	public static String TOPIC_URL_PREFIX = "/c?cle=";
	public static String CATALOG_URL_PREFIX = "/l?";
//	public static final String CATALOG_URL_PREFIX3 = "/c/";
	
	public static String SNAPSHOT_URL_PREFIX = "/p/snapshot.html";
	
	public static String ARTICLE_CATALOG_URL = "/article/catalog/";
	public static String ARTICLE_URL = "/article/";
	
	static{
		Configuration config = (Configuration)SpringUtil.getBean("configuration");
		FRONT_DOMAIN = config.get("frontDomain", FRONT_DOMAIN);
		COMMODITY_URL_PREFIX = FRONT_DOMAIN + COMMODITY_URL_PREFIX;
		COMMODITY_URL_PREFIX2 = FRONT_DOMAIN + COMMODITY_URL_PREFIX2;
		SHOP_URL_PREFIX = FRONT_DOMAIN + SHOP_URL_PREFIX;
		TOPIC_URL_PREFIX = FRONT_DOMAIN + TOPIC_URL_PREFIX;
		CATALOG_URL_PREFIX = FRONT_DOMAIN + CATALOG_URL_PREFIX;
		//CATALOG_URL_PREFIX3 = FRONT_DOMAIN + CATALOG_URL_PREFIX3;
		SNAPSHOT_URL_PREFIX = FRONT_DOMAIN + SNAPSHOT_URL_PREFIX;
		
		ARTICLE_CATALOG_URL = FRONT_DOMAIN + ARTICLE_CATALOG_URL;
		ARTICLE_URL = FRONT_DOMAIN + ARTICLE_URL;
	}
	
	public static String genCommodityUrl(Long commodityId){
		String encryptId = com.sohu.bp.utils.crypt.AESUtil.encryptIdV2(commodityId);
		if(StringUtils.isBlank(encryptId))
			return "";
		else
			return COMMODITY_URL_PREFIX+encryptId;
	}
	
	public static Long getCommodityIdFromUrl(String url){
		String encryptId = url.replace(COMMODITY_URL_PREFIX, "");
		encryptId = encryptId.replace(COMMODITY_URL_PREFIX2, "");
		return com.sohu.bp.utils.crypt.AESUtil.decryptIdV2(encryptId);
	}
	
	public static String genShopUrl(Integer sellerId){
		String encryptId = com.sohu.bp.utils.crypt.AESUtil.encryptIdV2(sellerId.longValue());
		if(StringUtils.isBlank(encryptId))
			return "";
		else
			return SHOP_URL_PREFIX+encryptId;
	}
	
	public static Long getShopIdFromUrl(String url){
		String encryptId = url.replace(SHOP_URL_PREFIX, "");
		return com.sohu.bp.utils.crypt.AESUtil.decryptIdV2(encryptId);
	}
	
	public static String genCatalogUrl(Integer catalogId){
		String encryptId = com.sohu.bp.utils.crypt.AESUtil.encryptIdV2(catalogId.longValue());
		if(StringUtils.isBlank(encryptId))
			return "";
		else
			return CATALOG_URL_PREFIX+encryptId;
	}
	
	public static Long getCatalogIdFromUrl(String url){
		String encryptId = url.replace(CATALOG_URL_PREFIX, "");
		int index = encryptId.indexOf("catalogId=");
		if(index < 0)
			return -1L;
		encryptId = encryptId.substring(index + "catalogId=".length());
		index = encryptId.indexOf("&");
		if(index >= 0) {
			encryptId = encryptId.substring(0, index);
		}
		
		if(NumberUtils.isDigits(encryptId))
			return NumberUtils.toLong(encryptId);
		return com.sohu.bp.utils.crypt.AESUtil.decryptIdV2(encryptId);
	}
	
	public static Long getTopicIdFromUrl(String url) {
		String encryptId = url.replace(TOPIC_URL_PREFIX, "");
		
		if(NumberUtils.isDigits(encryptId))
			return NumberUtils.toLong(encryptId);
		return com.sohu.bp.utils.crypt.AESUtil.decryptIdV2(encryptId);
	}
	
	public static String encryptId(long id) {
		String encryptId = com.sohu.bp.utils.crypt.AESUtil.encryptIdV2(id);
		if(StringUtils.isBlank(encryptId))
			return "";
		else
			return encryptId;
    }
	
	public static long decryptId(String s) {
		return com.sohu.bp.utils.crypt.AESUtil.decryptIdV2(s);
    }
	
	public static String genSnapshotUrl(Long orderId, Long commodityId, Long tagId){
		String encOrderId = com.sohu.bp.utils.crypt.AESUtil.encryptIdV2(orderId.longValue());
		if(StringUtils.isBlank(encOrderId))
			return "";
		String encCommodityId = com.sohu.bp.utils.crypt.AESUtil.encryptIdV2(commodityId.longValue());
		if(StringUtils.isBlank(encCommodityId))
			return "";
		String encTagId = com.sohu.bp.utils.crypt.AESUtil.encryptIdV2(tagId.longValue());
		if(StringUtils.isBlank(encTagId))
			return "";
		
		return SNAPSHOT_URL_PREFIX+"?ode="+encOrderId+"&pde="+encCommodityId+"&tge="+encTagId;
	}
    
    public static void main(String args[]){
//    	System.out.println(genCommodityUrl(100000305L));
//    	System.out.println(getCommodityIdFromUrl("http://life.sohu.com/p/cbn9b4h_59oaqohuuokppa_1181969"));
//    	System.out.println(getShopIdFromUrl("http://life.sohu.com/s?spe=y_2aukiardkvaekomfdkva_2612373"));
    	System.out.println(getCatalogIdFromUrl("http://life.sohu.com/l?catalogId=1007&page=1&filterData=%7B%22sortField%22%3A%22discountPrice%22%2C%22sortType%22%3A%222%22%7D"));
    }
}
