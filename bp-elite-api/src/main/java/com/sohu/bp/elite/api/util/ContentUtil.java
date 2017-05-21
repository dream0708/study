
package com.sohu.bp.elite.api.util;

import com.sohu.bp.elite.api.api.bean.ContentBean;
import com.sohu.bp.elite.model.TEliteSourceType;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author nicholastang
 * 2016-08-27 11:57:40
 * TODO
 */

public class ContentUtil {

	private static Logger logger = LoggerFactory.getLogger(ContentUtil.class);
	
	
/**
	 * 预处理富文本
	 * @param content
	 * @param richHtml
	 * @return
	 */

	public static String preDealContent(String content) {
	    if(StringUtils.isEmpty(content))
            return "";
        content = SecurityUtil.filterContent(content, false);
//      if(richHtml)
//          content = FormatUtil.doEdit(content);
        
        //content = HtmlEncodeUtil.forHtml(content);
        
        content = washHtml(content);
        return content;
	}
	
	   /**
     * 清洗html
     * @param html
     * @return
     */
    public static String washHtml(String html) {
        if(StringUtils.isBlank(html))
            return "";
        html = html.replaceAll("(?i)</[div|tr|p]>", "<br>");
        Whitelist whitelist = new Whitelist();
        whitelist.addTags("br").addAttributes("img", "src").addAttributes("a", "href");
        whitelist.addEnforcedAttribute("img", "class", "pic")
                .addEnforcedAttribute("a", "target", "_blank")
                .addEnforcedAttribute("a", "class", "link");
        html = Jsoup.clean(html,whitelist);
        html = html.replace("\n", "");
        //html = washTagA(html);
        return html;
    }

    public static ContentBean parseContent(String content, TEliteSourceType source)
    {
        ContentBean contentBean = new ContentBean();
        if(StringUtils.isBlank(content))
            return contentBean;

        String plainText = "";
        List<String> imgList = new ArrayList<String>();
        try
        {
            Document doc = Jsoup.parse(content);
            Elements imgEles = doc.select("img");
            Iterator<Element> iter = imgEles.iterator();
            while(iter.hasNext())
            {
                Element ele = (Element)iter.next();
                String imgUrl = ele.attr("src");
                if(StringUtils.isNotBlank(imgUrl))
                    imgList.add(imgUrl);
                ele.remove();
            }
            plainText = doc.body().html();
            if(source == TEliteSourceType.WRITE_WX){
                Whitelist whitelist = new Whitelist();
                whitelist.addTags("br");
                plainText = Jsoup.clean(plainText, whitelist);
                plainText = HtmlUtils.htmlUnescape(plainText.replaceAll("\n", "").replaceAll("<br>", "\n"));
            } else {
                plainText = HtmlUtils.htmlUnescape(Jsoup.clean(plainText, new Whitelist()));
            }
        }catch(Exception e)
        {
            logger.error("", e);
        }

        contentBean.setPlainText(plainText);
        contentBean.setImgList(imgList);

        return contentBean;
    }
}

