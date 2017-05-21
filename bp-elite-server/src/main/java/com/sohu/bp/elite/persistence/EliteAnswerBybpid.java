package com.sohu.bp.elite.persistence;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "elite_answer_bybpid")
public class EliteAnswerBybpid extends AbstractEntity {

	private static final long serialVersionUID = 4211429204007588242L;
	private Long id;
	private Long bpId;
	private Long answerId;
	private Integer status;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "bp_id", nullable = false)
	public Long getBpId() {
		return this.bpId;
	}

	public void setBpId(Long bpId) {
		this.bpId = bpId;
	}

	@Column(name = "answer_id", nullable = false)
	public Long getAnswerId() {
		return this.answerId;
	}

	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}

	@Column(name = "status", nullable = false)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Transient
	public Serializable getInternalId() {
		return id;
	}
}