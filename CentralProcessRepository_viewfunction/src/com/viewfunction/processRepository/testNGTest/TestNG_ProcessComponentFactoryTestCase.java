package com.viewfunction.processRepository.testNGTest;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.processBureau.HistoricProcessStep;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessSpace;
import com.viewfunction.processRepository.processBureau.ProcessStep;
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;

public class TestNG_ProcessComponentFactoryTestCase {

	private static final String _TEST_PROCESSSPACE_NAME="TEST_ProcessSpace";
	@Test
	public void testConnectProcessSpace() throws ProcessRepositoryRuntimeException{		
		ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(_TEST_PROCESSSPACE_NAME);		
		Assert.assertTrue(targetProcessSpace instanceof ProcessSpace,"Class Type should by ProcessSpace");		
		Assert.assertEquals(targetProcessSpace.getProcessSpaceName(),_TEST_PROCESSSPACE_NAME);		
	}
	
	@Test
	public void testCreateProcessObject(){
		ProcessObject _ProcessObject=ProcessComponentFactory.createProcessObject("processObjectId", "processDefinitionId", false);
		Assert.assertTrue(_ProcessObject instanceof ProcessObject,"Class Type should by ProcessObject");	
		Assert.assertEquals(_ProcessObject.getProcessObjectId(),"processObjectId");
		Assert.assertEquals(_ProcessObject.getProcessDefinitionId(),"processDefinitionId");
		Assert.assertEquals(_ProcessObject.isFinished(),false);
	}
	
	@Test
	public void testCreateProcessStep(){		
		ProcessStep _ProcessStep=ProcessComponentFactory.createProcessStep("stepName", "stepDefinitionKey", "stepId", "processObjectId", "processDefinitionId", new Date());		
		Assert.assertTrue(_ProcessStep instanceof ProcessStep,"Class Type should by ProcessStep");	
		Assert.assertEquals(_ProcessStep.getStepName(),"stepName");		
	}
	
	@Test
	public void testCreateHistoricProcessStep(){		
		HistoricProcessStep _HistoricProcessStep=ProcessComponentFactory.createHistoricProcessStep("stepName", "stepDefinitionKey", "stepId", "processObjectId", "processDefinitionId","stepAssignee",new Date(),new Date(),null);
		Assert.assertTrue(_HistoricProcessStep instanceof HistoricProcessStep,"Class Type should by ProcessStep");	
		Assert.assertEquals(_HistoricProcessStep.getStepName(),"stepName");		
	}
}