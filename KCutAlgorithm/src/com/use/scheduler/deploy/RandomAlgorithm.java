package com.use.scheduler.deploy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.use.resource.PeterNode;
import com.use.resource.platform.IPlatform;
import com.use.simulator.ASimulator;
import com.use.simulator.PeterSimulator;
import com.use.workflow.service.PeterService;

public class RandomAlgorithm implements IPeterDeploy {
	private final List<PeterService> enabledServicePool = new ArrayList<PeterService>();
	private final List<PeterService> undeployedService = new ArrayList<PeterService>();
	protected Random index = new Random(1);

	@Override
	public void deploy() throws Exception {
		PeterSimulator simulator = ASimulator.getInstance();
		IPlatform paltform = simulator.getCluster();
		// while(!undeployedService.isEmpty()){
		for (PeterService service : undeployedService) {
			PeterNode node = paltform.getResource(index.nextInt(paltform.getResourcelist().size()));
			node.addDeployService(service);
		}
		// }
	}

	@Override
	public void generateSCG(int startDeployServiceId) {
		undeployedService.addAll(enabledServicePool);
	}

	@Override
	public List<PeterService> getEnabledServicePool() {
		return enabledServicePool;
	}

	@Override
	public List<PeterService> getUndeployedService() {
		return undeployedService;
	}
}
