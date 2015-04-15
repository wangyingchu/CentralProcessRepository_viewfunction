package com.viewfunction.processRepository.extension;

import java.util.List;
import java.util.Map;

import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.processBureau.ProcessObject;

public interface StepContext {
	public String getProcessSpaceName();
	public String getProcessType();
	public String getProcessDefinitionId();
	public String getProcessObjectId();
	public String getProcessStepName();
	public String getProcessStepId();
	public ProcessObject getProcessObject() throws ProcessRepositoryRuntimeException;
		
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
