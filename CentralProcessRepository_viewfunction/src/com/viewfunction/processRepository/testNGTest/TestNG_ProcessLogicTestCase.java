package com.viewfunction.processRepository.testNGTest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.viewfunction.processRepository.exception.ProcessRepositoryDeploymentException;
import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessSpace;
import com.viewfunction.processRepository.processBureau.ProcessStep;
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;
public class TestNG_ProcessLogicTestCase {
	
	private static final String _TEST_PROCESSSPACE_NAME="TEST_ProcessSpace";
	@Test
	public void testProcessObjectOperation() throws ProcessRepositoryDeploymentException, ProcessRepositoryRuntimeException, FileNotFoundException{
		ProcessSpace _ProcessSpace=ProcessComponentFactory.connectProcessSpace(_TEST_PROCESSSPACE_NAME);	
		FileInputStream fileInputStream = new FileInputStream(new File("processDefine/FinancialReportProcess.bpmn20.xml"));
		boolean addResult=_ProcessSpace.addProcessDefinition("FinancialReportProcess.bpmn20.xml",fileInputStream); 
		Assert.assertTrue(addResult);
	    ProcessObject _ProcessObject=_ProcessSpace.launchProcess("financialReport",null);
	    
	    List<ProcessStep> processStepList_1=_ProcessObject.getCurrentProcessSteps();
	    Assert.assertEquals(processStepList_1.size(),1);
	    Assert.assertEquals(processStepList_1.get(0).getStepName(),"Write monthly financial report");	    
	    ProcessObject queryProcessObject=_ProcessSpace.getProcessObjectById(_ProcessObject.getProcessObjectId());	    
	    List<ProcessStep> processStepList_2=queryProcessObject.getCurrentProcessSteps();
	    Assert.assertEquals(processStepList_2.size(),1);
	    Assert.assertEquals(processStepList_2.get(0).getStepName(),"Write monthly financial report");
	    
	    ProcessStep firstProcessStep=_ProcessObject.getCurrentProcessSteps().get(0);
	    Assert.assertEquals(firstProcessStep.getStepName(),"Write monthly financial report");	    
	    firstProcessStep.setStepOwner("ABC");
	    //firstProcessStep.setStepAssignee("wangychu"); //this line will active ProcessRepositoryRuntimeException
	    firstProcessStep.saveCurrentStep("wangychu");
	    
	    firstProcessStep=_ProcessObject.getCurrentProcessSteps().get(0);	   
	    Assert.assertEquals(firstProcessStep.getStepOwner(),"ABC");
	    
	    firstProcessStep.completeCurrentStep("Param Vir Chakra");
	    Assert.assertFalse(_ProcessObject.isFinished());
	 
	    ProcessStep secondProcessStep=_ProcessObject.getCurrentProcessSteps().get(0);	   
	    Assert.assertEquals(secondProcessStep.getStepName(),"Verify monthly financial report");
	    secondProcessStep.completeCurrentStep("KK The Great");	    
	    Assert.assertTrue(_ProcessObject.isFinished());	 
	    
	    ProcessObject _ProcessObject_2=_ProcessSpace.launchProcess("financialReport",null);
	    ProcessStep firstProcessStep_2=_ProcessObject_2.getCurrentProcessSteps().get(0);
	    firstProcessStep_2.handleCurrentStep("FairyAngela");
	    firstProcessStep_2=_ProcessObject_2.getCurrentProcessSteps().get(0);	    
	    Assert.assertEquals(firstProcessStep_2.getStepAssignee(),"FairyAngela");
	    Assert.assertEquals(firstProcessStep_2.getStepName(),"Write monthly financial report");
	    //firstProcessStep_2.completeCurrentStep("wyc"); //this line will active ProcessRepositoryRuntimeException
	    firstProcessStep_2.completeCurrentStep("FairyAngela");
	    
	    Assert.assertEquals(_ProcessObject_2.getFinishedProcessSteps().size(),1);
	    Assert.assertEquals(_ProcessObject_2.getFinishedProcessSteps().get(0).getStepName(),"Write monthly financial report");
	    Assert.assertEquals(_ProcessObject_2.getFinishedProcessSteps().get(0).getStepAssignee(),"FairyAngela");	   
	    
	    ProcessStep secondProcessStep_2=_ProcessObject_2.getCurrentProcessSteps().get(0);
	    Assert.assertEquals(secondProcessStep_2.getStepName(),"Verify monthly financial report");
	    Assert.assertEquals(secondProcessStep_2.getStepAssignee(),null);
	    Assert.assertFalse(_ProcessObject_2.isFinished());	 
	    secondProcessStep_2.completeCurrentStep("wangychu");
	    Assert.assertTrue(_ProcessObject_2.isFinished());	
	    Assert.assertEquals(_ProcessObject_2.getFinishedProcessSteps().size(),2);
	}
}