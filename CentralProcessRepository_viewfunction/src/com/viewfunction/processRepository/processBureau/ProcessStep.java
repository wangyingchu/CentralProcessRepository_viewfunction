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
	public Date getEndTime(); // this method is used to get the end date of a finished child process step
	
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
	
	public void updateCurrentStepDueDate(Date dueDate) throws ProcessRepositoryRuntimeException;	
	public boolean handleCurrentStep(String userId) throws ProcessRepositoryRuntimeException;
	public boolean completeCurrentStep(String userId) throws ProcessRepositoryRuntimeException;
	public boolean completeCurrentStep(String userId,Map<String,Object> processVariables) throws ProcessRepositoryRuntimeException;
	public boolean saveCurrentStep(String userId) throws ProcessRepositoryRuntimeException;
	public boolean returnCurrentStep() throws ProcessRepositoryRuntimeException;
	public boolean reassignCurrentStep(String newUserId) throws ProcessRepositoryRuntimeException;
	
	public boolean delegateCurrentStep(String delegateToUserId) throws ProcessRepositoryRuntimeException;
	public boolean isDelegatedStep() throws ProcessRepositoryRuntimeException;
	public boolean resolveDelegateJob() throws ProcessRepositoryRuntimeException;
	public boolean resolveDelegateJob(Map<String, Object> processVariables) throws ProcessRepositoryRuntimeException;
	
	public boolean setStepPriority(int priority) throws ProcessRepositoryRuntimeException;
	public int getStepPriority()throws ProcessRepositoryRuntimeException;
	
	public boolean isSuspendedStep() throws ProcessRepositoryRuntimeException;
	
	public void addComment(ProcessComment processComment);
	public List<ProcessComment> getComments();
	
	public ProcessStep createChildProcessStep(String childStepAssignee,String childStepName,String childStepDescription,Date childStepDueDate);
	public boolean deleteChildProcessStepByStepId(String childStepId);
	public boolean deleteChildProcessSteps();
	public List<ProcessStep> getChildProcessSteps();
	public boolean isAllChildProcessStepsFinished();	
	public ProcessStep getParentProcessStep() throws ProcessRepositoryRuntimeException;	
	
	public Integer getProcessDefinitionVersion();
}