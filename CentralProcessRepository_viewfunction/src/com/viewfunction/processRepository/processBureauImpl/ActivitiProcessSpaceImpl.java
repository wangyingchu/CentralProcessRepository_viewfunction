package com.viewfunction.processRepository.processBureauImpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.activiti.engine.delegate.event.ActivitiEventType;
import com.viewfunction.processRepository.exception.ProcessRepositoryDeploymentException;
import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.extension.ProcessSpaceEventListener;
import com.viewfunction.processRepository.extension.ProcessSpaceEventType;
import com.viewfunction.processRepository.extensionImpl.ActivitiProcessSpaceEventListenerImpl;
import com.viewfunction.processRepository.processBureau.HistoricProcessStep;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessSpace;
import com.viewfunction.processRepository.processBureau.ProcessStep;
import com.viewfunction.processRepository.processBureau.ProcessStepDefinition;
import com.viewfunction.processRepository.util.PerportyHandler;
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;

public class ActivitiProcessSpaceImpl implements ProcessSpace{
	
	private String processSpaceName;
	private ProcessEngine processEngine;
	public ActivitiProcessSpaceImpl(String processSpaceName,ProcessEngine processEngine){
		this.processEngine=processEngine;
		this.processSpaceName=processSpaceName;
	}

	@Override
	public String getProcessSpaceName() {		
		return processSpaceName;
	}

	@Override
	public boolean addProcessDefinition(String classPathProcessDefinationFile) throws ProcessRepositoryDeploymentException {
		RepositoryService repositoryService = this.processEngine.getRepositoryService();
		String _DeploymentId=null;
		try{
			Deployment _Deployment=repositoryService.createDeployment().addClasspathResource(classPathProcessDefinationFile).tenantId(this.getProcessSpaceName()).deploy();
			_DeploymentId=_Deployment.getId();
		}catch(ActivitiException e){
			e.printStackTrace();
			throw new ProcessRepositoryDeploymentException();			
		}		
		if(_DeploymentId!=null){
			return true;
		}else{
			return false;
		}				
	}
	
	@Override
	public boolean addProcessDefinition(String fileName,InputStream inputStream)throws ProcessRepositoryDeploymentException {
		if(!fileName.endsWith(".bpmn20.xml")&!fileName.endsWith(".bpmn")){
			throw new ProcessRepositoryDeploymentException();
		}
		RepositoryService repositoryService = this.processEngine.getRepositoryService();		
		String _DeploymentId=null;
		try{
			Deployment _Deployment=repositoryService.createDeployment().addInputStream(fileName,inputStream).tenantId(this.getProcessSpaceName()).deploy();
			_DeploymentId=_Deployment.getId();			
		}catch(ActivitiException e){
			e.printStackTrace();
			throw new ProcessRepositoryDeploymentException();			
		}		
		if(_DeploymentId!=null){
			return true;
		}else{
			return false;
		}				
	}

	@Override
	public ProcessObject launchProcess(String processType,String startUserId) throws ProcessRepositoryDeploymentException {		
		try{
			IdentityService identityService=null;
			if(startUserId!=null){
				identityService=this.processEngine.getIdentityService();			
				identityService.setAuthenticatedUserId(startUserId);
			}			
			RuntimeService runtimeService = this.processEngine.getRuntimeService();
			ProcessInstance _ProcessInstance=runtimeService.startProcessInstanceByKeyAndTenantId(processType, this.getProcessSpaceName());
			ProcessObject _ProcessObject=ProcessComponentFactory.createProcessObject(_ProcessInstance.getProcessInstanceId(),_ProcessInstance.getProcessDefinitionId(),_ProcessInstance.isEnded());	
			((ActivitiProcessObjectImpl)_ProcessObject).setProcessEngine(this.processEngine);
			if(startUserId!=null){
				identityService.setAuthenticatedUserId(null);
			}			
			return _ProcessObject;
		}catch(ActivitiException e){
			e.printStackTrace();
				throw new ProcessRepositoryDeploymentException();			
		}		 
	}
	
