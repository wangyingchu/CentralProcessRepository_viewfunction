package com.viewfunction.processRepository.processBureauImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;

import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.processBureau.ProcessComment;
import com.viewfunction.processRepository.processBureau.ProcessStep;

public class ActivitiProcessStepImpl implements ProcessStep{
	
	private String stepName;
	private String stepDefinitionKey;
	private String stepAssignee;
	private String stepDescription;
	private String stepId;
	private String parentStepId;
	private String owner;
	private String processObjectId;
	private String processDefinitionId;
	private Date createTime;
	private Date dueDate;
	private Date endTime;
	
	private ProcessEngine processEngine;
	
	public ActivitiProcessStepImpl(String stepName,String stepDefinitionKey,String stepId,String processObjectId,String processDefinitionId,Date createTime){
		this.stepName=stepName;
		this.stepDefinitionKey=stepDefinitionKey;
		this.stepId=stepId;
		this.processObjectId=processObjectId;
		this.processDefinitionId=processDefinitionId;
		this.createTime=createTime;
	}
	
	public void setProcessEngine(ProcessEngine processEngine){
		this.processEngine=processEngine;
	}
	
	@Override
	public String getStepName() {		
		return this.stepName;
	}

	@Override
	public String getStepDefinitionKey() {		
		return this.stepDefinitionKey;
	}

	@Override
	public String getStepAssignee() {		
		return this.stepAssignee;
	}

	@Override
	public String getStepDescription() {		
		return this.stepDescription;
	}

	@Override
	public String getStepId() {		
		return this.stepId;
	}

	@Override
	public String getParentStepId() {		
		return this.parentStepId;
	}

	@Override
	public String getStepOwner() {		
		return this.owner;
	}

	@Override
	public String getProcessObjectId() {		
		return this.processObjectId;
	}

	@Override
	public String getProcessDefinitionId() {	
		return this.processDefinitionId;
	}

	@Override
	public Date getCreateTime() {		
		return this.createTime;
	}

	@Override
	public Date getDueDate() {		
		return this.dueDate;
	}

	@Override
	public void setStepAssignee(String assignee) {
		this.stepAssignee=assignee;		
	}

	@Override
	public void setStepDescription(String description) {
		this.stepDescription=description;		
	}

	@Override
	public void setParentStepId(String parentStepId) {
		this.parentStepId=parentStepId;		
	}

	@Override
	public void setStepOwner(String owner) {
		this.owner=owner;		
	}

	@Override
	public void setDueDate(Date dueDate) {
		this.dueDate=dueDate;		
	}

	@Override
	public boolean completeCurrentStep(String userId) throws ProcessRepositoryRuntimeException {
		TaskService taskService = this.processEngine.getTaskService();		
		Task currentTask=taskService.createTaskQuery().taskId(this.stepId).singleResult();		
		if(currentTask.getAssignee()==null){
			taskService.claim(currentTask.getId(), userId);			
		}else{
			if(!currentTask.getAssignee().equals(userId)){
				throw new ProcessRepositoryRuntimeException();
			}
		}
		
		DelegationState taskDelegateState=currentTask.getDelegationState();
		if(taskDelegateState!=null){
			switch(taskDelegateState){
				case PENDING:
					//resolve task will clear owner and set assignee back to owner
					taskService.resolveTask(currentTask.getId());
					break;
				case RESOLVED:
					break;
			}
		}
		
		taskService.complete(currentTask.getId());		
		return true;
	}

	@Override
	public boolean completeCurrentStep(String userId,Map<String, Object> processVariables)throws ProcessRepositoryRuntimeException {
		TaskService taskService = this.processEngine.getTaskService();		
		Task currentTask=taskService.createTaskQuery().taskId(this.stepId).singleResult();		
		if(currentTask.getAssignee()==null){
			taskService.claim(currentTask.getId(), userId);			
		}else{
			if(!currentTask.getAssignee().equals(userId)){
				throw new ProcessRepositoryRuntimeException();
			}
		}
		
		DelegationState taskDelegateState=currentTask.getDelegationState();
		if(taskDelegateState!=null){
			switch(taskDelegateState){
				case PENDING:
					//resolve task will clear owner and set assignee back to owner
					taskService.resolveTask(currentTask.getId());
					break;
				case RESOLVED:
					break;
			}
		}
		
		taskService.complete(currentTask.getId(),processVariables);		
		return true;
	}
	
