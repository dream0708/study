package com.sohu.bp.elite.persistence;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "elite_question_bybpid")
public class EliteQuestionBybpid extends AbstractEntity {

	private static final long serialVersionUID = -1314991469425074631L;
	private Long id;
	private Long bpId;
	private Long questionId;
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

	@Column(name = "question_id", nullable = false)
	public Long getQuestionId() {
		return this.questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
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