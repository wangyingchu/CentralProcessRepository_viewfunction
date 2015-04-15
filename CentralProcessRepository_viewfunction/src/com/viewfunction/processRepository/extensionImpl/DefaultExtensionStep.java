package com.viewfunction.processRepository.extensionImpl;

import com.viewfunction.processRepository.extension.StepContext;

public class DefaultExtensionStep extends ActivitiExtensionStepImpl{
	@Override
	public void executeExtensionLogic(StepContext stepContext) {
		stepContext.getAllProcessVariables();			
	}
}
