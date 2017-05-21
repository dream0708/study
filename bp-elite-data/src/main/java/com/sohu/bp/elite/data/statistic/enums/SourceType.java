package com.sohu.bp.elite.data.statistic.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nicholastang on 2016/12/7.
 */
public enum SourceType {
    SM_SMALL_PIC(1, "狐首", "www_jiaju", "狐首", "0"),
    //SM_JIAJU_TEXT(2, "狐首家居文字区", "www_jiaju", "狐首", "1"),
    SM_SHEQU_TEXT(3, "狐首社区", "www_shequ", "狐首", "1"),
    //NM_SMALL_PIC(4, "闻首小图", "news", "闻首", "0"),
    //NM_HEADLINE(5, "闻首头条", "news", "闻首", "1"),
    NM_TEXT(6, "闻首", "news", "闻首", "2"),
    //PW_SMALL_PIC(7, "微门户小图", "wei", "微门户", "0"),
    PW_TEXT(8, "微门户", "wei", "微门户", "1"),
    MSM_JIAJU(9, "手搜", "msohu", "手搜", "1"),
    WEIXIN(10, "微信公众号", "weixin", "微信公众号", "1"),
    DAJIATAN(11, "狐首大家谈", "www_dajiatan", "狐首", "0");

    SourceType(Integer code, String desc, String source, String sourceDesc, String pos){
        this.code = code;
        this.desc = desc;
        this.source = source;
        this.sourceDesc = sourceDesc;
        this.pos = pos;
    }

    public static Map<Integer, String> descMap = new HashMap<Integer, String>(){
        {
            put(1, "狐首小图");
            //put(2, "狐首家居文字区");
            put(3, "狐首社区文字区");
            //put(4, "闻首小图");
            //put(5, "闻首头条");
            put(6, "闻首文字区");
            //put(7, "微门户小图");
            put(8, "微门户文字区");
            put(9, "手搜");
            put(10, "微信公众号");
            put(11, "狐首大家谈");
        }
    };

    public static Map<String, SourceType> sourceTypeMap = new HashMap<String, SourceType>(){
        {
            put("www_jiaju", SM_SMALL_PIC);
            put("www_shequ", SM_SHEQU_TEXT);
            put("news", NM_TEXT);
            put("wei", PW_TEXT);
            put("msohu", MSM_JIAJU);
            put("weixin", WEIXIN);
            put("www_dajiatan", DAJIATAN);
        }
    };

    private Integer code;
    private String desc;
    private String source;
    private String sourceDesc;
    private String pos;
    private String unionDesc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getSourceDesc() {
        return sourceDesc;
    }

    public void setSourceDesc(String sourceDesc) {
        this.sourceDesc = sourceDesc;
    }

    public String getUnionDesc() {
        return this.desc + "_" + this.getPos();
    }

    public void setUnionDesc(String unionDesc) {
        this.unionDesc = unionDesc;
    }
}
