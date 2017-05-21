package com.sohu.bp.elite.task;

import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.service.web.ExpertTeamService;
import com.sohu.bp.elite.util.SpringUtil;

import org.slf4j.Logger;

/**
 * 用于更新expert team持久化的线程类
 * @author zhijungou
 * 2017年2月27日
 */
public class EliteExpertTeamUpdateAsyncTask extends EliteAsyncTask {
    private static final Logger log = LoggerFactory.getLogger(EliteExpertTeamUpdateAsyncTask.class);
    
    private Long bpId;
    private Long questionId;
    private ExpertTeamService expertTeamService;
    
    public EliteExpertTeamUpdateAsyncTask(Long bpId, Long questionId) {
        expertTeamService = SpringUtil.getBean("expertTeamService", ExpertTeamService.class);
        this.bpId = bpId;
        this.questionId = questionId;
    }
    
    @Override
    public void run() {
        expertTeamService.addNewAnswered(bpId, questionId);
        log.info("update expert bpId = {}, answered question = {}", bpId, questionId);
    }
}
