package com.sohu.bp.elite.api.api.bean;

import java.io.Serializable;

/**
 * Created by nicholastang on 2017/3/13.
 */
public class EcologyAnswer extends AbstractEntity{
    private static final long serialVersionUID = 5040485593775218366L;
    private Long id = 13804L;
    private String content = "这是一个回答";
    private Integer favoriteNum = 0;
    private Integer likeNum = 0;
    private String link = "http://test.bar.focus.cn/a/angmj";
    private EcologyUser author = new EcologyUser();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getFavoriteNum() {
        return favoriteNum;
    }

    public void setFavoriteNum(Integer favoriteNum) {
        this.favoriteNum = favoriteNum;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public EcologyUser getAuthor() {
        return author;
    }

    public void setAuthor(EcologyUser author) {
        this.author = author;
    }

    @Override
    public Serializable getInternalId() {
        return this.getId();
    }
}
