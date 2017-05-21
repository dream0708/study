package com.sohu.bp.elite.task;

/**
 * create time: 2016年6月18日 上午11:14:46
 * @auther dexingyang
 */
public class TaskEvent {

	private String data;  //json
	
	public TaskEvent(){
	}

	public TaskEvent(String data, String type) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[data="+data+"]");
		return sb.toString();
	}
	
}
