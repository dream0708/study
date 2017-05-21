package com.sohu.bp.elite.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "elite_media")
public class EliteMedia extends AbstractEntity {

	private static final long serialVersionUID = -7363265163817016479L;
	private Long id;
	private Integer type;
	private Long questionId;
	private Long answerId;
	private Long mediaGivenId;
	private String url;
	private Date updateTime;
	private Date uploadTime;
	private Long uploadHost;
	private Integer uploadPort;
	private Integer status;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "question_id")
	public Long getQuestionId() {
		return this.questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	@Column(name = "answer_id")
	public Long getAnswerId() {
		return this.answerId;
	}

	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}

	@Column(name = "update_time")
	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "upload_time")
	public Date getUploadTime() {
		return this.uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	@Column(name = "upload_host")
	public Long getUploadHost() {
		return this.uploadHost;
	}

	public void setUploadHost(Long uploadHost) {
		this.uploadHost = uploadHost;
	}

	@Column(name = "upload_port")
	public Integer getUploadPort() {
		return this.uploadPort;
	}

	public void setUploadPort(Integer uploadPort) {
		this.uploadPort = uploadPort;
	}

	@Column(name = "status", nullable = false)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "media_given_id")
	public Long getMediaGivenId() {
		return mediaGivenId;
	}

	public void setMediaGivenId(Long mediaGivenId) {
		this.mediaGivenId = mediaGivenId;
	}

	@Transient
	public Serializable getInternalId() {
		return id;
	}
}