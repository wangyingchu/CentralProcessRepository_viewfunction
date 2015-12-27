package com.viewfunction.processRepository.processBureau;

public interface ProcessStepDefinition {
	public String StepType_PeopleOperationStep="PEOPLE_OPERATION_STEP";
	public String getStepName();
	public String getStepId(); 
	public String getStepDescription();
	public String getStepType();
}