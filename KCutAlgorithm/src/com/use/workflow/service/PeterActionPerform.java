package com.use.workflow.service;

import java.util.ArrayList;
import java.util.Random;

import com.use.config.PeterVariable;

public class PeterActionPerform extends ArrayList<PeterService> {
	private static PeterActionPerform instance;

	public PeterActionPerform() {
		instance = this;
	}
	
	public void alloc() {
		Random randomCommunicationCost = new Random();
		Random randomComputationCost = new Random();
		Random serviceAttribute = new Random(15);
		Random isForkRandom = new Random(2);
		
		int branch = PeterVariable.getInstance().getLevelOneFork();
		if (branch < PeterVariable.getInstance().getLevelTwoFork()) {
			branch = PeterVariable.getInstance().getLevelTwoFork();
		}
		
		int id = 0;
		for (int i = 0; i < PeterVariable.getInstance().getTotalServiceNumber(); i++) {
			
			PeterService service = new PeterService();
			service.setId(id++);
			randomCommunicationCost.setSeed(id + 5);
			randomComputationCost.setSeed(id);
			service.setComputationCost((randomComputationCost.nextInt(30)+5) * PeterVariable.getInstance().getMultipleComputationFactor());
			service.setCommunicationCost((randomCommunicationCost.nextInt(30)+5) * PeterVariable.getInstance().getMultipleCommunicationFactor());
			
			int isFork = isForkRandom.nextInt(2);
			serviceAttribute.nextInt(PeterVariable.getInstance().getLevelOneFork());
			serviceAttribute.nextInt(PeterVariable.getInstance().getLevelTwoFork());
			if (isFork == 0) {
				service.setForkAttribute( serviceAttribute.nextInt(branch) + 1 );
				if (service.getForkAttribute() == 1) {
					service.setStartService(true);
				}
			}
			else {
				service.setJoinAttribute( serviceAttribute.nextInt(branch) + 1 );
				service.setStartService(true);
			}
			
			this.add(service);
		}
	}

	public PeterService getService(int index) {
		return this.remove(index);
	}

	public static PeterActionPerform getInstance() {
		return instance;
	}
	
	
}
