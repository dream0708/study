package com.sohu.bp.elite.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import com.sohu.bp.elite.bean.ContentBean;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.util.FormatUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-08-27 11:57:40
 * TODO
 */
public class ContentUtil {

	private static Logger logger = LoggerFactory.getLogger(ContentUtil.class);
	public static final String AT_USER_ATTR_NAME = "data-at-user-id";
	public static final String AT_MARK_ATTR_NAME = "data-at-mark";
	public static final String AT_USER_ATTR_ORIID = "data-user-originid";
	public static final String AT_USER_ATTR_TYPE = "data-user-type";
	public static final String DATA_ORIGIN_IMAGE = "data-origin-image";
	public static final String DEFAULT_IMG_PLACEHOLDER = "###[图片]###";
	public static final String FINAL_IMG_TEXT = " [图片] ";
	public static final List<String> WhiteDomainList = new ArrayList<String>(){
		{
			add(".sohu.com");
			add(".focus.cn");
			add(".sogou.com");
		}
	};

	private static String WhiteDomainReg = "";
	static{
		StringBuilder regSB = new StringBuilder("(http|https)://");
		for(int i=0; i<WhiteDomainList.size(); i++){
			String domain = WhiteDomainList.get(i);
			if(StringUtils.isBlank(domain))
				continue;
			if(i > 0)
				regSB.append("|");
			regSB.append("(\\S+").append(domain).append("/)");
		}
		regSB.append("\\s*");
		WhiteDomainReg = regSB.toString();
	}
	/**
	 * 预处理富文本
	 * @param content
	 * @param richHtml
	 * @param source 根据源的不同来进行不同处理，对于微信小程序来讲，需要将/n变为<br>
	 * @return
	 */
	public static String preDealContent(String content, boolean richHtml, boolean titleFlag) {
		if(StringUtils.isEmpty(content))
			return "";
		if (titleFlag) {
		    content = content.replaceAll("\\n", "");
		    content = Jsoup.clean(content, new Whitelist());
		} else {
		    content = content.replaceAll("\\n", "<br>");
		    content = SecurityUtil.filterContent(content, false);
		    content = washHtml(content);
		}
		return content;
	}
	
	//截取str 从0开始length长度的字符串
	public static String subString(String str, int length){
		if(str == null || str.trim().isEmpty())
			return "";
		if(str.length() <= length)
			return str;
		else
			return str.substring(0, length);
	}
	
	/**
	 * 解析文本
	 * @param content
	 * @return
	 */
	public static ContentBean parseContent(String content, TEliteSourceType source)
	{
		ContentBean contentBean = new ContentBean();
		if(StringUtils.isBlank(content))
			return contentBean;
		
		String plainText = "";
		String imgText = "";
		List<String> imgList = new ArrayList<String>();
		try {
			Document doc = Jsoup.parse(content);
			Elements imgEles = doc.select("img");
			Iterator<Element> iter = imgEles.iterator();
			while(iter.hasNext())
			{
				Element ele = (Element)iter.next();
				String imgUrl = ele.attr("src");
				if(StringUtils.isNotBlank(imgUrl)) {
					imgList.add(ImageUtil.removeImgProtocol(imgUrl));
				    ele.before(DEFAULT_IMG_PLACEHOLDER);
				}
				ele.remove();
			}
			imgText = doc.body().html();
	        imgText = HtmlUtils.htmlUnescape(imgText.replaceAll("\\n", ""));
			Whitelist whitelist = new Whitelist();
			whitelist.addTags("br");
			imgText = Jsoup.clean(imgText, whitelist);
			if (source != TEliteSourceType.WRITE_WX) {
			    contentBean.setFormatText(imgText.replace(DEFAULT_IMG_PLACEHOLDER, ""));
			} else {
			    contentBean.setFormatText(imgText.replace(DEFAULT_IMG_PLACEHOLDER, "").replaceAll("<br>", "\n"));
			}
			//TODO 等小程序改完了取format之后可以更改此地方
			if (source != TEliteSourceType.WRITE_WX) {
			    imgText = imgText.replaceAll("<br>", " ");
			} else {
			    imgText = imgText.replaceAll("<br>", "\n");
			}
		} catch(Exception e) {
			logger.error("", e);
		}
		plainText = imgText.replace(DEFAULT_IMG_PLACEHOLDER, "");
		imgText = imgText.replace(DEFAULT_IMG_PLACEHOLDER, FINAL_IMG_TEXT);
		contentBean.setPlainText(plainText);
		contentBean.setImgList(imgList);
		contentBean.setImgText(imgText);
		
		return contentBean;
	}
	
