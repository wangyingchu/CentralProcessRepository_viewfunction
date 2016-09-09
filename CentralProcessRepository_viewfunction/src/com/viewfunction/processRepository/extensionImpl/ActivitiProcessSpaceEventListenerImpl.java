package com.viewfunction.processRepository.extensionImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.JobQuery;
import org.activiti.engine.task.Task;

import com.viewfunction.processRepository.extension.ExtensionStepRuntimeInfo;
import com.viewfunction.processRepository.extension.ProcessSpaceEventContext;
import com.viewfunction.processRepository.extension.ProcessSpaceEventListener;
import com.viewfunction.processRepository.extension.ProcessSpaceEventType;
import com.viewfunction.processRepository.processBureau.HistoricProcessStep;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureauImpl.ActivitiHistoricProcessStepImpl;
import com.viewfunction.processRepository.processBureauImpl.ActivitiProcessObjectImpl;
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;

public abstract class ActivitiProcessSpaceEventListenerImpl implements ActivitiEventListener,ProcessSpaceEventListener{

	@Override
	public boolean isFailOnException() {
		return false;
	}

	@Override
	public void onEvent(ActivitiEvent activitiEvent) {
		ProcessSpaceEventType processSpaceEventType=null;
		boolean isForbiddenEventType = true;
		switch(activitiEvent.getType()){
			case TASK_ASSIGNED:{ 
				processSpaceEventType=ProcessSpaceEventType.PROCESSSTEP_ASSIGNED;
				isForbiddenEventType=false;
				break;
			}
			case TASK_CREATED:{ 
				processSpaceEventType=ProcessSpaceEventType.PROCESSSTEP_CREATED;
				isForbiddenEventType=false;
				break;
			}
			case TASK_COMPLETED:{ 
				processSpaceEventType=ProcessSpaceEventType.PROCESSSTEP_COMPLETED;
				isForbiddenEventType=false;
				break;
			}
			case PROCESS_COMPLETED:{ 
				processSpaceEventType=ProcessSpaceEventType.PROCESSINSTANCE_COMPLETED;
				isForbiddenEventType=false;
				break;		
			}
			case PROCESS_CANCELLED:{ 
				processSpaceEventType=ProcessSpaceEventType.PROCESSINSTANCE_CANCELLED;
				isForbiddenEventType=false;
				break;
			}
			case JOB_EXECUTION_SUCCESS:{ 
				processSpaceEventType=ProcessSpaceEventType.PROCESSEXTENSIONSTEP_SUCCESS;
				isForbiddenEventType=false;
				break;
			}
			case JOB_EXECUTION_FAILURE:{ 
				processSpaceEventType=ProcessSpaceEventType.PROCESSEXTENSIONSTEP_FAILURE;
				isForbiddenEventType=false;
				break;
			}
			case JOB_RETRIES_DECREMENTED:{ 
				processSpaceEventType=ProcessSpaceEventType.PROCESSEXTENSIONSTEP_RETRY_DECREMENTED;
				isForbiddenEventType=false;
				break;
			}
			case JOB_CANCELED:{ 
				processSpaceEventType=ProcessSpaceEventType.PROCESSEXTENSIONSTEP_CANCELED;
				isForbiddenEventType=false;
				break;
			}	
		}
		
		if(isForbiddenEventType){
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXx");
			System.out.println("Get not Supported eventType: "+activitiEvent.getType());
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXx");
			return;
		}
		
		ActivitiProcessSpaceEventContextImpl activitiProcessSpaceEventContextImpl=new ActivitiProcessSpaceEventContextImpl(activitiEvent.getEngineServices());
		activitiProcessSpaceEventContextImpl.setProcessSpaceEventType(processSpaceEventType);
		
		String executionId=activitiEvent.getExecutionId();
		String processDefinitionId=activitiEvent.getProcessDefinitionId();
		String processInstanceId=activitiEvent.getProcessInstanceId();
		
		Task currentHandlingTask=null;
		if(executionId==null){
			//executionId is null means the operation trigger this event is not executed in a process context,for example create a sub task
			//so here we need check the real process execution context from other way such as from parent task
			boolean hasNoExecutionId=true;
			if(activitiEvent.getType().equals(ActivitiEventType.TASK_CREATED)||activitiEvent.getType().equals(ActivitiEventType.TASK_ASSIGNED)){
				ActivitiEntityEvent activitiEntityEvent=(ActivitiEntityEvent)activitiEvent;
				Task currentTask=(Task)activitiEntityEvent.getEntity();
				
				currentHandlingTask=currentTask;
				
				String parentTaskId=currentTask.getParentTaskId();
				if(parentTaskId!=null){
					Task parentTask=activitiEvent.getEngineServices().getTaskService().createTaskQuery().taskId(parentTaskId).singleResult();
					executionId=parentTask.getExecutionId();
					processInstanceId=parentTask.getProcessInstanceId();
					processDefinitionId=parentTask.getProcessDefinitionId();
					if(executionId!=null){
						hasNoExecutionId=false;
					}
				}
			}
			if(hasNoExecutionId){
				System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXx");
				System.out.println("Have no executionId for eventType: "+activitiEvent.getType());
				System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXx");
				return;
			}
		}
		
		String processSpaceName=activitiEvent.getEngineServices().getProcessEngineConfiguration().getProcessEngineName();
		ProcessEngine processEngine=ProcessEngines.getProcessEngine(processSpaceName);
		HistoricProcessInstanceQuery eventHistoricProcessInstanceQuery=activitiEvent.getEngineServices().getHistoryService().createHistoricProcessInstanceQuery();
		
		if(processInstanceId!=null){
			HistoricProcessInstance eventHistoricProcessInstance=eventHistoricProcessInstanceQuery.processInstanceId(processInstanceId).singleResult();
			boolean processInstanceFinished=false;
			if(eventHistoricProcessInstance!=null){
				Date endTime=eventHistoricProcessInstance.getEndTime();
				if(endTime!=null){
					processInstanceFinished=true;
				}else{
					processInstanceFinished=false;
				}
			}
			ProcessObject eventProcessObject=ProcessComponentFactory.createProcessObject(processInstanceId, processDefinitionId,processInstanceFinished);
			((ActivitiProcessObjectImpl)eventProcessObject).setProcessEngine(processEngine);		
			((ActivitiProcessObjectImpl)eventProcessObject).setProcessStartTime(eventHistoricProcessInstance.getStartTime());
			((ActivitiProcessObjectImpl)eventProcessObject).setProcessEndTime(eventHistoricProcessInstance.getEndTime());
			((ActivitiProcessObjectImpl)eventProcessObject).setProcessDurationInMillis(eventHistoricProcessInstance.getDurationInMillis());
			((ActivitiProcessObjectImpl)eventProcessObject).setProcessStartUserId(eventHistoricProcessInstance.getStartUserId());
			activitiProcessSpaceEventContextImpl.setEventAttachedProcessObject(eventProcessObject);
		}
		
		/*
		System.out.println("*******************************************************");
		System.out.println("--------------------");
		System.out.println("Param:");
		System.out.println("getExecutionId- "+activitiEvent.getExecutionId());
		System.out.println("getProcessDefinitionId- "+activitiEvent.getProcessDefinitionId());
		System.out.println("getProcessInstanceId- "+activitiEvent.getProcessInstanceId());
		System.out.println("getType- "+activitiEvent.getType());
		System.out.println("--------------------");
		System.out.println("Execution:");
		*/
		
		Execution currentExecution=activitiEvent.getEngineServices().getRuntimeService().createExecutionQuery().executionId(executionId).singleResult();
		String activityId=currentExecution.getActivityId();
		/*
		System.out.println("getActivityId- "+currentExecution.getActivityId());
		System.out.println("getId- "+currentExecution.getId());
		System.out.println("getParentId- "+currentExecution.getParentId());
		System.out.println("getProcessInstanceId- "+currentExecution.getProcessInstanceId());
		System.out.println("getTenantId- "+currentExecution.getTenantId());
		System.out.println("isEnded- "+currentExecution.isEnded());
		System.out.println("isSuspended- "+currentExecution.isSuspended());
		System.out.println("--------------------");
		System.out.println("ProcessInstance:");
		*/
		/*
		HistoricProcessInstanceQuery historicProcessInstanceQuery=activitiEvent.getEngineServices().getHistoryService().createHistoricProcessInstanceQuery();
		HistoricProcessInstance historicProcessInstance=historicProcessInstanceQuery.processInstanceId(processInstanceId).singleResult();
		System.out.println("getBusinessKey- "+historicProcessInstance.getBusinessKey());
		System.out.println("getDeleteReason- "+historicProcessInstance.getDeleteReason());
		System.out.println("getDurationInMillis- "+historicProcessInstance.getDurationInMillis());
		System.out.println("getEndTime- "+historicProcessInstance.getEndTime());
		System.out.println("getId- "+historicProcessInstance.getId());
		System.out.println("getName- "+historicProcessInstance.getName());
		System.out.println("getProcessDefinitionId- "+historicProcessInstance.getProcessDefinitionId());
		System.out.println("getStartActivityId- "+historicProcessInstance.getStartActivityId());
		System.out.println("getStartTime- "+historicProcessInstance.getStartTime());
		System.out.println("getStartUserId- "+historicProcessInstance.getStartUserId());
		System.out.println("getSuperProcessInstanceId- "+historicProcessInstance.getSuperProcessInstanceId());
		System.out.println("getTenantId- "+historicProcessInstance.getTenantId());
		System.out.println("--------------------");
		System.out.println("ActivityInstance:");
		HistoricActivityInstanceQuery historicActivityInstanceQuery=activitiEvent.getEngineServices().getHistoryService().createHistoricActivityInstanceQuery();
		List<HistoricActivityInstance>  aaaaXXList=historicActivityInstanceQuery.executionId(executionId).list();
		for(HistoricActivityInstance activityInstance:aaaaXXList){
			//HistoricActivityInstance activityInstance=historicActivityInstanceQuery.executionId(executionId).singleResult();
			System.out.println(activityInstance);
		}
		System.out.println("--------------------");
		System.out.println("TaskInstance:");
		*/
		HistoricTaskInstanceQuery historicTaskInstanceQuery=activitiEvent.getEngineServices().getHistoryService().createHistoricTaskInstanceQuery();
		
		List<HistoricTaskInstance> historicTaskInstanceList=historicTaskInstanceQuery.executionId(executionId).taskDefinitionKey(activityId).list();
		if(historicTaskInstanceList.size()>0){
			List<HistoricProcessStep> historicProcessStepList=new ArrayList<HistoricProcessStep>();
			for(HistoricTaskInstance historicTaskInstance:historicTaskInstanceList){
				HistoricProcessStep currentHistoricProcessStep=ProcessComponentFactory.createHistoricProcessStep(
						historicTaskInstance.getName(),historicTaskInstance.getTaskDefinitionKey(),historicTaskInstance.getId(),
						historicTaskInstance.getProcessInstanceId(),historicTaskInstance.getProcessDefinitionId(),
						historicTaskInstance.getAssignee(),historicTaskInstance.getStartTime(),historicTaskInstance.getEndTime(),historicTaskInstance.getDurationInMillis(),
						historicTaskInstance.getDescription(),historicTaskInstance.getParentTaskId(),historicTaskInstance.getOwner(),historicTaskInstance.getDueDate());
				((ActivitiHistoricProcessStepImpl)currentHistoricProcessStep).setProcessEngine(processEngine);	
				historicProcessStepList.add(currentHistoricProcessStep);
			}
			activitiProcessSpaceEventContextImpl.setEventAttachedProcessSteps(historicProcessStepList);
		}
		
		if(currentHandlingTask!=null){
			//Current handling task is a new created and assigned sub task,not have process definition,so need add it in additional
			HistoricProcessStep currentHistoricProcessStep=ProcessComponentFactory.createHistoricProcessStep(
					currentHandlingTask.getName(),currentHandlingTask.getTaskDefinitionKey(),currentHandlingTask.getId(),
					processInstanceId,processDefinitionId,
					currentHandlingTask.getAssignee(),currentHandlingTask.getCreateTime(),null,null,
					currentHandlingTask.getDescription(),currentHandlingTask.getParentTaskId(),currentHandlingTask.getOwner(),currentHandlingTask.getDueDate());
			((ActivitiHistoricProcessStepImpl)currentHistoricProcessStep).setProcessEngine(processEngine);	
			activitiProcessSpaceEventContextImpl.getEventAttachedProcessSteps().add(currentHistoricProcessStep);
		}
		
		/*
		HistoricTaskInstance historicTaskInstance=historicTaskInstanceQuery.executionId(executionId).taskDefinitionKey(activityId).singleResult();
		if(historicTaskInstance!=null){
			System.out.println("historicTaskInstance- "+historicTaskInstance);
			System.out.println("getAssignee- "+historicTaskInstance.getAssignee());
			System.out.println("getClaimTime- "+historicTaskInstance.getClaimTime());
			System.out.println("getCreateTime- "+historicTaskInstance.getCreateTime());
			System.out.println("getDeleteReason- "+historicTaskInstance.getDeleteReason());
			System.out.println("getDescription- "+historicTaskInstance.getDescription());
			System.out.println("getDueDate- "+historicTaskInstance.getDueDate());
			System.out.println("getDurationInMillis- "+historicTaskInstance.getDurationInMillis());
			System.out.println("getEndTime- "+historicTaskInstance.getEndTime());
			System.out.println("getExecutionId- "+historicTaskInstance.getExecutionId());
			System.out.println("getId- "+historicTaskInstance.getId());
			System.out.println("getName- "+historicTaskInstance.getName());
			System.out.println("getOwner- "+historicTaskInstance.getOwner());
			System.out.println("getParentTaskId- "+historicTaskInstance.getParentTaskId());
			System.out.println("getPriority- "+historicTaskInstance.getPriority());
			System.out.println("getProcessDefinitionId- "+historicTaskInstance.getProcessDefinitionId());
			System.out.println("getProcessInstanceId- "+historicTaskInstance.getProcessInstanceId());
			System.out.println("getStartTime- "+historicTaskInstance.getStartTime());
			System.out.println("getTenantId- "+historicTaskInstance.getTenantId());
			System.out.println("getTime- "+historicTaskInstance.getTime());
			System.out.println("getWorkTimeInMillis- "+historicTaskInstance.getWorkTimeInMillis());
			System.out.println("getTaskDefinitionKey- "+historicTaskInstance.getTaskDefinitionKey());
			
			HistoricProcessStep currentHistoricProcessStep=ProcessComponentFactory.createHistoricProcessStep(
					historicTaskInstance.getName(),historicTaskInstance.getTaskDefinitionKey(),historicTaskInstance.getId(),
					historicTaskInstance.getProcessInstanceId(),historicTaskInstance.getProcessDefinitionId(),
					historicTaskInstance.getAssignee(),historicTaskInstance.getStartTime(),historicTaskInstance.getEndTime(),historicTaskInstance.getDurationInMillis(),
					historicTaskInstance.getDescription(),historicTaskInstance.getParentTaskId(),historicTaskInstance.getOwner(),historicTaskInstance.getDueDate());
			((ActivitiHistoricProcessStepImpl)currentHistoricProcessStep).setProcessEngine(processEngine);	
			
			System.out.println("==========================");
			System.out.println("==========================");
			System.out.println("==========================");
			System.out.println(currentHistoricProcessStep);
			System.out.println("==========================");
			System.out.println("==========================");
			System.out.println("==========================");
		}
		*/
		
		//for current event's task, taskDefinitionKey is Execution's activityId
		/*
		List<HistoricTaskInstance> yyyList=historicTaskInstanceQuery.executionId(executionId).taskDefinitionKey(activityId).list(); 
		for(HistoricTaskInstance historicTaskInstance:yyyList){
			System.out.println("historicTaskInstance- "+historicTaskInstance);
			System.out.println("getAssignee- "+historicTaskInstance.getAssignee());
			System.out.println("getClaimTime- "+historicTaskInstance.getClaimTime());
			System.out.println("getCreateTime- "+historicTaskInstance.getCreateTime());
			System.out.println("getDeleteReason- "+historicTaskInstance.getDeleteReason());
			System.out.println("getDescription- "+historicTaskInstance.getDescription());
			System.out.println("getDueDate- "+historicTaskInstance.getDueDate());
			System.out.println("getDurationInMillis- "+historicTaskInstance.getDurationInMillis());
			System.out.println("getEndTime- "+historicTaskInstance.getEndTime());
			System.out.println("getExecutionId- "+historicTaskInstance.getExecutionId());
			System.out.println("getId- "+historicTaskInstance.getId());
			System.out.println("getName- "+historicTaskInstance.getName());
			System.out.println("getOwner- "+historicTaskInstance.getOwner());
			System.out.println("getParentTaskId- "+historicTaskInstance.getParentTaskId());
			System.out.println("getPriority- "+historicTaskInstance.getPriority());
			System.out.println("getProcessDefinitionId- "+historicTaskInstance.getProcessDefinitionId());
			System.out.println("getProcessInstanceId- "+historicTaskInstance.getProcessInstanceId());
			System.out.println("getStartTime- "+historicTaskInstance.getStartTime());
			System.out.println("getTenantId- "+historicTaskInstance.getTenantId());
			System.out.println("getTime- "+historicTaskInstance.getTime());
			System.out.println("getWorkTimeInMillis- "+historicTaskInstance.getWorkTimeInMillis());
			System.out.println("getTaskDefinitionKey- "+historicTaskInstance.getTaskDefinitionKey());
			
			HistoricProcessStep currentHistoricProcessStep=ProcessComponentFactory.createHistoricProcessStep(
					historicTaskInstance.getName(),historicTaskInstance.getTaskDefinitionKey(),historicTaskInstance.getId(),
					historicTaskInstance.getProcessInstanceId(),historicTaskInstance.getProcessDefinitionId(),
					historicTaskInstance.getAssignee(),historicTaskInstance.getStartTime(),historicTaskInstance.getEndTime(),historicTaskInstance.getDurationInMillis(),
					historicTaskInstance.getDescription(),historicTaskInstance.getParentTaskId(),historicTaskInstance.getOwner(),historicTaskInstance.getDueDate());
			((ActivitiHistoricProcessStepImpl)currentHistoricProcessStep).setProcessEngine(processEngine);	
		}
		System.out.println("*******************************************************");
		*/
		
		if(processInstanceId!=null){
			JobQuery jobQuery=activitiEvent.getEngineServices().getManagementService().createJobQuery();
			List<Job> jobList= jobQuery.executionId(executionId).processInstanceId(processInstanceId).list();
			if(jobList.size()>0){
				List<ExtensionStepRuntimeInfo> extensionStepList=new ArrayList<ExtensionStepRuntimeInfo>();
				for(Job currentJob:jobList){
					ActivitiExtensionStepRuntimeInfoImpl currentActivitiExtensionStepRuntimeInfoImpl=new ActivitiExtensionStepRuntimeInfoImpl();
					extensionStepList.add(currentActivitiExtensionStepRuntimeInfoImpl);
					currentActivitiExtensionStepRuntimeInfoImpl.setDueDate(currentJob.getDuedate());
					currentActivitiExtensionStepRuntimeInfoImpl.setExtensionStepClassName(currentJob.getClass().getName());
					currentActivitiExtensionStepRuntimeInfoImpl.setFailMessage(currentJob.getExceptionMessage());
					currentActivitiExtensionStepRuntimeInfoImpl.setProcessDefinitionId(currentJob.getProcessDefinitionId());
					currentActivitiExtensionStepRuntimeInfoImpl.setProcessObjectId(currentJob.getProcessInstanceId());
					currentActivitiExtensionStepRuntimeInfoImpl.setRetryTimes(currentJob.getRetries());
					currentActivitiExtensionStepRuntimeInfoImpl.setStepDefinitionKey(activityId);
					currentActivitiExtensionStepRuntimeInfoImpl.setStepId(currentJob.getId());
				}		
				activitiProcessSpaceEventContextImpl.setEventAttachedExtensionSteps(extensionStepList);	
			}
		}		
		
		executeEventHandleLogic(activitiProcessSpaceEventContextImpl);
		
		/*
		if(activitiEvent.getType().equals(ActivitiEventType.ENGINE_CREATED)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEvent
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.ENGINE_CLOSED)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEvent
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.ENTITY_CREATED)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEntityEvent
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.ENTITY_INITIALIZED)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEntityEvent
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.ENTITY_UPDATED)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEntityEvent
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.ENTITY_DELETED)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEntityEvent
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.ENTITY_SUSPENDED)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEntityEvent
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.ENTITY_ACTIVATED)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEntityEvent
			System.out.println(activitiEvent);
		}		
		if(activitiEvent.getType().equals(ActivitiEventType.JOB_EXECUTION_SUCCESS)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEntityEvent
			System.out.println(activitiEvent);
		}		
		if(activitiEvent.getType().equals(ActivitiEventType.JOB_EXECUTION_FAILURE)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEntityEvent and org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiExceptionEvent
			System.out.println(activitiEvent);
		}		
		if(activitiEvent.getType().equals(ActivitiEventType.JOB_RETRIES_DECREMENTED)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEntityEvent
			System.out.println(activitiEvent);
		}		
		if(activitiEvent.getType().equals(ActivitiEventType.TIMER_FIRED)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEntityEvent
			System.out.println(activitiEvent);
		}				
		if(activitiEvent.getType().equals(ActivitiEventType.JOB_CANCELED)){
			//org.activitiÃ¢â‚¬Â¦Ã¢â‚¬â€¹ActivitiEntityEvent
			System.out.println(activitiEvent);
		}		
		if(activitiEvent.getType().equals(ActivitiEventType.ACTIVITY_STARTED)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.ACTIVITY_COMPLETED)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.ACTIVITY_CANCELLED)){
			//
			System.out.println(activitiEvent);
		}		
		if(activitiEvent.getType().equals(ActivitiEventType.ACTIVITY_SIGNALED)){
			//
		}
		if(activitiEvent.getType().equals(ActivitiEventType.ACTIVITY_MESSAGE_RECEIVED)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.ACTIVITY_ERROR_RECEIVED)){
			//
			System.out.println(activitiEvent);
		}		
		if(activitiEvent.getType().equals(ActivitiEventType.UNCAUGHT_BPMN_ERROR)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.ACTIVITY_COMPENSATE)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.VARIABLE_CREATED)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.VARIABLE_UPDATED)){
			//
			System.out.println(activitiEvent);
		}		
		if(activitiEvent.getType().equals(ActivitiEventType.VARIABLE_DELETED)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.TASK_ASSIGNED)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.TASK_CREATED)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.TASK_COMPLETED)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.PROCESS_COMPLETED)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.PROCESS_CANCELLED)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.MEMBERSHIP_CREATED)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.MEMBERSHIP_DELETED)){
			//
			System.out.println(activitiEvent);
		}
		if(activitiEvent.getType().equals(ActivitiEventType.MEMBERSHIPS_DELETED)){
			//
			System.out.println(activitiEvent);
		}
		*/
	}

	@Override
	public abstract void executeEventHandleLogic(ProcessSpaceEventContext processSpaceEventContext);

}
