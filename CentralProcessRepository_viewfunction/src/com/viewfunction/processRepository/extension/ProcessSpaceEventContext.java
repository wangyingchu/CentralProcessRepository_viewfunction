package com.viewfunction.processRepository.extension;

import java.util.List;

import com.viewfunction.processRepository.processBureau.HistoricProcessStep;
import com.viewfunction.processRepository.processBureau.ProcessObject;

public interface ProcessSpaceEventContext {
	
	public ProcessSpaceEventType getProcessSpaceEventType();
	
	public String getProcessSpaceName();
	
	public ProcessObject getEventAttachedProcessObject();
	
	public List<HistoricProcessStep> getEventAttachedProcessSteps();
	
	public List<ExtensionStepRuntimeInfo> getEventAttachedExtensionSteps();
	
	public String getProcessType();
	
}
