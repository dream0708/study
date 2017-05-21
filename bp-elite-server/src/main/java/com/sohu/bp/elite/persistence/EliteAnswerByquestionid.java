package com.sohu.bp.elite.persistence;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "elite_answer_byquestionid")
public class EliteAnswerByquestionid extends AbstractEntity {

	private static final long serialVersionUID = -8653476464314658136L;
	private Long id;
	private Long questionId;
	private Long answerId;
	private Long specialId;
	private Integer specialType;
	private Integer status;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public EliteAnswerByquestionid setId(Long id) {
		this.id = id;
		return this;
	}

	@Column(name = "question_id", nullable = false)
	public Long getQuestionId() {
		return this.questionId;
	}

	public EliteAnswerByquestionid setQuestionId(Long questionId) {
		this.questionId = questionId;
		return this;
	}

	@Column(name = "answer_id", nullable = false)
	public Long getAnswerId() {
		return this.answerId;
	}

	public EliteAnswerByquestionid setAnswerId(Long answerId) {
		this.answerId = answerId;
		return this;
	}

	@Column(name = "status", nullable = false)
	public Integer getStatus() {
		return status;
	}

	public EliteAnswerByquestionid setStatus(Integer status) {
		this.status = status;
		return this;
	}

	@Transient
	public Serializable getInternalId() {
		return id;
	}

    public Long getSpecialId() {
        return specialId;
    }

    public EliteAnswerByquestionid setSpecialId(Long specialId) {
        this.specialId = specialId;
        return this;
    }

    public Integer getSpecialType() {
        return specialType;
    }

    public EliteAnswerByquestionid setSpecialType(Integer specialType) {
        this.specialType = specialType;
        return this;
    }
}