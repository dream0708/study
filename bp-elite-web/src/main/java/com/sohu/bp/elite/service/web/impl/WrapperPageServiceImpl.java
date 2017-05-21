package com.sohu.bp.elite.service.web.impl;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sohu.bp.elite.action.bean.question.EliteQuestionDisplayBean;
import com.sohu.bp.elite.action.bean.subject.SubjectBean;
import com.sohu.bp.elite.action.bean.tag.TagItemBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.decoration.model.ExpertRole;
import com.sohu.bp.elite.OverallData;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.enums.SideBarPageType;
import com.sohu.bp.elite.filter.OverallDataFilter;
import com.sohu.bp.elite.service.web.WrapperPageService;
import com.sohu.bp.elite.util.ContentUtil;
import com.sohu.bp.elite.util.HttpUtil;

/**
 * 
 * @author nicholastang
 * 2016-08-31 17:09:10
 * TODO
 */
public class WrapperPageServiceImpl implements WrapperPageService
{
	private Logger logger = LoggerFactory.getLogger(WrapperPageServiceImpl.class);
	private static final String IndexHeaderKey = "index_header";
	private static final String IndexHeaderWithoutBTKey = "index_header_without_bt";
	private static final String IndexFooterKey = "index_footer";
	private static final String ToolbarKey = "toolbar";
	private static final String SideBarKey = "sidebar";
	private static final String DomainHolder = "{domain}";
	private static final String VersionHolder = "{version}";
	private static final String HeadKeywords = "问吧、装修问答、装修知识、装修攻略";
 	private static final String HeadDescription = "搜狐焦点家居问吧是互动型家居知识分享社区，汇聚海量真实的装修用户群体，集合最受装修业主关注的问题和需求，分享真实、专业的装修经验和装修攻略。";
	private static final String Origin_Img_Domain = "avatarimg.bjctc.scs.sohucs.com";
	private static final String New_Img_Domain = "3074a34158850.cdn.sohucs.com";
	
	private static String LoggedDisFrag = "" + 
	"<li tb-block=\"login\">" + 
			"<a href=\"javascript:;\" btn-action=\"menu\">" + 
				"<span>%s</span>" + 
				"<i class=\"iconfont icons-arrowdown\">&#xe602;</i>" + 
			"</a>" + 
			"<div class=\"list-box login\">" + 
				"<div class=\"inner\">" + 
					"<div class=\"avatar-box\">" + 
						"<img src=\"%s\">" + 
					 "</div>" + 
					 "<h3><span>%s</span><small>%s</small></h3>" + 
					 "<div class=\"ctr-box\">" + 
					 	"<a href=\"javascript:;\" class=\"pull-right\" btn-action=\"logout\">退出</a>" + 
					 	"<a href=\"/decoration/profile.html\" tb-login=\"1\">帐号管理</a>" + 
					 "</div>" + 
				"</div>" + 
             "</div>" + 
	"</li>";
	
