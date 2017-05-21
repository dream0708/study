package com.sohu.bp.elite.api.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.api.util.PropertyFilterUtil;
import com.sohu.bp.elite.api.util.TagIdsUtil;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangzhihao
 *         2016/7/14
 */
@Controller
@RequestMapping("/innerapi/question")
public class EliteQuestionApi {

    private static final Logger log = LoggerFactory.getLogger(EliteQuestionApi.class);

    private EliteThriftServiceAdapter eliteThriftServiceAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();

    @ResponseBody
    @RequestMapping(value = "index-data", produces = "application/json;charset=utf-8")
    public String getById(@RequestParam("id") Long questionId){
        JSONObject json = ResponseJSON.getDefaultResJSON();
        try {
            TEliteQuestion tEliteQuestion = eliteThriftServiceAdapter.getQuestionById(questionId);
            if(tEliteQuestion != null) {
                JSONObject dataJSON = JSONObject.fromObject(JSON.toJSONString(tEliteQuestion, PropertyFilterUtil.getFilter(TEliteQuestion.class)));
                if (tEliteQuestion.getSpecialType() == BpType.Elite_Vote.getValue() && StringUtils.isNotBlank(tEliteQuestion.getOptions())) {
                    StringBuilder builder = new StringBuilder();
                    try {
                        JSONArray optionArray = JSONArray.fromObject(tEliteQuestion.getOptions());
                        for (int i = 0; i < optionArray.size(); i++) {
                            String description = optionArray.getJSONObject(i).getString("description");
                            builder.append(description).append(Constants.BLANK_SPLIT_CHAR);
                        }
                    } catch (Exception e) {
                        log.error("", e);
                    }
                    dataJSON.put("options", builder.toString());
                }
                List<Integer> validStatus = new ArrayList<>();
                validStatus.add(EliteAnswerStatus.AUDITING.getValue());
                validStatus.add(EliteAnswerStatus.PASSED.getValue());
                validStatus.add(EliteAnswerStatus.PUBLISHED.getValue());
                int answerNum = eliteThriftServiceAdapter.getQuestionAnswerNumByStatus(questionId, validStatus);
                dataJSON.put("answerNum", answerNum);
                json.put("data", dataJSON);

            }else {
                json = ResponseJSON.getErrorParamsJSON();
            }
        } catch (TException e) {
            log.error("", e);
            json = ResponseJSON.getErrorInternalJSON();
        }
        return json.toString();
    }

    @ResponseBody
    @RequestMapping(value = "answers/index-data", produces = "application/json;charset=utf-8")
    public String getAnswersByQuestionId(@RequestParam("id") Long questionId){
        JSONObject json = ResponseJSON.getDefaultResJSON();
        try {
            List<TEliteAnswer> tEliteAnswers = eliteThriftServiceAdapter.getAnswersByQuestionId(questionId);
            TagIdsUtil.addTagIds(tEliteAnswers);
            if(tEliteAnswers != null && tEliteAnswers.size() > 0){
                json.put("data", JSON.toJSONString(tEliteAnswers, PropertyFilterUtil.getFilter(TEliteAnswer.class)));
            }else {
                json = ResponseJSON.getErrorParamsJSON();
            }
        }catch (TException e){
            log.error("", e);
            json = ResponseJSON.getErrorInternalJSON();
        }
        return json.toString();
    }
}
