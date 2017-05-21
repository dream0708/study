package com.sohu.bp.elite.service;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.bean.SearchGlobalListResult;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.model.TSearchExpertTeamCondition;
import com.sohu.bp.elite.model.TSearchGlobalCondition;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import com.sohu.bp.elite.model.TSearchUserCondition;
import com.sohu.bp.elite.task.IndexParam;

/**
 * @author zhangzhihao
 *         2016/7/14
 */
public interface EliteSearchService {

    
    ListResult searchQuestionId(TSearchQuestionCondition condition);

    ListResult searchQuestion(TSearchQuestionCondition condition);

    ListResult searchAnswer(TSearchAnswerCondition condition);
    
    ListResult searchAnswerId(TSearchAnswerCondition condition);

    ListResult searchUser(TSearchUserCondition condition);
    
    ListResult searchUserId(TSearchUserCondition condition);
    
    ListResult searchExpertTeam(TSearchExpertTeamCondition condition);

    SearchGlobalListResult searchGlobal(TSearchGlobalCondition condition);

}
