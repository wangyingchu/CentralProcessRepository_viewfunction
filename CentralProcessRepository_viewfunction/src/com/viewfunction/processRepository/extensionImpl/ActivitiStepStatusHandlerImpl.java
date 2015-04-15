package com.viewfunction.processRepository.extensionImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.task.IdentityLink;

import com.viewfunction.processRepository.extension.StepStatusHandler;

public class ActivitiStepStatusHandlerImpl implements StepStatusHandler {
	
	private DelegateTask delegateTask;
	
	public ActivitiStepStatusHandlerImpl(DelegateTask delegateTask){
		this.delegateTask=delegateTask;
		//delegateTask.getDelegationState();
	}

	@Override
	public String getStepAssignee() {
		return this.delegateTask.getAssignee();
	}

	@Override
	public void setStepAssignee(String assignee) {
		this.delegateTask.setAssignee(assignee);
	}

	@Override
	public String getStepDescription() {
		return this.delegateTask.getDescription();
	}

	@Override
	public void setStepDescription(String description) {
		this.delegateTask.setDescription(description);
	}

	@Override
	public String getStepName() {
		return this.delegateTask.getName();
	}

	@Override
	public void setStepName(String name) {
		this.delegateTask.setName(name);
	}

	@Override
	public String getStepOwner() {
		return this.delegateTask.getOwner();
	}

	@Override
	public void setStepOwner(String owner) {
		this.delegateTask.setOwner(owner);
	}

	@Override
	public Date getDueDate() {
		return this.delegateTask.getDueDate();
	}

	@Override
	public void setDueDate(Date dueDate) {
		this.delegateTask.setDueDate(dueDate);
	}

	@Override
	public String getStepDefinitionKey() {
		return this.delegateTask.getTaskDefinitionKey();
	}

	@Override
	public Date getCreateTime() {
		return this.delegateTask.getCreateTime();
	}

	@Override
	public String getStepId() {	
		return this.delegateTask.getId();
	}

	@Override
	public int getStepPriority() {		
		return this.delegateTask.getPriority();
	}

	@Override
	public void setStepPriority(int priority) {
		this.delegateTask.setPriority(priority);
	}

	@Override
	public List<String> getStepCandidateRole() {
		List<String> stepCandidateRoleList=new ArrayList<String>();
		Set<IdentityLink> identityLink=this.delegateTask.getCandidates();		
		Iterator<IdentityLink> identityLinkIterator=identityLink.iterator();		
		while (identityLinkIterator.hasNext()){
			IdentityLink cure=identityLinkIterator.next();			
			String groupId=cure.getGroupId();//if getGroupId() is a none null value means this is a role
			if(groupId!=null){
				stepCandidateRoleList.add(groupId);				
			}			
		}		
		return stepCandidateRoleList;
	}

	@Override
	public List<String> getStepCandidateParticipant() {
		List<String> stepCandidateParticipantList=new ArrayList<String>();
		Set<IdentityLink> identityLink=this.delegateTask.getCandidates();		
		Iterator<IdentityLink> identityLinkIterator=identityLink.iterator();		
		while (identityLinkIterator.hasNext()){
			IdentityLink cure=identityLinkIterator.next();
			String userId=cure.getUserId(); //if getUserId() is a none null value means this is a participant
			if(userId!=null){
				stepCandidateParticipantList.add(userId);				
			}			
		}		
		return stepCandidateParticipantList;
	}

	@Override
	public void addStepCandidateRole(String role) {
		this.delegateTask.addCandidateGroup(role);		
	}

	@Override
	public void addStepCandidateParticipant(String participant) {
		this.delegateTask.addCandidateUser(participant);		
	}

	@Override
	public void deleteStepCandidateRole(String role) {
		this.delegateTask.deleteCandidateGroup(role);		
	}

	@Override
	public void deleteStepCandidateParticipant(String participant) {
		this.delegateTask.deleteCandidateUser(participant);		
	}

	@Override
	public void addStepCandidateRoles(List<String> roleList) {
		this.delegateTask.addCandidateGroups(roleList);		
	}

	@Override
	public void addStepCandidateParticipants(List<String> participantList) {
		this.delegateTask.addCandidateUsers(participantList);		
	}

	@Override
	public List<String> getProcessVariableNames() {
		List<String> variableNamesList=new ArrayList<String>();		
		Set<String> variableNamesSet=this.delegateTask.getVariableNames();
		Iterator<String> variableIterator=variableNamesSet.iterator();		
		while (variableIterator.hasNext()){
			String variableName=variableIterator.next();
			variableNamesList.add(variableName);	
		}				
		return variableNamesList;
	}

	@Override
	public Map<String, Object> getAllProcessVariables() {		
		return this.delegateTask.getVariables();
	}

	@Override
	public Map<String, Object> getProcessVariables(List<String> variableNamesList) {
		return this.delegateTask.getVariables(variableNamesList);
	}

	@Override
	public Object getProcessVariable(String variableName) {		
		return this.delegateTask.getVariable(variableName);
	}

	@Override
	public void removeProcessVariable(String variableName) {
		this.delegateTask.removeVariable(variableName);		
	}

	@Override
	public void removeProcessVariables(List<String> variableNamesList) {
		this.delegateTask.removeVariables(variableNamesList);		
	}

	@Override
	public void removeAllProcessVariable() {
		this.delegateTask.removeVariables();		
	}

	@Override
	public void addProcessVariable(String variableName, Object variableValue) {
		this.delegateTask.setVariable(variableName, variableValue);		
	}

	@Override
	public void addProcessVariables(Map<String, Object> variables) {
		this.delegateTask.setVariables(variables);		
	}

	@Override
	public boolean hasProcessVariables() {		
		return this.delegateTask.hasVariables();
	}

	@Override
	public boolean hasProcessVariable(String variableName) {		
		return this.delegateTask.hasVariable(variableName);
	}
}
