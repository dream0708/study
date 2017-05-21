package com.sohu.bp.elite.adapter;

import java.util.List;
import java.util.Map;

import com.sohu.bp.crypt.BpCryptService.Client;
import com.sohu.bp.elite.model.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.service.EliteThriftService;
import com.sohu.suc.thrift.pool.ThriftConnectionManager;

/**
 * @author zhangzhihao
 *         2016/7/18
 */
public class EliteThriftServiceAdapter implements EliteThriftService.Iface {

    private static Logger log = LoggerFactory.getLogger(EliteThriftServiceAdapter.class);

    private ThriftConnectionManager thriftConnectionManager;
    public void setThriftConnectionManager(ThriftConnectionManager thriftConnectionManager) {
        this.thriftConnectionManager = thriftConnectionManager;
    }

    private EliteThriftService.Client getClient(TFramedTransport tFramedTransport){
        TProtocol tProtocol = new TBinaryProtocol(tFramedTransport);
        return new EliteThriftService.Client(tProtocol);
    }

    private TFramedTransport getThriftConnection(){
        return thriftConnectionManager.getConnectionPool().getConnection();
    }

    private void returnThriftConnection(TFramedTransport tFramedTransport){
        thriftConnectionManager.getConnectionPool().returnCon(tFramedTransport);
    }

    @FunctionalInterface
    private interface Runner<T>{
        T run(EliteThriftService.Client client) throws TException;
    }

    private <T> T execute(Runner<T> runner) throws TException{
        TFramedTransport tFramedTransport = null;
        try {
            tFramedTransport = getThriftConnection();
            return runner.run(getClient(tFramedTransport));
        }catch (TException e){
            log.error(e.getMessage(), e);
            if(e instanceof TTransportException && tFramedTransport != null){
                tFramedTransport.close();
            }
            throw e;
        }finally {
            returnThriftConnection(tFramedTransport);
        }
    }

    @Override
    public long insertQuestion(TEliteQuestion question) throws TException {
        return execute(client -> client.insertQuestion(question));
    }

    @Override
    public boolean updateQuestion(TEliteQuestion question) throws TException {
        return execute(client -> client.updateQuestion(question));
    }

    @Override
    public TEliteQuestion getQuestionById(long questionId) throws TException {
        return execute(client -> client.getQuestionById(questionId));
    }

    @Override
    public List<TEliteQuestion> getQuestionsByIds(List<Long> questionIds) throws TException {
        return execute(client -> client.getQuestionsByIds(questionIds));
    }

    @Override
    public boolean batchAuditQuestion(List<Long> passQuestionIds, List<Long> rejectedQuestionIds) throws TException {
        return execute(client -> client.batchAuditQuestion(passQuestionIds, rejectedQuestionIds));
    }

    @Override
    public boolean passOneQuestion(long questionId) throws TException {
        return execute(client -> client.passOneQuestion(questionId));
    }

    @Override
    public boolean rejectOneQuestion(long questionId) throws TException {
        return execute(client -> client.rejectOneQuestion(questionId));
    }

    @Override
    public boolean deleteOneQuestion(long questionId) throws TException {
        return execute(client -> client.deleteOneQuestion(questionId));
    }

    @Override
    public boolean sysDeleteOneQuestion(long questionId) throws TException {
        return execute(client -> client.sysDeleteOneQuestion(questionId));
    }

    @Override
    public TQuestionListResult getAuditingQuestions(int start, int count) throws TException {
        return execute(client -> client.getAuditingQuestions(start, count));
    }

    @Override
    public List<TEliteQuestion> getQuestionsByBpId(long bpId) throws TException {
        return execute(client -> client.getQuestionsByBpId(bpId));
    }

    @Override
    public long insertAnswer(TEliteAnswer answer) throws TException {
        return execute(client -> client.insertAnswer(answer));
    }

    @Override
    public boolean updateAnswer(TEliteAnswer answer) throws TException {
        return execute(client -> client.updateAnswer(answer));
    }

    @Override
    public TEliteAnswer getAnswerById(long answerId) throws TException {
        return execute(client -> client.getAnswerById(answerId));
    }

    @Override
    public List<TEliteAnswer> getAnswersByQuestionId(long questionId) throws TException {
        return execute(client -> client.getAnswersByQuestionId(questionId));
    }

