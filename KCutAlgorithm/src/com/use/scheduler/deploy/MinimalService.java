package com.use.scheduler.deploy;

import com.use.workflow.service.PeterService;

public class MinimalService {
	int nodeIndex;
	PeterService service;

	public MinimalService(PeterService service, int nodeIndex) {
		this.nodeIndex = nodeIndex;
		this.service = service;
	}

	public int getNodeIndex() {
		return this.nodeIndex;
	}

	public PeterService getService() {
		return this.service;
	}

	@Override
	public String toString() {
		return service + ":" + nodeIndex;
	}
}
