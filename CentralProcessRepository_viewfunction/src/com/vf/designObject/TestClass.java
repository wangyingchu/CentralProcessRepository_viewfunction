package com.vf.designObject;

import java.util.List;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class TestClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {	
		
		ProcessEngineConfiguration  _ProcessEngineConfiguration=  ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");		
		//	ProcessEngine processEngine=_ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration().buildProcessEngine();		
		ProcessEngine processEngine=_ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration().setProcessEngineName("Default Process Repository").buildProcessEngine();
		//ProcessEngine processEngine=_ProcessEngineConfiguration.setProcessEngineName("Default Process Repository").buildProcessEngine();
		//System.out.println(processEngine);		
		//System.out.println(processEngine.VERSION);
		
		System.out.println(processEngine.getName());
		RepositoryService repositoryService = processEngine.getRepositoryService();
		System.out.println(repositoryService);		
		
		repositoryService.createDeployment().name("process-one.bar")
		//	.addClasspathResource("DefaultTestProcess.activiti")
		.addClasspathResource("FinancialReportProcess.bpmn20.xml")
		//	.addClasspathResource("FinancialReportProcess.png")		
		.deploy();
		
		RuntimeService runtimeService = processEngine.getRuntimeService();	
	
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("financialReport");
		
		//runtimeService.startProcessInstanceByKey("financialReport");		 
		//runtimeService.startProcessInstanceByKey("financialReport");		 
		//runtimeService.startProcessInstanceByKey("financialReport");
		//runtimeService.startProcessInstanceByKey("financialReport");		
		System.out.println(processInstance.getProcessDefinitionId());
		System.out.println(processInstance.getBusinessKey());
		System.out.println(processInstance.getProcessInstanceId());
		System.out.println(processInstance.getId());
		
		TaskService taskService = processEngine.getTaskService();
		
		List<Task> processInstanceTasks=taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).list();

		System.out.println("processInstancetask:"+processInstanceTasks);
		
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
		
		

		 System.out.println(	"************************************");
		 
		 
		 System.out.println(tasks.get(0).getOwner());
		
		 tasks.get(0).setOwner("HelloWorld");
		 
		 taskService.saveTask(tasks.get(0));
		 tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
		 System.out.println(tasks.get(0).getOwner());
		 
		 System.out.println(	"************************************");
		
		
		
		System.out.println(tasks);
		for(Task _Task:tasks){			
			System.out.println(	"=====================");
			
			System.out.println("getAssignee"+_Task.getAssignee());
			System.out.println("getDescription: "+_Task.getDescription());			
			System.out.println("getExecutionId: "+_Task.getExecutionId());			
			System.out.println("getId: "+_Task.getId());			
			System.out.println("getName: "+_Task.getName());
			System.out.println("getOwner: "+_Task.getOwner());
			System.out.println("getParentTaskId: "+_Task.getParentTaskId());
			System.out.println("getProcessDefinitionId"+_Task.getProcessDefinitionId());
			System.out.println("getProcessInstanceId: "+_Task.getProcessInstanceId());
			System.out.println("getTaskDefinitionKey: "+_Task.getTaskDefinitionKey());
			System.out.println("getCreateTime: "+_Task.getCreateTime());
			System.out.println("getDelegationState: "+_Task.getDelegationState());
			System.out.println("getDueDate: "+_Task.getDueDate());
			
			System.out.println(	"=====================");
			
		}
		
		System.out.println(	"XXXXXXXXXXXXXXXXXXXX");
		Task currentTask=tasks.get(0);
		System.out.println(currentTask.getAssignee());
		
		
		 taskService.claim(currentTask.getId(), "fozzie");		
		 
		 
		
		  tasks = taskService.createTaskQuery().taskAssignee("fozzie").list();
	
		 
		  System.out.println(tasks.get(0).getAssignee());
		  System.out.println(tasks.get(0).getOwner());
		  
		  
		  tasks.get(0).setOwner("ABC");
		  System.out.println(tasks.get(0).getOwner());
		 System.out.println(	"XXXXXXXXXXXXXXXXXXXX");
		 
		 taskService.saveTask(tasks.get(0));
		 
		 
		 
		 
		 
		 tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
			
			System.out.println(tasks);
		 
		 
			 List<Task> managementTasks = taskService.createTaskQuery().taskAssignee("management").list();
		    for (Task task : managementTasks) {
		      System.out.println("Task for management: " + task.getName());
		      
		      // Complete the task
		      //taskService.complete(task.getId());
		    }
		 
		  
		//    taskService.complete(tasks.get(0).getId());
		    
		    
		    
		    
		    managementTasks = taskService.createTaskQuery().taskAssignee("management").list();
		    for (Task task : managementTasks) {
		      System.out.println("Task for management: " + task.getName());
		      
		      // Complete the task
		      //taskService.complete(task.getId());
		    }
		 
		    processInstanceTasks=taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).list();

			System.out.println("processInstancetask:"+processInstanceTasks.get(0).getName());
		    
		    
		    
		    
		    
		    
		    
		 
		
		/*
		
		
		
		TaskService taskService = processEngine.getTaskService();
		System.out.println(taskService);
		ManagementService managementService = processEngine.getManagementService();
		System.out.println(managementService);
		IdentityService identityService = processEngine.getIdentityService();
		System.out.println(identityService);
		HistoryService historyService = processEngine.getHistoryService();
		System.out.println(historyService);
		FormService formService = processEngine.getFormService();
		System.out.println(formService);
		*/
		
	//	processEngine.close();
		
		/**/

	}

}