	private static String SideBarDisFrag = "" +
	"<div class=\"pull-left sidebar\">" + 
		"<div class=\"coil info\">" + 
			"<a href=\"/decoration/profile/aut/update.html\" class=\"avatar-edit\">" + 
				"<i class=\"mask\"></i><span class=\"mask\">编辑头像</span>" + 
				"<img src=\"%s\">" + 
			"</a>" + 
			"<h4 class=\"title\">%s</h4>" + 
			"<p class=\"txt-center\"><a href=\"/decoration/profile/aut/update.html\">编辑个人资料</a></p>" + 
		"</div>" + 
		"<div class=\"coil location\">" + 
		"<h5 class=\"title\">" + 
			"%s" + 
			"<span>上次登录</span>" + 
		"</h5>" + 
		"<p>%s</p>" + 
		"</div>" + 
		"<div class=\"coil nav-list\">" + 
			"%s" + 
			"<section>" + 
				"<h4 class=\"title\"><a href=\"javascript:;\" class=\"active\"><span class=\"pull-right\"><i class=\"iconfont icons-arrowup\">&#xe636;</i>" +
				"<i class=\"iconfont icons-arrowdown\">&#xe602;</i></span>我的账户</a></h4>" + 
				"<ul class=\"box hide\">" + 
					"<li class=\"first\"><span class=\"ico-dot\"><i></i></span><a href=\"/decoration/profile/security.html\">账号安全</a></li>" + 
					"<li class=\"last\"><span class=\"ico-dot\"><i></i></span><a href=\"/decoration/profile/bind.html\">账号绑定</a></li>" + 
				"</ul>" + 
			"</section>" + 
			"<section>" + 
				"<h4 class=\"title\">" + 
					"<a href=\"javascript:;\" class=\"active\"><span class=\"pull-right\"><i class=\"iconfont icons-arrowup\">&#xe636;</i>" +
					"<i class=\"iconfont icons-arrowdown\">&#xe602;</i></span>我的服务</a>" + 
				"</h4>" + 
				"<ul class=\"box hide\">" + 
					"<li class=\"first\"><span class=\"ico-dot\"><i></i></span><a href=\"/decoration/profile/manage/signup.html\">我的装修</a></li>" + 
					"<li class=\"last\"><span class=\"ico-dot\"><i></i></span><a href=\"/decoration/profile/message.html\">我的消息</a></li>" + 
				"</ul>" + 
			"</section>" + 
			"<section>" + 
				"<h4 class=\"title\">" + 
					"<a href=\"javascript:;\" class=\"active\"><span class=\"pull-right\"><i class=\"iconfont icons-arrowup\">&#xe636;</i>" +
					"<i class=\"iconfont icons-arrowdown\">&#xe602;</i></span>关注收藏</a>" + 
				"</h4>" + 
				"<ul class=\"box hide\">" + 
					"<li class=\"first\"><span class=\"ico-dot\"><i></i></span><a href=\"/decoration/profile/follow/company.html\">我的关注</a></li>" + 
					"<li class=\"last\"><span class=\"ico-dot\"><i></i></span><a href=\"/decoration/profile/favorite/works.html\">我的收藏</a></li>" + 
				"</ul>" + 
			"</section>" + 
			"%s" + 
			"<section>" + 
				"<h4 class=\"title\">" + 
					"<a href=\"javascript:;\"><span class=\"pull-right\"><i class=\"iconfont icons-arrowup\">&#xe636;</i>" +
					"<i class=\"iconfont icons-arrowdown\">&#xe602;</i></span>我的问吧</a>" + 
				"</h4>" + 
				"<ul class=\"box\">" + 
					"<li class=\"first %s\"><span class=\"ico-dot\"><i></i></span><a href=\"http://bar.focus.cn/pqp\">我的提问</a></li>" +
					"<li class=\"%s\"><span class=\"ico-dot\"><i></i></span><a href=\"http://bar.focus.cn/pap\">我的回答</a></li>" +
					"<li class=\"%s\"><span class=\"ico-dot\"><i></i></span><a href=\"http://bar.focus.cn/mf\">我的粉丝</a></li>" +
					"<li class=\"%s\"><span class=\"ico-dot\"><i></i></span><a href=\"http://bar.focus.cn/cq\">我的关注</a></li>" +
					"<li class=\"last %s\"><span class=\"ico-dot\"><i></i></span><a href=\"http://bar.focus.cn/ca\">我的收藏</a></li>" +
				"</ul>" + 
			"</section>" + 
			"<section>" + 
				"<h4 class=\"title\"><a href=\"/decoration/profile/diary/list.html\">发布装修日记</a></h4>" + 
			"</section>" + 
		"</div>" + 
	"</div>";
	
