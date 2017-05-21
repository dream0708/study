package com.sohu.bp.elite.api.api;

import com.alibaba.fastjson.JSON;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.util.PropertyFilterUtil;
import com.sohu.bp.elite.api.util.TagIdsUtil;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.util.ResponseJSON;
import net.sf.json.JSONObject;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhangzhihao
 *         2016/7/14
 */
@Controller
@RequestMapping("/innerapi/answer")
public class EliteAnswerApi{

    private Logger log = LoggerFactory.getLogger(EliteQuestionApi.class);

    private EliteThriftServiceAdapter eliteThriftServiceAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();

    @ResponseBody
    @RequestMapping(value = "index-data", produces = "application/json;charset=utf-8")
    public String getById(@RequestParam("id") Long answerId){
        JSONObject json = ResponseJSON.getDefaultResJSON();
        try {
            TEliteAnswer tEliteAnswer = eliteThriftServiceAdapter.getAnswerById(answerId);
            TagIdsUtil.addTagIds(tEliteAnswer);
            if(tEliteAnswer != null) {
                json.put("data", JSON.toJSONString(tEliteAnswer, PropertyFilterUtil.getFilter(TEliteAnswer.class)));
            }else {
                json = ResponseJSON.getErrorParamsJSON();
            }
        } catch (TException e) {
            log.error("", e);
            json = ResponseJSON.getErrorInternalJSON();
        }

        return json.toString();
    }
}
