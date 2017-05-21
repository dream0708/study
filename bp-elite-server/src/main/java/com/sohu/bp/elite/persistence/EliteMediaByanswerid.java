package com.sohu.bp.elite.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "elite_media_byanswerid")
public class EliteMediaByanswerid extends AbstractEntity {

	private static final long serialVersionUID = 338834400808427015L;
	private Long id;
	private Long answerId;
	private Long mediaId;
	private Integer type;
	private Integer status;
	private Date updateTime;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "answer_id", nullable = false)
	public Long getAnswerId() {
		return this.answerId;
	}

	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}
	
	@Column(name = "media_id", nullable = false)
	public Long getMediaId() {
		return mediaId;
	}

	public void setMediaId(Long mediaId) {
		this.mediaId = mediaId;
	}
	
	@Column(name = "type", nullable = false)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "status", nullable = false)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Column(name = "update_time", nullable = false)
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Transient
	public Serializable getInternalId() {
		return id;
	}
}