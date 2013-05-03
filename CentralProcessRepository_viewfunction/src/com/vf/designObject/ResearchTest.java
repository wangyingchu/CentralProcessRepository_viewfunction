package com.vf.designObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricDetailQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import com.viewfunction.processRepository.exception.ProcessRepositoryDeploymentException;
import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.processBureau.HistoricProcessStep;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessSpace;
import com.viewfunction.processRepository.processBureau.ProcessStep;
import com.viewfunction.processRepository.util.PerportyHandler;
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;

public class ResearchTest {
	private static final String _TEST_PROCESSSPACE_NAME = "BeaconManufactureLTD";

	/**
	 * @param args
	 * @throws ProcessRepositoryRuntimeException
	 */
	public static void main_1(String[] args)
			throws ProcessRepositoryRuntimeException {
		/*
		 * ProcessSpace _ProcessSpace; try { _ProcessSpace =
		 * ProcessComponentFactory.connectProcessSpace(_TEST_PROCESSSPACE_NAME);
		 * FileInputStream fileInputStream = new FileInputStream(new
		 * File("processDefine/DecisionProcess.bpmn20.xml")); boolean
		 * addResult=_ProcessSpace
		 * .addProcessDefinition("DecisionProcess.bpmn20.xml",fileInputStream);
		 * System.out.println(addResult); // ProcessObject
		 * _ProcessObject=_ProcessSpace.launchProcess("DecisionProcess");
		 * 
		 * 
		 * } catch (ProcessRepositoryRuntimeException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (FileNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (ProcessRepositoryDeploymentException e)
		 * { // TODO Auto-generated catch block e.printStackTrace(); }
		 */

		ProcessEngineConfiguration _ProcessEngineConfiguration;

		try {
			String dataPersistenceType = PerportyHandler.getPerportyValue(PerportyHandler.DATA_PERSISTENCE_TYPE).trim();
			if (dataPersistenceType	.equals(PerportyHandler.DATA_PERSISTENCE_TYPE_DATABASE)) {
				_ProcessEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
				_ProcessEngineConfiguration.setDatabaseSchemaUpdate(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_databaseSchemaUpdate)	.trim());
				_ProcessEngineConfiguration.setJdbcDriver(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcDriver).trim());
				_ProcessEngineConfiguration.setJdbcUrl(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcUrl).trim());
				_ProcessEngineConfiguration.setJdbcUsername(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcUsername).trim());
				if (PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcPassword).trim().equals("NA")) {
					_ProcessEngineConfiguration.setJdbcPassword("");
				} else {
					_ProcessEngineConfiguration.setJdbcPassword(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcPassword).trim());
				}
				_ProcessEngineConfiguration.setJobExecutorActivate(Boolean.parseBoolean(PerportyHandler.getPerportyValue(PerportyHandler.DATA_PERSISTENCE_TYPE).trim()));
			} else if (dataPersistenceType.equals(PerportyHandler.DATA_PERSISTENCE_TYPE_INMEMERY)) {
				_ProcessEngineConfiguration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
			} else {
				throw new ProcessRepositoryRuntimeException();
			}
		} catch (ProcessRepositoryRuntimeException e) {
			e.printStackTrace();
			throw new ProcessRepositoryRuntimeException();
		}

		ProcessEngine processEngine = _ProcessEngineConfiguration.setProcessEngineName(_TEST_PROCESSSPACE_NAME).buildProcessEngine();

		/*
		 * Map<String,Object> variables=new HashMap<String,Object>();
		 * 
		 * variables.put("input", "response1"); variables.put("input_3",
		 * "response1");
		 * 
		 * System.out.println(processEngine); RuntimeService runtimeService =
		 * processEngine.getRuntimeService(); //ProcessInstance
		 * _ProcessInstance=
		 * runtimeService.startProcessInstanceByKey("DecisionProcess"
		 * ,variables);
		 */

		// System.out.println(_ProcessInstance.getProcessInstanceId());
		/*
		 * Map<String,Object> mp=runtimeService.getVariables("1401"); //901 1001
		 * 1401 System.out.println(mp); TaskService taskService =
		 * processEngine.getTaskService(); Task
		 * currentTask=taskService.createTaskQuery
		 * ().processInstanceId("1401").singleResult();
		 * System.out.println(currentTask.getTaskDefinitionKey());
		 */
		// http://wiki.alfresco.com/wiki/Activiti_Workflows_with_decision_points

		HistoryService historyService = processEngine.getHistoryService();
		System.out.println(historyService);

		
		 HistoricActivityInstanceQuery historicActivityInstanceQuery= historyService.createHistoricActivityInstanceQuery(); 
		 //query finished step participent involoed; 
		 List<HistoricActivityInstance> historicActivityInstanceList =historicActivityInstanceQuery.taskAssignee("FinancialEmployeeA").finished().list();
		  for(HistoricActivityInstance historicActivityInstance:historicActivityInstanceList){
		  System.out.println("--------------------------");
		  System.out.println(historicActivityInstance.getProcessDefinitionId());
		 System.out.println(historicActivityInstance.getProcessInstanceId());
		  System.out.println(historicActivityInstance.getActivityName());
		  System.out.println(historicActivityInstance.getActivityId());
		  System.out.println(historicActivityInstance.getEndTime());
		  System.out.println(historicActivityInstance.getAssignee()); 
		  }
		 
		
		  HistoricProcessInstanceQuery
		  historicProcessInstanceQuery=historyService.createHistoricProcessInstanceQuery(); 
		  //query activity started by  participent 
		  List<HistoricProcessInstance> historicProcessInstanceList= historicProcessInstanceQuery.startedBy("QualityInspectionEmployeeA").orderByProcessInstanceStartTime().asc().list();
		  historicProcessInstanceList =historicProcessInstanceQuery.startedBy("QualityInspectionEmployeeA" ).orderByProcessInstanceStartTime().asc().list();
		  System.out.println("-----------query activity started by  participent---------------");
		  for(HistoricProcessInstance historicActivityInstance:historicProcessInstanceList){
			  
			  System.out.println("--------------------------");
			  System.out.println(historicActivityInstance .getProcessDefinitionId());
			  System.out.println(historicActivityInstance.getStartUserId());
			  System.out.println(historicActivityInstance.getStartTime());
			  System.out.println(historicActivityInstance.getEndTime());
			  System.out.println(historicActivityInstance.getStartActivityId());
			  System.out.println(historicActivityInstance.getDurationInMillis());
			  System.out.println(historicActivityInstance.getId()); 
			  System.out.println(historicActivityInstance.getEndActivityId()); 
			  
			 // System.out.println(historicActivityInstance.); 
			  
			
			  
			  
			  
		  }
		  System.out.println("-----------query activity started by  participent---------------");

		HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService
				.createHistoricTaskInstanceQuery();
		// List<HistoricTaskInstance>
		// historicTaskInstanceList=historicTaskInstanceQuery.processInstanceId("1801").list();
		List<HistoricTaskInstance> historicTaskInstanceList = historicTaskInstanceQuery.list();
		for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
			System.out.println("--------------------------");
			System.out.println(historicTaskInstance.getTaskDefinitionKey());
			System.out.println(historicTaskInstance.getAssignee());
			System.out.println(historicTaskInstance.getDueDate());
			System.out.println(historicTaskInstance.getStartTime());
			System.out.println(historicTaskInstance.getName());
			System.out.println(historicTaskInstance.getProcessInstanceId());
		}

		HistoricDetailQuery historicDetailQuery = historyService
				.createHistoricDetailQuery();

		List<HistoricDetail> historicDetailList = historicDetailQuery.list();

		for (HistoricDetail historicDetail : historicDetailList) {
			historicDetail.getTime();
		}
