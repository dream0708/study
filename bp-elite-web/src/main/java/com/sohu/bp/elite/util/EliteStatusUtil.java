package com.sohu.bp.elite.util;

import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.decoration.model.TagStatus;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.enums.EliteUserStatus;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteUser;

/**
 * @author zhangzhihao
 *         2016/8/30
 */
public class EliteStatusUtil {

    public static boolean isValidStatus(TEliteQuestion question) {
        return question != null && (question.getStatus() == EliteQuestionStatus.PUBLISHED.getValue() || question.getStatus() == EliteQuestionStatus.PASSED.getValue());
    }

    public static boolean isDelStatus(TEliteQuestion question) {
        return (null == question || question.getStatus() == EliteQuestionStatus.DEL.getValue() || question.getStatus() == EliteQuestionStatus.SYSDEL.getValue());
    }

    public static boolean isInvalidStatus(TEliteQuestion question) {
        return !isValidStatus(question);
    }

    public static boolean isValidStatus(TEliteAnswer answer) {
        return answer != null && (answer.getStatus() == EliteAnswerStatus.PUBLISHED.getValue() || answer.getStatus() == EliteAnswerStatus.PASSED.getValue() || answer.getStatus() == EliteAnswerStatus.CHOOSE.getValue());
    }

    public static boolean isInvalidStatus(TEliteAnswer answer) {
        return !isValidStatus(answer);
    }

    public static boolean isValidStatus(TEliteUser user) {
        return user != null && (user.getStatus() == EliteUserStatus.VALID.getValue());
    }

    public static boolean isInvalidStatus(TEliteUser user) {
        return !isValidStatus(user);
    }

    public static boolean isValidStatus(Tag tag){
        return tag != null && TagStatus.WORK == tag.getStatus();
    }

    public static boolean isInvalidStatus(Tag tag){
        return !isValidStatus(tag);
    }
    public static String merge(int... status){
        if(status == null || status.length == 0){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for(int s : status){
            sb.append(s + Constants.TAG_IDS_SEPARATOR);
        }
        return sb.toString();
    }
}
