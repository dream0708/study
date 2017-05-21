package com.sohu.bp.elite.action.bean.person;

public class UserRestrictBean {
	private Integer answerNum = 0;
	private Integer questionNum = 0;
	
	public UserRestrictBean(){};
	
	public UserRestrictBean(int answerNum, int questionNum){
		this.answerNum = answerNum;
		this.questionNum = questionNum;
	}
	
	public Integer getAnswerNum() {
		return answerNum;
	}
	public void setAnswerNum(Integer answerNum) {
		this.answerNum = answerNum;
	}
	public Integer getQuestionNum() {
		return questionNum;
	}
	public void setQuestionNum(Integer questionNum) {
		this.questionNum = questionNum;
	}
	
}
