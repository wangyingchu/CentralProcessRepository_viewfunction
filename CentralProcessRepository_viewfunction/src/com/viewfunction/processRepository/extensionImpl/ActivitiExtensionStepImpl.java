package com.viewfunction.processRepository.extensionImpl;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;


import com.viewfunction.processRepository.extension.ExtensionStep;
import com.viewfunction.processRepository.extension.StepContext;

 public abstract class ActivitiExtensionStepImpl implements ExtensionStep,JavaDelegate{
	private Map<String,Object> processVariables;

	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		StepContext stepContext=new ActivitiStepContextImpl(delegateExecution);
		executeExtensionLogic(stepContext);
		if(this.processVariables!=null){
			delegateExecution.setVariables(this.processVariables);
		}
	}
	
	@Override
	public abstract void executeExtensionLogic(StepContext stepContext);
	
	@Override
	public void setProcessVariables(Map<String,Object> processVariables){
		this.processVariables=processVariables;
	};
}
