package com.sohu.bp.elite.task;

import com.sohu.bp.elite.enums.EliteIndexType;

/**
 * @author zhangzhihao
 *         2016/7/13
 */
public class IndexParam {
    private Integer indexType = EliteIndexType.Build_Index.getValue();
    private String objectId;
    private String objectType;
    private String extra = "-1";

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Integer getIndexType() {
        return indexType;
    }

    public void setIndexType(Integer indexType) {
        this.indexType = indexType;
    }

    @Override
    public String toString() {
        return "(objectId=" + objectId + ", objectType=" + objectType + ", extra=" + extra;
    }
}
