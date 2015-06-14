package com.viewfunction.processRepository.processBureau;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.viewfunction.processRepository.exception.ProcessRepositoryDeploymentException;
import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.extension.ProcessSpaceEventListener;
import com.viewfunction.processRepository.extension.ProcessSpaceEventType;

public interface ProcessSpace {
	
	public int PROCESS_STATUS_FINISHED=0;
	public int PROCESS_STATUS_UNFINISHED=1;
	public int PROCESS_STATUS_ALL=2;
	
	public String getProcessSpaceName();
	public boolean addProcessDefinition(String classPathProcessDefinationFile) throws ProcessRepositoryDeploymentException;
	public boolean addProcessDefinition(String fileName, InputStream inputStream) throws ProcessRepositoryDeploymentException;
	
	public String getProcessNameByDefinitionId(String processDefinId);	
	
	public ProcessObject launchProcess(String processType,String startUserId) throws ProcessRepositoryDeploymentException;
	public ProcessObject launchProcess(String processType,Map<String,Object> processVariables,String startUserId) throws ProcessRepositoryDeploymentException;	
	public boolean deleteProcessByProcessObjectId(String processObjectId,String deleteReason);
	
	public ProcessObject getProcessObjectById(String processObjectId) throws ProcessRepositoryRuntimeException;
	public List<ProcessObject> getProcessObjectsByProcessType(String processType,int processStatus) throws ProcessRepositoryRuntimeException;
	
	public List<ProcessObject> getProcessObjectsByStartUserId(String startUserId,int processStatus) throws ProcessRepositoryRuntimeException;
	public List<HistoricProcessStep> getHistoricProcessStepByInvolvedUserId(String involvedUserId,int processStatus) throws ProcessRepositoryRuntimeException;
	
	public List<ProcessStep> getProcessStepsByRole(String roleName);
	public List<ProcessStep> getProcessStepsByParticipant(String participantName);
	
	public void registerProcessEventListener(ProcessSpaceEventType processEventType,ProcessSpaceEventListener processEventListener);
}