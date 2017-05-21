package com.sohu.bp.elite.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nicholastang on 2017/2/24.
 */
public class DateUtil {
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat sdfSimple = new SimpleDateFormat("yyyy-MM-dd");

    /**
     *
     * @param timeStr 时间字符串
     * @param sdf 字符串类型对应的sdf 或则 sdfSimple
     * @return
     */
    public static Date parse(String timeStr, SimpleDateFormat sdf) {
        synchronized (sdf) {
            try {
                return sdf.parse(timeStr);
            }catch (Exception e) {
                logger.error("", e);
                return null;
            }

        }

    }

    /**
     *
     * @param timeCond 时间毫秒数
     * @param sdf 要转换成字符串对应的sdf 或者 sdfSimple
     * @return
     */
    public static String format(long timeCond, SimpleDateFormat sdf) {
        synchronized (sdf) {
            try {
                return sdf.format(new Date(timeCond));
            }catch (Exception e) {
                logger.error("", e);
                return null;
            }
        }
    }
}
