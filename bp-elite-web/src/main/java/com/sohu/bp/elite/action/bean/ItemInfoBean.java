package com.sohu.bp.elite.action.bean;

/**
 * 用于在feed流或广场点击更多信息时展现
 * @author zhijungou
 * 2017年4月20日
 */
public class ItemInfoBean {
    private Integer itemType;
    private String itemId;
    private boolean hasFollowed;
    private boolean hasFavorited;
    private boolean hasLiked;
    private boolean hasTread;
    private boolean hasAnswered;
    private boolean hasChoosed;
    private boolean isUser;
    
    public Integer getItemType() {
        return itemType;
    }
    public ItemInfoBean setItemType(Integer itemType) {
        this.itemType = itemType;
        return this;
    }
    public String getItemId() {
        return itemId;
    }
    public ItemInfoBean setItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }
    public boolean getHasFollowed() {
        return hasFollowed;
    }
    public ItemInfoBean setHasFollowed(boolean hasFollowed) {
        this.hasFollowed = hasFollowed;
        return this;
    }
    public boolean getHasFavorited() {
        return hasFavorited;
    }
    public ItemInfoBean setHasFavorited(boolean hasFavorited) {
        this.hasFavorited = hasFavorited;
        return this;
    }
    public boolean getHasAnswered() {
        return hasAnswered;
    }
    public ItemInfoBean setHasAnswered(boolean hasAnswered) {
        this.hasAnswered = hasAnswered;
        return this;
    }
    public boolean getHasChoosed() {
        return hasChoosed;
    }
    public ItemInfoBean setHasChoosed(boolean hasChoosed) {
        this.hasChoosed = hasChoosed;
        return this;
    }
    public boolean isHasLiked() {
        return hasLiked;
    }
    public ItemInfoBean setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
        return this;
    }
    public boolean isHasTread() {
        return hasTread;
    }
    public ItemInfoBean setHasTread(boolean hasTread) {
        this.hasTread = hasTread;
        return this;
    }
    public boolean isUser() {
        return isUser;
    }
    public ItemInfoBean setUser(boolean isUser) {
        this.isUser = isUser;
        return this;
    }
}
