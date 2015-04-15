package com.viewfunction.processRepository.extensionImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.repository.ProcessDefinition;

import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.extension.StepContext;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessSpace;
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;

public class ActivitiStepContextImpl implements StepContext{
	private DelegateExecution activitiDelegateExecution;
	
	public ActivitiStepContextImpl(DelegateExecution delegateExecution){
		this.activitiDelegateExecution = delegateExecution;
		//delegateExecution.getParentId();
	}

	@Override
	public String getProcessSpaceName() {
		ProcessEngineConfiguration processEngineConfiguration=this.activitiDelegateExecution.getEngineServices().getProcessEngineConfiguration();
		return processEngineConfiguration.getProcessEngineName();
	}

	@Override
	public ProcessObject getProcessObject() throws ProcessRepositoryRuntimeException {
		String processSpaceName = getProcessSpaceName();
		ProcessSpace processSpace=ProcessComponentFactory.connectProcessSpace(processSpaceName);
		String processInstanceId=this.activitiDelegateExecution.getProcessInstanceId();
		ProcessObject processObject=processSpace.getProcessObjectById(processInstanceId);		
		return processObject;
	}

	@Override
	public String getProcessType() {
		ProcessDefinition currentProcessDefinition=this.activitiDelegateExecution.getEngineServices().getRepositoryService().getProcessDefinition(getProcessDefinitionId());
		String processTypeName=currentProcessDefinition.getKey();
		return processTypeName;
	}

	@Override
	public String getProcessStepName() {
		return this.activitiDelegateExecution.getCurrentActivityName();
	}

	@Override
	public String getProcessStepId() {
		return this.activitiDelegateExecution.getCurrentActivityId();
	}

	@Override
	public String getProcessDefinitionId() {
		return this.activitiDelegateExecution.getProcessDefinitionId();
	}

	@Override
	public String getProcessObjectId() {
		return this.activitiDelegateExecution.getProcessInstanceId();
	}	
	
	@Override
	public List<String> getProcessVariableNames() {
		List<String> variableNamesList=new ArrayList<String>();		
		Set<String> variableNamesSet=this.activitiDelegateExecution.getVariableNames();
		Iterator<String> variableIterator=variableNamesSet.iterator();		
		while (variableIterator.hasNext()){
			String variableName=variableIterator.next();
			variableNamesList.add(variableName);	
		}				
		return variableNamesList;
	}

	@Override
	public Map<String, Object> getAllProcessVariables() {		
		return this.activitiDelegateExecution.getVariables();
	}

	@Override
	public Map<String, Object> getProcessVariables(List<String> variableNamesList) {
		return this.activitiDelegateExecution.getVariables(variableNamesList);
	}

	@Override
	public Object getProcessVariable(String variableName) {		
		return this.activitiDelegateExecution.getVariable(variableName);
	}

	@Override
	public void removeProcessVariable(String variableName) {
		this.activitiDelegateExecution.removeVariable(variableName);		
	}

	@Override
	public void removeProcessVariables(List<String> variableNamesList) {
		this.activitiDelegateExecution.removeVariables(variableNamesList);		
	}

	@Override
	public void removeAllProcessVariable() {
		this.activitiDelegateExecution.removeVariables();		
	}

	@Override
	public void addProcessVariable(String variableName, Object variableValue) {
		this.activitiDelegateExecution.setVariable(variableName, variableValue);		
	}

	@Override
	public void addProcessVariables(Map<String, Object> variables) {
		this.activitiDelegateExecution.setVariables(variables);		
	}

	@Override
	public boolean hasProcessVariables() {		
		return this.activitiDelegateExecution.hasVariables();
	}

	@Override
	public boolean hasProcessVariable(String variableName) {		
		return this.activitiDelegateExecution.hasVariable(variableName);
	}
}
