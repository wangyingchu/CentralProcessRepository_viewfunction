package com.viewfunction.processRepository.extension;

import java.util.Date;

public interface ExtensionStepRuntimeInfo {
	public String getExtensionStepClassName();
	public Date getDueDate();
	public String getFailMessage();
	public String getProcessObjectId();
	public String getProcessDefinitionId();
	public int getRetryTimes();
	public String getStepDefinitionKey();	
	public String getStepId();
}