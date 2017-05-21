namespace java com.sohu.bp.elite.model

struct TEliteQuestion {
    1:i64       id          = 0;
    2:i64       bpId        = 0;
    3:string    title       = "";
    4:string    detail      = "";
    5:string    tagIds      = "";
    6:i64       createTime  = 0;
    7:i64       updateTime  = 0;
    8:i64       publishTime = 0;
    9:i32       relationType= 0;
    10:i64      relationId  = 0;
    11:i32      source      = 0;
    12:i32      status      = 0;
    13:i32      version     = 0;
    14:i64      createHost  = 0;
    15:i32      createPort  = 0;
    16:i64      updateHost  = 0;
    17:i32      updatePort  = 0;
    18:string   sourceUrl   = "";
    19:i64      areaCode    = 0;
    20:i64		specialId	= 0;
    21:i32		specialType = 0;
    22:string   options     = "";
    23:string	highlightText = "";
    24:string	highlightWords = "";
    25:map<i32,i32> counts;
}

struct  TEliteAnswer {
    1:i64       id          = 0;
    2:i64       bpId        = 0;
    3:i64       questionId  = 0;
    4:string    content     = "";
    5:string    tagIds      = "";
    6:i32       source      = 0;
    7:i64       createTime  = 0;
    8:i64       updateTime  = 0;
    9:i64       publishTime = 0;
    10:i32      status      = 0;
    11:i64      createHost  = 0;
    12:i32      createPort  = 0;
    13:i64      updateHost  = 0;
    14:i32      updatePort  = 0;
    15:string   sourceUrl   = "";
    16:i64      areaCode    = 0;
    17:i64      specialId   = 0;
    18:i32      specialType = 0;
    19:string	highlightText = "";
    20:string	highlightWords = "";
}

struct  TEliteUser {
    1:i64       bpId        = 0;
    2:i32       gender      = 0;
    3:i64       birthday    = 0;
    4:string    description = "";
    5:i32       grade       = 0;
    6:string    tagIds      = "";
    7:i32       status      = 0;
    8:i64       firstLoginTime  = 0;
    9:i64       lastLoginTime   = 0;
    10:i32   	gold        = 0;
    11:i32		firstLogin  = 0;
    12:i32		identity	= 0;
    13:string	nick		="";
    14:i64      areaCode    = 0;
    15:string	highlightText = "";
    16:string	highlightWords = "";
}

struct TQuestionListResult {
    1:list<TEliteQuestion> questions;
    2:i64                  total = 0;
}

struct TAnswerListResult {
    1:list<TEliteAnswer> answers;
    2:i64                  total = 0;
}

struct TUserListResult {
    1:list<TEliteUser> users;
    2:i64                  total = 0;
}

struct TUserIdListResult {
    1:list<i64> userIds;
    2:i64       total   =   0;
}

struct TQuestionIdListResult {
    1:list<i64> questionIds;
    2:i64       total   =   0;
}

struct TAnswerIdListResult {
    1:list<i64> answerIds;
    2:i64       total   =   0;
}

struct TSearchGlobalListResult {
    1:list<TEliteQuestion>          questions;
    2:list<TEliteAnswer>            answers;
    3:list<TEliteUser>              users;
    5:list<string>                  rank;
    10:map<string, i32>             totalCounts;
}

struct TEliteSubject{
	1:i64		id			= 0;
	2:i64		userId		= 0;
	3:string	name		= "";
	4:string	brief		= "";
	5:string	detail		= "";
	6:i64		areaCode	= 0;
	7:i64		createTime	= 0;
	8:i64		updateTime	= 0;
	9:i64		createHost	= 0;
	10:i64		updateHost	= 0;
	11:i32		createPort	= 0;
	12:i32		updatePort	= 0;
	13:i32		status		= 0;
	14:string	cover		= "";
}

struct TSubjectListResult{
	1:list<TEliteSubject> subjects;
	2:i64				  	total = 0;
}

struct TEliteTopic{
	1:i64		id			= 0;
	2:i64		userId		= 0;
	3:string	title		= "";
	4:string	brief		= "";
	5:string	cover		= "";
	6:i64		questionId	= 0;
	7:i64		startTime	= 0;
	8:i64		endTime		= 0;
	9:i32		type		= 0;
	10:i64		createTime	= 0;
	11:i64		createHost	= 0;
	12:i64		updateTime	= 0;
	13:i64		updateHost 	= 0;
	14:i32		status		= 0;
}

enum TEliteMediaType{
   IMAGE         = 1
   VIDEO         = 2
}

enum TEliteMediaStatus{
   INVALID        = 1
   WORK           = 2
   DELETE         = 3
}