	@Override
	public boolean saveCurrentStep(String userId) throws ProcessRepositoryRuntimeException {
		TaskService taskService = this.processEngine.getTaskService();		
		Task currentTask=taskService.createTaskQuery().taskId(this.stepId).singleResult();
		if(currentTask==null){
			throw new ProcessRepositoryRuntimeException();			
		}
		if(currentTask.getAssignee()!=null&&!currentTask.getAssignee().equals(userId)){
			throw new ProcessRepositoryRuntimeException();	
		}else{
			if(this.stepAssignee!=null){
				currentTask.setAssignee(this.stepAssignee);
			}
			if(this.stepDescription!=null){
				currentTask.setDescription(this.stepDescription);
			}
			if(this.parentStepId!=null){
				currentTask.setParentTaskId(this.parentStepId);
			}
			if(this.owner!=null){
				currentTask.setOwner(this.owner);
			}
			if(this.dueDate!=null){
				currentTask.setDueDate(this.dueDate);
			}			
			taskService.saveTask(currentTask);
			return true;
		}		
	}

	@Override
	public boolean handleCurrentStep(String userId)	throws ProcessRepositoryRuntimeException {
		TaskService taskService = this.processEngine.getTaskService();		
		Task currentTask=taskService.createTaskQuery().taskId(this.stepId).singleResult();
		if(currentTask==null){
			throw new ProcessRepositoryRuntimeException();			
		}else{
			if(currentTask.getAssignee()!=null&&!currentTask.getAssignee().equals(userId)){
				throw new ProcessRepositoryRuntimeException();
			}else{
				taskService.claim(currentTask.getId(), userId);				
				return true;
			}			
		}	
	}

	@Override
	public void addComment(ProcessComment processComment) {
		TaskService taskService = this.processEngine.getTaskService();		
		taskService.addComment(this.stepId, null, processComment.getCommentMessage());		
	}

