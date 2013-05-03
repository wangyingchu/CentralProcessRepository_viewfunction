package com.viewfunction.processRepository.processBureauImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Comment;
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
		TaskService taskService = this.processEngine.getTaskService();		
		Task currentTask=taskService.createTaskQuery().taskId(this.stepId).singleResult();
		if(currentTask==null){
			throw new ProcessRepositoryRuntimeException();			
		}
		taskService.setAssignee(this.stepId, null);	
		return true;
	}

	@Override
	public boolean reassignCurrentStep(String newUserId) throws ProcessRepositoryRuntimeException {
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
}