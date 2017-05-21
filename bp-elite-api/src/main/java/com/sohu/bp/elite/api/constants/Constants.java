package com.sohu.bp.elite.api.constants;

/**
 * @author zhangzhihao
 *         2016/8/17
 */
public class Constants {
    public static final String DEFAULT_AVATAR = "http://img.mp.itc.cn/upload/20160621/ca88644c2b3c4625ad886f997f431753.jpg";
    public static final String DEFAULT_NICK = "搜狐焦点网友";
	public static final int Comment_Length = 70;
	public static final String DEFAULT_CACHE_SPLIT_CHAR = "_";
	public static final String DEFAULT_SPLIT_CHAR = ";";
	public static final String DELETED_ANSWER_SPLIT_CHAR = ",";
	public static final String BLANK_SPLIT_CHAR = " ";
    
	//前端缓存KEY值
	public static final String ELITE_SUBJECT_KEY = "Elite_Subject_Key";
	public static final String ELITE_TOPIC_KEY = "Elite_Topic_Key";
	public static final String ELITE_FRAGMENT_KEY = "Elite_Fragment_Key";
	public static final String ELITE_TAGSQUARE_KEY = "Elite_TagSquare_Key";
	public static final String ELITE_COLUMN_KEY = "Elite_Column_Key";
	public static final String ELITE_FOCUS_ORDER_KEY = "Elite_Focus_Order_Key";
	public static final String ELITE_FOCUS_ORDER_SET_KEY = "Elite_Focus_Order_Set_Key";
	
	public static final String ELITE_FOLLOW_USER_LOGIN = "Elite_Follow_User_Login";
	
	public static final Integer COMPLEX_ID_SHIFT = 10;
	public static final Integer COMPLEX_ID_SHIFT_BIG = 1000;
	public static final Integer SQUARE_INITIAL_ELITE_NUM = 20;
	public static final Long SQUARE_SET_SIZE = 1000l;
	public static final Integer CRAWL_TAG_SIZE = 6;
	public static final Integer SQUARE_PAGE_SIZE = 20;
	
	public static final long DAY_MILLISECONDS = 24 * 60 * 60 * 1000;
	public static final int CACHE_TTL = 60 * 5;
	public static final long DEFAULT_IP = 0;
	public static final int DEFAULT_PORT = 0;
	
	//主站fragment
    public static final long RECOM_EXPERT_POSID = 27;
    public static final long RECOM_EXPERT_AREACODE = 0;
    public static final String RECOM_EXPERT_URL = "/q/go?invitedUserId=";
    public static final int RECOM_EXPERT_COUNT = 9;
    
    //crawl互斥锁占位符
    public static final String CRAWL_MUTEX_LOCK_FLAG = "locked";
    
    //message data replace holder
    public final static String MESSAGE_DATA_NICKNAME = "${nickname}";
    public final static String MESSAGE_DATA_QUESTIONTITLE = "${question_title}";
    public final static String MESSAGE_DATA_JUMPURL = "${jump_url}";
    
    //微信
    public final static Integer TEMPLATE_ID = 5;
    //专家团推送限制
    public static final Integer EXPERT_TEAM_DEFAULT_NUM = 6;
    
}