    @Override
    public List<TEliteAnswer> getAnswersByBpId(long bpId) throws TException {
        return execute(client -> client.getAnswersByBpId(bpId));
    }

    @Override
    public int getUserAnswerNumByStatus(long bpId, List<Integer> statusList) throws TException {
        return execute(client -> client.getUserAnswerNumByStatus(bpId, statusList));
    }

    @Override
    public int getQuestionAnswerNumByStatus(long questionId, List<Integer> statusList) throws TException {
        return execute(client -> client.getQuestionAnswerNumByStatus(questionId, statusList));
    }

    @Override
    public boolean batchAuditAnswer(List<Long> passAnswerIds, List<Long> rejectedAnswerIds) throws TException {
        return execute(client -> client.batchAuditAnswer(passAnswerIds, rejectedAnswerIds));
    }

    @Override
    public boolean passOneAnswer(long answerId) throws TException {
        return execute(client -> client.passOneAnswer(answerId));
    }

    @Override
    public boolean rejectOneAnswer(long answerId) throws TException {
        return execute(client -> client.rejectOneAnswer(answerId));
    }

    @Override
    public boolean deleteOneAnswer(long answerId) throws TException {
        return execute(client -> client.deleteOneAnswer(answerId));
    }

    @Override
    public boolean sysDeleteOneAnswer(long answerId) throws TException {
        return execute(client -> client.sysDeleteOneAnswer(answerId));
    }

    @Override
    public TAnswerListResult getAuditingAnswers(int start, int count) throws TException {
        return execute(client -> client.getAuditingAnswers(start, count));
    }

    @Override
    public long insertUser(TEliteUser user) throws TException {
        return execute(client -> client.insertUser(user));
    }

    @Override
    public boolean updateUser(TEliteUser user) throws TException {
        return execute(client -> client.updateUser(user));
    }

    @Override
    public TEliteUser getUserByBpId(long bpId) throws TException {
        return execute(client -> client.getUserByBpId(bpId));
    }

    @Override
    public TQuestionListResult searchQuestion(TSearchQuestionCondition condition) throws TException {
        return execute(client -> client.searchQuestion(condition));
    }

    @Override
    public TQuestionIdListResult searchQuestionId(TSearchQuestionCondition condition) throws TException {
        return execute(client -> client.searchQuestionId(condition));
    }

    @Override
    public TAnswerListResult searchAnswer(TSearchAnswerCondition condition) throws TException {
        return execute(client -> client.searchAnswer(condition));
    }

    @Override
    public TUserListResult searchUser(TSearchUserCondition condition) throws TException {
        return execute(client -> client.searchUser(condition));
    }

    @Override
    public TSearchGlobalListResult searchGlobal(TSearchGlobalCondition condition) throws TException {
        return execute(client -> client.searchGlobal(condition));
    }

    @Override
    public TSubjectListResult getAllHistory(int start, int count) throws TException{
    	return execute(client -> client.getAllHistory(start, count));
    }
    
    @Override
    public long setEliteSubjectHistory(TEliteSubject subject) throws TException{
    	return execute(client -> client.setEliteSubjectHistory(subject));
    }
    
    @Override
    public TSubjectListResult getHistoryByStatus(int status, int start, int count) throws TException{
    	return execute(client -> client.getHistoryByStatus(status, start, count));
    }
    
    @Override
    public long getAllHistoryCount() throws TException{
    	return execute(client -> client.getAllHistoryCount());
    }
    
    @Override
    public TEliteSubject getHistoryById(long id) throws TException{
    	return execute(client -> client.getHistoryById(id));
    }

	@Override
	public boolean setEliteTopicHistory(TEliteTopic topic) throws TException {
		return execute(client -> client.setEliteTopicHistory(topic));
	}

	@Override
	public TEliteTopic getEliteTopicById(long id) throws TException {
		return execute(client -> client.getEliteTopicById(id));
	}

	@Override
	public long getEliteTopicCount() throws TException {
		return execute(client -> client.getEliteTopicCount());
	}

	@Override
	public long getEliteTopicCountByStatus(int status) throws TException {
		return execute(client -> client.getEliteTopicCountByStatus(status));
	}

	@Override
	public List<TEliteTopic> getAllEliteTopic(int start, int count) throws TException {
		return execute(client -> client.getAllEliteTopic(start, count));
	}