	@Override
	public List<ProcessComment> getComments() {
		TaskService taskService = this.processEngine.getTaskService();
		List<Comment> processObjectComments=taskService.getTaskComments(this.stepId);
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
	public boolean returnCurrentStep() throws ProcessRepositoryRuntimeException {
		if(hasParentStep()){
			//don't allow return a child process step
			throw new ProcessRepositoryRuntimeException();			
		}		
		TaskService taskService = this.processEngine.getTaskService();		
		Task currentTask=taskService.createTaskQuery().taskId(this.stepId).singleResult();
		if(currentTask==null){
			throw new ProcessRepositoryRuntimeException();			
		}
		taskService.setAssignee(this.stepId, null);	
		return true;
	}
	
	@Override
	public void updateCurrentStepDueDate(Date dueDate) throws ProcessRepositoryRuntimeException {
		TaskService taskService = this.processEngine.getTaskService();		
		Task currentTask=taskService.createTaskQuery().taskId(this.stepId).singleResult();
		if(currentTask==null){
			throw new ProcessRepositoryRuntimeException();			
		}			
		taskService.setDueDate(this.stepId, dueDate);
		this.setDueDate(dueDate);
	}	

	@Override
	public boolean reassignCurrentStep(String newUserId) throws ProcessRepositoryRuntimeException {
		TaskService taskService = this.processEngine.getTaskService();		
		Task currentTask=taskService.createTaskQuery().taskId(this.stepId).singleResult();
		if(currentTask==null){
			throw new ProcessRepositoryRuntimeException();			
		}		
		String newTaskOwner=currentTask.getAssignee();
		taskService.setAssignee(this.stepId, newUserId);
		taskService.setOwner(this.stepId, newTaskOwner);		
		return true;
	}
	
	@Override
	public boolean delegateCurrentStep(String newUserId) throws ProcessRepositoryRuntimeException {
		TaskService taskService = this.processEngine.getTaskService();		
		Task currentTask=taskService.createTaskQuery().taskId(this.stepId).singleResult();
		if(currentTask==null){
			throw new ProcessRepositoryRuntimeException();			
		}		
		String newTaskOwner=currentTask.getAssignee();
		taskService.delegateTask(this.stepId, newUserId);
		taskService.setOwner(this.stepId, newTaskOwner);
		return true;
	}

	@Override
	public ProcessStep createChildProcessStep(String childStepAssignee,String childStepName,String childStepDescription,Date childSteDdueDate) {		
		TaskService taskService = this.processEngine.getTaskService();
		Task childTask = taskService.newTask();
		childTask.setAssignee(childStepAssignee);		
		if(this.getStepAssignee()!=null){
			childTask.setOwner(getStepAssignee());			
		}
		childTask.setParentTaskId(getStepId()); 
		childTask.setName(childStepName);
		childTask.setDescription(childStepDescription);
		childTask.setTenantId(this.processEngine.getName());
		if(childSteDdueDate!=null){
			childTask.setDueDate(childSteDdueDate);
		}else{
			childTask.setDueDate(getDueDate());
		}		
		taskService.saveTask(childTask);			
		String childProcessStepId=childTask.getId();
		Date createTime=childTask.getCreateTime();		
		ProcessStep childProcessStep=new ActivitiProcessStepImpl(childStepName,getStepDefinitionKey(),childProcessStepId,getProcessObjectId(),getProcessDefinitionId(),createTime);				
		if(childTask.getParentTaskId()!=null){
			childProcessStep.setParentStepId(childTask.getParentTaskId());
		}			
		if(childTask.getAssignee()!=null){
			childProcessStep.setStepAssignee(childTask.getAssignee());
		}			
		if(childTask.getDescription()!=null){
			childProcessStep.setStepDescription(childTask.getDescription());
		}			
		if(childTask.getOwner()!=null){
			childProcessStep.setStepOwner(childTask.getOwner());
		}			
		if(childTask.getDueDate()!=null){
			childProcessStep.setDueDate(childTask.getDueDate());
		}			
		return childProcessStep;
	}

	@Override
	public boolean deleteChildProcessStepByStepId(String stepId) {
		TaskService taskService = this.processEngine.getTaskService();
		//also delete related sub task
		taskService.deleteTask(stepId,true);
		List<Task> childTasks=taskService.getSubTasks(getStepId());
		for(Task currentTask:childTasks){
			if(currentTask.getId().equals(stepId)){
				return false;
			}
		}		
		return true;
	}

	@Override
	public boolean deleteChildProcessSteps() {
		TaskService taskService = this.processEngine.getTaskService();
		List<Task> childTasks=taskService.getSubTasks(getStepId());
		List<String> childTaskIdArray=new ArrayList<String>();
		for(Task currentTask:childTasks){
			childTaskIdArray.add(currentTask.getId());		
		}
		//also delete related sub task
		taskService.deleteTasks(childTaskIdArray, true);			
		childTasks=taskService.getSubTasks(getStepId());
		if(childTasks.size()==0){
			return true;
		}else{
			return false;
		}		
	}

	@Override
	public List<ProcessStep> getChildProcessSteps() {
		List<ProcessStep> childProcessStepList =new ArrayList<ProcessStep>();				
		HistoryService historyService=this.processEngine.getHistoryService();			
		List<HistoricTaskInstance> childHistoricTaskList= historyService.createHistoricTaskInstanceQuery().taskParentTaskId(this.stepId).orderByTaskCreateTime().asc().list();		
		for(HistoricTaskInstance currentHistoricTaskInstance:childHistoricTaskList){
			String stepName=currentHistoricTaskInstance.getName();						
			String childProcessStepId=currentHistoricTaskInstance.getId();				
			String stepAssignee=currentHistoricTaskInstance.getAssignee(); 			
			Date startTime=currentHistoricTaskInstance.getStartTime();
			Date endTime=currentHistoricTaskInstance.getEndTime();			
			String description=currentHistoricTaskInstance.getDescription(); 
			String parentStepId=currentHistoricTaskInstance.getId();
			String owner=currentHistoricTaskInstance.getOwner();
			Date dueDate=currentHistoricTaskInstance.getDueDate();				
			ActivitiProcessStepImpl childProcessStep=new ActivitiProcessStepImpl(stepName,getStepDefinitionKey(),childProcessStepId,getProcessObjectId(),getProcessDefinitionId(),startTime);			
			if(parentStepId!=null){
				childProcessStep.setParentStepId(parentStepId);
			}			
			if(stepAssignee!=null){
				childProcessStep.setStepAssignee(stepAssignee);
			}			
			if(description!=null){
				childProcessStep.setStepDescription(description);
			}			
			if(owner!=null){
				childProcessStep.setStepOwner(owner);
			}			
			if(dueDate!=null){
				childProcessStep.setDueDate(dueDate);
			}	
			if(endTime!=null){
				childProcessStep.setEndTime(endTime);
			}				
			childProcessStepList.add(childProcessStep);		
		}		
		return childProcessStepList;
	}

	@Override
	public boolean isAllChildProcessStepsFinished() {
		long allChildTasksNumber=this.processEngine.getHistoryService().createHistoricTaskInstanceQuery().taskParentTaskId(getStepId()).count();
		if(allChildTasksNumber==0){
			return false;
		}		
		long finihedChildTasksNumber=this.processEngine.getHistoryService().createHistoricTaskInstanceQuery().taskParentTaskId(getStepId()).finished().count();
		if(finihedChildTasksNumber==allChildTasksNumber){
			return true;
		}else{
			return false;
		}		
	}

	@Override
	public boolean hasParentStep() {
		if(this.getParentStepId()!=null){
			return true;
		}else{
			return false;
		}		
	}

	@Override
	public boolean hasChildStep() {
		long allChildTasksNumber=this.processEngine.getHistoryService().createHistoricTaskInstanceQuery().taskParentTaskId(getStepId()).count();
		if(allChildTasksNumber==0){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public Date getEndTime() {		
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Override
	public ProcessStep getParentProcessStep() throws ProcessRepositoryRuntimeException{
		if(hasParentStep()){			
			HistoryService historyService=this.processEngine.getHistoryService();			
			HistoricTaskInstance parentHistoricTask= historyService.createHistoricTaskInstanceQuery().taskId(this.getParentStepId()).singleResult();			
			String stepName=parentHistoricTask.getName();						
			String parentProcessStepId=parentHistoricTask.getId();				
			String stepAssignee=parentHistoricTask.getAssignee(); 			
			Date startTime=parentHistoricTask.getStartTime();
			Date endTime=parentHistoricTask.getEndTime();			
			String description=parentHistoricTask.getDescription(); 
			String parentStepId=parentHistoricTask.getParentTaskId();
			String owner=parentHistoricTask.getOwner();
			Date dueDate=parentHistoricTask.getDueDate();	
			
			ActivitiProcessStepImpl parentProcessStep=new ActivitiProcessStepImpl(stepName,getStepDefinitionKey(),parentProcessStepId,getProcessObjectId(),getProcessDefinitionId(),startTime);			
			if(parentStepId!=null){
				parentProcessStep.setParentStepId(parentStepId);
			}			
			if(stepAssignee!=null){
				parentProcessStep.setStepAssignee(stepAssignee);
			}			
			if(description!=null){
				parentProcessStep.setStepDescription(description);
			}			
			if(owner!=null){
				parentProcessStep.setStepOwner(owner);
			}			
			if(dueDate!=null){
				parentProcessStep.setDueDate(dueDate);
			}	
			if(endTime!=null){
				parentProcessStep.setEndTime(endTime);
			}				
			return parentProcessStep;			
		}else{
			throw new ProcessRepositoryRuntimeException();
		}		
	}	
}