namespace java com.sohu.bp.elite.service
include "EliteModel.thrift"
include "EliteSearchModel.thrift"

service EliteThriftService {

    /*问题*/
    i64 insertQuestion(1:EliteModel.TEliteQuestion question);
    i64 insertQuestionWithOptions(1:EliteModel.TEliteQuestion question, 2:bool securityFlag);
    bool updateQuestion(1:EliteModel.TEliteQuestion question);
    EliteModel.TEliteQuestion getQuestionById(1:i64 questionId);
    list<EliteModel.TEliteQuestion> getQuestionsByIds(1:list<i64> questionIds);
    list<EliteModel.TEliteQuestion> getQuestionsByBpId(1:i64 bpId);
    list<EliteModel.TEliteQuestion> getQuestionsBySpecial(1:i64 specialId, 2:i32 specialType);
    i32 getUserQuestionNumByStatus(1:i64 bpId, 2:list<i32> statusList);
    //审核
    bool batchAuditQuestion(1:list<i64> passQuestionIds, 2:list<i64> rejectedQuestionIds);
    bool passOneQuestion(1:i64 questionId);
    bool rejectOneQuestion(1:i64 questionId);
    bool deleteOneQuestion(1:i64 questionId);
    bool sysDeleteOneQuestion(1:i64 questionId);
    EliteModel.TQuestionListResult getAuditingQuestions(1:i32 start, 2:i32 count);


    /*回答*/
    i64 insertAnswer(1:EliteModel.TEliteAnswer answer);
    i64 insertAnswerWithOptions(1:EliteModel.TEliteAnswer answer, 2:bool securityFlag);
    bool updateAnswer(1:EliteModel.TEliteAnswer answer);
    EliteModel.TEliteAnswer getAnswerById(1:i64 answerId);
    list<EliteModel.TEliteAnswer> getAnswersByQuestionId(1:i64 questionId);
    list<EliteModel.TEliteAnswer> getAnswersByBpId(1:i64 bpId);
    i32 getUserAnswerNumByStatus(1:i64 bpId, 2:list<i32> statusList);
    i32 getQuestionAnswerNumByStatus(1:i64 questionId, 2:list<i32> statusList);
    //审核
    bool batchAuditAnswer(1:list<i64> passAnswerIds, 2:list<i64> rejectedAnswerIds);
    bool passOneAnswer(1:i64 answerId);
    bool rejectOneAnswer(1:i64 answerId);
    bool deleteOneAnswer(1:i64 answerId);
    bool sysDeleteOneAnswer(1:i64 answerId);
    EliteModel.TAnswerListResult getAuditingAnswers(1:i32 start, 2:i32 count);


    /*用户*/
    i64 insertUser(1:EliteModel.TEliteUser user);
    bool updateUser(1:EliteModel.TEliteUser user);
    EliteModel.TEliteUser getUserByBpId(1:i64 bpId);

    /*搜索*/
    EliteModel.TQuestionListResult searchQuestion(1:EliteSearchModel.TSearchQuestionCondition condition);
    EliteModel.TQuestionIdListResult searchQuestionId(1:EliteSearchModel.TSearchQuestionCondition condition);
    EliteModel.TAnswerListResult searchAnswer(1:EliteSearchModel.TSearchAnswerCondition condition);
    EliteModel.TAnswerIdListResult searchAnswerId(1:EliteSearchModel.TSearchAnswerCondition condition);
    EliteModel.TUserListResult searchUser(1:EliteSearchModel.TSearchUserCondition condition);
    EliteModel.TSearchGlobalListResult searchGlobal(1:EliteSearchModel.TSearchGlobalCondition condition);
    EliteModel.TUserIdListResult searchUserId(1:EliteSearchModel.TSearchUserCondition condition);
    EliteModel.TExpertTeamListResult searchExpertTeam(1:EliteSearchModel.TSearchExpertTeamCondition condition);
    
	/*专题*/
	i64 setEliteSubjectHistory(1:EliteModel.TEliteSubject subject);
	EliteModel.TSubjectListResult getHistoryByStatus(1:i32 status, 2:i32 start, 3:i32 count);
	EliteModel.TSubjectListResult getAllHistory(1:i32 start, 2:i32 count);
	i64 getAllHistoryCount();
	EliteModel.TEliteSubject getHistoryById(1:i64 id);
	
	/*话题*/
	bool setEliteTopicHistory(1:EliteModel.TEliteTopic topic);
	EliteModel.TEliteTopic getEliteTopicById(1:i64 id);
	i64	getEliteTopicCount();
	i64	getEliteTopicCountByStatus(1:i32 status);
	list<EliteModel.TEliteTopic> getAllEliteTopic(1:i32 start, 2:i32 count);
	list<EliteModel.TEliteTopic> getAllEliteTopicByStatus(1:i32 status, 2:i32 start, 3:i32 count);
	
	/*碎片时间*/
	bool setEliteFragmentHistory(1:EliteModel.TEliteFragment fragment);
	i32 getFragmentCount();
	i32 getFragmentCountByType(1:EliteModel.TEliteFragmentType type);
	list<EliteModel.TEliteFragment> getFragmentByType(1:EliteModel.TEliteFragmentType type);
	EliteModel.TEliteFragment getFragmentById(1:i64 id);
	list<EliteModel.TEliteFragment> getAllFragment();
	
	/*多媒体*/
	i64 createMedia(1:EliteModel.TEliteMedia media);
	bool updateMedia(1:EliteModel.TEliteMedia media);
	bool removeMedia(1:EliteModel.TEliteMedia media);
	EliteModel.TEliteMedia getById(1:i64 id);
	list<EliteModel.TEliteMedia> getMediaListByQuestionIdAndType(1:i64 questionId, 2:EliteModel.TEliteMediaType type);
	list<EliteModel.TEliteMedia> getMediaListByAnswerIdAndType(1:i64 answerId, 2:EliteModel.TEliteMediaType type);

	/*重建索引*/
	bool rebuildQuestion(1:i64 questionId);
	bool rebuildAnswer(1:i64 answerId);
	bool rebuildUser(1:i64 bpId);
	bool rebuildQuestionsForUser(1:i64 bpId);
	bool rebuildAnswersForUser(1:i64 bpId);
	bool rebuildAnswersForQuestion(1:i64 questionId);
	bool rebuildQuestionsForSpecial(1:i64 specialId, 2:i32 specialType);
	bool rebuildExpertTeam(1:i64 bpId);

	/*关注用户或标签*/
	i64 setEliteFollow(1:EliteModel.TEliteFollow follow);
	bool updateEliteFollow(1:EliteModel.TEliteFollow follow);
	i32 getEliteFollowCountByType(1:EliteModel.TEliteFollowType type);
	EliteModel.TEliteFollow getEliteFollowById(1:i64 id);
	list<EliteModel.TEliteFollow> getEliteFollowByType(1:EliteModel.TEliteFollowType type, 2:i32 start, 3:i32 count);
	
	/*专栏*/
	i64 setEliteColumn(1:EliteModel.TEliteColumn column);
	bool updateEliteColumn(1:EliteModel.TEliteColumn column);
	EliteModel.TEliteColumn getEliteColumnById(1:i64 columnId);
	EliteModel.TColumnListResult getAllEliteColumn(1:i32 start, 2:i32 count);
	EliteModel.TColumnListResult getAllEliteColumnByStatus(1:i32 start, 2:i32 count, 3:i32 status);
	
	//用户审核
	EliteModel.TUserListResult getAuditingExperts(1:i32 start, 2:i32 count);
    EliteModel.TUserListResult getExperts(1:i32 start, 2:i32 count);
    EliteModel.TEliteUser getExpert(1:i64 bpId);
    bool reloadExpertsCache();
    bool passExpert(1:i64 bpId);
    bool rejectExpert(1:i64 bpId, 2:string reason);
    bool batchAuditExpert(1:list<i64> passIds, 2:list<i64> rejectIds, 3:string reason);
    
    //专家团
    i64 insertExpertTeam(1:EliteModel.TEliteExpertTeam expert);
    bool updateExpertTeam(1:EliteModel.TEliteExpertTeam expert);
    EliteModel.TEliteExpertTeam getExpertTeamByBpId(1:i64 bpId);
    list<EliteModel.TEliteExpertTeam> getBatchExpertTeams(1:list<i64> expertIds);
    bool addExpertNewPush(1:i64 bpId, 2:i64 questionId);
    bool addExpertNewAnswered(1:i64 bpId, 2:i64 questionId);
    bool addExpertBatchNewPush(1:list<i64> bpIds, 2:i64 questionId);
    EliteModel.TExpertTeamListResult getExpertTeamsBySortFields(1:i32 start, 2:i32 count, 3:string sortField);
    bool addExpertTag(1:i32 tagId);
    bool removeExpertTag(1:i32 tagId);
    list<i32> getExpertTagIds();
	
	//专家分组
	i64	insertExpertTeamInfo(1:EliteModel.TEliteExpertTeamInfo teamInfo);
	EliteModel.TEliteExpertTeamInfo getExpertTeamInfoById(1:i64 id);
	bool updateExpertTeamInfo(1:EliteModel.TEliteExpertTeamInfo teamInfo);
    list<EliteModel.TEliteExpertTeamInfo> getExpertTeamInfos();
	map<i64,string> getExpertTeamInfoMap();
	
    //admin
    bool saveEliteAdmin(1:EliteModel.TEliteAdmin eliteAdmin);
    bool updateEliteAdmin(1:EliteModel.TEliteAdmin eliteAdmin);
    i32  getEliteAdminStatus(1:i64 bpId);
    bool superAdmin(1:i64 bpId);
    
    //推送
    bool postMessage(1:i64 bpId, 2:EliteModel.TEliteMessagePushType messageType, 3:EliteModel.TEliteMessageData messageData, 4:EliteModel.TEliteMessageStrategy strategy);
	
	//用户行为包装
	bool likeAnswer(1:i64 answerId, 2:i64 bpId, 3:i64 ip, 4:i32 port, 5:i64 time, 6:bool messageFlag, 7:bool feedFlag);
	bool unlikeAnswer(1:i64 answerId, 2:i64 bpId);
	bool favoriteAnswer(1:i64 answerId, 2:i64 bpId, 3:i64 ip, 4:i32 port, 5:i64 time, 6:bool messageFlag, 7:bool feedFlag);
	bool unfavoriteAnswer(1:i64 answerId, 2:i64 bpId);
	bool followQuestion(1:i64 questionId, 2:i64 bpId, 3:i64 ip, 4:i32 port, 5:i64 time, 6:bool messageFlag, 7:bool feedFlag);
	bool unfollowQuestion(1:i64 questionId, 2:i64 bpId, 3:i64 ip, 4:i32 port, 5:i64 time);
	bool followPeople(1:i64 followedId, 2:i64 followId, 3:i64 ip, 4:i32 port, 5:i64 time, 6:bool messageFlag);
	bool unfollowPeople(1:i64 followedId, 2:i64 followId, 3:i64 ip, 4:i32 port, 5:i64 time);
	bool commentAnswer(1:i64 answerId, 2:i64 bpId, 3:string content, 4:i64 ip, 5:i32 port, 6:i64 time, 7:bool messageFlag, 8:bool feedFlag);
	bool treadAnswer(1:i64 answerId, 2:i64 bpId, 3:i64 ip, 4:i32 port, 5:i64 time);
	bool untreadAnswer(1:i64 answerId, 2:i64 bpId);
	bool favoriteTargetItem(1:i32 targetType, 2:i64 targetId, 3:i64 bpId, 4:i64 ip, 5:i32 port, 6:i64 time);
	bool unfavoriteTargetItem(1:i32 targetType, 2:i64 targetId, 3:i64 bpId);
	
	//广场逻辑
    i32 getNewSquareNum(1:i64 latestTime);
    list<EliteModel.TEliteSquareItem> getNewSquareList(1:i64 latestTime);
    list<EliteModel.TEliteSquareItem> getSquareBackward(1:i64 feedId, 2:i32 feedType, 3:i32 count);
    list<EliteModel.TEliteSquareItem> getSquareBackwardPage(1:i32 start, 2:i32 count);
    bool insertSquare(1:i64 feedId, 2:i32 feedType);
    bool flushSquare();
    bool removeSquareItem(1:i64 feedId, 2:i32 feedType);
    i32 getNewSelectedSquareNum(1:i64 latestTime);
    list<EliteModel.TEliteSquareItem> getNewSelectedSquareList(1:i64 latestTime);
    list<EliteModel.TEliteSquareItem> getSelectedSquareBackward(1:i64 feedId, 2:i32 feedType, 3:i32 count);
    list<EliteModel.TEliteSquareItem> getSelectedSquareBackwardPage(1:i32 start, 2:i32 count);
    list<EliteModel.TEliteSquareItem> getSelectedSquareBackwardByOldestTime(1:i64 oldestTime, 2:i32 count);
    list<EliteModel.TEliteSquareItem> getSelectedSquareForwardByLatestTime(1:i64 latestTime, 2:i32 count);
    bool insertSelectedSquare(1:i64 feedId, 2:i32 feedType);
    bool flushSelectedSquare();
    bool removeSelectedSquareItem(1:i64 feedId, 2:i32 feedType);
    bool isInSelectedSquare(1:i64 feedId, 2:i32 feedType);
}