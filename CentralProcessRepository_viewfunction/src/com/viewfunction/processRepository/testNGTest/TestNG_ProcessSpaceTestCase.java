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

public class TestNG_ProcessSpaceTestCase {
	
	private static final String _TEST_PROCESSSPACE_NAME="TEST_ProcessSpace";	
	@Test
	public void testProcessSpaceOperation() throws ProcessRepositoryDeploymentException, ProcessRepositoryRuntimeException, FileNotFoundException{
		ProcessSpace _ProcessSpace=ProcessComponentFactory.connectProcessSpace(_TEST_PROCESSSPACE_NAME);
		Assert.assertEquals(_ProcessSpace.getProcessSpaceName(),_TEST_PROCESSSPACE_NAME);
		
		FileInputStream fileInputStream = new FileInputStream(new File("processDefine/FinancialReportProcess.bpmn20.xml"));
		boolean addResult=_ProcessSpace.addProcessDefinition("FinancialReportProcess.bpmn20.xml",fileInputStream);   
	    
	    Assert.assertTrue(addResult);		    
	    ProcessObject _ProcessObject=_ProcessSpace.launchProcess("financialReport",null);
	    Assert.assertNotNull(_ProcessObject);	    
	    ProcessObject queryProcessObject=_ProcessSpace.getProcessObjectById(_ProcessObject.getProcessObjectId());	    
	    Assert.assertEquals(_ProcessObject.getProcessDefinitionId(), queryProcessObject.getProcessDefinitionId());
	    Assert.assertEquals(_ProcessObject.getProcessObjectId(), queryProcessObject.getProcessObjectId());
	    Assert.assertEquals(_ProcessObject.isFinished(), queryProcessObject.isFinished());
	    
	    String processName=_ProcessSpace.getProcessNameByDefinitionId(_ProcessObject.getProcessDefinitionId());	   
	    Assert.assertEquals(processName,"financialReport");
	    
	    List<ProcessObject> _ProcessObjectList=_ProcessSpace.getProcessObjectsByProcessType("financialReport",ProcessSpace.PROCESS_STATUS_UNFINISHED);
	    Assert.assertEquals(_ProcessObjectList.size(),1);
	    _ProcessSpace.launchProcess("financialReport",null);
	    _ProcessSpace.launchProcess("financialReport",null);
	    _ProcessObjectList=_ProcessSpace.getProcessObjectsByProcessType("financialReport",ProcessSpace.PROCESS_STATUS_UNFINISHED);
	    Assert.assertEquals(_ProcessObjectList.size(),3);
	    
	    ProcessObject executeProcessObject=_ProcessObjectList.get(0);
	    executeProcessObject.getCurrentProcessSteps().get(0).completeCurrentStep("KK the Great"); //execute step "Write monthly financial report"
	    executeProcessObject.getCurrentProcessSteps().get(0).completeCurrentStep("KK the Great"); //execute step "Verify monthly financial report"
	    _ProcessObjectList=_ProcessSpace.getProcessObjectsByProcessType("financialReport",ProcessSpace.PROCESS_STATUS_UNFINISHED);
	    Assert.assertTrue(executeProcessObject.isFinished());	    
	    Assert.assertEquals(_ProcessObjectList.size(),2);
	    
	    _ProcessSpace.launchProcess("financialReport",null);
	    _ProcessSpace.launchProcess("financialReport",null);
	    _ProcessSpace.launchProcess("financialReport",null);
	    
	    List<ProcessStep>  roleProcessStepList=_ProcessSpace.getProcessStepsByRole("accountancy");
	    List<ProcessStep>  roleProcessStepList_2=_ProcessSpace.getProcessStepsByRole("management");		    	    
	    Assert.assertEquals(roleProcessStepList.size(),5);
	    Assert.assertEquals(roleProcessStepList_2.size(),0);
	    ProcessStep firstStep=roleProcessStepList.get(0);
	    
	    firstStep.handleCurrentStep("FairyAngela");	    
	    roleProcessStepList=_ProcessSpace.getProcessStepsByRole("accountancy");
	    roleProcessStepList_2=_ProcessSpace.getProcessStepsByRole("management");
	    Assert.assertEquals(roleProcessStepList.size(),4);
	    Assert.assertEquals(roleProcessStepList_2.size(),0);	    
	    List<ProcessStep> participantProcessStepList=_ProcessSpace.getProcessStepsByParticipant("FairyAngela");
	    Assert.assertEquals(participantProcessStepList.size(),1);
	    
	    firstStep.completeCurrentStep("FairyAngela");
	    participantProcessStepList=_ProcessSpace.getProcessStepsByParticipant("FairyAngela");
	    Assert.assertEquals(participantProcessStepList.size(),0);
	    roleProcessStepList_2=_ProcessSpace.getProcessStepsByRole("management");
	    Assert.assertEquals(roleProcessStepList_2.size(),1);	    
	    roleProcessStepList=_ProcessSpace.getProcessStepsByRole("accountancy");
	    Assert.assertEquals(roleProcessStepList.size(),4);	    
	}
}