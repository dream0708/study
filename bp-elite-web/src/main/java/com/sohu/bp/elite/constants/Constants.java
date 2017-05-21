package com.sohu.bp.elite.constants;

/**
 * 
 * @author nicholastang
 * 2016-07-27 16:08:40
 * TODO
 */
public class Constants {
	public static final String CHARSET = "UTF-8";
	
	public static final String DEFAULT_SPLIT_CHAR = "_";

	public static final String ID_SPLIT_CHAR = "|";
	
	public static final String DEFAULT_IP_ADDRESS = "127.0.0.1";
	
	//静态资源版本号
	public final static String STATIC_VERSION_CODE_NAME = "staticVersionCode";
	public final static String APP_STATIC_VERSION_CODE_NAME = "appStaticVersionCode";
	public final static String UX_DOMAIN = "ux_domain";
	public final static String MAIN_DOMAIN = "main_domain";
	public final static String ASK_DOMAIN = "ask_domain";
	public static final String INNER_DOMAIN = "inner_domain";
	
	public static final String DEFAULT_AVATAR = "//avatarimg.bjcnc.img.sohucs.com/c_lfill,w_115,h_115,g_center/bp_7c93fa3df7914d6bb95bceee518c75d1";
	public static final String DEFAULT_SUBJECT_COVER = "//avatarimg.bjcnc.img.sohucs.com/bp_9d2c565295ab4764b24b0e71960fb92f";
	public static final String DEFAULT_NICK = "搜狐焦点网友";

	public static final String DEFAULT_TAG_AVATAR = "//avatarimg.bjcnc.img.sohucs.com/c_lfill,w_115,h_115,g_center/bp_7c93fa3df7914d6bb95bceee518c75d1";
	public static final String DEFAULT_QUESTION_AVATAR = "//avatarimg.bjcnc.img.sohucs.com/c_lfill,w_115,h_115,g_center/bp_7c93fa3df7914d6bb95bceee518c75d1";
	public static final String DEFAULT_ANSWER_AVATAR = "//avatarimg.bjcnc.img.sohucs.com/c_lfill,w_115,h_115,g_center/bp_7c93fa3df7914d6bb95bceee518c75d1";

	public static final String TAG_IDS_SEPARATOR = ";";

	public static final int Title_length = 256;
	public static final int Text_length = 20000;
	public static final int Comment_Length = 170;
	public static final int SUBJECT_LENGTH = 6;
	public static final int COLUMN_LENGTH = 6;
	public static final String SUBJECT_SPLIT_CHAR = "##";
	//提问和回答的限定数量
	public static final int QUESTION_RESTRICT_NUM = 999;
	public static final int ANSWER_RESTRICT_NUM = 999;
	//精选回答的过期时间
	public static final int TOP_FEEDS_OVERDUE = 12 * 60 * 60;
	
	public static final String PAGE_INDEX = "redirect:/index.html";
	public static final String PAGE_LOGIN = "redirect:/login";
	
	public static final String PAGE_404 = "/common/404";
	public static final String PAGE_503 = "/common/503";
	public static final String APP_PAGE_404 = "/app/blank";

	public static final int DEFAULT_SEARCH_COUNT = 3;
	
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int DEFAULT_APP_SIZE = 9;
	public static final int APP_SIZE_LITTLE = 3;
	public static final int DEFAULT_INVITE_PAGE_SIZE = 5;

	public static final int DEFAULT_NO_READ_MSG_SIZE = 5;

	//前端缓存KEY值,和api模块保持一致
	public static final String ELITE_SUBJECT_KEY = "Elite_Subject_Key";
	public static final String ELITE_TOPIC_KEY = "Elite_Topic_Key";
	public static final String ELITE_FRAGMENT_KEY = "Elite_Fragment_Key";
	public static final String ELITE_COLUMN_KEY = "Elite_Column_Key";
	public static final String ELITE_FOCUS_ORDER_KEY = "Elite_Focus_Order_Key";
	public static final String ELITE_FOCUS_ORDER_SET_KEY = "Elite_Focus_Order_Set_Key";
	public static final String ELITE_FEATURE_WRAPPER_KEY = "Elite_Feature_Wrapper_Key";

	public static final String TOTAL_ANSWER_CACHE_KEY = "TOTAL_ANSWER_CACHE_KEY";
	public static final String TOTAL_LIKE_NUM_OF_USER_CACHE_KEY = "TOTAL_LIKE_NUM_OF_USER_CACHE_KEY";
	public static final String TAG_INDEX_CACHE_KEY = "TAG_INDEX_CACHE_KEY";
	public static final String TAG_QUESTIONS_CACHE_KEY = "TAG_QUESTIONS_CACHE_KEY";
	public static final String TAG_ANSWERS_CACHE_KEY = "TAG_ANSWERS_CACHE_KEY";
	public static final String USER_INDEX_CACHE_KEY = "USER_INDEX_CACHE_KEY";

	//SessionSNAFilter中Regex的解析
	public static final String SPACE = " ";
	public static final String ENTER = "\n";
	
	//SSDB中用户初次登录的占位符
	public static final String LOGIN_OCCUPY = "Login_Occupy";
	
