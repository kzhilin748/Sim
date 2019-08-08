package com.use.scheduler.deploy;

import java.util.List;

import com.use.workflow.service.PeterService;

public interface IPeterDeploy extends IDeploy {
	void generateSCG(int startDeployServiceId);
	List<PeterService> getEnabledServicePool();
	List<PeterService> getUndeployedService();
}