	private static final String HEADER_JS = "" + 
    "<script>" +
        "$(function(){" +
            "(function(){" +
              "if($('[data-finish]').length==0){" +
                "var url = window.location.href.split('?');" + 
                "if(url[1]){" + 
                  "var reg = /^bpdebug=/;" +
                   "if(reg.test(url[1])){" +
                    "return;" +
                  "}" +
                "}" +
               "$.post('/common/error?errorUrl='+url[0]);" +
              "window.location.href = '/common/404?errorUrl='+url[0];" +
            "}" +
            "})()" +   
      "});" +
    "</script>";
	
	private static final String FOOTER_HTML = "<div data-finish=\"finish\" style=\"display:none\"></div>";
     
	private String indexHeaderFetchUrl;
	private String indexFooterFetchUrl;
	private CacheManager redisCacheManager;
	
	private RedisCache wrapperPageRedis;
	
	public String getIndexHeaderFetchUrl() {
		return indexHeaderFetchUrl;
	}

	public void setIndexHeaderFetchUrl(String indexHeaderFetchUrl) {
		this.indexHeaderFetchUrl = indexHeaderFetchUrl;
	}

	public String getIndexFooterFetchUrl() {
		return indexFooterFetchUrl;
	}

	public void setIndexFooterFetchUrl(String indexFooterFetchUrl) {
		this.indexFooterFetchUrl = indexFooterFetchUrl;
	}

