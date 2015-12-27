package com.viewfunction.processRepository.util.factory;

import java.util.Date;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;

import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.processBureau.HistoricProcessStep;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessSpace;
import com.viewfunction.processRepository.processBureau.ProcessStep;
import com.viewfunction.processRepository.processBureauImpl.ActivitiHistoricProcessStepImpl;
import com.viewfunction.processRepository.processBureauImpl.ActivitiProcessObjectImpl;
import com.viewfunction.processRepository.processBureauImpl.ActivitiProcessSpaceImpl;
import com.viewfunction.processRepository.processBureauImpl.ActivitiProcessStepImpl;
import com.viewfunction.processRepository.util.PerportyHandler;
import com.viewfunction.processRepository.util.RuntimeEnvironmentHandler;

public class ProcessComponentFactory {
	
	private static ProcessEngineConfiguration  _ProcessEngineConfiguration;	
	
	public static ProcessSpace connectProcessSpace(String processSpaceName)throws ProcessRepositoryRuntimeException{
		ProcessEngine processEngine=ProcessEngines.getProcessEngine(processSpaceName);
		if(processEngine!=null){
			return new ActivitiProcessSpaceImpl(processSpaceName,processEngine);
		}		
		if(_ProcessEngineConfiguration==null){			
			try {
				String dataPersistenceType=PerportyHandler.getPerportyValue(PerportyHandler.DATA_PERSISTENCE_TYPE).trim();
				if(dataPersistenceType.equals(PerportyHandler.DATA_PERSISTENCE_TYPE_DATABASE)){					
					_ProcessEngineConfiguration=ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
					_ProcessEngineConfiguration.setDatabaseSchemaUpdate(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_databaseSchemaUpdate).trim());					
					_ProcessEngineConfiguration.setJdbcDriver(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcDriver).trim());
					_ProcessEngineConfiguration.setJdbcUrl(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcUrl).trim());					
					_ProcessEngineConfiguration.setJdbcUsername(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcUsername).trim());
					if(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcPassword).trim().equals("NA")){
						_ProcessEngineConfiguration.setJdbcPassword("");						
					}else{
						_ProcessEngineConfiguration.setJdbcPassword(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcPassword).trim());
					}					
				}else if(dataPersistenceType.equals(PerportyHandler.DATA_PERSISTENCE_TYPE_INMEMERY)){
					_ProcessEngineConfiguration=ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();					
				}else if(dataPersistenceType.equals(PerportyHandler.DATA_PERSISTENCE_TYPE_INFILEDB)){					
					String fileDBLocation=RuntimeEnvironmentHandler.getApplicationRootPath()+"processRepository";
					String jdbcURL="jdbc:derby:"+fileDBLocation+";create=true";						
					_ProcessEngineConfiguration=ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
					_ProcessEngineConfiguration.setDatabaseSchemaUpdate(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_databaseSchemaUpdate).trim());
					_ProcessEngineConfiguration.setJdbcDriver("org.apache.derby.jdbc.EmbeddedDriver");
					_ProcessEngineConfiguration.setJdbcUrl(jdbcURL);//"jdbc:derby:{fullpath}processRepository;create=true"
					_ProcessEngineConfiguration.setJdbcUsername("cprdb");
					_ProcessEngineConfiguration.setJdbcPassword("wyc");							
				}
				else{					
					throw new ProcessRepositoryRuntimeException();
				}				
				_ProcessEngineConfiguration.setJobExecutorActivate(Boolean.parseBoolean(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jobExecutorActivate).trim()));					
				_ProcessEngineConfiguration.setAsyncExecutorActivate(Boolean.parseBoolean(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_asyncExecutorActivate).trim()));
				_ProcessEngineConfiguration.setAsyncExecutorEnabled(Boolean.parseBoolean(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_asyncExecutorEnabled).trim()));				
			} catch (ProcessRepositoryRuntimeException e) {				
				e.printStackTrace();
				throw new ProcessRepositoryRuntimeException();
			}						
		}		
		processEngine=_ProcessEngineConfiguration.setProcessEngineName(processSpaceName).buildProcessEngine();	
		ProcessEngines.registerProcessEngine(processEngine);	
		ProcessSpace _ProcessSpace=new ActivitiProcessSpaceImpl(processSpaceName,processEngine);	
		return _ProcessSpace;
	}
	
	public static ProcessObject createProcessObject(String processObjectId,String processDefinitionId,boolean isFinished){
		return new ActivitiProcessObjectImpl(processObjectId,processDefinitionId,isFinished);
	}
	
	public static ProcessStep createProcessStep(String stepName,String stepDefinitionKey,String stepId,String processObjectId,String processDefinitionId,Date createTime){
		return new ActivitiProcessStepImpl(stepName,stepDefinitionKey,stepId,processObjectId,processDefinitionId,createTime);		
	}
	
	public static HistoricProcessStep createHistoricProcessStep(String stepName,String stepDefinitionKey,String stepId,String processObjectId,String processDefinitionId,String stepAssignee, 
			Date startTime,Date endTime,Long durationInMillis,String description, String parentStepId,String owner,Date dueDate){
		return new ActivitiHistoricProcessStepImpl(stepName,stepDefinitionKey,stepId,processObjectId,processDefinitionId,stepAssignee,startTime,endTime,durationInMillis,description,parentStepId,owner,dueDate);		
	}	
}