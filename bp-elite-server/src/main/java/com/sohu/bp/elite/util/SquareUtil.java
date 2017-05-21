package com.sohu.bp.elite.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.FeedType;

/**
 * 广场的工具类
 * @author zhijungou
 * 2017年4月27日
 */
public class SquareUtil {
    private static final Logger log = LoggerFactory.getLogger(SquareUtil.class);
    
    public static Long getComplexId(Long feedId, FeedType feedType){
        if(null == feedId || feedId <= 0) return null;
        return (feedId * Constants.COMPLEX_ID_SHIFT + feedType.getValue()) * Constants.COMPLEX_ID_SHIFT;
    }
    
    public static FeedType getFeedType(Long complexId){
        if(null == complexId || complexId < Constants.COMPLEX_ID_SHIFT){
            return FeedType.QUESTION;
        }
        int value = (int) ((complexId/Constants.COMPLEX_ID_SHIFT) % Constants.COMPLEX_ID_SHIFT);
        return FeedType.getFeedTypeByValue(value);
    }
    
    public static Long getFeedId(Long complexId){
        if(null == complexId || complexId < Constants.COMPLEX_ID_SHIFT){
            return null;
        }
        Long objectId = (complexId / Constants.COMPLEX_ID_SHIFT) / Constants.COMPLEX_ID_SHIFT;
        return objectId;
    }
}
