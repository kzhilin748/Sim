package com.use.resource;

import java.util.ArrayList;
import java.util.List;

import com.use.exception.ProcessorException;
import com.use.queue.Queue;
import com.use.queue.SubmitQueueSJF;
import com.use.workflow.service.PeterService;
import com.use.workflow.task.IAttribute;

public class PeterNode extends ANode implements IResPeter {
	private final List<PeterService> deployService = new ArrayList<PeterService>();
	private int totalCommunicationCost;
	private int totalComputationCost;
	private final Queue<IAttribute> RunningQueue = new Queue<IAttribute>();
	private final Queue<IAttribute> waittingQueue = new SubmitQueueSJF<IAttribute>();
	private List<Integer>thisVmservicelist= new ArrayList<Integer>();
	
	public PeterNode(IRes res) {
		super(res);
	}

	@Override
	public void addDeployService(PeterService service) {
		deployService.add(service);
		thisVmservicelist.add(service.getId());
		if (!service.getVmStoreList().contains(this)) {
			service.getVmStoreList().add(this);
		}
		this.totalCommunicationCost += service.getCommunicationCost();
		this.totalComputationCost += service.getComputationCostByRate();
		
	}
	
	@Override
	public List<PeterService> getDeployService() {
		return this.deployService;
	}

	@Override
	public int getTotalCommunicationCost() {
		return this.totalCommunicationCost;
	}

	@Override
	public int getTotalComputationCost() {
		return this.totalComputationCost;
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
	public <WaitingQueue extends Queue<IAttribute>> WaitingQueue getWaitingQueue() {
		return (WaitingQueue) waittingQueue;
	}
	
	@Override
	public Queue<IAttribute> getRunningQueue(){
		return RunningQueue;
	}

	@Override
	protected void parseResInfo(IRes res) {
		
	}
	public List<Integer> getthisVmservicelist(){
		return thisVmservicelist;
	}
}
