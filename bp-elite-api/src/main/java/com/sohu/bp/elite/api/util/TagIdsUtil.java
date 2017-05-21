package com.sohu.bp.elite.api.util;

import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangzhihao
 *         2016/9/19
 */
public class TagIdsUtil {

    private static Logger log = LoggerFactory.getLogger(TagIdsUtil.class);
    private static EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();

    public static void addTagIds(TEliteAnswer answer){
        if(answer != null){
            TEliteQuestion question = null;
            try{
                question = eliteAdapter.getQuestionById(answer.getQuestionId());
            }catch (Exception e){
                log.error("", e);
            }
            if(question != null) {
                answer.setTagIds(question.getTagIds());
            }
        }
    }

    public static void addTagIds(List<TEliteAnswer> answers){
        if(answers != null && answers.size() > 0){
            for(TEliteAnswer answer : answers){
                addTagIds(answer);
            }
        }
    }
}