	@Override
	public ProcessObject launchProcess(String processType,Map<String,Object> processVariables,String startUserId) throws ProcessRepositoryDeploymentException {		
		try{
			IdentityService identityService=null;
			if(startUserId!=null){
				identityService=this.processEngine.getIdentityService();			
				identityService.setAuthenticatedUserId(startUserId);
			}
			RuntimeService runtimeService = this.processEngine.getRuntimeService();
			ProcessInstance _ProcessInstance= runtimeService.startProcessInstanceByKeyAndTenantId(processType,processVariables, this.getProcessSpaceName());
			ProcessObject _ProcessObject=ProcessComponentFactory.createProcessObject(
					 _ProcessInstance.getProcessInstanceId(),_ProcessInstance.getProcessDefinitionId(),_ProcessInstance.isEnded());	
			((ActivitiProcessObjectImpl)_ProcessObject).setProcessEngine(this.processEngine);
			if(startUserId!=null){
				identityService.setAuthenticatedUserId(null);
			}
			return _ProcessObject;
		}catch(ActivitiException e){
			e.printStackTrace();
				throw new ProcessRepositoryDeploymentException();			
		}		 
	}	
	
	@Override
	public ProcessObject getProcessObjectById(String processObjectId) throws ProcessRepositoryRuntimeException {
		ProcessObject _ProcessObject=null;
		HistoryService historyService = processEngine.getHistoryService();
		HistoricProcessInstanceQuery historicProcessInstanceQuery=historyService.createHistoricProcessInstanceQuery(); 
		HistoricProcessInstance historicProcessInstance=historicProcessInstanceQuery.processInstanceId(processObjectId).processInstanceTenantId(this.getProcessSpaceName()).singleResult();
		if(historicProcessInstance==null){
			return null;
		}		
		boolean hasFinished=historicProcessInstance.getEndTime()==null?false:true;		
		_ProcessObject=ProcessComponentFactory.createProcessObject(historicProcessInstance.getId(),historicProcessInstance.getProcessDefinitionId(),hasFinished);
		((ActivitiProcessObjectImpl)_ProcessObject).setProcessEngine(this.processEngine);		
		((ActivitiProcessObjectImpl)_ProcessObject).setProcessStartTime(historicProcessInstance.getStartTime());
		((ActivitiProcessObjectImpl)_ProcessObject).setProcessEndTime(historicProcessInstance.getEndTime());
		((ActivitiProcessObjectImpl)_ProcessObject).setProcessDurationInMillis(historicProcessInstance.getDurationInMillis());
		((ActivitiProcessObjectImpl)_ProcessObject).setProcessStartUserId(historicProcessInstance.getStartUserId());		
		return _ProcessObject;
	}	
	
	@Override
	public List<ProcessObject> getProcessObjectsByProcessType(String processType,int processStatus) throws ProcessRepositoryRuntimeException {		
		List<ProcessDefinition> _ProcessDefinitionList =this.processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(processType).processDefinitionTenantId(this.getProcessSpaceName()).list();
		if(_ProcessDefinitionList.size()==0){
			throw new ProcessRepositoryRuntimeException();
		}			
		HistoryService historyService = this.processEngine.getHistoryService();
		HistoricProcessInstanceQuery historicProcessInstanceQuery=historyService.createHistoricProcessInstanceQuery(); 
		List<HistoricProcessInstance> historicProcessInstanceList;
		if(processStatus==ProcessSpace.PROCESS_STATUS_FINISHED){
			historicProcessInstanceList=historicProcessInstanceQuery.processDefinitionKey(processType).processInstanceTenantId(this.getProcessSpaceName()).finished().orderByProcessInstanceId().asc().list();
		}else if(processStatus==ProcessSpace.PROCESS_STATUS_UNFINISHED){
			historicProcessInstanceList=historicProcessInstanceQuery.processDefinitionKey(processType).processInstanceTenantId(this.getProcessSpaceName()).unfinished().orderByProcessInstanceId().asc().list();
		}else if(processStatus==ProcessSpace.PROCESS_STATUS_ALL){
			historicProcessInstanceList=historicProcessInstanceQuery.processDefinitionKey(processType).processInstanceTenantId(this.getProcessSpaceName()).orderByProcessInstanceId().asc().list();
		}else{
			throw new ProcessRepositoryRuntimeException();
		}		
		ArrayList<ProcessObject> _ProcessObjectList=new ArrayList<ProcessObject>();
		for(HistoricProcessInstance historicProcessInstance:historicProcessInstanceList){
			boolean hasFinished=historicProcessInstance.getEndTime()==null?false:true;		
			ProcessObject _ProcessObject=ProcessComponentFactory.createProcessObject(historicProcessInstance.getId(),historicProcessInstance.getProcessDefinitionId(),hasFinished);
			((ActivitiProcessObjectImpl)_ProcessObject).setProcessEngine(this.processEngine);		
			((ActivitiProcessObjectImpl)_ProcessObject).setProcessStartTime(historicProcessInstance.getStartTime());
			((ActivitiProcessObjectImpl)_ProcessObject).setProcessEndTime(historicProcessInstance.getEndTime());
			((ActivitiProcessObjectImpl)_ProcessObject).setProcessDurationInMillis(historicProcessInstance.getDurationInMillis());
			((ActivitiProcessObjectImpl)_ProcessObject).setProcessStartUserId(historicProcessInstance.getStartUserId());	
			_ProcessObjectList.add(_ProcessObject);
		}
		return _ProcessObjectList;
	}

