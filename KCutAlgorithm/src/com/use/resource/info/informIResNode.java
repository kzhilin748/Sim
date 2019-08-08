package com.use.resource.info;

import com.use.exception.ProcessorException;
import com.use.queue.Queue;
import com.use.resource.IRes;
import com.use.workflow.task.IAttribute;

public class informIResNode implements IRes {
	private int id;

	public informIResNode(int id) {
		this.id = id;
	}
	
	@Override
	public int getMemory() {
		return 0;
	}

	@Override
	public int getTotalCpu() {
		return 0;
	}

	@Override
	public int getFreeCpu() {
		return 0;
	}

	@Override
	public boolean decCpuUsage(int freedCpu) throws ProcessorException {
		return false;
	}

	@Override
	public boolean incCpuUsage(int usedCpu) throws ProcessorException {
		return false;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public <WaitingQueue extends Queue<IAttribute>> WaitingQueue getWaitingQueue() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
