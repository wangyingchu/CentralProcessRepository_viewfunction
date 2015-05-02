package com.viewfunction.processRepository.processBureauImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricTaskInstance;

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
	
	private ProcessEngine processEngine;	
	
	public ActivitiHistoricProcessStepImpl(String stepName,String stepDefinitionKey,String stepId,String processObjectId,String processDefinitionId,String stepAssignee, Date startTime,Date endTime,Long durationInMillis,
			String description, String parentStepId,String owner,Date dueDate){
		this.stepName=stepName;
		this.stepDefinitionKey=stepDefinitionKey;
		this.stepId=stepId;
		this.processObjectId=processObjectId;
		this.processDefinitionId=processDefinitionId;		
		this.stepAssignee=stepAssignee;
		this.startTime=startTime;
		this.endTime=endTime;
		this.durationInMillis=durationInMillis;
		this.stepDescription=description;
		this.parentStepId=parentStepId;
		this.dueDate=dueDate;
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

	@Override
	public boolean hasParentStep() {		
		if(this.getParentStepId()!=null){
			return true;
		}else{
			return false;
		}	
	}

	@Override
	public boolean hasChildStep() {		
		HistoryService historyService=this.processEngine.getHistoryService();		
		long childTaskCount=historyService.createHistoricTaskInstanceQuery().taskParentTaskId(this.stepId).count();		
		if(childTaskCount>0){
			return true;
		}else{
			return false;
		}		
	}

	public ProcessEngine getProcessEngine() {
		return processEngine;
	}

	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}

	@Override
	public List<HistoricProcessStep> getChildProcessSteps() {		
		List<HistoricProcessStep> historicProcessStepList=new ArrayList<HistoricProcessStep>();
		HistoryService historyService=this.processEngine.getHistoryService();			
		List<HistoricTaskInstance> childHistoricTaskList= historyService.createHistoricTaskInstanceQuery().taskParentTaskId(this.stepId).orderByTaskCreateTime().asc().list();		
		for(HistoricTaskInstance currentHistoricTaskInstance:childHistoricTaskList){
			String stepName=currentHistoricTaskInstance.getName();			
			String stepDefinitionKey=this.stepDefinitionKey;
			String stepId=currentHistoricTaskInstance.getId();			
			String processObjectId=this.processObjectId;
			String processDefinitionId=this.processDefinitionId;
			String stepAssignee=currentHistoricTaskInstance.getAssignee(); 			
			Date startTime=currentHistoricTaskInstance.getStartTime();
			Date endTime=currentHistoricTaskInstance.getEndTime();
			Long durationInMillis=currentHistoricTaskInstance.getDurationInMillis();
			String description=currentHistoricTaskInstance.getDescription(); 
			String parentStepId=currentHistoricTaskInstance.getId();
			String owner=currentHistoricTaskInstance.getOwner();
			Date dueDate=currentHistoricTaskInstance.getDueDate();		
			HistoricProcessStep currentHistoricProcessStep=new			
			 ActivitiHistoricProcessStepImpl(stepName,stepDefinitionKey,stepId,processObjectId,processDefinitionId,stepAssignee, startTime,endTime,durationInMillis,
						description, parentStepId,owner,dueDate);			
			historicProcessStepList.add(currentHistoricProcessStep);			
		}		
		return historicProcessStepList;
	}	
}