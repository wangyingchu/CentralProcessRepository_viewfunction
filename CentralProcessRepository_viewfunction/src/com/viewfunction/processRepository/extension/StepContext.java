package com.viewfunction.processRepository.extension;

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
	public Map<String,Object> getProcessVariables();
}
