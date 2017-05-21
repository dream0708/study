package com.sohu.bp.elite.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.SearchConditionFilterUtil;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.elite.bean.EliteSearchResultBean;
import com.sohu.bp.elite.bean.SearchGlobalListResult;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.model.TSearchExpertTeamCondition;
import com.sohu.bp.elite.model.TSearchGlobalCondition;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import com.sohu.bp.elite.model.TSearchUserCondition;
import com.sohu.bp.elite.service.EliteSearchService;
import com.sohu.bp.elite.util.BpHttpUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangzhihao
 *         2016/7/14
 */
public class EliteSearchServiceImpl implements EliteSearchService {

    private static final Logger log = LoggerFactory.getLogger(EliteSearchServiceImpl.class);
    private BpDecorationServiceAdapter decorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();

    private String searchHost;

    private String searchQuestionUri = "/decoration/search/innerapi/search/question";
    private String searchAnswerUri = "/decoration/search/innerapi/search/answer";
    private String searchUserUri = "/decoration/search/innerapi/search/user";
    private String searchGlobalUri = "/decoration/search/innerapi/search/elite-global";
    private String searchExpertTeamUri = "/decoration/search/innerapi/search/elite-expert";


    public void setSearchHost(String searchHost) {
        this.searchHost = searchHost;
    }

    @Override
    public ListResult searchQuestionId(TSearchQuestionCondition condition) {
        if(condition == null)
            return new ListResult();
        return getAutoCompleteSearchResult(searchQuestionUri, searchHost, getSearchQuestionParams(condition));
    }

    @Override
    public ListResult searchQuestion(TSearchQuestionCondition condition) {
        if(condition == null)
            return new ListResult();
        return getSearchResult(searchQuestionUri, searchHost, getSearchQuestionParams(condition));
    }
    
    @Override
    public ListResult searchAnswerId(TSearchAnswerCondition condition) {
        if (null == condition) return new ListResult();
        return getAutoCompleteSearchResult(searchAnswerUri, searchHost, getSearchAnswerParams(condition));
    }

    @Override
    public ListResult searchAnswer(TSearchAnswerCondition condition) {
        if(condition == null)
            return new ListResult();
        return getSearchResult(searchAnswerUri, searchHost, getSearchAnswerParams(condition));
    }

    @Override
    public ListResult searchUserId(TSearchUserCondition condition) {
        if(condition == null)
            return new ListResult();
        return getAutoCompleteSearchResult(searchUserUri, searchHost, getSearchUserParams(condition));
    }

    @Override
    public ListResult searchUser(TSearchUserCondition condition) {
        if(condition == null)
            return new ListResult();
        return getSearchResult(searchUserUri, searchHost, getSearchUserParams(condition));
    }
    
    @Override
    public ListResult searchExpertTeam(TSearchExpertTeamCondition condition) {
        if (condition == null) return new ListResult();
        return getSearchResult(searchExpertTeamUri, searchHost, getSearchExpertTeamParam(condition));
    }

    @Override
    public SearchGlobalListResult searchGlobal(TSearchGlobalCondition condition) {
        if(condition == null || StringUtils.isBlank(condition.getKeywords()))
            return null;
        String response = BpHttpUtil.post(searchGlobalUri, searchHost, getSearchGlobalParams(condition));
        if(StringUtils.isNotBlank(response)) {
            JSONObject jsonObject = JSONObject.fromObject(response);
            if (jsonObject.getInt("code") == 0) {
                JSONArray rankJsonArray = jsonObject.getJSONObject("data").getJSONArray("rank");
                List<String> rankList = new ArrayList<>();
                for(Object rank : rankJsonArray){
                    rankList.add(rank.toString());
                }

                Map<String, Integer> totalCounts = new HashMap<>();
                List<EliteSearchResultBean> questions = getSearchGlobalResult(response, totalCounts, "question");
                List<EliteSearchResultBean> answers = getSearchGlobalResult(response, totalCounts, "answer");
                List<EliteSearchResultBean> users = getSearchGlobalResult(response, totalCounts, "user");
                
                SearchGlobalListResult listResult = new SearchGlobalListResult();
                listResult.setQuestions(questions);
                listResult.setAnswers(answers);
                listResult.setUsers(users);
                listResult.setTotalCounts(totalCounts);
                listResult.setRank(rankList);

                return listResult;
            } else {
                log.info("elite search global error");
            }
        }
        return null;
    }

    private Map<String, String> getSearchQuestionParams(TSearchQuestionCondition condition){
        return SearchConditionFilterUtil.getParamsFromCondition(condition);
    }

