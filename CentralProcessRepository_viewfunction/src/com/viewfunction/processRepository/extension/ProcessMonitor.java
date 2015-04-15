package com.viewfunction.processRepository.extension;

public interface ProcessMonitor {
	public void executeGeneralMonitorLogic(StepContext stepContext);
	
	public void executeProcessStartMonitorLogic(StepContext stepContext);
	
	public void executeProcessEndMonitorLogic(StepContext stepContext);
	
	public void executeProcessTransitionMonitorLogic(StepContext stepContext);
}