	public CacheManager getRedisCacheManager() {
		return redisCacheManager;
	}

	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}
	
	public void init()
	{
		wrapperPageRedis = (RedisCache)redisCacheManager.getCache(CacheConstants.CACHE_BP_ELITE_INDEX_PAGE);
		wrapperPageRedis.remove(IndexHeaderKey);
		wrapperPageRedis.remove(IndexHeaderWithoutBTKey);
		wrapperPageRedis.remove(IndexFooterKey);
		//wrapperPageRedis.remove(ToolbarKey);
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.web.WrapperPageService#getIndexHeaderHtml()
	 */
	@Override
	public String getIndexHeaderHtml(UserInfo userInfo, EliteQuestionDisplayBean questionBean, SubjectBean subjectBean, TagItemBean tagBean, boolean showBigToolbar) {
		String indexHeaderRedisKey = showBigToolbar ? IndexHeaderKey : IndexHeaderWithoutBTKey;
		String html = (String)wrapperPageRedis.get(indexHeaderRedisKey);
		String mainDomain = OverallDataFilter.mainDomain;
		String askDomain = OverallDataFilter.askDomain;
		if(StringUtils.isBlank(html))
		{
			html = HttpUtil.get(indexHeaderFetchUrl, new HashMap<String, String>());
			if(StringUtils.isNotBlank(html))
			{
				StringBuilder sb = new StringBuilder(html);
				String regEx = "<link rel=\"stylesheet\" .*>";
				Pattern pat = Pattern.compile(regEx);
				Matcher mat = pat.matcher(sb.toString());
				while(mat.find())
				{
					String matchString = mat.group(0);
					int start = mat.start();
					sb.delete(start, start+matchString.length());
					
					mat  = pat.matcher(sb.toString());
				}
				
				
				String matchString = "<script ";
				int matchIndex = sb.indexOf(matchString);
				if(matchIndex >= 0) {   
					sb.insert(matchIndex, WrapperPageServiceImpl.getStyleFile());
				}
				matchString = "</head>";
				matchIndex = sb.indexOf(matchString);
				sb.insert(matchIndex, HEADER_JS + "\n");
				
				//删除qq js引用
				String qqJs = "<script charset=\"utf-8\" src=\"http://wpa.b.qq.com/cgi/wpa.php\"></script>";
				if (sb.indexOf(qqJs) >= 0) sb.delete(sb.indexOf(qqJs), sb.indexOf(qqJs) + qqJs.length());
				//删除</body></html>
				sb.delete(sb.indexOf("</body>"), sb.length());

				//删除个人中心
				int myStart = sb.indexOf("<li tb-block=\"my\"");
				int myEnd = sb.indexOf("</li>", myStart) + "</li>".length();
				sb.delete(myStart, myEnd);

				html = sb.toString();
				if(showBigToolbar) {
					html = html.replace("<a href=\"/forum/index.html\" target=\"_blank\" class=\"link active\"><span>论坛</span>",
							"<a href=\"/forum/index.html\" target=\"_blank\" class=\"link\"><span>论坛</span>");

					//替换搜索
					StringBuilder barSearchSB = new StringBuilder("<div class=\"pull-right\">\n")
							.append("            <label><i class=\"iconfont\">&#xe607;</i>\n")
							.append("            <span class=\"placeholder\">大家正在搜: 日式装修</span>\n")
							.append("              <input type=\"text\" class=\"ipt\" maxlength=\"40\">\n")
							.append("            </label><a href=\"javascript:;\" class=\"search\">搜索</a>\n")
							.append("          </div>");
					int searchStart = html.indexOf("<form method=\"get\" target=\"_blank\" action=\"/decoration/list/all.html\">");
					int searchEnd = html.indexOf("</form>", searchStart) + "</form>".length();
					html = html.replace(html.substring(searchStart, searchEnd), barSearchSB.toString());
					
				} else{
					html = html.substring(0, html.indexOf("<header id=\"header\" class=\"header\">"));
				}


				html = html.replace("<title>论坛_搜狐焦点家居</title>", "<title>问吧_搜狐焦点家居_装修知识分享社区</title>");
				html = html.replaceAll("meta name=\"keywords\" content=\"(.*)\"", "meta name=\"keywords\" content=\"" + HeadKeywords + "\"");
				html = html.replaceAll("meta name=\"description\" content=\"(.*)\"", "meta name=\"description\" content=\"" + HeadDescription + "\"");


				html = html.replaceAll("<a href=\"((?!http|https|javascript|//)\\S*)\"", "<a href=\"" + mainDomain + "$1\"");
				html = html.replaceAll(mainDomain+"/ask/index.html", "//" + askDomain);
				//html = html.replaceAll("bar.focus.cn", askDomain);
				html = html.replaceAll("/decoration/index.html", "");
				
		        //去掉图片协议头
				html = html.replaceAll("(?<=img.{0,20}src=\")(http|https):", "");
				wrapperPageRedis.put(indexHeaderRedisKey, html);
			}
		}
		
		String domain = OverallDataFilter.uxDomain;
		String version = OverallData.getStaticVerCode();
		html = html.replace(DomainHolder, domain);
		html = html.replace(VersionHolder, version);
		
		if(null != userInfo)
		{
			String theLoggedFrag = String.format(LoggedDisFrag, userInfo.getNick(), userInfo.getAvatar(), userInfo.getNick(), "生活不在别处，在这里");
			html = html.replace("<li tb-block=\"login\"><a href=\"javascript:;\" btn-action=\"BPlogin\" tb-login=\"0\">免注册登录</a></li>", theLoggedFrag);
		}

		if(null != questionBean) {
			html = html.replaceAll("<title>(.*)</title>", "<title>" + questionBean.getTitle() + "_问吧_搜狐焦点家居</title>");
			String tagNames = "";
			if (questionBean.getTagList() != null && questionBean.getTagList().size() > 0) {
				for (TagItemBean tagItemBean : questionBean.getTagList()) {
					tagNames += tagItemBean.getTagName() + "、";
				}
			}
			html = html.replaceAll("meta name=\"keywords\" content=\"(.*)\"", "meta name=\"keywords\" content=\"" + tagNames + HeadKeywords + "\"");
			html = html.replaceAll("meta name=\"description\" content=\"(.*)\"", "meta name=\"description\" content=\"" + questionBean.getTitle() + ":" + questionBean.getPlainText() + "\"");
		}

		if(null != subjectBean) {
			html = html.replaceAll("<title>(.*)</title>", "<title>" + subjectBean.getName() + "_问吧_搜狐焦点家居</title>");

			String sectionNames = "";
			try {
				JSONArray detailArray = subjectBean.getDetail();
				if (null != detailArray && detailArray.size() > 0) {
					for (int i = 0; i < detailArray.size(); i++) {
						JSONObject detailJSON = detailArray.getJSONObject(i);
						if (null != detailJSON && detailJSON.containsKey("section_name")) {
							sectionNames += detailJSON.getString("section_name") + "、";
						}
					}
				}
			}catch(Exception e){
				logger.error("", e);
			}

			html = html.replaceAll("meta name=\"keywords\" content=\"(.*)\"", "meta name=\"keywords\" content=\"" + sectionNames + HeadKeywords + "\"");
			html = html.replaceAll("meta name=\"description\" content=\"(.*)\"", "meta name=\"description\" content=\"" + subjectBean.getBrief() + "\"");
		}

		if(null != tagBean) {
			html = html.replaceAll("<title>(.*)</title>", "<title>" + tagBean.getTagName() + "_问吧_搜狐焦点家居</title>");
		}
		return html;
	}

	@Override
	public String getIndexHeaderHtml(UserInfo userInfo, EliteQuestionDisplayBean questionBean, boolean showBigToolbar) {
		return this.getIndexHeaderHtml(userInfo, questionBean, null, null, showBigToolbar);
	}

	@Override
	public String getIndexHeaderHtml(UserInfo userInfo, SubjectBean subjectBean, boolean showBigToolbar) {
		return this.getIndexHeaderHtml(userInfo, null, subjectBean, null, showBigToolbar);
	}

	@Override
	public String getIndexHeaderHtml(UserInfo userInfo, TagItemBean tagBean, boolean showBigToolbar) {
		return this.getIndexHeaderHtml(userInfo, null, null, tagBean, showBigToolbar);
	}

	@Override
 	public String getIndexHeaderHtml(UserInfo userInfo, boolean showBigToolbar) {
		return this.getIndexHeaderHtml(userInfo, null, null, null, showBigToolbar);
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.web.WrapperPageService#getIndexFooterHtml()
	 */
	@Override
	public String getIndexFooterHtml() {
		String mainDomain = OverallDataFilter.mainDomain;
		String askDomain = OverallDataFilter.askDomain;
		String html = (String)wrapperPageRedis.get(IndexFooterKey);
		if(StringUtils.isBlank(html))
		{
			html = HttpUtil.get(indexFooterFetchUrl, new HashMap<String, String>());
			if(StringUtils.isNotBlank(html))
			{
				String regEx = "<body .*>";
				Pattern pat = Pattern.compile(regEx);
				Matcher mat = pat.matcher(html);
				if(mat.find())
				{
					String matchString = mat.group(0);
					html = html.substring(html.indexOf(matchString)+matchString.length());
				}
				html = html.substring(0, html.indexOf("</body>"));
				
				regEx = "<script.*forum-home.js.*></script>";
				pat = Pattern.compile(regEx);
				mat = pat.matcher(html);
				if(mat.find())
				{
					String matchString = mat.group(0);
					html = html.replace(matchString, "");
				}
				StringBuilder htmlBuilder = new StringBuilder(html);
				htmlBuilder.insert(htmlBuilder.indexOf("</footer>"), FOOTER_HTML);
				html = htmlBuilder.toString();
				html = html.replaceAll("<a href=\"((?!http|https|javascript|//)\\S*)\"", "<a href=\"" + mainDomain + "$1\"");
				html = html.replaceAll(mainDomain+"/ask/index.html", "//" + askDomain);
				html = html.replaceAll("bar.focus.cn", askDomain);
				html = html.replaceAll("/decoration/index.html", "");
				html = html.replaceAll(Origin_Img_Domain, New_Img_Domain);
				html = ContentUtil.removeContentImageProtocol(html);
				wrapperPageRedis.put(IndexFooterKey, html);
			}
		}
		return html;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.web.WrapperPageService#resetIndexHeaderHtml()
	 */
	@Override
	public void resetIndexHeaderHtml() {
		wrapperPageRedis.remove(IndexHeaderKey);
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.web.WrapperPageService#resetIndexFooterHtml()
	 */
	@Override
	public void resetIndexFooterHtml() {
		wrapperPageRedis.remove(IndexFooterKey);
	}
	
	public static String getStyleFile() {
		return "<link href=\"//"+DomainHolder+"/bpeliteweb/"+VersionHolder+"/styles/vendors.css\" rel=\"stylesheet\">\n"; 
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.web.WrapperPageService#getToolbarHtml()
	 */
	@Deprecated
	public String getToolbarHtml(UserInfo userInfo, EliteQuestionDisplayBean questionBean) {
		String html = (String)wrapperPageRedis.get(ToolbarKey);
		String askDomain = OverallDataFilter.askDomain;
		String mainDomain = OverallDataFilter.mainDomain;
		if(StringUtils.isBlank(html))
		{
			html = HttpUtil.get(indexHeaderFetchUrl, new HashMap<String, String>());
			if(StringUtils.isNotBlank(html))
			{
				//for toolbar
				html = html.substring(0, html.indexOf("<header id=\"header\" class=\"header\">"));
				
				StringBuilder sb = new StringBuilder(html);
				String regEx = "<link rel=\"stylesheet\" .*>";
				Pattern pat = Pattern.compile(regEx);
				Matcher mat = pat.matcher(sb.toString());
				while(mat.find())
				{
					String matchString = mat.group(0);
					int start = mat.start();
					sb.delete(start, start+matchString.length());
					
					mat  = pat.matcher(sb.toString());
				}
				
				
				String matchString = "<script ";
				int matchIndex = sb.toString().indexOf(matchString);
				if(matchIndex >= 0)
				{
					sb.insert(matchIndex, WrapperPageServiceImpl.getStyleFile());
					
				}
				html = sb.toString();
				
				html = html.replace("<title>论坛_搜狐焦点家居</title>", "<title>问吧_搜狐焦点家居</title>");
				html = html.replace("<a href=\"/forum/index.html\" target=\"_blank\" class=\"link active\"><span>论坛</span>", 
						"<a href=\"/forum/index.html\" target=\"_blank\" class=\"link\"><span>论坛</span>");
				html = html.replaceAll("meta name=\"keywords\" content=\"(.*)\"", "meta name=\"keywords\" content=\"$1" + HeadKeywords + "\"");
				html = html.replaceAll("meta name=\"description\" content=\"(.*)\"", "meta name=\"description\" content=\"" + HeadDescription + "\"");


				html = html.replaceAll("<a href=\"((?!http|https|javascript|//)\\S*)\"", "<a href=\"" + mainDomain + "$1\"");
				html = html.replaceAll(mainDomain+"/ask/index.html", "//" + askDomain);
				html = html.replaceAll("bar.focus.cn", askDomain);
				html = html.replaceAll("/decoration/index.html", "");
				wrapperPageRedis.put(ToolbarKey, html);
			}
		}
		
		String domain = OverallDataFilter.uxDomain;
		String version = OverallData.getStaticVerCode();
		html = html.replace(DomainHolder, domain);
		html = html.replace(VersionHolder, version);
		
		if(null != userInfo)
		{
			String theLoggedFrag = String.format(LoggedDisFrag, userInfo.getNick(), userInfo.getAvatar(), userInfo.getNick(), "生活不在别处，在这里");
			html = html.replace("<li tb-block=\"login\"><a href=\"javascript:;\" btn-action=\"BPlogin\" tb-login=\"0\">免注册登录</a></li>", theLoggedFrag);
		}
		if(null != questionBean){
			html = html.replaceAll("<title>(.*)</title>", "<title>" + questionBean.getTitle() + "_问吧_搜狐焦点家居</title>");
			String tagNames="";
			if(questionBean.getTagList() != null && questionBean.getTagList().size() > 0){
				for(TagItemBean tagItemBean : questionBean.getTagList()){
					tagNames += tagItemBean.getTagName() + "、";
				}
			}
			html = html.replaceAll("meta name=\"keywords\" content=\"(.*)\"", "meta name=\"keywords\" content=\"" + tagNames + HeadKeywords + "\"");
			html = html.replaceAll("meta name=\"description\" content=\"(.*)\"", "meta name=\"description\" content=\"" + questionBean.getTitle() + ":" + questionBean.getPlainText() + "\"");
		}
		return html;
	}

	@Deprecated
 	public String getToolbarHtml(UserInfo userInfo) {
		return this.getToolbarHtml(userInfo, null);
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.web.WrapperPageService#resetToolbarHtml()
	 */
	@Deprecated
	public void resetToolbarHtml() {
		wrapperPageRedis.remove(ToolbarKey);
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.web.WrapperPageService#getSidebarHtml(com.sohu.bp.bean.UserInfo)
	 */
	@Override
	public String getSidebarHtml(UserDetailDisplayBean userInfoBean, SideBarPageType pageType) {
		String askDomain = OverallDataFilter.askDomain;
		String mainDomain = OverallDataFilter.mainDomain;

		String[] args = new String[11];
		args[0] = userInfoBean.getAvatar();
		args[1] = userInfoBean.getNick();
		
		args[2] = "<span class=\"pull-right\">安全</span>";
		if(!userInfoBean.isAccountSafe())
			args[2] = "<span class=\"pull-right txt-alert\">异常</span>";
		
		args[3] = userInfoBean.getLastLoginInfo();
		
		args[4] = "";
		if(userInfoBean.getBpUserType() > 1)
		{
			String userType = "";
			String typeHolder = "<li class=\"last\"><span class=\"ico-dot\"><i></i></span><a href=\"/decoration/profile/aut/update.html\">信息管理</a></li>";
			switch(userInfoBean.getBpUserType())
			{
				case 11:
					userType = "自媒体";
					break;
				case 21:
					userType = "设计师";
					break;
				case 22:
					userType = "工长";
					break;
				case 60:
					userType = "公司";
					typeHolder = "" + 
					"<li><span class=\"ico-dot\"><i></i></span><a href=\"/decoration/profile/manage/expert.html\">员工管理</a></li>" + 
					"<li class=\"last\"><span class=\"ico-dot\"><i></i></span><a href=\"/decoration/profile/manage/company.html\">公司设置</a></li>";
					break;
			}
			
			args[4] = "" + 
			"<section>" + 
				"<h4 class=\"title\">" + 
					"<a href=\"javascript:;\" class=\"active\">" + 
						"<span class=\"pull-right\"><i class=\"iconfont icons-arrowup\">&#xe636;</i><i class=\"iconfont icons-arrowdown\">&#xe602;</i></span>" +
						userType + 
					"</a>" + 
				"</h4>" + 
				"<ul class=\"box hide\">" + 
					"<li class=\"first\"><span class=\"ico-dot\"><i></i></span><a href=\"/decoration/profile/publish.html\">内容管理</a></li>" + 
					typeHolder + 
				"</ul>" + 
			"</section>";
		}
		
		args[5] = "";
		if(userInfoBean.getBpUserType() == 1)
		{
			args[5] = "" + 
			"<section>" + 
				"<h4 class=\"title\"><a href=\"/decoration/profile/publish.html\">合作入驻</a></h4>" + 
			"</section>";
		}
		
		args[6] = "";
		args[7] = "";
		args[8] = "";
		args[9] = "";
		args[10] = "";
		
		args[pageType.getValue() + 6] = "active";
		
		
		String newString = String.format(SideBarDisFrag, args);

		newString = newString.replaceAll("/ask/index.html", "//" + askDomain);
		newString = newString.replaceAll("<a href=\"((?!http|https|javascript|//)\\S*)\"", "<a href=\"" + mainDomain + "$1\"");
		newString = newString.replaceAll("bar.focus.cn", askDomain);
		newString = newString.replaceAll("/decoration/index.html", "");
		return  newString;
	}

	
}