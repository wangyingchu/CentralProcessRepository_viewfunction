package com.viewfunction.processRepository.processBureau;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;

public interface ProcessStep {	
	public String getStepName();
	public String getStepDefinitionKey();	
	public String getStepId();
	public String getProcessObjectId();
	public String getProcessDefinitionId();	
	public Date getCreateTime();
	
	public String getStepAssignee();
	public void setStepAssignee(String assignee);
	
	public String getStepDescription();
	public void setStepDescription(String description);		
		
	public String getParentStepId();	
	public void setParentStepId(String parentStepId);
	
	public boolean hasParentStep();
	public boolean hasChildStep();
	
	public String getStepOwner();	
	public void setStepOwner(String owner);		
	
	public Date getDueDate();	
	public void setDueDate(Date dueDate);
	
	public boolean handleCurrentStep(String userId) throws ProcessRepositoryRuntimeException;
	public boolean completeCurrentStep(String userId) throws ProcessRepositoryRuntimeException;
	public boolean completeCurrentStep(String userId,Map<String,Object> processVariables) throws ProcessRepositoryRuntimeException;
	public boolean saveCurrentStep(String userId) throws ProcessRepositoryRuntimeException;
	public boolean returnCurrentStep() throws ProcessRepositoryRuntimeException;
	public boolean reassignCurrentStep(String newUserId) throws ProcessRepositoryRuntimeException;
	
	public void addComment(ProcessComment processComment);
	public List<ProcessComment> getComments();
	
	public ProcessStep createChildProcessStep(String childStepAssignee,String childStepName,String childStepDescription,Date childStepDueDate);
	public boolean deleteChildProcessStepByStepId(String childStepId);
	public boolean deleteChildProcessSteps();
	public List<ProcessStep> getChildProcessSteps();
	public boolean isAllChildProcessStepsFinished();
	
}