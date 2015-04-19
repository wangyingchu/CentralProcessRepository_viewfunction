package com.viewfunction.processRepository.extensionImpl;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import com.viewfunction.processRepository.extension.ProcessStepMonitor;
import com.viewfunction.processRepository.extension.StepContext;
import com.viewfunction.processRepository.extension.StepRouteSettingHandler;

public abstract class ActivitiProcessStepMonitorImpl implements ProcessStepMonitor,TaskListener {

	private static final long serialVersionUID = -37715328956049552L;

	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution delegateExecution=delegateTask.getExecution();
		StepContext stepContext=new ActivitiStepContextImpl(delegateExecution);
		StepRouteSettingHandler stepRouteSettingHandler=new ActivitiStepRouteSettingHandlerImpl(delegateTask);		
		String eventName=delegateTask.getEventName();
		
		if(TaskListener.EVENTNAME_ASSIGNMENT.equals(eventName)){
			executeStepAssignMonitorLogic(stepContext,stepRouteSettingHandler);
		}
		if(TaskListener.EVENTNAME_COMPLETE.equals(eventName)){
			executeStepCompleteMonitorLogic(stepContext,stepRouteSettingHandler);
		}
		if(TaskListener.EVENTNAME_CREATE.equals(eventName)){
			executeStepCreateMonitorLogic(stepContext,stepRouteSettingHandler);
		}
		if(TaskListener.EVENTNAME_DELETE.equals(eventName)){
			executeStepDeleteMonitorLogic(stepContext,stepRouteSettingHandler);
		}		
		executeGeneralMonitorLogic(stepContext,stepRouteSettingHandler);
	}
	
	@Override
	public abstract void executeGeneralMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler);
	
	@Override
	public abstract void executeStepAssignMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler);
	
	@Override
	public abstract void executeStepCompleteMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler);
	
	@Override
	public abstract void executeStepCreateMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler);
	
	@Override
	public abstract void executeStepDeleteMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler);
}