	public static ContentBean parseContent(String content){
		return parseContent(content, null);
	}
	
	/**
	 * 清洗html
	 * @param html
	 * @return
	 */
	public static String washHtml(String html) {
		if(StringUtils.isBlank(html))
			return "";
		html = html.replaceAll("(?i)<(/)?(div|tr|p)>", "<br>");
		Whitelist whitelist = new Whitelist();
		whitelist.addTags("br").addAttributes("img", "src").addAttributes("a", "href", AT_USER_ATTR_NAME, AT_MARK_ATTR_NAME, AT_USER_ATTR_ORIID, AT_USER_ATTR_TYPE);
		whitelist.addEnforcedAttribute("img", "class", "pic")
				.addEnforcedAttribute("a", "target", "_self")
				.addEnforcedAttribute("a", "class", "link");
		html = Jsoup.clean(html,whitelist);
		html = html.replace("\n", "");
		html = brTrim(html);
		//html = washTagA(html);
		return html;
	}
	
	/**
	 * 清晰a标签
	 * @param html
	 * @return
	 */
	public static String washTagA(String html) {
		if(StringUtils.isBlank(html))
			return "";


		logger.info(WhiteDomainReg);
		Document doc = Jsoup.parse(html);
		if(null == doc)
			return "";
		Elements aEles = doc.select("a");
		Iterator<Element> iter = aEles.iterator();
		while(iter.hasNext()) {
			Element ele = (Element)iter.next();
			String link = ele.absUrl("href");
			if(StringUtils.isNotBlank(link)){
				link = link.toLowerCase();
				Matcher matcher = Pattern.compile(WhiteDomainReg).matcher(link);
				if(matcher.find()){
					continue;
				}
			}

			logger.info("link is not illegal.link="+link);
			ele.remove();
		}

		return doc.body().html();
	}
	
	/**
	 * 清洗content中img标签的协议头
	 * @param content
	 * @return
	 */
	public static String removeContentImageProtocol(String content) {
		Document doc = Jsoup.parse(content);
		Elements eles = doc.select("img");
		for(Element ele : eles){
			String src = ImageUtil.removeImgProtocol(ele.attr("src"));
			ele.attr("src", src);
		}
		return doc.body().html().toString();
	}
	
	public static String removeOptionsImageProtocol(String content) {
	    if (StringUtils.isBlank(content)) return "";
	    try {
	        JSONArray options = JSONArray.fromObject(content);
	        for (int i = 0; i < options.size(); i++) {
	            JSONObject option = options.getJSONObject(i);
	            option.put("img", ImageUtil.removeImgProtocol(option.getString("img")));
	        }
	        content = options.toString();
	    } catch (Exception e) {
	        logger.error("", e);
	    }
	    return content;
	}
	
    /**
     * 用于将内容换为具有小图和"data-origin-image"属性的内容
     * @param content
     * @return
     */
    public static String changeContentImgs(String content) {
        if (StringUtils.isBlank(content)) return "";
        Document doc = Jsoup.parse(content);
        Elements eles = doc.select("img");
        Iterator<Element> iterator = eles.iterator();
        while (iterator.hasNext()) {
            Element ele = iterator.next();
            String imgUrl = ele.attr("src");
            if (StringUtils.isBlank(imgUrl)) ele.remove();
            ele.attr("src", ImageUtil.getSmallImage(imgUrl));
            ele.attr(DATA_ORIGIN_IMAGE, ImageUtil.getSmallImage(imgUrl, null, Constants.MEDIUM_IMAGE_RATIO));
        }
        return doc.body().html();
    }
    
    /**
     * 获取专题内容中的问题数量
     * @param detail
     * @return
     */
   public static int getSubjectNum(String detail) {
       Pattern pattern = Pattern.compile("\\d{3,}");
       if (detail.contains(" " + Constants.SUBJECT_SPLIT_CHAR))
           pattern = Pattern.compile("(\\d+{3,})\\s+##");
       int num = 0;
       Matcher m = pattern.matcher(detail);
       while (m.find()) {
           num++;
       }
       return num;
   }
    

	public static String brTrim(String html) {
		if (StringUtils.isBlank(html)) {
			return "";
		}
		html = html.replaceAll("(?i)^(\\s|<br>)*", "").replaceAll("(?i)(<br>(\\s)*){2,}", "<br>").replaceAll("(?i)(\\s|<br>)*$", "");
		return html;
	}
}
