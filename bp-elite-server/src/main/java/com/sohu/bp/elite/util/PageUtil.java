package com.sohu.bp.elite.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PageUtil {
    private static final Logger log = LoggerFactory.getLogger(PageUtil.class);

    private static final String KEY_START_PAGENO = "startPageNo";
    private static final String KEY_END_PAGENO = "endPageNo";
    private static final String KEY_PRE_PAGENO = "prePageNo";
    private static final String KEY_NEXT_PAGENO = "nextPageNo";
    private static final String KEY_HIT_NUM = "hitNum";
    private static final String KEY_PAGENO = "pageNo";
    private static final String KEY_PAGE_COUNT = "pageCount";

    public static Map<String, String> getPageInfo(int pageNo, int pageSize, int hitNum) {
        int startPageNo = getStartPageNo();
        int endPageNo = getEndPageNo(hitNum, pageSize);
        int prePageNo = getPreviousPageNo(pageNo, endPageNo, hitNum);
        int nextPageNo = getNextPageNo(pageNo, endPageNo, hitNum);

        Map<String, String> pageInfoMap = new HashMap<String, String>();
        pageInfoMap.put(KEY_START_PAGENO, startPageNo + "");
        pageInfoMap.put(KEY_END_PAGENO, endPageNo + "");
        pageInfoMap.put(KEY_PRE_PAGENO, prePageNo + "");
        pageInfoMap.put(KEY_NEXT_PAGENO, nextPageNo + "");
        pageInfoMap.put(KEY_HIT_NUM, hitNum + "");
        pageInfoMap.put(KEY_PAGENO, pageNo + "");
        pageInfoMap.put(KEY_PAGE_COUNT, endPageNo + "");
        log.info(pageInfoMap.toString());
        return pageInfoMap;
    }

    private static int getStartPageNo() {
        return 1;
    }

    private static int getEndPageNo(int hitNum, int pageSize) {
        if (hitNum == 0) {
            return 1;
        }
        int pageNoMax = hitNum % pageSize == 0 ? hitNum / pageSize : hitNum / pageSize + 1;
        return pageNoMax;
    }

    private static int getPreviousPageNo(int pageNo, int endPageNo, int hitNum) {
        if (hitNum == 0) {
            return -1;
        }
        int prePageNo = pageNo - 1;
        if (prePageNo < 1 || prePageNo > endPageNo) {
            return -1;
        }
        return prePageNo;
    }

    private static int getNextPageNo(int pageNo, int endPageNo, int hitNum) {
        if (hitNum == 0) {
            return -1;
        }
        int nextPageNo = pageNo + 1;
        if (nextPageNo < 1 || nextPageNo > endPageNo) {
            return -1;
        }
        return nextPageNo;
    }
}