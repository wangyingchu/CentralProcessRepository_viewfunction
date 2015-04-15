package com.viewfunction.processRepository.extensionImpl;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;


import com.viewfunction.processRepository.extension.ExtensionStep;
import com.viewfunction.processRepository.extension.StepContext;

 public abstract class ActivitiExtensionStepImpl implements ExtensionStep,JavaDelegate{	

	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		StepContext stepContext=new ActivitiStepContextImpl(delegateExecution);
		executeExtensionLogic(stepContext);			
	}
	
	@Override
	public abstract void executeExtensionLogic(StepContext stepContext);	
}