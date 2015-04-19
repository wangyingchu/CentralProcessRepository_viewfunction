package com.viewfunction.processRepository.extension;

public interface ProcessStepMonitor {
	public void executeGeneralMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepStatusHandler);
	
	public void executeStepAssignMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepStatusHandler);
	
	public void executeStepCompleteMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepStatusHandler);
	
	public void executeStepCreateMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepStatusHandler);
	
	public void executeStepDeleteMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepStatusHandler);
}