struct TEliteMedia{
   1:i64                   id             = 0;
   2:TEliteMediaType       type           = TEliteMediaType.VIDEO;
   3:i64                   questionId     = 0;
   4:i64                   answerId       = 0;
   5:i64                   mediaGivenId   = 0;
   6:string                url            = "";
   7:i64                   updateTime     = 0;
   8:i64                   uploadTime     = 0;
   9:i64                   uploadHost     = 0;
   10:i32                  uploadPort     = 0;
   11:TEliteMediaStatus    status         = TEliteMediaStatus.INVALID;
}

enum TEliteFragmentType{
	NAVLABEL	= 1
	SLOGAN      = 2
}

struct TEliteFragment{
	1:i64							id			= 0;
	2:i64							userId		= 0;
	3:i32							position	= 0;
	4:i32							status		= 0;
	5:string						name		= "";
	6:string						detail		= "";
	7:i64							createTime	= 0;
	8:i64							createHost	= 0;
	9:i32							createPort	= 0;
	10:i64							updateTime	= 0;
	11:i64							updateHost	= 0;
	12:i32							updatePort	= 0;
	13:TEliteFragmentType			type		= TEliteFragmentType.NAVLABEL;
}

enum TEliteSourceType{
    WRITE_PC        = 1
    WRITE_MOBILE    = 2
    WRITE_APP       = 3
    WRITE_WX		= 4
    CRAWL           = 10
}

enum TEliteFollowType{
	ELITE_USER	   = 1
	ELITE_TAG      = 2
}

struct TEliteFollow{
	1:i64				id				= 0;
	2:i64				userId			= 0;
	3:i64				bpId			= 0;
	4:TEliteFollowType	type			= TEliteFollowType.ELITE_USER;
	5:i32				status			= 0;
	6:i64				createTime		= 0;
	7:i64				createHost		= 0;
	8:i32				createPort		= 0;
	9:i64				updateTime		= 0;
	10:i64				updateHost		= 0;
	11:i32				updatePort		= 0;
}

struct TEliteColumn{
	1:i64				id				= 0;
	2:string			name			= "";
	3:string			cover			= "";
	4:string			userInfo		= "";
	5:string			tags			= "";
	6:string			brief			= "";
	7:string			content			= "";
	8:i64				createTime		= 0;
	9:i64				updateTime		= 0;
	10:i64				publishTime		= 0;
	11:i32				status			= 0;
	12:i32				type			= 0;
	13:string			description		= "";
}

struct TColumnListResult{
	1:list<TEliteColumn> columns;
	2:i64				 total			= 0;
}

struct TEliteExpertTeam{
    1:i64           bpId                = 0;
    2:i32           pushNum             = 0;
    3:i32           answeredNum         = 0;
    4:i32           score               = 0;
    5:i32           identity            = 0;
    6:string        unansweredId        = "";
    7:string        answeredId          = "";
    8:i64           lastPushTime        = 0;
    9:i64           lastAnsweredTime    = 0;
    10:i64			team				= 0;		
}

struct TEliteExpertTeamInfo {
	1:i64					id					= 0;
	2:string				teamName			= "";
	3:i64					creatorId			= 0;
	4:string				authorizatedIds		= "";
	5:i32					status				= 0;
}

struct TExpertTeamListResult{
    1:list<TEliteExpertTeam>    expertTeam;
    2:i64                       total           =   0;
}

struct TEliteAdmin {
    1:i64         bpId       = 0;
    2:i32         status     = 0;
}

//推送
enum TEliteMessagePushType {
    INBOX = 0;
    WECHAT = 1;
    CELL_MESSAGE = 2;
    STRONG = 10;
    MEDIUM = 20;
    WEAK = 30;
    CELL_AND_INBOX = 40;
}

enum TEliteMessageTimePeriodType {
    DAY_TIME = 10;
    ALL_TIME = 20;
}

enum TEliteMessageFrequenceType {
    UNLIMIT = 0;
    DAY = 1;
    HOUR = 2;
    
}

struct TEliteMessageStrategy {
    1:TEliteMessageTimePeriodType   timePeriodType      =   TEliteMessageTimePeriodType.DAY_TIME;
    2:TEliteMessageFrequenceType    frequenceType       =   TEliteMessageFrequenceType.DAY;
    3:i32                           frequenceValue      =   6;
    4:string                        identity            =   "";
}

struct TEliteMessageData {
    1:i32                           inboxMessageDataValue=   0;
    3:string                        inboxMessageContent  =   "";
    4:i32                           sendCloudTemplate   =   0;
    5:map<string,string>            sendCloudVariables;
    6:i32                           wechatTemplateId     =   5;
    7:string                        wechatData           =   "";
    8:string                        wechatUrl            =   "";
}

struct TEliteSquareItem {
    1:i32   feedType    =   1;
    2:i64   feedId      =   0;
}

