package com.sohu.bp.elite.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 投票等选择的具体类
 * @author zhijungou
 * 2017年4月7日
 */
public class EliteOptionItemBean {
    private String description;
    private String img;
    private String imgSmall;
    private Integer count;
    private List<String> descriptionParts;
    
    public String getDescription() {
        return description;
    }
    public EliteOptionItemBean setDescription(String description) {
        this.description = description;
        return this;
    }
    public String getImg() {
        return img;
    }
    public EliteOptionItemBean setImg(String img) {
        this.img = img;
        return this;
    }
    public Integer getCount() {
        return count;
    }
    public EliteOptionItemBean setCount(Integer count) {
        this.count = count;
        return this;
    }
    public String getImgSmall() {
        return imgSmall;
    }
    public EliteOptionItemBean setImgSmall(String imgSmall) {
        this.imgSmall = imgSmall;
        return this;
    }
    
    public List<String> getDescriptionParts() {
        List<String> descriptions = new ArrayList<String>();
        if (StringUtils.isNotBlank(description)) {
            Matcher matcher = Pattern.compile(">([^<>]+?)<").matcher(description);
            while (matcher.find()) {
                descriptions.add(matcher.group(1));
            }
        }
        return descriptions;
    }
    public EliteOptionItemBean setDescriptionParts(List<String> descriptionParts) {
        this.descriptionParts = descriptionParts;
        return this;
    }
}