	@Override
	public List<ProcessStep> getProcessStepsByRole(String roleName) {
		TaskService taskService=this.processEngine.getTaskService();
		List<Task> taskList=taskService.createTaskQuery().taskCandidateGroup(roleName).taskTenantId(this.getProcessSpaceName()).orderByTaskCreateTime().asc().list();		
		return createProcessStepList(taskList);
	}

	@Override
	public List<ProcessStep> getProcessStepsByParticipant(String participantName) {
		TaskService taskService=this.processEngine.getTaskService();
		List<Task> taskList=taskService.createTaskQuery().taskAssignee(participantName).taskTenantId(this.getProcessSpaceName()).orderByTaskCreateTime().asc().list();		
		return createProcessStepList(taskList);
	}
	
	private List<ProcessStep> createProcessStepList(List<Task> taskList){
		ArrayList<ProcessStep> processStepList=new ArrayList<ProcessStep>();
		for(Task currentTask:taskList){	
			String stepDefinitionKey=null;			
			String processObjectId=null;
			String processDefinitionId=null;			
			if(currentTask.getParentTaskId()!=null){
				//should use parent task's process info to build process data				
				HistoryService historyService=this.processEngine.getHistoryService();				
				HistoricTaskInstance parentTask=historyService.createHistoricTaskInstanceQuery().taskId(currentTask.getParentTaskId()).taskTenantId(this.getProcessSpaceName()).singleResult();				
				stepDefinitionKey=parentTask.getTaskDefinitionKey();
				processObjectId=parentTask.getProcessInstanceId();
				processDefinitionId=parentTask.getProcessDefinitionId();				
			}else{
				stepDefinitionKey=currentTask.getTaskDefinitionKey();
				processObjectId=currentTask.getProcessInstanceId();
				processDefinitionId=currentTask.getProcessDefinitionId();				
			}			
			ProcessStep currentStep=ProcessComponentFactory.createProcessStep(currentTask.getName(),stepDefinitionKey, currentTask.getId(),processObjectId,processDefinitionId, currentTask.getCreateTime());
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
	public String getProcessNameByDefinitionId(String processDefinId) {
		RepositoryService repositoryService = this.processEngine.getRepositoryService();
		ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinId).processDefinitionTenantId(this.getProcessSpaceName()).singleResult();		
		if(processDefinition!=null){
			return processDefinition.getKey();
		}else{
			return null;
		}
	}

	@Override
	public List<ProcessObject> getProcessObjectsByStartUserId(String startUserId, int processStatus) throws ProcessRepositoryRuntimeException {
		HistoryService historyService = this.processEngine.getHistoryService();
		HistoricProcessInstanceQuery historicProcessInstanceQuery=historyService.createHistoricProcessInstanceQuery(); 
		List<HistoricProcessInstance> historicProcessInstanceList;
		if(processStatus==ProcessSpace.PROCESS_STATUS_FINISHED){
			historicProcessInstanceList=historicProcessInstanceQuery.startedBy(startUserId).processInstanceTenantId(this.getProcessSpaceName()).finished().orderByProcessInstanceId().asc().list();
		}else if(processStatus==ProcessSpace.PROCESS_STATUS_UNFINISHED){
			historicProcessInstanceList=historicProcessInstanceQuery.startedBy(startUserId).processInstanceTenantId(this.getProcessSpaceName()).unfinished().orderByProcessInstanceId().asc().list();
		}else if(processStatus==ProcessSpace.PROCESS_STATUS_ALL){
			historicProcessInstanceList=historicProcessInstanceQuery.startedBy(startUserId).processInstanceTenantId(this.getProcessSpaceName()).orderByProcessInstanceId().asc().list();
		}else{
			throw new ProcessRepositoryRuntimeException();
		}		
		ArrayList<ProcessObject> _ProcessObjectList=new ArrayList<ProcessObject>();
		for(HistoricProcessInstance historicProcessInstance:historicProcessInstanceList){
			boolean hasFinished=historicProcessInstance.getEndTime()==null?false:true;		
			ProcessObject _ProcessObject=ProcessComponentFactory.createProcessObject(historicProcessInstance.getId(),historicProcessInstance.getProcessDefinitionId(),hasFinished);
			((ActivitiProcessObjectImpl)_ProcessObject).setProcessEngine(this.processEngine);		
			((ActivitiProcessObjectImpl)_ProcessObject).setProcessStartTime(historicProcessInstance.getStartTime());
			((ActivitiProcessObjectImpl)_ProcessObject).setProcessEndTime(historicProcessInstance.getEndTime());
			((ActivitiProcessObjectImpl)_ProcessObject).setProcessDurationInMillis(historicProcessInstance.getDurationInMillis());
			((ActivitiProcessObjectImpl)_ProcessObject).setProcessStartUserId(historicProcessInstance.getStartUserId());	
			_ProcessObjectList.add(_ProcessObject);
		}
		return _ProcessObjectList;
	}

	@Override
	public List<HistoricProcessStep> getHistoricProcessStepByInvolvedUserId(String involvedUserId, int processStatus) throws ProcessRepositoryRuntimeException {		
		HistoryService historyService = processEngine.getHistoryService();
		HistoricTaskInstanceQuery historicTaskInstanceQuery= historyService.createHistoricTaskInstanceQuery(); 		
		List<HistoricTaskInstance> historicTaskInstanceList=null;		
		if(processStatus==ProcessSpace.PROCESS_STATUS_FINISHED){
			historicTaskInstanceList =historicTaskInstanceQuery.taskAssignee(involvedUserId).taskTenantId(this.getProcessSpaceName()).finished().orderByProcessInstanceId().asc().list();
		}else if(processStatus==ProcessSpace.PROCESS_STATUS_UNFINISHED){
			historicTaskInstanceList =historicTaskInstanceQuery.taskAssignee(involvedUserId).taskTenantId(this.getProcessSpaceName()).unfinished().orderByProcessInstanceId().asc().list();
		}else if(processStatus==ProcessSpace.PROCESS_STATUS_ALL){
			historicTaskInstanceList =historicTaskInstanceQuery.taskAssignee(involvedUserId).taskTenantId(this.getProcessSpaceName()).orderByProcessInstanceId().asc().list();
		}else{
			throw new ProcessRepositoryRuntimeException();
		}		
		List<HistoricProcessStep> _HistoricProcessStepList=new ArrayList<HistoricProcessStep>();
		for(HistoricTaskInstance _HistoricTaskInstance:historicTaskInstanceList){			
			String parentTaskId=_HistoricTaskInstance.getParentTaskId();
			if(parentTaskId!=null){
				HistoricTaskInstance parentHistoricTaskInstance=historyService.createHistoricTaskInstanceQuery().taskId(parentTaskId).singleResult();				
				HistoricProcessStep currentHistoricProcessStep=ProcessComponentFactory.createHistoricProcessStep(
						_HistoricTaskInstance.getName(),parentHistoricTaskInstance.getTaskDefinitionKey(),_HistoricTaskInstance.getId(),
						parentHistoricTaskInstance.getProcessInstanceId(),parentHistoricTaskInstance.getProcessDefinitionId(),
						_HistoricTaskInstance.getAssignee(),_HistoricTaskInstance.getStartTime(),_HistoricTaskInstance.getEndTime(),
						_HistoricTaskInstance.getDurationInMillis(),_HistoricTaskInstance.getDescription(),_HistoricTaskInstance.getParentTaskId(),
						_HistoricTaskInstance.getOwner(),_HistoricTaskInstance.getDueDate());				
				((ActivitiHistoricProcessStepImpl)currentHistoricProcessStep).setProcessEngine(processEngine);				
				_HistoricProcessStepList.add(currentHistoricProcessStep);							
			}else{
				HistoricProcessStep currentHistoricProcessStep=ProcessComponentFactory.createHistoricProcessStep(
						_HistoricTaskInstance.getName(),_HistoricTaskInstance.getTaskDefinitionKey(),_HistoricTaskInstance.getId(),
						_HistoricTaskInstance.getProcessInstanceId(),_HistoricTaskInstance.getProcessDefinitionId(),
						_HistoricTaskInstance.getAssignee(),_HistoricTaskInstance.getStartTime(),_HistoricTaskInstance.getEndTime(),
						_HistoricTaskInstance.getDurationInMillis(),_HistoricTaskInstance.getDescription(),_HistoricTaskInstance.getParentTaskId(),
						_HistoricTaskInstance.getOwner(),_HistoricTaskInstance.getDueDate());		
				((ActivitiHistoricProcessStepImpl)currentHistoricProcessStep).setProcessEngine(processEngine);
				_HistoricProcessStepList.add(currentHistoricProcessStep);							
			}			
		}		
		return _HistoricProcessStepList;
	}

	@Override
	public boolean deleteProcessByProcessObjectId(String processObjectId,String deleteReason) throws ProcessRepositoryRuntimeException{
		HistoryService historyService = processEngine.getHistoryService();
		HistoricProcessInstanceQuery historicProcessInstanceQuery=historyService.createHistoricProcessInstanceQuery(); 
		HistoricProcessInstance historicProcessInstance=historicProcessInstanceQuery.processInstanceId(processObjectId).processInstanceTenantId(this.getProcessSpaceName()).singleResult();
		if(historicProcessInstance==null){
			throw new ProcessRepositoryRuntimeException();
		}		
		RuntimeService runtimeService = this.processEngine.getRuntimeService();
		runtimeService.deleteProcessInstance(processObjectId, deleteReason);
		return true;
	}
	
	@Override
	public boolean suspendProcessByProcessObjectId(String processObjectId) throws ProcessRepositoryRuntimeException{
		RuntimeService runtimeService = this.processEngine.getRuntimeService();
		ProcessInstanceQuery processInstanceQuery=runtimeService.createProcessInstanceQuery();
		ProcessInstance processInstance=processInstanceQuery.processInstanceId(processObjectId).processInstanceTenantId(this.getProcessSpaceName()).singleResult();
		if(processInstance==null){
			throw new ProcessRepositoryRuntimeException();
		}
		if(processInstance.isEnded()){
			//Can only suspend not finished process
			return false;
		}else{
			if(processInstance.isSuspended()){
				return true;
			}else{
				runtimeService.suspendProcessInstanceById(processObjectId);
				return true;
			}
		}
	}
	
	@Override
	public boolean activateProcessByProcessObjectId(String processObjectId) throws ProcessRepositoryRuntimeException{
		RuntimeService runtimeService = this.processEngine.getRuntimeService();
		ProcessInstanceQuery processInstanceQuery=runtimeService.createProcessInstanceQuery();
		ProcessInstance processInstance=processInstanceQuery.processInstanceId(processObjectId).processInstanceTenantId(this.getProcessSpaceName()).singleResult();
		if(processInstance==null){
			throw new ProcessRepositoryRuntimeException();
		}
		if(processInstance.isEnded()){
			//Can only activate not finished process
			return false;
		}else{
			if(!processInstance.isSuspended()){
				return true;
			}else{
				runtimeService.activateProcessInstanceById(processObjectId);
				return true;
			}
		}
	}

	@Override
	public void registerProcessEventListener(ProcessSpaceEventType processEventType,ProcessSpaceEventListener processEventListener) {
		ActivitiProcessSpaceEventListenerImpl processEventListenerImpl=(ActivitiProcessSpaceEventListenerImpl)processEventListener;
		switch(processEventType){
			case PROCESSSTEP_ASSIGNED:
				this.processEngine.getRuntimeService().addEventListener(processEventListenerImpl, ActivitiEventType.TASK_ASSIGNED);
				break;
			case PROCESSSTEP_CREATED:
				this.processEngine.getRuntimeService().addEventListener(processEventListenerImpl, ActivitiEventType.TASK_CREATED);
				break;
			case PROCESSSTEP_COMPLETED:
				this.processEngine.getRuntimeService().addEventListener(processEventListenerImpl, ActivitiEventType.TASK_COMPLETED);
				break;
			case PROCESSINSTANCE_COMPLETED:
				this.processEngine.getRuntimeService().addEventListener(processEventListenerImpl, ActivitiEventType.PROCESS_COMPLETED);
				break;
			case PROCESSINSTANCE_CANCELLED:
				this.processEngine.getRuntimeService().addEventListener(processEventListenerImpl, ActivitiEventType.PROCESS_CANCELLED);
				break;
			case PROCESSEXTENSIONSTEP_SUCCESS:
				this.processEngine.getRuntimeService().addEventListener(processEventListenerImpl, ActivitiEventType.JOB_EXECUTION_SUCCESS);
				break;
			case PROCESSEXTENSIONSTEP_FAILURE:
				this.processEngine.getRuntimeService().addEventListener(processEventListenerImpl, ActivitiEventType.JOB_EXECUTION_FAILURE);
				break;
			case PROCESSEXTENSIONSTEP_RETRY_DECREMENTED:
				this.processEngine.getRuntimeService().addEventListener(processEventListenerImpl, ActivitiEventType.JOB_RETRIES_DECREMENTED);
				break;
			case PROCESSEXTENSIONSTEP_CANCELED:
				this.processEngine.getRuntimeService().addEventListener(processEventListenerImpl, ActivitiEventType.JOB_CANCELED);
				break;
		}	
	}

	@Override
	public List<ProcessStepDefinition> getProcessStepsInfoByDefinitionName(String processDefinitionKey) {
		List<ProcessStepDefinition> processStepDefinationList=new ArrayList<ProcessStepDefinition>();
		RepositoryService repositoryService = this.processEngine.getRepositoryService();
		ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).processDefinitionTenantId(this.getProcessSpaceName()).latestVersion().singleResult();
		String lastProcessDefinitionId=processDefinition.getId();
		BpmnModel processDefinitionBPMNModel=repositoryService.getBpmnModel(lastProcessDefinitionId);
		Collection<FlowElement> flowElementCollection=processDefinitionBPMNModel.getMainProcess().getFlowElements();
		Iterator<FlowElement> iterator=	flowElementCollection.iterator();
		while(iterator.hasNext()){
			FlowElement currentElement=iterator.next();
			String currentElementClassName=currentElement.getClass().getName();
			if(currentElementClassName.endsWith("UserTask")){
				ActivitiProcessStepDefinitionImpl currentProcessStepDefinition=new ActivitiProcessStepDefinitionImpl();
				currentProcessStepDefinition.setStepId(currentElement.getId());
				currentProcessStepDefinition.setStepName(currentElement.getName());
				currentProcessStepDefinition.setStepDescription(currentElement.getDocumentation());
				currentProcessStepDefinition.setStepType(ProcessStepDefinition.StepType_PeopleOperationStep);
				processStepDefinationList.add(currentProcessStepDefinition);
			}
		}
		return processStepDefinationList;
	}

