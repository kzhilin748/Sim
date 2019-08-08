package com.use.workflow.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.use.config.PeterVariable;
import com.use.resource.PeterNode;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.TaskLink;

public class PeterService implements IAttribute, Cloneable {
	private int id;
	private int communicationCost;
	private int computationCost;
	private int joinAttribute;
	private int forkAttribute;
	private boolean isStartService;
	private List<TaskLink> nextServicePool = new ArrayList<TaskLink>();
	private List<TaskLink> previousServicePool = new ArrayList<TaskLink>();
	private List<TaskLink> globalServicePool = new ArrayList<TaskLink>();
	private int[] costDiffPool;
	private int[] costInMachinePool;
	private boolean isScheduled;
	private List<PeterNode> vmStoreList = new ArrayList<PeterNode>();
	private float rate=1;
	
	public PeterService() {
		joinAttribute = forkAttribute = 1;
		costDiffPool = new int[PeterVariable.getInstance().getVmNumber() + PeterVariable.getInstance().getExtraVMNumber()];
		costInMachinePool = new int[PeterVariable.getInstance().getVmNumber() + PeterVariable.getInstance().getExtraVMNumber()];
		isScheduled = false;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
//	@Override
//	public String toString() {
//		return (this.id+1) + ":" + this.getCommunicationCost() + ":" + this.getComputationCost();
//	}

	public int getCommunicationCost() {
		return communicationCost;
	}

	public void setCommunicationCost(int communicationCost) {
		this.communicationCost = communicationCost;
	}

	public int getComputationCostByRate() {
		return (int) (this.getComputationCost() * this.getRate());
	}
	
	public int getComputationCost() {
		return this.computationCost;
	}

	public void setComputationCost(int computationCost) {
		this.computationCost = computationCost;
	}

	// public int getCommunicationTime() {
	// return serviceCommunicationCost;
	// }
	//
	// public void setCommunicationTime(int communicationTime) {
	// this.serviceCommunicationCost = communicationTime;
	// }
	//
	// public int getCommunicationCost() {
	// return serviceComputationCost;
	// }
	//
	// public void setCommunicationCost(int communicationCost) {
	// this.serviceComputationCost = communicationCost;
	// }

	@Override
	public int getId() {
		return id;
	}

	public int getId(int offset) {
		return id + offset;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getJoinAttribute() {
		return this.joinAttribute;
	}

	public int getForkAttribute() {
		return this.forkAttribute;
	}

	public void setJoinAttribute(int joinAttribute) {
		this.joinAttribute = joinAttribute;
	}

	public void setForkAttribute(int forkAttribute) {
		this.forkAttribute = forkAttribute;
	}

	public boolean isStartService() {
		return this.isStartService;
	}

	public void setStartService(boolean isStartService) {
		this.isStartService = isStartService;
	}

	@Override
	public void setScheduled(boolean isScheduled) {
		this.isScheduled = isScheduled;
	}

	@Override
	public boolean isScheduled() {
		return isScheduled;
	}

	public List<TaskLink> getNextServicePool() {
		return this.nextServicePool;
	}

	public List<TaskLink> getPreviousServicePool() {
		return this.previousServicePool;
	}

	public int getCostInMachine(int index) {
		return this.costInMachinePool[index];
	}

	public void setCostInMachine(int index, int value) {
		this.costInMachinePool[index] = value;
	}

	public int[] getCostInMachinePool() {
		return this.costInMachinePool;
	}

	public int getCostDiff(int index) {
		return this.costDiffPool[index];
	}

	public void setCostDiff(int index, int value) {
		this.costDiffPool[index] = value;
	}

	public int[] getCostDiffPool() {
		return this.costDiffPool;
	}
	
	public void clearCostInMachinePool() {
		Arrays.fill(costInMachinePool, 0);
	}

	public void clearCostDiffPool() {
		Arrays.fill(costDiffPool, 0);
	}
	
	public List<TaskLink> getGlobalServicePool() {
		return this.globalServicePool;
	}
	
	public void addNextServicePool(TaskLink link) {
		this.nextServicePool.add(link);
		this.globalServicePool.add(link);
	}

	public void addPreviousServicePool(TaskLink link) {
		this.previousServicePool.add(link);
		this.globalServicePool.add(link);
	}

	public void addGlobalServicePool(TaskLink link) {
		this.globalServicePool.add(link);
	}

	@Override
	public void setStarted(boolean isStarted) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFinishTime(long finishTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSubmitTime(long submitTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getSubmitTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getFinishTime() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public List<PeterNode> getVmStoreList() {
		return vmStoreList;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	@Override
	public void setNumberOfProcessors(int numberOfProcessors) {
		
	}

	@Override
	public int getNumberOfProcessors() {
		return 0;
	}
	
}
