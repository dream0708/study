package com.sohu.bp.elite.constants;

public class Constants {
	public static final String DB_DEFAULT_SPLIT_CHAR = "_";
	public static final char CACHE_DEFAULT_SPLIT_CHAR = '_';
	public static final String ELITE_INTERACTION_BYOBJECTID_SPLIT_CHAR = "_";
	public static final String TAG_IDS_SEPARATOR = ";";
	
	public static final long ELITE_DEFAULT_HOST = 127000000001L;
	public static final int ELITE_DEFAULT_PORT = 0;
	
	public final static long SCORE_NEVER_USED = Long.MIN_VALUE;
	
	public final static String CACHE_OCCUPY_VALUE = "OCCUPY";
	
	public final static String SSDB_INTERACTION_EXIST = "ISEXISTINTERACTION";
	
	public final static String SSDB_INTERACTION_EXIST_INTERACTIONID = "EXIST_INTERACTIONID";
	
	public final static long TAG_PROMETHEUS_ID = 0;

	public final static String Auditing_Questions_Key = "Auditing_Questions_key";
	public final static String Auditing_Answers_Key = "Auditing_Answers_Key";
	
	public final static String Elite_Fragment_Key = "Elite_Fragment_Key";
	public final static String Elite_Subject_Key = "Elite_Subject_Key";
	public final static String Elite_Subject_Status_Key = "Subject_Status_Key";
	public final static String Elite_Topic_Key = "Elite_Topic_Key";
	public final static String Elite_Topic_Status_Key = "Topic_Status_Key";
	public final static String Elite_Follow_Key = "Elite_Follow_Key";

	//SSDB中用户初次登录的占位符
	public static final String LOGIN_OCCUPY = "Login_Occupy";
	
	//默认关注标签和用户的前端缓存
	public static final String ELITE_FOLLOW_USER_KEY = "Elite_Follow_User_Key";
	public static final String ELITE_FOLLOW_TAG_KEY = "Elite_Follow_Tag_Key";
	public static final String ELITE_FOLLOW_USER_LOGIN = "Elite_Follow_User_Login";
	public static final Long ELITE_FOLLOW_USER_LOGIN_SCORE = 0l;
	
	//专家团初始分数
	public static final Integer ELITE_EXPERT_TEAM_INIT_SCORE = 100;	
	//text允许的字节数，用于截断(mysql text 长度为0-65535，我们取为60000)
	public static final Integer DB_TEXT_LENGTH = 60000;
	//专家团的最大长度,如果长度超过10000则从新截取
	public static final Integer ELITE_EXPERT_CACHE_SET_MAX_LENGTH = 10000;
	
	//推送时间段
	public static final int MESSAGE_STRATEGY_START_TIME = 9;
	public static final int MESSAGE_STRATEGY_END_TIME = 21;
	
	//消息占位符
    public final static String MESSAGE_DATA_NICKNAME = "${nickname}";
    public final static String MESSAGE_DATA_QUESTIONTITLE = "${question_title}";
    public final static String MESSAGE_DATA_JUMPURL = "${jump_url}";
    public final static String MESSAGE_DATA_REASON = "${reason}";
    
    /**
     * 广场相关常量
     */
    public static final int SQUARE_MAX_NEW_ELITE_NUM = 20;    
    public static final int SQUARE_SET_SIZE = 2000;
    public static final int SELECTED_SQUARE_SET_SZIE = 1000;
    public static final int SQUARE_RECENT_SET_SIZE = 6;
    public static final int COMPLEX_ID_SHIFT = 10;
}
