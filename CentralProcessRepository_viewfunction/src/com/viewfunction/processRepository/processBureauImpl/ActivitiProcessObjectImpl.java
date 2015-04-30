package com.viewfunction.processRepository.processBureauImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.impl.RepositoryServiceImpl;

import com.viewfunction.processRepository.processBureau.HistoricProcessStep;
import com.viewfunction.processRepository.processBureau.ProcessComment;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessStep;
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;

public class ActivitiProcessObjectImpl implements ProcessObject{
	private String processObjectId;
	private String processDefinitionId;
	private boolean isFinished;	
	
	private Date processStartTime;
	private Date processEndTime;
	private Long processDurationInMillis;
	private String processStartUserId;
	
	private ProcessEngine processEngine;
	
	public ActivitiProcessObjectImpl(String processObjectId,String processDefinitionId,boolean isFinished){
		this.processObjectId=processObjectId;
		this.processDefinitionId=processDefinitionId;
		this.isFinished=isFinished;
	}
	
	public void setProcessEngine(ProcessEngine processEngine){
		this.processEngine=processEngine;
	}
	
	@Override
	public String getProcessObjectId() {	
		return processObjectId;
	}

	@Override
	public String getProcessDefinitionId() {		
		return processDefinitionId;
	}

	@Override
	public boolean isFinished(){		
		if(isFinished){
			return isFinished;
		}
		if(this.processEngine!=null){				
			RuntimeService runtimeService = this.processEngine.getRuntimeService();		
			ProcessInstanceQuery _ProcessInstanceQuery=runtimeService.createProcessInstanceQuery();
			ProcessInstance _ProcessInstance=_ProcessInstanceQuery.processInstanceId(this.processObjectId).singleResult();	
			//after a processInstance is ended,_ProcessInstanceQuery.processInstanceId will return null
			if(_ProcessInstance==null){
				return true;
			}else{
				return _ProcessInstance.isEnded();
			}			
		}else{
			return isFinished;
		}			
	}

	@Override
	public List<ProcessStep> getCurrentProcessSteps() {
		TaskService taskService = this.processEngine.getTaskService();		
		List<Task> processInstanceTasks=taskService.createTaskQuery().processInstanceId(this.processObjectId).list();
		ArrayList<ProcessStep> processStepList=new ArrayList<ProcessStep>();
		for(Task currentTask:processInstanceTasks){			
			ProcessStep currentStep=ProcessComponentFactory.createProcessStep(currentTask.getName(),currentTask.getTaskDefinitionKey(), currentTask.getId(), currentTask.getProcessInstanceId(), currentTask.getProcessDefinitionId(), currentTask.getCreateTime());
			((ActivitiProcessStepImpl)currentStep).setProcessEngine(this.processEngine);			
			if(currentTask.getAssignee()!=null){
				currentStep.setStepAssignee(currentTask.getAssignee());				
			}
			if(currentTask.getDescription()!=null){
				currentStep.setStepDescription(currentTask.getDescription());
			}
			if(currentTask.getParentTaskId()!=null){
				currentStep.setParentStepId(currentTask.getParentTaskId());				
			}
			if(currentTask.getOwner()!=null){
				currentStep.setStepOwner(currentTask.getOwner());
			}
			if(currentTask.getDueDate()!=null){
				currentStep.setDueDate(currentTask.getDueDate());
			}
			processStepList.add(currentStep);
		}		
		return processStepList;
	}
	
	@Override
	public List<HistoricProcessStep> getFinishedProcessSteps() {
		HistoryService historyService =  this.processEngine.getHistoryService();
		List<HistoricTaskInstance> _HistoricTaskInstanceList=historyService.createHistoricTaskInstanceQuery().processInstanceId(this.processObjectId).finished().orderByHistoricTaskInstanceEndTime().desc().list();
		List<HistoricProcessStep> _HistoricProcessStepList=new ArrayList<HistoricProcessStep>();
		for(HistoricTaskInstance _HistoricTaskInstance:_HistoricTaskInstanceList){
			HistoricProcessStep currentHistoricProcessStep=ProcessComponentFactory.createHistoricProcessStep(
					_HistoricTaskInstance.getName(),_HistoricTaskInstance.getTaskDefinitionKey(),_HistoricTaskInstance.getId(),
					_HistoricTaskInstance.getProcessInstanceId(),_HistoricTaskInstance.getProcessDefinitionId(),
					_HistoricTaskInstance.getAssignee(),_HistoricTaskInstance.getStartTime(),_HistoricTaskInstance.getEndTime(),_HistoricTaskInstance.getDurationInMillis(),
					_HistoricTaskInstance.getDescription(),_HistoricTaskInstance.getParentTaskId(),_HistoricTaskInstance.getOwner(),_HistoricTaskInstance.getDueDate());
			((ActivitiHistoricProcessStepImpl)currentHistoricProcessStep).setProcessEngine(this.processEngine);			
			_HistoricProcessStepList.add(currentHistoricProcessStep);			
		}		
		return _HistoricProcessStepList;
	}

	@Override
	public void addComment(ProcessComment processComment) {
		TaskService taskService = this.processEngine.getTaskService();		
		taskService.addComment(null, this.processObjectId, processComment.getCommentMessage());		
	}

