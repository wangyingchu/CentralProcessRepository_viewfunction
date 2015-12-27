package com.viewfunction.processRepository.processBureauImpl;

import com.viewfunction.processRepository.processBureau.ProcessStepDefinition;

public class ActivitiProcessStepDefinitionImpl implements ProcessStepDefinition{
	
	private String stepName;
	private String stepId;
	private String stepDescription;
	private String stepType;

	@Override
	public String getStepName() {
		return this.stepName;
	}

	@Override
	public String getStepId() {
		return this.stepId;
	}

	@Override
	public String getStepDescription() {
		return this.stepDescription;
	}

	@Override
	public String getStepType() {
		return this.stepType;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public void setStepId(String stepId) {
		this.stepId = stepId;
	}

	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}

	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

}
