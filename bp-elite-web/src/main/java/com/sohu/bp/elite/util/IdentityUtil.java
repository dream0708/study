package com.sohu.bp.elite.util;

import com.sohu.bp.elite.enums.EliteUserIdentity;

/**
 * 用于用户identity的转换
 * @author zhijungou
 * 2017年2月28日
 */
public class IdentityUtil {
    public static String getIdentityString(Integer identity) {
        if (null == identity) return null;
        if (EliteUserIdentity.EXPERT.getValue() != identity) identity = 0;
        return String.format("%02d", identity);
    }
}
