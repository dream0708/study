package com.sohu.bp.elite.listener;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.service.web.KafkaService;
import com.sohu.bp.elite.task.EliteExpertTeamAsyncTask;
import com.sohu.bp.elite.task.EliteExpertTeamAsyncTaskPool;
import com.sohu.bp.elite.util.SpringUtil;

import net.sf.json.JSONObject;

public class EliteExpertTeamListener extends KafkaListener{
    private static final Logger log = LoggerFactory.getLogger(EliteExpertTeamListener.class);
    
    public void init() {
        this.setTaskHandler(new Function<String, Boolean>() {

            @Override
            public Boolean apply(String msg) {
                JSONObject question = JSONObject.fromObject(msg);
                log.info("expert team listener get msg : " + msg);
                try {
                    EliteExpertTeamAsyncTaskPool.addTask(new EliteExpertTeamAsyncTask(question.getLong("id"), question.getLong("bpId"), null));
                    return true;
                } catch (Exception e) {
                    log.error("", e);
                    return false;
                }
            }
        });
        this.myRun();
    } 
}
