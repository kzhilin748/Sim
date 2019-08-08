package com.use.resource;

import java.util.List;

import com.use.queue.Queue;
import com.use.workflow.service.PeterService;
import com.use.workflow.task.IAttribute;

public interface IResPeter extends IRes {
	Queue<IAttribute> getRunningQueue();
	void addDeployService(PeterService service);
	List<PeterService> getDeployService();
	int getTotalCommunicationCost();
	int getTotalComputationCost();
}