    private Map<String, String> getSearchAnswerParams(TSearchAnswerCondition condition){
        return SearchConditionFilterUtil.getParamsFromCondition(condition);
    }

    private Map<String, String> getSearchUserParams(TSearchUserCondition condition){
        return SearchConditionFilterUtil.getParamsFromCondition(condition);
    }
    
    private Map<String, String> getSearchExpertTeamParam(TSearchExpertTeamCondition condition) {
        return SearchConditionFilterUtil.getParamsFromCondition(condition);
    }

    private Map<String, String> getSearchGlobalParams(TSearchGlobalCondition condition){
        Map<String, String> params = new HashMap<>();
        params.put("keywords", condition.getKeywords());
        params.put("from", String.valueOf(condition.getFrom()));
        params.put("count", String.valueOf(condition.getCount()));
        return params;
    }

    private ListResult getAutoCompleteSearchResult(String searchUri, String searchHost, Map<String, String> params) {
        ListResult listResult = new ListResult(0, new ArrayList<>());
        String response = BpHttpUtil.post(searchUri, searchHost, params);
        if(StringUtils.isNotBlank(response)){
            JSONObject json = JSONObject.fromObject(response);
            if(json != null && json.containsKey("code") && json.getInt("code") == 0 && json.containsKey("data")){
                JSONObject data = json.getJSONObject("data");
                if(data != null){
                    Long total = data.getLong("total");
                    List<Long> list = new ArrayList<Long>();
                    if (data.containsKey("idArray")) {
                        JSONArray idArray = data.getJSONArray("idArray");
                        if (idArray != null && idArray.size() > 0) {
                            for (int i = 0; i < idArray.size(); i++) {
                                list.add(idArray.getLong(i));
                            }
                        }
                    }
                    listResult.setEntities(list);
                    listResult.setTotal(total);
                }
            } else {
                log.error("search failed, response=" + response);
            }
        }
        return listResult;
    }
    private ListResult getSearchResult(String searchUri, String searchHost, Map<String, String> params){
        ListResult listResult = new ListResult(0, new ArrayList<>());
        String response = BpHttpUtil.post(searchUri, searchHost, params);
        if(StringUtils.isNotBlank(response)){
            JSONObject json = JSONObject.fromObject(response);
            if(json != null && json.containsKey("code") && json.getInt("code") == 0 && json.containsKey("data")){
                JSONObject data = json.getJSONObject("data");
                if(data != null){
                    Long total = data.getLong("total");
                    if (data.containsKey("array")) {
                        List<EliteSearchResultBean> list = new ArrayList<EliteSearchResultBean>();
                        JSONArray array = data.getJSONArray("array");
                        for (int i = 0; i < array.size(); i++) {
                        	EliteSearchResultBean bean = new EliteSearchResultBean();
                        	JSONObject ele = array.getJSONObject(i);
                        	bean.setId(ele.getLong("id")).setHighlightWord(ele.getString("highlightWord"));
                        	JSONObject highlightText = ele.getJSONObject("highlightText");
                        	String key = String.valueOf(highlightText.keys().next());
                        	bean.setHighlightText((String)highlightText.getJSONArray(key).get(0));
                        	list.add(bean);
                        }
                        listResult.setEntities(list);
                    } else {
                        List<Long> list = new ArrayList<Long>();
                        JSONArray idArray = data.getJSONArray("idArray");
                        if(idArray != null && idArray.size() > 0){
                            for(int i = 0; i < idArray.size(); i++){
                                list.add(idArray.getLong(i));
                            }
                        }
                        listResult.setEntities(list);
                    }
                        listResult.setTotal(total);
                    }
            } else {
                log.error("search failed, response=" + response);
            }
        }
        return listResult;
    }
    
    private List<EliteSearchResultBean> getSearchGlobalResult(String response, Map<String, Integer> totalCounts, String dataType) {
        JSONObject responseJSON = JSONObject.fromObject(response);
        List<EliteSearchResultBean> list = new ArrayList<EliteSearchResultBean>();
        JSONObject data = responseJSON.getJSONObject("data").getJSONObject(dataType);
        totalCounts.put(dataType, data.getInt("total"));
        JSONArray array = data.getJSONArray("array");
        for (int i = 0; i < array.size(); i++) {
            JSONObject element = array.getJSONObject(i);
            EliteSearchResultBean bean = new EliteSearchResultBean();
            bean.setId(element.getLong("id")).setHighlightWord(element.getString("highlightWord"));
            JSONObject highlightText = element.getJSONObject("highlightText");
            bean.setHighlightText(highlightText.getString(String.valueOf(highlightText.keys().next())));
            list.add(bean);
        }
        return list;
    }


}
