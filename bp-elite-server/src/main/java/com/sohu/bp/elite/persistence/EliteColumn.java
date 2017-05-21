package com.sohu.bp.elite.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 专栏
 * @author zhijungou
 * 2016年11月9日
 */
@Entity
@Table(name = "elite_column")
public class EliteColumn extends AbstractEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String cover;
	private String userInfo;
	private String tags;
	private String brief;
	private String content;
	private Date createTime;
	private Date updateTime;
	private Date publishTime;
	private Integer status;
	private Integer type;
	private String description;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public EliteColumn setId(Long id) {
		this.id = id;
		return this;
	}
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public EliteColumn setName(String name) {
		this.name = name;
		return this;
	}
	@Column(name = "cover")
	public String getCover() {
		return cover;
	}

	public EliteColumn setCover(String cover) {
		this.cover = cover;
		return this;
	}
	@Column(name = "user_info")
	public String getUserInfo() {
		return userInfo;
	}

	public EliteColumn setUserInfo(String userInfo) {
		this.userInfo = userInfo;
		return this;
	}
	@Column(name = "tags")
	public String getTags() {
		return tags;
	}

	public EliteColumn setTags(String tags) {
		this.tags = tags;
		return this;
	}
	@Column(name = "content")
	public String getContent() {
		return content;
	}

	public EliteColumn setContent(String content) {
		this.content = content;
		return this;
	}
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public EliteColumn setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}
	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public EliteColumn setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
		return this;
	}
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public EliteColumn setStatus(Integer status) {
		this.status = status;
		return this;
	}
	
	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public EliteColumn setType(Integer type) {
		this.type = type;
		return this;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	@Transient
	public Serializable getInternalId() {
		return id;
	}
	
	@Override
	public String toString(){
		return "id = " + this.id + ", name = " + this.name + ", createTime = " + this.createTime + ", status = " + this.status;
	}

	@Column(name = "brief")
	public String getBrief() {
		return brief;
	}
	
	public EliteColumn setBrief(String brief) {
		this.brief = brief;
		return this;
	}
	
	@Column(name = "publish_time")
	public Date getPublishTime() {
		return publishTime;
	}

	public EliteColumn setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
		return this;
	}
	
	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
}
