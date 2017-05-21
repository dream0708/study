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


@Entity
@Table(name = "elite_topic")
public class EliteTopic extends AbstractEntity{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1596921052835746846L;
	
	private Long id;
	private Long userId;
	private	String title;
	private String brief;
	private String cover;
	private Long questionId;
	private Date startTime;
	private Date endTime;
	private Integer type;
	private Date createTime;
	private Long createHost;
	private Date updateTime;
	private Long updateHost;
	private Integer status;
	

	
	@Transient
	public Serializable getInternalId()
	{
		return id;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "user_id", nullable = false)
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	@Column(name = "title", nullable = false)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name = "brief", nullable = false)
	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}
	@Column(name = "cover", nullable = false)
	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}
	@Column(name = "question_id", nullable = false)
	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	@Column(name = "start_time", nullable = false)
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@Column(name = "end_time", nullable = false)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	@Column(name = "type", nullable = false)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	@Column(name = "create_time", nullable = false)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name = "create_host", nullable = false)
	public Long getCreateHost() {
		return createHost;
	}

	public void setCreateHost(Long createHost) {
		this.createHost = createHost;
	}
	@Column(name = "update_time", nullable = false)
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	@Column(name = "update_host", nullable = false)
	public Long getUpdateHost() {
		return updateHost;
	}

	public void setUpdateHost(Long updateHost) {
		this.updateHost = updateHost;
	}
	@Column(name = "status", nullable = false)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
