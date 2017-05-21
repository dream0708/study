package com.sohu.bp.elite.util;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.persistence.EliteAnswer;
import com.sohu.bp.elite.persistence.EliteQuestion;

public class ContentUtil {
    private static final Logger log = LoggerFactory.getLogger(ContentUtil.class);
    
    //判断是否能从广场进入优选广场
    //问题：30字
    //回答：200字/3图
    private static final int MIN_ANSWER_CONTENT_LENGTH = 200;
    private static final int MIN_IMG_NUM = 3;
    private static final int MIN_QUESTION_TITLE_AND_CONTENT_LENGTH = 30;
    
    public static boolean isContentValid(EliteAnswer answer) {
        boolean result = false;
        if (null == answer || StringUtils.isBlank(answer.getContent())) return result;
        String content = answer.getContent();
        Document document = Jsoup.parse(content);
        Elements imgEles = document.select("img");
        Iterator<Element> iter = imgEles.iterator();
        int imgNum = 0;
        while (iter.hasNext()) {
            Element ele = iter.next();
            imgNum++;
            ele.remove();
        }
        String plainText = document.body().html();
        int length = plainText.length();
        if (length >= MIN_ANSWER_CONTENT_LENGTH || imgNum >= MIN_IMG_NUM) result = true;
        log.info("selected square content examine, answer id = {}, result = {}", new Object[]{answer.getId(), result});
        return result;
    }
    
    public static boolean isContentValid(EliteQuestion question) {
        boolean result = false;
        if (null == question || StringUtils.isBlank(question.getTitle())) return result;
        String title = question.getTitle();
        String detail = question.getDetail();
        String content = title + Jsoup.parse(detail).text();
        int length = content.length();
        if (length >= MIN_QUESTION_TITLE_AND_CONTENT_LENGTH) result = true;
        log.info("selected square content examine, question id = {}, result = {}", new Object[]{question.getId(), result});
        return result;
    }
    
    public static String getPlainText(String html) {
        if (StringUtils.isBlank(html)) {
            return "";
        }
        String plainText = "";
        try {
            Document doc = Jsoup.parse(html);
            plainText = doc.text();
            plainText = plainText.replaceAll("\"img\":[^,]*", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plainText;
    }
}
