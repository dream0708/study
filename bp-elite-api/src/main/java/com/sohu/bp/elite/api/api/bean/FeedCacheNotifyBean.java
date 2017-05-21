package com.sohu.bp.elite.api.api.bean;

/**
 * Created by nicholastang on 2017/5/10.
 */
public class FeedCacheNotifyBean {
    public FeedCacheNotifyBean(long accountId) {
        this.accountId = accountId;
    }
    private long accountId;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
}
