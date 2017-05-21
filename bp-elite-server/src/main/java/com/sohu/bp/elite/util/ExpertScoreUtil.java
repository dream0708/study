package com.sohu.bp.elite.util;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

/**
 * 专家团计分util类
 * @author zhijungou
 * 2017年3月9日
 */
public class ExpertScoreUtil {
    private static final Logger log = LoggerFactory.getLogger(ExpertScoreUtil.class);
    
    public static final int getScore(Long pushTime, Long answerTime) {
        if (null == pushTime || null == answerTime ||answerTime < pushTime) {
            log.info("answerTime = {} and pushTIme = {} is not correct, please check", new Object[]{answerTime, pushTime});
            return 0;
        }
        return 2;
    }
}