	@Override
	public List<ProcessComment> getComments() {
		TaskService taskService = this.processEngine.getTaskService();
		List<Comment> processObjectComments=taskService.getProcessInstanceComments(this.processObjectId);
		if(processObjectComments==null||processObjectComments.size()==0){
			return null;			
		}else{
			List<ProcessComment> processObjectCommentList=new ArrayList<ProcessComment>();
			for(Comment comment:processObjectComments){				
				ProcessComment currentProcessComment=new ProcessComment();				
				currentProcessComment.setCommentMessage(comment.getFullMessage());
				currentProcessComment.setProcessObjectId(comment.getProcessInstanceId());
				currentProcessComment.setTime(comment.getTime());	
				processObjectCommentList.add(currentProcessComment);
			}			
			return processObjectCommentList;
		}
	}

	@Override
	public Date getProcessStartTime() {		
		if(this.processStartTime!=null){
			return this.processStartTime;
		}else{
			if(this.processEngine==null){
				return null;
			}else{
				HistoryService historyService = this.processEngine.getHistoryService();
				HistoricProcessInstanceQuery historicProcessInstanceQuery=historyService.createHistoricProcessInstanceQuery();
				HistoricProcessInstance historicProcessInstance= historicProcessInstanceQuery.processInstanceId(this.processObjectId).singleResult();
				return historicProcessInstance.getStartTime();							
			}			
		}		
	}

	@Override
	public Date getProcessEndTime() {
		if(this.processEndTime!=null){
			return this.processEndTime;
		}else{
			if(this.processEngine==null){
				return null;
			}else{
				HistoryService historyService = this.processEngine.getHistoryService();
				HistoricProcessInstanceQuery historicProcessInstanceQuery=historyService.createHistoricProcessInstanceQuery();
				HistoricProcessInstance historicProcessInstance= historicProcessInstanceQuery.processInstanceId(this.processObjectId).singleResult();
				return historicProcessInstance.getEndTime();				
			}
		}		
	}

	@Override
	public Long getProcessDurationInMillis() {
		if(this.processDurationInMillis!=null){
			return this.processDurationInMillis;
		}else{
			if(this.processEngine==null){
				return null;
			}else{
				HistoryService historyService = this.processEngine.getHistoryService();
				HistoricProcessInstanceQuery historicProcessInstanceQuery=historyService.createHistoricProcessInstanceQuery();
				HistoricProcessInstance historicProcessInstance= historicProcessInstanceQuery.processInstanceId(this.processObjectId).singleResult();
				return historicProcessInstance.getDurationInMillis();				
			}			
		}		
	}	

	@Override
	public String getProcessStartUserId() {
		if(this.processStartUserId!=null){
			return this.processStartUserId;
		}else{
			if(this.processEngine==null){
				return null;
			}else{
				HistoryService historyService = this.processEngine.getHistoryService();
				HistoricProcessInstanceQuery historicProcessInstanceQuery=historyService.createHistoricProcessInstanceQuery();
				HistoricProcessInstance historicProcessInstance= historicProcessInstanceQuery.processInstanceId(this.processObjectId).singleResult();
				return historicProcessInstance.getStartUserId();			
			}			
		}		
	}
	
	public void setProcessStartTime(Date processStartTime) {
		this.processStartTime = processStartTime;
	}

	public void setProcessEndTime(Date processEndTime) {
		this.processEndTime = processEndTime;
	}

	public void setProcessDurationInMillis(Long processDurationInMillis) {
		this.processDurationInMillis = processDurationInMillis;
	}
	
	public void setProcessStartUserId(String processStartUserId) {
		this.processStartUserId = processStartUserId;
	}

	@Override
	public List<String> getNextProcessSteps() {
		if(this.processEngine==null||isFinished()){
			return null;
		}else{
			TaskService taskService = this.processEngine.getTaskService();
			List<Task> tasks = taskService.createTaskQuery().processInstanceId(this.processObjectId).list();
			if(tasks.size()==0){
				return null;
			}
			List<String> nextStepList=new ArrayList<String>();
			Task task = tasks.get(0);
			RepositoryService repositoryService =this. processEngine.getRepositoryService();
			ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(task.getProcessDefinitionId());
			List<ActivityImpl> activitiList = def.getActivities();
			RuntimeService runtimeService = processEngine.getRuntimeService();
			String excId = task.getExecutionId();
			ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(excId).singleResult();
			String activitiId = execution.getActivityId();
			for (ActivityImpl activityImpl : activitiList) {
				String id = activityImpl.getId();
				if (activitiId.equals(id)){
					//System.out.println(activityImpl.getProperties());
					//System.out.println("当前任务：" + activityImpl.getProperty("name"));
					// 输出某个节点的某种属性
					List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
					// 获取从某个节点出来的所有线路
					for (PvmTransition tr : outTransitions) {
						PvmActivity ac = tr.getDestination();
						// 获取线路的终点节点
						//System.out.println("下一步任务任务：" + ac.getProperty("name"));
						nextStepList.add(ac.getProperty("name").toString());
					}
					break;
				}
			}
			return nextStepList;			
		}		
	}	
}