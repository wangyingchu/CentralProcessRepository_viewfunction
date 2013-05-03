package com.viewfunction.processRepository.processBureau;

import java.util.Date;

public class ProcessComment {	
	private String processObjectId;
	private String commentMessage;
	private Date time;
	public String getProcessObjectId() {
		return processObjectId;
	}
	public void setProcessObjectId(String processObjectId) {
		this.processObjectId = processObjectId;
	}
	public String getCommentMessage() {
		return commentMessage;
	}
	public void setCommentMessage(String commentMessage) {
		this.commentMessage = commentMessage;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}	
}
