package com.viewfunction.processRepository.extension;

public interface ProcessStepMonitor {
	public void executeGeneralMonitorLogic(StepContext stepContext,StepStatusHandler stepStatusHandler);
	
	public void executeStepAssignMonitorLogic(StepContext stepContext,StepStatusHandler stepStatusHandler);
	
	public void executeStepCompleteMonitorLogic(StepContext stepContext,StepStatusHandler stepStatusHandler);
	
	public void executeStepCreateMonitorLogic(StepContext stepContext,StepStatusHandler stepStatusHandler);
	
	public void executeStepDeleteMonitorLogic(StepContext stepContext,StepStatusHandler stepStatusHandler);
}
