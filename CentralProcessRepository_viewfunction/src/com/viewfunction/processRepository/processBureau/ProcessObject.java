package com.viewfunction.processRepository.processBureau;

import java.util.Date;
import java.util.List;

public interface ProcessObject {
	public String getProcessObjectId();
	public String getProcessDefinitionId();
	public boolean isFinished();
	public boolean isSuspended();
	public List<ProcessStep> getCurrentProcessSteps();
	public List<HistoricProcessStep> getFinishedProcessSteps();		
	public List<String> getNextProcessSteps();	
	public void addComment(ProcessComment processComment);
	public List<ProcessComment> getComments();	
	public Date getProcessStartTime();
	public Date getProcessEndTime();
	public Long getProcessDurationInMillis();
	public String getProcessStartUserId();
	public Integer getProcessDefinitionVersion();
}