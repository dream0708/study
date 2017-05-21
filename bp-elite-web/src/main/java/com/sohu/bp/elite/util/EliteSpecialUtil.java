package com.sohu.bp.elite.util;

import java.util.Objects;

import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.model.TEliteQuestion;

/**
 * 用于判断问题或回答类型
 * @author zhijungou
 * 2017年4月11日
 */
public class EliteSpecialUtil {
    public static boolean isChooseType(TEliteQuestion question) {
        Integer type = question.getSpecialType();
        return Objects.equals(type, BpType.Elite_Vote.getValue()) || Objects.equals(type, BpType.Elite_VS.getValue());
    }
    
    public static boolean isVoteType(TEliteQuestion question) {
    	Integer type = question.getSpecialType();
    	return Objects.equals(type, BpType.Elite_Vote.getValue());
    }
    
    public static boolean isVSType(TEliteQuestion question) {
    	Integer type = question.getSpecialType();
    	return Objects.equals(type, BpType.Elite_VS.getValue());
    }
}
