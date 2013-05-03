package com.viewfunction.processRepository.processBureauImpl;

import java.util.Date;

import com.viewfunction.processRepository.processBureau.HistoricProcessStep;

public class ActivitiHistoricProcessStepImpl implements HistoricProcessStep{
	private String stepName;
	private String stepDefinitionKey;
	private String stepAssignee;
	private String stepDescription;
	private String stepId;
	private String parentStepId;
	private String owner;
	private String processObjectId;
	private String processDefinitionId;	
	private Date dueDate;
	private Date startTime;
	private Date endTime;
	private Long durationInMillis;
	
	public ActivitiHistoricProcessStepImpl(String stepName,String stepDefinitionKey,String stepId,String processObjectId,String processDefinitionId,String stepAssignee, Date startTime,Date endTime,Long durationInMillis){
		this.stepName=stepName;
		this.stepDefinitionKey=stepDefinitionKey;
		this.stepId=stepId;
		this.processObjectId=processObjectId;
		this.processDefinitionId=processDefinitionId;		
		this.stepAssignee=stepAssignee;
		this.startTime=startTime;
		this.endTime=endTime;
		this.durationInMillis=durationInMillis;
	}
	
	@Override
	public String getStepName() {		
		return this.stepName;
	}

	@Override
	public String getStepDefinitionKey() {		
		return this.stepDefinitionKey;
	}

	@Override
	public String getStepAssignee() {		
		return this.stepAssignee;
	}

	@Override
	public String getStepDescription() {		
		return this.stepDescription;
	}

	@Override
	public String getStepId() {		
		return this.stepId;
	}

	@Override
	public String getParentStepId() {		
		return this.parentStepId;
	}

	@Override
	public String getStepOwner() {		
		return this.owner;
	}

	@Override
	public String getProcessObjectId() {		
		return this.processObjectId;
	}

	@Override
	public String getProcessDefinitionId() {	
		return this.processDefinitionId;
	}	

	@Override
	public Date getDueDate() {		
		return this.dueDate;
	}	

	@Override
	public Date getStartTime() {		
		return this.startTime;
	}

	@Override
	public Long getDurationInMillis() {		
		return this.durationInMillis;
	}

	@Override
	public Date getEndTime() {		
		return this.endTime;
	}	
	
	public void setStepDescription(String description) {
		this.stepDescription=description;		
	}
	
	public void setParentStepId(String parentStepId) {
		this.parentStepId=parentStepId;		
	}
	
	public void setStepOwner(String owner) {
		this.owner=owner;		
	}
	
	public void setDueDate(Date dueDate) {
		this.dueDate=dueDate;		
	}	
}