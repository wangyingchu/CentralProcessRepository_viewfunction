package com.viewfunction.processRepository.extensionImpl;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import com.viewfunction.processRepository.extension.ProcessStepMonitor;
import com.viewfunction.processRepository.extension.StepContext;
import com.viewfunction.processRepository.extension.StepStatusHandler;

public abstract class ActivitiProcessStepMonitorImpl implements ProcessStepMonitor,TaskListener {

	private static final long serialVersionUID = -37715328956049552L;

	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution delegateExecution=delegateTask.getExecution();
		StepContext stepContext=new ActivitiStepContextImpl(delegateExecution);
		StepStatusHandler stepStatusHandler=new ActivitiStepStatusHandlerImpl(delegateTask);		
		String eventName=delegateTask.getEventName();
		
		if(TaskListener.EVENTNAME_ASSIGNMENT.equals(eventName)){
			executeStepAssignMonitorLogic(stepContext,stepStatusHandler);
		}
		if(TaskListener.EVENTNAME_COMPLETE.equals(eventName)){
			executeStepCompleteMonitorLogic(stepContext,stepStatusHandler);
		}
		if(TaskListener.EVENTNAME_CREATE.equals(eventName)){
			executeStepCreateMonitorLogic(stepContext,stepStatusHandler);
		}
		if(TaskListener.EVENTNAME_DELETE.equals(eventName)){
			executeStepDeleteMonitorLogic(stepContext,stepStatusHandler);
		}		
		executeGeneralMonitorLogic(stepContext,stepStatusHandler);
	}
	
	@Override
	public abstract void executeGeneralMonitorLogic(StepContext stepContext,StepStatusHandler stepStatusHandler);
	
	@Override
	public abstract void executeStepAssignMonitorLogic(StepContext stepContext,StepStatusHandler stepStatusHandler);
	
	@Override
	public abstract void executeStepCompleteMonitorLogic(StepContext stepContext,StepStatusHandler stepStatusHandler);
	
	@Override
	public abstract void executeStepCreateMonitorLogic(StepContext stepContext,StepStatusHandler stepStatusHandler);
	
	@Override
	public abstract void executeStepDeleteMonitorLogic(StepContext stepContext,StepStatusHandler stepStatusHandler);
}