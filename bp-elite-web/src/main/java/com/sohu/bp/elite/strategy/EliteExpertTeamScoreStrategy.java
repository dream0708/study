package com.sohu.bp.elite.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.model.TEliteExpertTeam;
/**
 * 专家团选择算法的具体策略类。
 * 基于分数与匹配的标签数量进行计算。匹配标签的个数对专家的分数进行加权。
 * 选择EXPERT_NUM名专家：选择策略为EXPERT_HIGHEST_SCORE_NUM名分数最高与EXPERT_LEAST_TOTAL_NUM名推的最少的专家.
 * 分数的计算: (基础分数SCORE_BASE + 2 * ANSWERD_NUM - TOTAL_NUM) * 匹配标签个数的加权
 * @author zhijungou
 * 2017年2月23日
 */
public class EliteExpertTeamScoreStrategy implements EliteExpertTeamStrategy{
    private static final int EXPERT_NUM = 100;
    private static final int EXPERT_HIGHEST_SCORE_NUM = 50;
    private static final int EXPERT_LEAST_TOTAL_NUM = 30;
    private static final int EXPERT_RANDOM_NUM = EXPERT_NUM - EXPERT_HIGHEST_SCORE_NUM - EXPERT_LEAST_TOTAL_NUM;
    private static final float TAG_RATIO = 0.1F;
    private static final int SCORE_BASE = 100;
    private static final Comparator<TEliteExpertTeam> comparatorByScore = new Comparator<TEliteExpertTeam>() {

        @Override
        public int compare(TEliteExpertTeam o1, TEliteExpertTeam o2) {
            return o2.score - o1.score;
        }
    };
    
    public static final Comparator<TEliteExpertTeam> comparatorByMessageNum = new Comparator<TEliteExpertTeam>() {

        @Override
        public int compare(TEliteExpertTeam o1, TEliteExpertTeam o2) {
            return o1.getPushNum() - o2.getPushNum();
        }
        
    };
    @Override
    public List<Long> getSelectedExpertTeams(Set<Long> ids, Map<Long, String> tagMap,
            Map<Long, TEliteExpertTeam> expertMap) {
        if (null == ids || ids.size() <= EXPERT_NUM) return new ArrayList<Long>(ids);
        List<Long> idList = new ArrayList<Long>();
        List<TEliteExpertTeam> scoreExperts = new ArrayList<TEliteExpertTeam>();
        List<TEliteExpertTeam> numExperts = new ArrayList<TEliteExpertTeam>();
        Set<Map.Entry<Long, TEliteExpertTeam>> entrys = expertMap.entrySet();
        for (Map.Entry<Long, TEliteExpertTeam> entry : entrys) {
            TEliteExpertTeam originExpert = entry.getValue();
            TEliteExpertTeam expert = new TEliteExpertTeam();
            int time = tagMap.get(entry.getKey()).split(Constants.TAG_IDS_SEPARATOR).length;
            int score = (int) ((SCORE_BASE + originExpert.getAnsweredNum() * 2 - originExpert.getPushNum()) * (TAG_RATIO * (time - 1) + 1.0));
            expert.setPushNum(originExpert.getPushNum()).setBpId(originExpert.getBpId()).setScore(score);
            scoreExperts.add(expert);
        }
        scoreExperts.sort(comparatorByScore);
        // 获得分数最高的人
        int i = 0;
        for (TEliteExpertTeam expert : scoreExperts) {
            if (i < EXPERT_HIGHEST_SCORE_NUM) {
                idList.add(expert.getBpId());
            } else {
                numExperts.add(expert);
            }
            i++;
        }
        numExperts.sort(comparatorByMessageNum);
        // 获取推送最少的人
        Iterator<TEliteExpertTeam> iterator = numExperts.iterator();
        i = 0;
        while (iterator.hasNext() && i < EXPERT_LEAST_TOTAL_NUM) {
           idList.add(iterator.next().getBpId());
           iterator.remove();
           i++;
        }
        i = 0;
        Random rand = new Random();
        while (numExperts.size() > 0 && i < EXPERT_RANDOM_NUM) {
            int index = rand.nextInt(numExperts.size());
            idList.add(numExperts.get(index).getBpId());
            numExperts.remove(index);
            i++;
        }
        return idList;
    }
    
}

