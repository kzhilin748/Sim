package com.use.workflow.task;

import java.util.ArrayList;
import java.util.List;

import com.use.workflow.service.PeterService;

public class PeterTask extends DependTask implements IPeterAttribute {
	private PeterService service;
	private final List<TaskLink> globalServicePool;
	private long startTime;
	private boolean isSubmited = false;
	private int rank;
	private boolean inserted;

	public PeterTask(IDepend taskInfo) {
		super(taskInfo);
		globalServicePool = new ArrayList<TaskLink>();
	}

	@Override
	public PeterService getService() {
		return service;
	}

	@Override
	public void setService(PeterService service) {
		this.service = service;
	}

	@Override
	public List<TaskLink> getGlobalServicePool() {
		return globalServicePool;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public boolean isSubmited() {
		return isSubmited;
	}

	@Override
	public void setSubmited(boolean isSubmited) {
		this.isSubmited = isSubmited;
	}

	@Override
	public int getRank() {
		return rank;
	}

	@Override
	public void setRank(int rank) {
		this.rank = rank;
	}
//	@Override
//	public String toString() {
//		int workflowID = 0;
//		if (this.getBelongWorkflow() != null) {
//			workflowID = this.getBelongWorkflow().getId();
//		}
//		return workflowID + ":" + this.getId();
//	}

	@Override
	public boolean isInserted() {
		return inserted;
	}

	@Override
	public void setInserted(boolean bool) {
		this.inserted = bool;
	}
}