	//默认关注标签和用户的前端缓存
	public final static String ELITE_FOLLOW_KEY = "Elite_Follow_Key";
	public final static String ELITE_FOLLOW_USER_KEY = "Elite_Follow_User_Key";
	public final static String ELITE_FOLLOW_TAG_KEY = "Elite_Follow_Tag_Key";
	public final static String ELITE_FOLLOW_USER_LOGIN = "Elite_Follow_User_Login";
	public final static Long ELITE_FOLLOW_USER_LOGIN_SCORE = 0l;
	
	
	//message data replace holder
	public final static String MESSAGE_DATA_NICKNAME = "${nickname}";
	public final static String MESSAGE_DATA_QUESTIONTITLE = "${question_title}";
	public final static String MESSAGE_DATA_JUMPURL = "${jump_url}";
	public final static String MESSAGE_DATA_REASON = "${reason}";
	public final static String MESSAGE_DATA_CONTENT_TYPE = "${content_type}";
	
	//邀请回答设置的过期时间
	public final static Integer INVITE_EXPIRE_TIME = -2;
	public final static Integer INVITE_EXPIRE_TIME_SECOND = 2 * 24 * 60 * 60;
	
	//广场最初问答显示的条数和更新显示条数的最大值
	public static final Integer SQUARE_INITIAL_ELITE_NUM = 20;
	public static final Integer SQUARE_MAX_NEW_ELITE_NUM = 20;
	public static final Integer SQUARE_PAGE_SIZE = 20;
	public static final Long SQUARE_SET_SIZE = 1000l;
	//用于测试队列的维护情况
	public static final Integer COMPLEX_ID_SHIFT = 10;
	//组合ID平移量
	public static final Integer COMPLEX_ID_SHIFT_BIG = 1000;
	
	//有关图的相关参数
	//用于将大图转换为小图
	public static final String LITTLE_AVATAR = "/c_lfill,w_115,h_115,g_center";
	public static final String LITTLE_FEEDIMG = "/c_lfill,w_320,h_240,g_center";	
	public static final String OPTION_IMG = "/c_fill,w_200,h_200,g_center";
	//用于将焦点图转换为小图,手机端
	public static final String LITTLE_FOCUS_COVER = "/o_crop,c_zoom,w_0.6,h_0.6";	
	//将大图转换为小图的固定比例
	public static final String LITTLE_IMAGE_RATIO = "0.3";
	public static final String MEDIUM_IMAGE_RATIO = "0.7";
	//去除<img>标签的协议头
	public static final String PROTOCAL_HEADER = "(?i)(?:http|https):(.*)";
	
	//大图能够进行裁剪的源
	public static final String CUT_ABLE_SOURCE = "(?:avatarimg.bjcnc.img.sohucs.com|jj-crawl-img.bjctc.img.sohucs.com)";
	
	//推荐专家的默认数量
	public static final int DEFAULT_RECOM_EXPERTS_PAGE_SIZE = 3;
	public static final int WECHAT_RECOM_EXPERTS_NUM = 9;
	//文本中图片的模式
	public static final String CONTENT_IMG_PATTERN = "<img src=\"\" class=\"pic\">";

	//专栏中展示问题的数量
	public static final int COLUMN_QUESTION_NUM = 3;
	
	//有关推送的相关常量
	public static final String PUSH_SQUARE_UNREAD_EVENT = "squareUnreadEvent";
	
	//互斥锁的标志
	public static final String LOCK_FLAG = "locked";
	
	//专家申请默认标签数量
	public static final Integer TAG_CONSTRAINT = 6;
	
	//专家推送每天默认数量
	public static final Integer EXPERT_TEAM_DEFAULT_NUM = 6;
	//专家推送每小时默认数量
	public static final Integer EXPERT_TEAM_DEFAULT_NUM_PER_HOUR = 1;
	
	//站队默认选择内容
	public static final String VS_POS_TEXT = "<h4>A</h4><i>votes</i><p>\"说的好\"</p>";
	public static final String VS_POS_IMG = "//3074a34158850.cdn.sohucs.com/bp_507654ea105a47ed978745fe4d6190bf";
	public static final String VS_NEG_TEXT = "<h4>B</h4><i>votes</i><p>\"什么鬼\"</p>";
	public static final String VS_NEG_IMG = "//3074a34158850.cdn.sohucs.com/bp_e5f3093ac1954987b7d42d16761574ba";
	public static final String VS_POS_ANSWER_LABEL = "A";
	public static final String VS_NEG_ANSWER_LABEL = "B";
	public static final Integer VS_DEFAULT_CHOOSED_USER_NUM = 4;
	
	//问题关注人数
	public static final Integer DEFAULT_QUESTION_FOLLOW_NUM = 4;

	public static final String DEFAULT_SLOGAN = "理性家装 感性生活";
	
	/**
	 * 推荐广场相关常量
	 */
	public static final int DEFAULT_REC_SQUARE_PAGE_SIZE = 20;
	public static final int DEFAULT_REC_BACKWARD_NUM = 16;
	public static final int DEFAULT_REC_SELECTED_BACKWARD_NUM = 4;
	public static final int DEFAULT_REC_FORWARD_NUM = 5;
	public static final int DEFAULT_REC_FORWARD_SELECTED_BACKWARD_NUM = 5;
	public static final int DEFAULT_REC_FORWARD_SELECTED_FORWARD_NUM = 10;
	public static final String FUV = "FUV";
}