/*
		TaskService taskService = processEngine.getTaskService();
		// List<Task> tasks =
		// taskService.createTaskQuery().processInstanceId("1801").list();
		List<Task> tasks = taskService.createTaskQuery()
				.processInstanceId("3117").list();
System.out.println(tasks);
		Task task = tasks.get(0);

		RepositoryService repositoryService = processEngine
				.getRepositoryService();
		ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(task.getProcessDefinitionId());

		List<ActivityImpl> activitiList = def.getActivities();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		String excId = task.getExecutionId();
		ExecutionEntity execution = (ExecutionEntity) runtimeService
				.createExecutionQuery().executionId(excId).singleResult();
		String activitiId = execution.getActivityId();

		for (ActivityImpl activityImpl : activitiList) {
			String id = activityImpl.getId();
			if (activitiId.equals(id)) {

				System.out.println(activityImpl.getProperties());

				System.out.println("当前任务：" + activityImpl.getProperty("name"));
				// 输出某个节点的某种属性
				List<PvmTransition> outTransitions = activityImpl
						.getOutgoingTransitions();
				// 获取从某个节点出来的所有线路
				for (PvmTransition tr : outTransitions) {
					PvmActivity ac = tr.getDestination();
					// 获取线路的终点节点
					System.out.println("下一步任务任务：" + ac.getProperty("name"));
				}
				break;
			}
		}
		
		*/
		
		
		ProcessSpace _ProcessSpace=ProcessComponentFactory.connectProcessSpace(_TEST_PROCESSSPACE_NAME);		
		ProcessObject targetPO=_ProcessSpace.getProcessObjectById("3113");	
		
		System.out.println(targetPO.isFinished());		
		
		List<ProcessStep> poList=targetPO.getCurrentProcessSteps();
		System.out.println(poList);		
		for(ProcessStep processStep:poList){
			System.out.println(processStep.getStepName());
			System.out.println(processStep.getStepAssignee());			
		}
		
		
		List<HistoricProcessStep> finList=targetPO.getFinishedProcessSteps();
		
		System.out.println(finList);
		for(HistoricProcessStep historicProcessStep:finList){
			System.out.println(historicProcessStep.getStepName());
			System.out.println(historicProcessStep.getStepAssignee());
			
			System.out.println(historicProcessStep.getStartTime());
			System.out.println(historicProcessStep.getEndTime());			
		}
		System.out.println(targetPO.isFinished());
		System.out.println(targetPO.getNextProcessSteps());
		
	}
	
	
	
	public static void main(String[] args)throws ProcessRepositoryRuntimeException {
		ProcessEngineConfiguration _ProcessEngineConfiguration;
		try {
			String dataPersistenceType = PerportyHandler.getPerportyValue(PerportyHandler.DATA_PERSISTENCE_TYPE).trim();
			if (dataPersistenceType	.equals(PerportyHandler.DATA_PERSISTENCE_TYPE_DATABASE)) {
				_ProcessEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
				_ProcessEngineConfiguration.setDatabaseSchemaUpdate(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_databaseSchemaUpdate)	.trim());
				_ProcessEngineConfiguration.setJdbcDriver(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcDriver).trim());
				_ProcessEngineConfiguration.setJdbcUrl(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcUrl).trim());
				_ProcessEngineConfiguration.setJdbcUsername(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcUsername).trim());
				if (PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcPassword).trim().equals("NA")) {
					_ProcessEngineConfiguration.setJdbcPassword("");
				} else {
					_ProcessEngineConfiguration.setJdbcPassword(PerportyHandler.getPerportyValue(PerportyHandler.ACTIVITI_jdbcPassword).trim());
				}
				_ProcessEngineConfiguration.setJobExecutorActivate(Boolean.parseBoolean(PerportyHandler.getPerportyValue(PerportyHandler.DATA_PERSISTENCE_TYPE).trim()));
			} else if (dataPersistenceType.equals(PerportyHandler.DATA_PERSISTENCE_TYPE_INMEMERY)) {
				_ProcessEngineConfiguration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
			} else {
				throw new ProcessRepositoryRuntimeException();
			}
		} catch (ProcessRepositoryRuntimeException e) {
			e.printStackTrace();
			throw new ProcessRepositoryRuntimeException();
		}

		ProcessEngine processEngine = _ProcessEngineConfiguration.setProcessEngineName(_TEST_PROCESSSPACE_NAME).buildProcessEngine();
		
		TaskService taskService = processEngine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().processInstanceId("101").list();
		Task task = tasks.get(0);
		
		System.out.println(task.getAssignee());
		
		//task.setAssignee("FinancialEmployeeB");
		
		
		taskService.setAssignee(task.getId(), null);
		//System.out.println(task.getAssignee());
		
	}
	
	
	
	
	
}
