package com.sohu.bp.elite.api.api;

import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.util.ResponseJSON;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhangzhihao
 *         2016/8/27
 */
@Controller
@RequestMapping("innerapi/rebuild-index")
public class RebuildEliteIndexApi {

    private Logger log = LoggerFactory.getLogger(RebuildEliteIndexApi.class);
    private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();


    //重建一个问题索引
    @ResponseBody
    @RequestMapping(value = "question", produces = "application/json;charset=utf-8")
    public String rebuildQuestion(long questionId){
        try {
            eliteAdapter.rebuildQuestion(questionId);
        } catch (TException e) {
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        return ResponseJSON.getSucJSON().toString();
    }

    //重建一个回答索引
    @ResponseBody
    @RequestMapping(value = "answer", produces = "application/json;charset=utf-8")
    public String rebuildAnswer(long answerId){
        try {
            eliteAdapter.rebuildAnswer(answerId);
        } catch (TException e) {
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        return ResponseJSON.getSucJSON().toString();
    }

    //重建一个用户的索引
    @ResponseBody
    @RequestMapping(value = "user", produces = "application/json;charset=utf-8")
    public String rebuildUser(long bpId){
        try {
            eliteAdapter.rebuildUser(bpId);
        } catch (TException e) {
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        return ResponseJSON.getSucJSON().toString();
    }

    //重建该用户的所有问题的索引
    @ResponseBody
    @RequestMapping(value = "user/questions", produces = "application/json;charset=utf-8")
    public String rebuildAllQuestionsForUser(long bpId){
        try {
            eliteAdapter.rebuildQuestionsForUser(bpId);
        } catch (TException e) {
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        return ResponseJSON.getSucJSON().toString();
    }

    //重建该用户的所有回答的索引
    @ResponseBody
    @RequestMapping(value = "user/answers", produces = "application/json;charset=utf-8")
    public String rebuildAllAnswersForUser(long bpId){
        try {
            eliteAdapter.rebuildAnswersForUser(bpId);
        } catch (TException e) {
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        return ResponseJSON.getSucJSON().toString();
    }

    //重建该问题的所有回答的索引
    @ResponseBody
    @RequestMapping(value = "question/answers", produces = "application/json;charset=utf-8")
    public String rebuildAllAnswersForQuestion(long questionId){
        try {
            eliteAdapter.rebuildAnswersForQuestion(questionId);
        } catch (TException e) {
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        return ResponseJSON.getSucJSON().toString();
    }
    
    //重建一个专栏等特色的所有问题的索引
    @ResponseBody
    @RequestMapping(value = "special/questions", produces = "application/json;charset=utf-8")
    public String rebuildAllQuestionsForSpecial(Long specialId, Integer specialType){
    	try{
    		eliteAdapter.rebuildQuestionsForSpecial(specialId, specialType);
    	} catch (Exception e){
    		log.error("", e);
    		return ResponseJSON.getErrorInternalJSON().toString();
    	}
    	return ResponseJSON.getSucJSON().toString();
    }
}
