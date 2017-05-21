package com.sohu.bp.elite.util;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.BeanManagerFacade;
import com.sohu.bp.elite.Configuration;
import com.sohu.bp.util.FormatUtil;

/**
 * 对字符串进行安全过滤
 * create time: 2015年11月17日 下午10:56:04
 * @auther dexingyang
 */
public class SecurityUtil {

    private static Logger log = LoggerFactory.getLogger(SecurityUtil.class);
    
    private static final String regEx_script = "<[\\s]*?script[^>]*>[\\s\\S]*?</script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
    private static final Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
    
    private static String[] filter_words = {"script","onerror","createElement","appendChild","onload","XMLHttpRequest"};
    private static boolean enableOuterLink = false;
    private static Configuration configuration = null;
    
    static{
        configuration = BeanManagerFacade.getConfiguration();
        
        String f_words = configuration.get("filter.words");
        if(f_words != null && f_words.length() > 0)
            filter_words = f_words.split(";");
        
        if(configuration.getBoolean("enable.outerlink")){
        	enableOuterLink = true;
        }
    }

    public static String filterOptions(String options) {
        if (StringUtils.isBlank(options)) {
            return "";
        }
        try {
            JSONArray optionJSONArray = JSONArray.fromObject(options);
            for (int i=0;i<optionJSONArray.size();i++) {
                JSONObject optionJSON = optionJSONArray.getJSONObject(i);
                if (optionJSON.containsKey("img")) {
                    optionJSON.put("img", filterWords(optionJSON.getString("img")));
                }
                if (optionJSON.containsKey("description")) {
                    optionJSON.put("description", filterWords(optionJSON.getString("description")));
                }
            }
            return optionJSONArray.toString();
        } catch (Exception e) {
            log.error("", e);
        }
        return "";

    }
    public static String filterWords(String input){
    	if(StringUtils.isBlank(input))
    		return input;
    	
    	Matcher m_script; 
        
        try {       
            m_script = p_script.matcher(input);      
            input = m_script.replaceAll(""); // 过滤script标签     
            
            //过滤如script字符串
            for(int i=0;i < filter_words.length;i++){
                input = input.replaceAll("(?i)"+filter_words[i], "");
            }
            
        } catch (Exception e) {    
            log.error("filter content error.",e);
        }      
    
        return input;// 返回文本字符串
    }
    
    /**
     * 对字符串内容进行安全过滤,是否过滤外链，由系统全局配置觉得
     * @param input
     * @return
     */
    public static String filterContent(String input){
    	return filterContent(input, enableOuterLink);
    }
    
    /**
     * 对字符串内容进行安全过滤
     * @param input
     * @param enableLink  是否允许外域链接
     */
    public static String filterContent(String input, boolean enableLink){
    	if(StringUtils.isBlank(input))
    		return input;
    	
        Matcher m_script; 
    
        try {       
            m_script = p_script.matcher(input);      
            input = m_script.replaceAll(""); // 过滤script标签     
            
            //过滤如script字符串
            for(int i=0;i < filter_words.length;i++){
                input = input.replaceAll("(?i)"+filter_words[i], "");
            }
            
            input = linkFilter(input, enableLink);
        } catch (Exception e) {    
            log.error("filter content error.",e);
        }      
    
        return input;// 返回文本字符串
    }
    
    /**
     * 对于非sohu域的链接进行过滤,对于是sohu域的链接,统一加上http头部
     * @param input
     * @Param enableAlink 是否允许A标签的外链
     * @return
     */
    public static String linkFilter(String input,boolean enableOuterLink)
    {
    	if(StringUtils.isBlank(input))
    		return input;
    	
        String content = "";
        
        try{
            Document doc = Jsoup.parse(input);
            
            //img标签的地址转换放到ImageHandler中,会把外域的图片下载转存到sohu域下
            //过滤a,iframe,link,script标签,video[src]暂时不加
            Elements eles = doc.select("a[href],link[href],iframe[src],script[src],img");
            Iterator<Element> it = eles.iterator();
            String url = "";
            while(it.hasNext()){
                Element ele = (Element)it.next();
                String tagName = ele.tagName();
                
                //不能用ele.absUrl("src")方法,这种对于没有'http://'的情况会返回空串
                if("a".equals(tagName)){
                    url = ele.attr("href");
                    boolean isSohuDomain = isSohuDomain(url);
                    //不允许外链，则进行过滤
                    if(!isSohuDomain && !enableOuterLink){
                    	ele.after(ele.text());
                    	ele.remove();
                    }else{
                    	//判断链接是否以http://开头
                    	String lowerCaseUrl = url.toLowerCase();
//                    	if(!lowerCaseUrl.startsWith("http://")&&!lowerCaseUrl.startsWith("https://")){
//                    		ele.attr("href", "http://"+url);
//                    	}
                    }
                }else if("iframe".equals(tagName)||"script".equals(tagName)||"link".equals(tagName)){
                	ele.remove();
                }else if("img".equals(tagName)){
                    url = ele.attr("src");
                    //url = ele.absUrl("src");    //对于图片不带'http://'头部的,直接清除到标签.其他情况交给ImageHandler处理
                    if(org.apache.commons.lang.StringUtils.isBlank(url)){
                        ele.remove();
                    }
                }
            }
            content = doc.body().html();
            content = FormatUtil.formatAfterJsoup(content);
        }catch(Exception e){
            log.error("handle http and out link error.",e);
        }
        
        return content;
    }
    
    
    public static boolean isSohuDomain(String url){
        if(org.apache.commons.lang.StringUtils.isBlank(url))
            return false;
        
        boolean ret = false;
        String uperCase = url.toUpperCase();
        if (uperCase.indexOf("SOHU.COM") > -1 
            || uperCase.indexOf("SOHU.NET") > -1 
            || uperCase.indexOf("ITC.CN") > -1
                ||uperCase.indexOf("FOCUS.CN") > -1
                || uperCase.indexOf("SOGOU.COM") > -1
                || uperCase.startsWith("/"))
            ret = true;
        return ret;
    }
    
}
