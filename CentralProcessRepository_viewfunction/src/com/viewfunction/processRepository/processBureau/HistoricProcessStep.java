package com.viewfunction.processRepository.processBureau;

import java.util.Date;
import java.util.List;

public interface HistoricProcessStep {
	public String getStepName();
	public String getStepDefinitionKey();	
	public String getStepId();
	public String getProcessObjectId();
	public String getProcessDefinitionId();			
	public String getStepAssignee();
	public Date getStartTime();
	
	public String getStepDescription();	
	public String getParentStepId();		
	public String getStepOwner();		
	public Date getDueDate();	
	public Long getDurationInMillis();
	public Date getEndTime(); 	
	
	public boolean hasParentStep();
	public boolean hasChildStep();
	public List<HistoricProcessStep> getChildProcessSteps();
}