	@Override
	public InputStream getProcessDefinitionFlowDiagram(String processDefinitionKey) {
		RepositoryService repositoryService = this.processEngine.getRepositoryService();
		ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).processDefinitionTenantId(this.getProcessSpaceName()).latestVersion().singleResult();
		String lastProcessDefinitionId=processDefinition.getId();
		BpmnModel processDefinitionBPMNModel=repositoryService.getBpmnModel(lastProcessDefinitionId);
		ProcessDiagramGenerator procDiaGenerator = new DefaultProcessDiagramGenerator(); 
		String diagramFont="宋体";
		try {
			diagramFont=PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_definitionFlowDiagramFont).trim();
		} catch (ProcessRepositoryRuntimeException e) {
			e.printStackTrace();
		}
		InputStream is = procDiaGenerator.generateDiagram(  
				processDefinitionBPMNModel, "png",new ArrayList<String>(),new ArrayList<String>(), diagramFont,diagramFont,null,1);  
		return is;
	}

	@Override
	public boolean updateProcessDefinition(String processDefinId,InputStream inputStream)throws ProcessRepositoryDeploymentException {
		RepositoryService repositoryService = this.processEngine.getRepositoryService();		
		String _DeploymentId=null;
		try{
			ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinId).processDefinitionTenantId(this.getProcessSpaceName()).latestVersion().singleResult();
			if(processDefinition==null){
				return false;
			}else{
				String definitionFileName=processDefinId+".bpmn20.xml";
				Deployment _Deployment=repositoryService.createDeployment().addInputStream(definitionFileName,inputStream).tenantId(this.getProcessSpaceName()).deploy();
				_DeploymentId=_Deployment.getId();
			}
		}catch(ActivitiException e){
			e.printStackTrace();
			throw new ProcessRepositoryDeploymentException();			
		}		
		if(_DeploymentId!=null){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public InputStream getProcessDefinitionFile(String processDefinitionKey) {
		RepositoryService repositoryService = this.processEngine.getRepositoryService();
		ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).processDefinitionTenantId(this.getProcessSpaceName()).latestVersion().singleResult();
		return repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
	}

	@Override
	public void closeProcessSpace() {
		this.processEngine.close();
	}

	@Override
	public List<ProcessStepDefinition> getProcessStepsInfoByDefinitionName(String processDefinitionKey,int processDefinitionVersion) {
		List<ProcessStepDefinition> processStepDefinationList=new ArrayList<ProcessStepDefinition>();
		RepositoryService repositoryService = this.processEngine.getRepositoryService();
		ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).processDefinitionTenantId(this.getProcessSpaceName())
				.processDefinitionVersion(processDefinitionVersion).singleResult();
		String lastProcessDefinitionId=processDefinition.getId();
		BpmnModel processDefinitionBPMNModel=repositoryService.getBpmnModel(lastProcessDefinitionId);
		Collection<FlowElement> flowElementCollection=processDefinitionBPMNModel.getMainProcess().getFlowElements();
		Iterator<FlowElement> iterator=	flowElementCollection.iterator();
		while(iterator.hasNext()){
			FlowElement currentElement=iterator.next();
			String currentElementClassName=currentElement.getClass().getName();
			if(currentElementClassName.endsWith("UserTask")){
				ActivitiProcessStepDefinitionImpl currentProcessStepDefinition=new ActivitiProcessStepDefinitionImpl();
				currentProcessStepDefinition.setStepId(currentElement.getId());
				currentProcessStepDefinition.setStepName(currentElement.getName());
				currentProcessStepDefinition.setStepDescription(currentElement.getDocumentation());
				currentProcessStepDefinition.setStepType(ProcessStepDefinition.StepType_PeopleOperationStep);
				processStepDefinationList.add(currentProcessStepDefinition);
			}
		}
		return processStepDefinationList;
	}

	@Override
	public InputStream getProcessDefinitionFlowDiagram(String processDefinitionKey, int processDefinitionVersion) {
		RepositoryService repositoryService = this.processEngine.getRepositoryService();
		ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).processDefinitionTenantId(this.getProcessSpaceName())
				.processDefinitionVersion(processDefinitionVersion).singleResult();
		String lastProcessDefinitionId=processDefinition.getId();
		BpmnModel processDefinitionBPMNModel=repositoryService.getBpmnModel(lastProcessDefinitionId);
		ProcessDiagramGenerator procDiaGenerator = new DefaultProcessDiagramGenerator(); 
		String diagramFont="宋体";
		try {
			diagramFont=PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_definitionFlowDiagramFont).trim();
		} catch (ProcessRepositoryRuntimeException e) {
			e.printStackTrace();
		}
		InputStream is = procDiaGenerator.generateDiagram(  
				processDefinitionBPMNModel, "png",new ArrayList<String>(),new ArrayList<String>(), diagramFont,diagramFont,null,1);  
		return is;
	}

	@Override
	public InputStream getProcessDefinitionFile(String processDefinitionKey, int processDefinitionVersion) {
		RepositoryService repositoryService = this.processEngine.getRepositoryService();
		ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).processDefinitionTenantId(this.getProcessSpaceName())
				.processDefinitionVersion(processDefinitionVersion).singleResult();
		return repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
	}	
}