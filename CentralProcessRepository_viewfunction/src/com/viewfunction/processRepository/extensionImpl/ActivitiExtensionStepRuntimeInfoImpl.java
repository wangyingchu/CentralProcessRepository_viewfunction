package com.viewfunction.processRepository.extensionImpl;

import java.util.Date;

import com.viewfunction.processRepository.extension.ExtensionStepRuntimeInfo;

public class ActivitiExtensionStepRuntimeInfoImpl implements ExtensionStepRuntimeInfo{
	
	private String extensionStepClassName;
	private Date dueDate;
	private String failMessage;
	private String processObjectId;
	private String processDefinitionId;
	private int retryTimes;
	private String stepDefinitionKey;
	private String stepId;
	
	@Override
	public String getExtensionStepClassName() {
		return this.extensionStepClassName;
	}
	
	public void setExtensionStepClassName(String extensionStepClassName) {
		this.extensionStepClassName=extensionStepClassName;
	}

	@Override
	public Date getDueDate() {
		return this.dueDate;
	}
	
	public void setDueDate(Date dueDate) {
		this.dueDate=dueDate;
	}

	@Override
	public String getFailMessage() {
		return this.failMessage;
	}
	
	public void setFailMessage(String failMessage) {
		this.failMessage=failMessage;
	}

	@Override
	public String getProcessObjectId() {
		return this.processObjectId;
	}
	
	public void setProcessObjectId(String processObjectId) {
		this.processObjectId=processObjectId;
	}

	@Override
	public String getProcessDefinitionId() {
		return this.processDefinitionId;
	}
	
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId=processDefinitionId;
	}

	@Override
	public int getRetryTimes() {
		return this.retryTimes;
	}
	
	public void setRetryTimes(int retryTimes) {
		this.retryTimes=retryTimes;
	}

	@Override
	public String getStepDefinitionKey() {
		return this.stepDefinitionKey;
	}
	
	public void setStepDefinitionKey(String stepDefinitionKey) {
		this.stepDefinitionKey=stepDefinitionKey;
	}

	@Override
	public String getStepId() {
		return this.stepId;
	}

	public void setStepId(String stepId) {
		this.stepId=stepId;
	}
}
