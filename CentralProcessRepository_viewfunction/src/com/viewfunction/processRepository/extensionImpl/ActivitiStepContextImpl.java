package com.viewfunction.processRepository.extensionImpl;

import java.util.Map;

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
	public Map<String, Object> getProcessVariables() {
		return this.activitiDelegateExecution.getVariables();
	}
}
