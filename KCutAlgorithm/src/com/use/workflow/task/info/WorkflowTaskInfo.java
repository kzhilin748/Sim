package com.use.workflow.task.info;

import java.util.List;

import com.use.resource.IRes;
import com.use.workflow.IWorkflow;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.TaskLink;



public class WorkflowTaskInfo extends ATaskInfo implements IDepend{

	long actuallyStartTime;
	long executionTime;
	
	public long getActuallyStartTime() {
		return this.actuallyStartTime;
	}

	public long getExecutionTime() {
		return this.executionTime;
	}

	public void setActuallyStartTime(long actuallyStartTime) {
		this.actuallyStartTime = actuallyStartTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public void setExecutionTime(int excutionTime) {
		
	}

	@Override
	public void setScheduled(boolean isScheduled) {
	}

	@Override
	public void setStarted(boolean isStarted) {
		
	}

	@Override
	public void setFinishTime(long finishTime) {
		
	}

	@Override
	public void setSubmitTime(long submitTime) {
		
	}

	@Override
	public boolean isStarted() {
		return false;
	}

	@Override
	public long getSubmitTime() {
		return 0;
	}

	@Override
	public long getFinishTime() {
		return 0;
	}

	@Override
	public List<TaskLink> getParentTaskLink() {
		return null;
	}

	@Override
	public List<TaskLink> getChildTaskLink() {
		return null;
	}

	@Override
	public long getEST() {
		return 0;
	}

	@Override
	public void setEST(long EST) {
		
	}

	@Override
	public long getEFT() {
		return 0;
	}

	@Override
	public void setEFT(long EFT) {
		
	}

	@Override
	public IWorkflow getBelongWorkflow() {
		return null;
	}

	@Override
	public void setBelongWorkflow(IWorkflow belongWorkflow) {
		
	}

	@Override
	public <Node extends IRes> Node getBelongRes() {
		return null;
	}

	@Override
	public <Node extends IRes> void setBelongRes(Node belongRes) {
		
	}
	
}
