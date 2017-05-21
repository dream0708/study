package com.sohu.bp.elite.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sohu.bp.elite.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.task.EliteTrackVisitorAsyncTask;
import com.sohu.bp.elite.util.LogCollectUtil;

/**
 * Created by nicholastang on 2016/11/9.
 */
@Service
@Aspect
public class VisitorTracker {
    private static final Logger logger = LoggerFactory.getLogger(VisitorTracker.class);

    @Before("execution(* com.sohu.bp.elite.action.IndexAction.index(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.AnswerAction.getQuestionAnswerComments(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.AnswerAction.showAnswerPage(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.AnswerAction.showUpdateAnswerPage(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.AnswerAction.showCommentPage(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.FansAction.questionFansList(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.FansAction.userFansList(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.FansAction.tagFansList(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.FeatureAction.getSubjectList(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.FeatureAction.getSubjectById(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.FollowAction.followTagList(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.FollowAction.followPersonList(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.FollowAction.followQuestionList(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.InviteAction.go(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.QuestionAction.showPublishQuestionPage(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.QuestionAction.showUpdateQuestionPage(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.QuestionAction.getQuestionWithAnswers(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.QuestionAction.getQuestionTags(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.SearchAction.showSearchPage(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.SearchAction.showSearchGlobalResult(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.SearchAction.searchQuestion(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.SearchAction.searchAnswer(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.SearchAction.searchUser(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.SearchAction.searchTag(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.TagAction.getTagQuestions(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.TagAction.searchAnswer(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.TagAction.list(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserAction.getMyFavorites(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserAction.myFans(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserAction.myQuestionOrAnswers(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.myHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.myQuestionHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.myAnswerHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.userHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.userQuestionHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.userAnswerHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.designerHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.designerQuestionHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.designerAnswerHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.foremanHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.foremanQuestionHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.foremanAnswerHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.selfHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.selfQuestionHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.selfAnswerHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.companyHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.UserHomeAction.companyQuestionHome(..)) or "
    		+ "execution(* com.sohu.bp.elite.action.ColumnAction.getColumnDetail(..)) or "
			+ "execution(* com.sohu.bp.elite.action.ColumnAction.getColumnList(..)) or "
			+ "execution(* com.sohu.bp.elite.action.ColumnAction.getColumnQuestions(..))")
    public void track(JoinPoint point){
       EliteAsyncTaskPool.addTask(new EliteTrackVisitorAsyncTask(point));
    }
}
