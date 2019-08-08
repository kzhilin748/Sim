package com.use.workflow;

import java.util.ArrayList;
import java.util.List;

import com.use.workflow.task.IDepend;


public abstract class AWorkflow implements IWorkflow {
	protected List<IDepend> taskList;
	protected long interArrivalTime;
	
	public AWorkflow(int interArrivalTime) {
		this.interArrivalTime = interArrivalTime;
		this.taskList = new ArrayList<IDepend>();
	}
	
	@Override
	public List<IDepend> getTaskList() {
		return this.taskList;
	}

	public long getInterArrivalTime() {
		return this.interArrivalTime;
	}

	@Override
	public void setNumberOfProcessors(int numberOfProcessors) {
		
	}

	@Override
	public int getNumberOfProcessors() {
		return 0;
	}

}
