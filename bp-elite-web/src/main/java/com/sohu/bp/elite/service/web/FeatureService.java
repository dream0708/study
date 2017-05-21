package com.sohu.bp.elite.service.web;

import java.util.List;

import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.elite.action.bean.FeedListDisplayBean;
import com.sohu.bp.elite.action.bean.feature.EliteFeatureAppWrapperBean;
import com.sohu.bp.elite.action.bean.feature.EliteFeatureItemBean;
import com.sohu.bp.elite.action.bean.feature.EliteFocusBean;
import com.sohu.bp.elite.action.bean.feature.EliteHeadFragment;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.model.TEliteColumn;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSubject;
import com.sohu.bp.elite.model.TEliteTopic;

import net.sf.json.JSONArray;

/**
 * 
 * @author zhijungou
 * 2016年8月26日
 * 和专栏有关的服务，涵盖专题，话题，标签导航，标签广场等内容
 * TODO 乐乐那边接口的内容
 */
public interface FeatureService {
	/**
	 * 获取专题列表
	 * @param start
	 * @param count
	 * @return
	 */
	public List<TEliteSubject> getEliteSubject(Integer start, Integer count );
	/**
	 * 获取专题的数量
	 * @return
	 */
	public Long getEliteCount();
	/**
	 * 获取某一专题
	 * @param id
	 * @return
	 */
	public TEliteSubject getEliteSubjectById(Long id);
	/**
	 * 获取话题列表
	 * @param start
	 * @param count
	 * @return
	 */
	public List<TEliteTopic> getEliteTopic(Integer start, Integer count);
	/**
	 * 获取某一话题
	 * @param id
	 * @return
	 */
	public TEliteTopic getEliteTopicById(Long id);
	/**
	 * 获取话题数量
	 * @return
	 */
	public long getTopicCount();
	/**
	 * 获取导航标签
	 * @return
	 */
	public JSONArray getTagNav();

	/**
	 *
	 * @return
	 */
	public String getSlogan();
	/**
	 * 获取标签广场
	 * @param tagId
	 * @return
	 */
	public List<Integer> getTagSquare(Integer tagId);
	/**
	 * 获取精选回答
	 * @param start
	 * @param count
	 * @return
	 */ 
	public FeedListDisplayBean getTopFeeds(FeedListDisplayBean bean, Long bpId, Integer start, Integer count);
	/**
	 * 获取相关问题
	 * @param bpId
	 * @param questionId
	 * @return
	 */
	public List<TEliteQuestion> getRelevantQuestion(Long bpId, Long questionId);
	/**
	 * 获取问题相关标签
	 * @param question
	 * @return
	 */
	public List<Tag> getQuestionRecomTags(Long question);
	/**
	 * 获取用户推荐,tag用于控制轮播
	 * @param userId
	 * @return
	 */
	public List<UserDetailDisplayBean> getRecomUsers(Long viewerId, Integer tag);
	/**
	 * 获取问题的回答数
	 * @param QuestionId
	 * @return
	 */
	public Long getAnswerNum(Long QuestionId);
	/**
	 * 获取推荐标签
	 * @param bpId
	 * @return
	 */
	public List<Tag> getRecomTags(Long bpId);
	/**
	 * 获取推荐内容
	 * @param type
	 * @param count
	 * @return
	 */
	public List<String> getRecomList(Integer type, Integer count);
	/**
	 * 获取焦点图
	 * @return
	 */
	public List<EliteFocusBean> getFocus();
	/**
	 * 获取App头图
	 * @return
	 */
	public EliteHeadFragment getAppHeader();
	/**
	 * 获取App所有内容包装类
	 * @return
	 */
	public EliteFeatureAppWrapperBean getAppWrapper();
}
