package com.viewfunction.processRepository.extension;

import java.util.Map;

public interface ExtensionStep {
	public void executeExtensionLogic(StepContext stepContext);
	
	public void setProcessVariables(Map<String,Object> processVariables);	
}
