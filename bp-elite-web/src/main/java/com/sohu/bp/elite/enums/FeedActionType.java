package com.sohu.bp.elite.enums;

import org.apache.poi.hssf.record.formula.functions.Choose;

/**
 * @author zhangzhihao
 *         2016/9/13
 */
public enum FeedActionType implements IEnum{
//    FOLLOW(1, "关注"),
//    LIKE(2, "点赞"),
//    FAVORITE(3, "收藏"),
//    COMMENT(4, "评论"),
//    SHARE(5, "分享"),
//    ASK(6, "提问"),
//    ANSWER(7, "回答"),
//    ADD(8, "新增"),
//    UNFOLLOW(9, "取消关注"),
//    UNLIKE(10, "取消点赞"),
//    UNFAVORITE(11, "取消收藏"),
//    UPDATE_QUESTION(12, "更新问题"),
//    UPDATE_ANSWER(13, "更新回答"),
//    DELETE_QUESTION(14, "删除问题"),
//    DELETE_ANSWER(15, "删除回答"),
//    PUBLISH_VOTE(16, "发布了投票"),
//    PUBLISH_VS(17, "发布了晒图"),
//    VOTE(18, "投票了"),
//    VOTE_AND_ANSWER(19, "投票并回答了"),
//    VS(19, "站队了"),
//    VS_AND_COMMENT(20, "站队并评论了"),
//    VS_COMMENT(21, "站队的评论");
    SIMPLE_FEED(1, "基本的Feed流,"),
    PUBLISH_QUESTION(2, "发布问题"),
    PUBLISH_ANSWER(3, "发布回答"),
    PUBLISH_ANSWER_WITH_TAGS(4, "发布回答，入标签流"),
    UPDATE_QUESTION(5, "更新问题"),
    DELETE_QUESTION(6, "删除问题"),
    UPDATE_ANSWER(7, "更新回答"),
    DELETE_ANSWER(8, "删除回答");

    private int value;
    private String desc;

    FeedActionType(int value, String desc){
        this.value = value;
        this.desc = desc;
    }

    @Override
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
