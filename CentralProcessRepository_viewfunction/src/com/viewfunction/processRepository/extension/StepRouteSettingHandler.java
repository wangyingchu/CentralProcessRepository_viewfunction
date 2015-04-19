package com.viewfunction.processRepository.extension;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StepRouteSettingHandler {
	public String getStepAssignee();
	public void setStepAssignee(String assignee);	
	public String getStepDescription();
	public void setStepDescription(String description);	
	public String getStepName();
	public void setStepName(String name);	
	public String getStepOwner();	
	public void setStepOwner(String owner);	
	public Date getDueDate();	
	public void setDueDate(Date dueDate);
	
	public String getStepDefinitionKey();	
	public Date getCreateTime();	
	public String getStepId();
			
	public int getStepPriority();
	public void setStepPriority(int priority);
			
	public List<String> getStepCandidateRole();
	public List<String> getStepCandidateParticipant();
	public void addStepCandidateRole(String role);	
	public void addStepCandidateRoles(List<String> roleList);
	public void addStepCandidateParticipant(String participant);
	public void addStepCandidateParticipants(List<String> participantList);
	public void deleteStepCandidateRole(String role);	
	public void deleteStepCandidateParticipant(String participant);
	
	public List<String> getProcessVariableNames();
	public Map<String,Object> getAllProcessVariables();
	public Map<String,Object> getProcessVariables(List<String> variableNamesList);
	public Object getProcessVariable(String variableName);
	
	public void removeProcessVariable(String variableName);
	public void removeProcessVariables(List<String> variableNamesList);
	public void removeAllProcessVariable();
	
	public void addProcessVariable(String variableName,Object variableValue);
	public void addProcessVariables(Map<String,Object> variables);
	
	public boolean hasProcessVariables();
	public boolean hasProcessVariable(String variableName);	
}