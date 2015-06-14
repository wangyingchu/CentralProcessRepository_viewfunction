package com.viewfunction.processRepository.extensionImpl;

import java.util.List;

import org.activiti.engine.EngineServices;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.repository.ProcessDefinition;

import com.viewfunction.processRepository.extension.ExtensionStepRuntimeInfo;
import com.viewfunction.processRepository.extension.ProcessSpaceEventContext;
import com.viewfunction.processRepository.extension.ProcessSpaceEventType;
import com.viewfunction.processRepository.processBureau.HistoricProcessStep;
import com.viewfunction.processRepository.processBureau.ProcessObject;

public class ActivitiProcessSpaceEventContextImpl implements ProcessSpaceEventContext{
	
	private ProcessSpaceEventType processSpaceEventType;
	private EngineServices engineServices;
	private ProcessObject eventAttachedProcessObject;
	private List<HistoricProcessStep> eventAttachedProcessStepList;
	private List<ExtensionStepRuntimeInfo> eventAttachedExtensionStepList;
	
	public ActivitiProcessSpaceEventContextImpl(EngineServices engineServices){
		this.engineServices=engineServices;
	}
	
	@Override
	public String getProcessType() {
		ProcessDefinition currentProcessDefinition=this.engineServices.getRepositoryService().getProcessDefinition(eventAttachedProcessObject.getProcessDefinitionId());
		String processTypeName=currentProcessDefinition.getKey();
		return processTypeName;
	}
	
	@Override
	public ProcessSpaceEventType getProcessSpaceEventType() {
		return this.processSpaceEventType;
	}
	
	public void setProcessSpaceEventType(ProcessSpaceEventType processSpaceEventType){
		this.processSpaceEventType=processSpaceEventType;
	}

	@Override
	public String getProcessSpaceName() {
		ProcessEngineConfiguration processEngineConfiguration=this.engineServices.getProcessEngineConfiguration();
		return processEngineConfiguration.getProcessEngineName();
	}

	@Override
	public ProcessObject getEventAttachedProcessObject() {
		return this.eventAttachedProcessObject;
	}

	public void setEventAttachedProcessObject(ProcessObject processObject) {
		this.eventAttachedProcessObject = processObject;
	}

	@Override
	public List<HistoricProcessStep> getEventAttachedProcessSteps() {
		return this.eventAttachedProcessStepList;
	}
	
	public void setEventAttachedProcessSteps(List<HistoricProcessStep> eventAttachedProcessStepList) {
		this.eventAttachedProcessStepList=eventAttachedProcessStepList;
	}

	@Override
	public List<ExtensionStepRuntimeInfo> getEventAttachedExtensionSteps() {
		return this.eventAttachedExtensionStepList;
	}
	
	public void setEventAttachedExtensionSteps(List<ExtensionStepRuntimeInfo> eventAttachedExtensionStepList) {
		this.eventAttachedExtensionStepList=eventAttachedExtensionStepList;
	}
}
