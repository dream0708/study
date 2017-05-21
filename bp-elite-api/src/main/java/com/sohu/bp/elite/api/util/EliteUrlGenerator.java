package com.sohu.bp.elite.api.util;

import com.sohu.bp.elite.Configuration;

/**
 * Created by nicholastang on 2017/3/13.
 */
public class EliteUrlGenerator {
    private static volatile String DOMAIN = "bar.focus.cn";
    private static final Configuration configuration;
    static {
        configuration = (Configuration) SpringUtil.getBean("configuration");
        DOMAIN = configuration.get("askDomain", DOMAIN);
    }
    public static String getQuestionUrl(long id) {
        return "https://" + DOMAIN + "/q/" + IDUtil.encodeId(id);
    }

    public static String getAnswerUrl(long id) {
        return "https://" + DOMAIN + "/a/" + IDUtil.encodeId(id);
    }

    public static String getUserUrl(long id) {
        return "https://" + DOMAIN + "/pu/" + IDUtil.encodeId(id);
    }

    public static String getUserAskUrl(long id) {
        return "https://" + DOMAIN + "/q/go?invitedUserId=" + IDUtil.encodeId(id);
    }
}
