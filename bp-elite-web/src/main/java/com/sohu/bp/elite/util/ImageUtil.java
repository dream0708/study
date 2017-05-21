package com.sohu.bp.elite.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sohu.bp.elite.constants.Constants;

/**
 * 用于处理图片转换的工具类
 * @author zhijungou
 * 2016年11月25日
 */
public class ImageUtil {
	
	private static final Map<String, String> HTTPS_TRANSLATE_DOMAIN;
	
	static{
		HTTPS_TRANSLATE_DOMAIN = new LinkedHashMap<String, String>();
        HTTPS_TRANSLATE_DOMAIN.put("//jj-crawl-img\\.bjcnc\\.img\\.sohucs\\.com", "https://home0.img.focus.cn");
        HTTPS_TRANSLATE_DOMAIN.put("//jj-crawl-img\\.bjctc\\.img\\.sohucs\\.com", "https://home0.img.focus.cn");
        HTTPS_TRANSLATE_DOMAIN.put("//[^/]*?img[^/]*?sohucs\\.com", "https://home.img.focus.cn");
        HTTPS_TRANSLATE_DOMAIN.put("(?<!https:)(?:http:){0,1}//(?=[^/]*\\.(?:com|cn|net|org|edu|tv))", "https://");
	}
	
	/**
	 * 获得wap端焦点小图
	 * @param source
	 * @return
	 */
	public static String getWapFocusCover(String source){
		String img = source;
		if(null != img){
			Matcher m = Pattern.compile(Constants.CUT_ABLE_SOURCE).matcher(source);
			if(m.find()){
				m = Pattern.compile(".com/(.*?/)").matcher(img);
				if(m.find()){
					StringBuilder imgOri = new StringBuilder(img);
					imgOri.delete(m.start(1), m.end(1));
					img = imgOri.toString();
				}
				m = Pattern.compile(".com").matcher(img);
				if(m.find()){
				StringBuilder imgNew = new StringBuilder(img);
				imgNew.insert(m.end(), Constants.LITTLE_FOCUS_COVER);
				img = imgNew.toString();
				}
			}
		}
		img = removeImgProtocol(img);
		return img;
	}
	
	/**
	 * 根据比例来获取小图
	 * @param source
	 * @param ratio
	 * @return
	 */
	public static String getSmallImage(String source, String cutField, String ratio){
		String ratioString = null == cutField ? ("/o_crop,c_zoom,w_" + ratio + ",h_" + ratio) : cutField;
		String img = source;
		if(null != img){
			Matcher m = Pattern.compile(Constants.CUT_ABLE_SOURCE).matcher(source);
			if(m.find()){
				m = Pattern.compile(".com/(.*?/)").matcher(img);
				if(m.find()){
					StringBuilder imgOri = new StringBuilder(img);
					imgOri.delete(m.start(1), m.end(1));
					img = imgOri.toString();
				}
				m = Pattern.compile(".com").matcher(img);
				if(m.find()){
				StringBuilder imgNew = new StringBuilder(img);
				imgNew.insert(m.end(), ratioString);
				img = imgNew.toString();
				}
			}
		}
		img = removeImgProtocol(img);
		return img;
	}
	/**
	 * 用于去除img标签的协议头
	 * @param source
	 * @return
	 */
	public static String removeImgProtocol(String source){
		if(null == source) return null;
		String res = source;
		Pattern pattern = Pattern.compile(Constants.PROTOCAL_HEADER);
		Matcher matcher = pattern.matcher(source);
		if(matcher.find()){
			res = matcher.group(1);
		}
		return res;
	}
	
	public static List<String> removeImgListProtocol(List<String> sources){
		List<String> imgs = new ArrayList<>();
		if(null != sources && sources.size() > 0){
			sources.forEach(source -> imgs.add(removeImgProtocol(source)));
		}
		return imgs;
	}
	
	public static String getSmallImage(String source){
		return getSmallImage(source, null, Constants.LITTLE_IMAGE_RATIO);
	}
	
	public static String changeWechatImgDomain(String html){
		Set<Map.Entry<String, String>> entrys = HTTPS_TRANSLATE_DOMAIN.entrySet();
		for(Map.Entry<String, String> entry : entrys){
//			Matcher matcher = Pattern.compile(entry.getKey()).matcher(html);
//			while(matcher.find()){
//				StringBuilder content = new StringBuilder(html);
//				content.replace(matcher.start(0), matcher.end(0), entry.getValue());
//				html = content.toString();
//				matcher = Pattern.compile(entry.getKey()).matcher(html);
//			}
			html = html.replaceAll(entry.getKey(), entry.getValue());
		}
		return html;
	}
	
	public static void main(String[] args) {
		String src = "<img src=\"//asdasdsadcom/c_lfill,w_115,h_115,g_center/bp_cdc6330b657641669cbad6e35fef1aa2\" class = \"img-circle\">"+
				"<img src=\"//avatarimg.bjcnc.img.sohucs.com/c_lfill,w_115,h_115,g_center/bp_cdc6330b657641669cbad6e35fef1aa2\" class = \"img-circle\"" +
				"//jj-crawl-img.bjctc.img.sohucs.com/c_lfill,w_115,h_115,g_center/95cfc84f1ec768d776e15189ba5945d8.jpg";
		String src2 = "<img src=\"//avatarimg.bjcnc.img.sohucs.com/c_lfill,w_115,h_115,g_center/bp_cdc6330b657641669cbad6e35fef1aa2\" class = \"img-circle\"";
		String src3 = "//avatarimg.bjcnc.img.sohucs.com/c_lfill,w_115,h_115,g_center/bp_622003b68c2b4eeab7e41b0b4a96835d";
		String src4 = "//q.qlogo.cn/qqapp/200034/7AC046941BBB5B4F3640581EECB4BA89/100";
		src4 = changeWechatImgDomain(src + src2 + src3 + src4);
		System.out.println("result : " + src4);
	}
}
