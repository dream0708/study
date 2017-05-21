package com.sohu.bp.elite.util;

import java.util.Objects;

import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.persistence.EliteAnswer;
import com.sohu.bp.elite.persistence.EliteQuestion;

public class EliteFeatureUtil {
    //判断是否是功能性回答，比如投票或晒图
    public static boolean isFeatureQuestion(EliteQuestion question) {
        Integer type = question.getSpecialType();
        return Objects.equals(type, BpType.Elite_Vote.getValue()) || Objects.equals(type, BpType.Elite_VS.getValue());
    }
    public static boolean isFeatureAnswer(EliteAnswer answer) {
        Integer type = answer.getSpecialType();
        return Objects.equals(type, BpType.Elite_Vote.getValue()) || Objects.equals(type, BpType.Elite_VS.getValue());
    }
    
    
    //判断是否为晒图贴
    public static boolean isVSQuestion(EliteQuestion question) {
        return Objects.equals(question.getSpecialType(), BpType.Elite_VS.getValue());
    }
    public static boolean isVSAnswer(EliteAnswer answer) {
        return Objects.equals(answer.getSpecialType(), BpType.Elite_VS.getValue());
    }
    
    //判读是否为投票贴
    public static boolean isVoteQuestion(EliteQuestion question) {
        return Objects.equals(question.getSpecialType(), BpType.Elite_Vote.getValue());
    }
    public static boolean isVoteAnswer(EliteAnswer answer) {
        return Objects.equals(answer.getSpecialType(), BpType.Elite_Vote.getValue());
    }
}
