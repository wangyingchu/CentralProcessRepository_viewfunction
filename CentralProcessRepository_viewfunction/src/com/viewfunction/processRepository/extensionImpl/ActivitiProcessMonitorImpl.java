package com.viewfunction.processRepository.extensionImpl;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import com.viewfunction.processRepository.extension.ProcessMonitor;
import com.viewfunction.processRepository.extension.StepContext;

public abstract class ActivitiProcessMonitorImpl implements ProcessMonitor,ExecutionListener{

	private static final long serialVersionUID = -8812366551680850387L;

	@Override
	public void notify(DelegateExecution delegateExecution) throws Exception {
		StepContext stepContext=new ActivitiStepContextImpl(delegateExecution);
		String eventName=delegateExecution.getEventName();
		if(ExecutionListener.EVENTNAME_START.equals(eventName)){
			executeProcessStartMonitorLogic(stepContext);
		}
		if(ExecutionListener.EVENTNAME_END.equals(eventName)){
			executeProcessEndMonitorLogic(stepContext);
		}
		if(ExecutionListener.EVENTNAME_TAKE.equals(eventName)){
			executeProcessTransitionMonitorLogic(stepContext);
		}
		executeGeneralMonitorLogic(stepContext);
	}

	@Override
	public abstract void executeGeneralMonitorLogic(StepContext stepContext);
	
	@Override
	public abstract void executeProcessStartMonitorLogic(StepContext stepContext);
	
	@Override
	public abstract void executeProcessEndMonitorLogic(StepContext stepContext);
	
	@Override
	public abstract void executeProcessTransitionMonitorLogic(StepContext stepContext);
	
}