	@Override
	public List<TEliteTopic> getAllEliteTopicByStatus(int status, int start, int count) throws TException {
		return execute(client -> client.getAllEliteTopicByStatus(status, start, count));
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteThriftService.Iface#createMedia(com.sohu.bp.elite.model.TEliteMedia)
	 */
	@Override
	public long createMedia(TEliteMedia media) throws TException {
		return execute(client -> client.createMedia(media));
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteThriftService.Iface#updateMedia(com.sohu.bp.elite.model.TEliteMedia)
	 */
	@Override
	public boolean updateMedia(TEliteMedia media) throws TException {
		return execute(client -> client.updateMedia(media));
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteThriftService.Iface#removeMedia(com.sohu.bp.elite.model.TEliteMedia)
	 */
	@Override
	public boolean removeMedia(TEliteMedia media) throws TException {
		return execute(client -> client.removeMedia(media));
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteThriftService.Iface#getById(long)
	 */
	@Override
	public TEliteMedia getById(long id) throws TException {
		return execute(client -> client.getById(id));
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteThriftService.Iface#getMediaListByQuestionIdAndType(long, com.sohu.bp.elite.model.TEliteMediaType)
	 */
	@Override
	public List<TEliteMedia> getMediaListByQuestionIdAndType(long questionId, TEliteMediaType type) throws TException {
		return execute(client -> client.getMediaListByQuestionIdAndType(questionId, type));
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteThriftService.Iface#getMediaListByAnswerIdAndType(long, com.sohu.bp.elite.model.TEliteMediaType)
	 */
	@Override
	public List<TEliteMedia> getMediaListByAnswerIdAndType(long answerId, TEliteMediaType type) throws TException {
		return execute(client -> client.getMediaListByAnswerIdAndType(answerId, type));
	}

    @Override
    public boolean rebuildQuestion(long questionId) throws TException {
        return execute(client -> client.rebuildQuestion(questionId));
    }

    @Override
    public boolean rebuildAnswer(long answerId) throws TException {
        return execute(client -> client.rebuildAnswer(answerId));
    }

    @Override
    public boolean rebuildUser(long bpId) throws TException {
        return execute(client -> client.rebuildUser(bpId));
    }

    @Override
    public boolean rebuildQuestionsForUser(long bpId) throws TException {
        return execute(client -> client.rebuildQuestionsForUser(bpId));
    }

    @Override
    public boolean rebuildAnswersForUser(long bpId) throws TException {
        return execute(client -> client.rebuildAnswersForUser(bpId));
    }

    @Override
    public boolean rebuildAnswersForQuestion(long questionId) throws TException {
        return execute(client -> client.rebuildAnswersForQuestion(questionId));
    }


    @Override
	public boolean setEliteFragmentHistory(TEliteFragment fragment) throws TException {
		return execute(client -> client.setEliteFragmentHistory(fragment));
	}

	@Override
	public int getFragmentCount() throws TException {
		return execute(client -> client.getFragmentCount());
	}



	@Override
	public TEliteFragment getFragmentById(long id) throws TException {
		return execute(client -> client.getFragmentById(id));
	}

	@Override
	public int getFragmentCountByType(TEliteFragmentType type) throws TException {
		return execute(client -> client.getFragmentCountByType(type));
	}

	@Override
	public List<TEliteFragment> getFragmentByType(TEliteFragmentType type) throws TException {
		return execute(client -> client.getFragmentByType(type));
	}

	@Override
	public List<TEliteFragment> getAllFragment() throws TException {
		return execute(client -> client.getAllFragment());
	}

	@Override
	public long setEliteFollow(TEliteFollow follow) throws TException {
		return execute(client -> client.setEliteFollow(follow));
	}

	@Override
	public boolean updateEliteFollow(TEliteFollow follow) throws TException {
		return execute(client -> client.updateEliteFollow(follow));
	}

	@Override
	public int getEliteFollowCountByType(TEliteFollowType type) throws TException {
		return execute(client -> client.getEliteFollowCountByType(type));
	}

	@Override
	public List<TEliteFollow> getEliteFollowByType(TEliteFollowType type, int start, int count) throws TException {
		return execute(client -> client.getEliteFollowByType(type, start, count));
	}

	@Override
	public TEliteFollow getEliteFollowById(long id) throws TException {
		return execute(client -> client.getEliteFollowById(id));
	}

	@Override
	public long setEliteColumn(TEliteColumn column) throws TException {
		return execute(client -> client.setEliteColumn(column));
	}

	@Override
	public boolean updateEliteColumn(TEliteColumn column) throws TException {
		return execute(client -> client.updateEliteColumn(column));
	}

	@Override
	public TEliteColumn getEliteColumnById(long columnId) throws TException {
		return execute(client -> client.getEliteColumnById(columnId));
	}

	@Override
	public TColumnListResult getAllEliteColumn(int start, int count) throws TException {
		return execute(client -> client.getAllEliteColumn(start, count));
	}

	@Override
	public TColumnListResult getAllEliteColumnByStatus(int start, int count, int status) throws TException {
		return execute(client -> client.getAllEliteColumnByStatus(start, count, status));
	}

	@Override
	public boolean rebuildQuestionsForSpecial(long specialId, int specialType) throws TException {
		return execute(client -> client.rebuildQuestionsForSpecial(specialId, specialType));
	}

	@Override
	public List<TEliteQuestion> getQuestionsBySpecial(long speicialId, int specialType) throws TException {
		return execute(client -> client.getQuestionsBySpecial(speicialId, specialType));
	}

    @Override
    public int getUserQuestionNumByStatus(long bpId, List<Integer> statusList) throws TException {
        return execute(client -> client.getUserQuestionNumByStatus(bpId, statusList));
    }

    @Override
    public TUserListResult getAuditingExperts(int start, int count) throws TException {
        return execute(client -> client.getAuditingExperts(start, count));
    }

    @Override
    public TUserListResult getExperts(int start, int count) throws TException {
        return execute(client -> client.getExperts(start, count));
    }

    @Override
    public boolean reloadExpertsCache() throws TException {
        return execute(client -> client.reloadExpertsCache());
    }

    @Override
    public boolean passExpert(long bpId) throws TException {
        return execute(client -> client.passExpert(bpId));
    }

    @Override
    public boolean rejectExpert(long bpId, String reason) throws TException {
        return execute(client -> client.rejectExpert(bpId, reason));
    }

    @Override
    public long insertExpertTeam(TEliteExpertTeam expert) throws TException {
        return execute(client -> client.insertExpertTeam(expert));
    }

    @Override
    public boolean updateExpertTeam(TEliteExpertTeam expert) throws TException {
        return execute(client -> client.updateExpertTeam(expert));
    }

    @Override
    public TEliteExpertTeam getExpertTeamByBpId(long bpId) throws TException {
        return execute(client -> client.getExpertTeamByBpId(bpId));
    }

    @Override
    public List<TEliteExpertTeam> getBatchExpertTeams(List<Long> expertIds) throws TException {
        return execute(client -> client.getBatchExpertTeams(expertIds));
    }

    @Override
    public TUserIdListResult searchUserId(TSearchUserCondition condition) throws TException {
        return execute(client -> client.searchUserId(condition));
    }

    @Override
    public boolean addExpertNewPush(long bpId, long questionId) throws TException {
        return execute(client -> client.addExpertNewPush(bpId, questionId));
    }

    @Override
    public boolean addExpertNewAnswered(long bpId, long questionId) throws TException {
        return execute(client -> client.addExpertNewAnswered(bpId, questionId));
    }

    @Override
    public boolean addExpertBatchNewPush(List<Long> bpIds, long questionId) throws TException {
        return execute(client -> client.addExpertBatchNewPush(bpIds, questionId));
    }

    @Override
    public boolean batchAuditExpert(List<Long> passIds, List<Long> rejectIds, String reason) throws TException {
        return execute(client -> client.batchAuditExpert(passIds, rejectIds, reason));
    }

    @Override
    public TExpertTeamListResult getExpertTeamsBySortFields(int start, int count, String sortField) throws TException {
        return execute(client -> client.getExpertTeamsBySortFields(start, count, sortField));
    }

    @Override
    public boolean saveEliteAdmin(TEliteAdmin eliteAdmin) throws TException {
        return execute(client -> client.saveEliteAdmin(eliteAdmin));
    }

    @Override
    public boolean updateEliteAdmin(TEliteAdmin eliteAdmin) throws TException {
        return execute(client -> client.updateEliteAdmin(eliteAdmin));
    }

    @Override
    public int getEliteAdminStatus(long bpId) throws TException {
        return execute(client -> client.getEliteAdminStatus(bpId));
    }

    @Override
    public boolean superAdmin(long bpId) throws TException {
        return execute(client -> client.superAdmin(bpId));
    }

    @Override
    public TEliteUser getExpert(long bpId) throws TException {
        return execute(client -> client.getExpert(bpId));
    }

    @Override
    public TExpertTeamListResult searchExpertTeam(TSearchExpertTeamCondition condition) throws TException {
        return execute(client -> client.searchExpertTeam(condition));
    }

    @Override
    public boolean rebuildExpertTeam(long bpId) throws TException {
        return execute(client -> client.rebuildExpertTeam(bpId));
    }

    @Override
    public boolean addExpertTag(int tagId) throws TException {
        return execute(client -> client.addExpertTag(tagId));
    }

    @Override
    public boolean removeExpertTag(int tagId) throws TException {
        return execute(client -> client.removeExpertTag(tagId));
    }

    @Override
    public List<Integer> getExpertTagIds() throws TException {
        return execute(client -> client.getExpertTagIds());
    }

    @Override
    public boolean postMessage(long bpId, TEliteMessagePushType messageType, TEliteMessageData messageData,
            TEliteMessageStrategy strategy) throws TException {
        return execute(client -> client.postMessage(bpId, messageType, messageData, strategy));
    }

    @Override
    public boolean likeAnswer(long answerId, long bpId, long ip, int port, long time, boolean messageFlag, boolean feedFlag)
            throws TException {
        return execute(client -> client.likeAnswer(answerId, bpId, ip, port, time, messageFlag, feedFlag));
    }

	@Override
	public boolean unlikeAnswer(long answerId, long bpId) throws TException {
		return execute(client -> client.unlikeAnswer(answerId, bpId));
	}

	@Override
	public boolean favoriteAnswer(long answerId, long bpId, long ip, int port, long time, boolean messageFlag, boolean feedFlag)
			throws TException {
		return execute(client -> client.favoriteAnswer(answerId, bpId, ip, port, time, messageFlag, feedFlag));
	}

	@Override
	public boolean unfavoriteAnswer(long answerId, long bpId) throws TException {
		return execute(client -> client.unfavoriteAnswer(answerId, bpId));
	}

	@Override
	public boolean unfollowQuestion(long questionId, long bpId, long ip, int port, long time) throws TException {
		return execute(client -> client.unfollowQuestion(questionId, bpId, ip, port, time));
	}

	@Override
	public boolean followPeople(long followedId, long followId, long ip, int port, long time, boolean messageFlag)
			throws TException {
		return execute(client -> client.followPeople(followedId, followId, ip, port, time, messageFlag));
	}

	@Override
	public boolean unfollowPeople(long followedId, long followId, long ip, int port, long time) throws TException {
		return execute(client -> client.unfollowPeople(followedId, followId, ip, port, time));
	}

	@Override
	public boolean followQuestion(long questionId, long bpId, long ip, int port, long time, boolean messageFlag, boolean feedFlag)
			throws TException {
		return execute(client -> client.followQuestion(questionId, bpId, ip, port, time, messageFlag, feedFlag));
	}

    @Override
    public boolean commentAnswer(long answerId, long bpId, String content, long ip, int port, long time,
            boolean messageFlag, boolean feedFlag) throws TException {
        return execute(client -> client.commentAnswer(answerId, bpId, content, ip, port, time, messageFlag, feedFlag));
    }

    @Override
    public boolean treadAnswer(long answerId, long bpId, long ip, int port, long time) throws TException {
        return execute(client -> client.treadAnswer(answerId, bpId, ip, port, time));
    }

    @Override
    public boolean untreadAnswer(long answerId, long bpId) throws TException {
        return execute(client -> client.untreadAnswer(answerId, bpId));
    }

	@Override
	public long insertExpertTeamInfo(TEliteExpertTeamInfo teamInfo) throws TException {
		return execute(client -> client.insertExpertTeamInfo(teamInfo));
	}

	@Override
	public boolean updateExpertTeamInfo(TEliteExpertTeamInfo teamInfo) throws TException {
		return execute(client -> client.updateExpertTeamInfo(teamInfo));
	}

	@Override
	public Map<Long, String> getExpertTeamInfoMap() throws TException {
		return execute(client -> client.getExpertTeamInfoMap());
	}

    @Override
    public List<TEliteExpertTeamInfo> getExpertTeamInfos() throws TException {
        return execute(client -> client.getExpertTeamInfos());
    }

    @Override
    public TEliteExpertTeamInfo getExpertTeamInfoById(long id) throws TException {
        return execute(client -> client.getExpertTeamInfoById(id));
    }

    @Override
    public TAnswerIdListResult searchAnswerId(TSearchAnswerCondition condition) throws TException {
        return execute(client -> client.searchAnswerId(condition));
    }

    @Override
    public boolean favoriteTargetItem(int targetType, long targetId, long bpId, long ip, int port, long time)
            throws TException {
        return execute(client -> client.favoriteTargetItem(targetType, targetId, bpId, ip, port, time));
    }

    @Override
    public boolean unfavoriteTargetItem(int targetType, long targetId, long bpId) throws TException {
        return execute(client -> client.unfavoriteTargetItem(targetType, targetId, bpId));
    }

    @Override
    public int getNewSquareNum(long latestTime) throws TException {
        return execute(client -> client.getNewSquareNum(latestTime));
    }

    @Override
    public List<TEliteSquareItem> getNewSquareList(long latestTime) throws TException {
        return execute(client -> client.getNewSquareList(latestTime));
    }

    @Override
    public List<TEliteSquareItem> getSquareBackward(long feedId, int feedType, int count) throws TException {
        return execute(client -> client.getSquareBackward(feedId, feedType, count));
    }

    @Override
    public boolean insertSquare(long feedId, int feedType) throws TException {
        return execute(client -> client.insertSquare(feedId, feedType));
    }

    @Override
    public boolean flushSquare() throws TException {
        return execute(client -> client.flushSquare());
    }

    @Override
    public boolean removeSquareItem(long feedId, int feedType) throws TException {
        return execute(client -> client.removeSquareItem(feedId, feedType));
    }

    @Override
    public int getNewSelectedSquareNum(long latestTime) throws TException {
        return execute(client -> client.getNewSelectedSquareNum(latestTime));
    }

    @Override
    public List<TEliteSquareItem> getNewSelectedSquareList(long latestTime) throws TException {
        return execute(client -> client.getNewSelectedSquareList(latestTime));
    }

    @Override
    public List<TEliteSquareItem> getSelectedSquareBackward(long feedId, int feedType, int count) throws TException {
        return execute(client -> client.getSelectedSquareBackward(feedId, feedType, count));
    }

    @Override
    public boolean insertSelectedSquare(long feedId, int feedType) throws TException {
        return execute(client -> client.insertSelectedSquare(feedId, feedType));
    }

    @Override
    public boolean flushSelectedSquare() throws TException {
        return execute(client -> client.flushSelectedSquare());
    }

    @Override
    public boolean removeSelectedSquareItem(long feedId, int feedType) throws TException {
        return execute(client -> client.removeSelectedSquareItem(feedId, feedType));
    }

    @Override
    public boolean isInSelectedSquare(long feedId, int feedType) throws TException {
        return execute(client -> client.isInSelectedSquare(feedId, feedType));
    }

	@Override
	public List<TEliteSquareItem> getSquareBackwardPage(int start, int count) throws TException {
		return execute(client -> client.getSquareBackwardPage(start, count));
	}

	@Override
	public List<TEliteSquareItem> getSelectedSquareBackwardPage(int start, int count) throws TException {
		return execute(client -> client.getSelectedSquareBackwardPage(start, count));
	}

    @Override
    public long insertQuestionWithOptions(TEliteQuestion question, boolean securityFlag) throws TException {
        return execute(client -> client.insertQuestionWithOptions(question, securityFlag));
    }

    @Override
    public long insertAnswerWithOptions(TEliteAnswer answer, boolean securityFlag) throws TException {
        return execute(client -> client.insertAnswerWithOptions(answer, securityFlag));
    }

    @Override
    public List<TEliteSquareItem> getSelectedSquareBackwardByOldestTime(long oldestTime, int count) throws TException {
        return execute(client -> client.getSelectedSquareBackwardByOldestTime(oldestTime, count));
    }

    @Override
    public List<TEliteSquareItem> getSelectedSquareForwardByLatestTime(long latestTime, int count) throws TException {
        return execute(client -> client.getSelectedSquareForwardByLatestTime(latestTime, count));
    }
    
}
