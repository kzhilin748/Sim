package com.use.scheduler.deploy;

import java.util.ArrayList;
import java.util.List;

import com.use.config.PeterVariable;
import com.use.resource.IRes;
import com.use.resource.PeterNode;
import com.use.resource.platform.PeterPlatform;
import com.use.simulator.ASimulator;
import com.use.simulator.PeterSimulator;
import com.use.workflow.IWorkflow;
import com.use.workflow.service.PeterService;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.IPeterAttribute;
import com.use.workflow.task.TaskLink;

/**
 *	Main Kcut Algorithm for deploy.
 */
public class KcutdontcareAvgload implements IPeterDeploy {
	private final List<PeterService> enabledServicePool = new ArrayList<PeterService>();
	private final List<PeterService> undeployedService = new ArrayList<PeterService>();
	private PeterSimulator simulator;
	private PeterPlatform platform;
	
	/**
	 * main deploy function.
	 */
	@Override	
	public void deploy() throws Exception {
		while (!undeployedService.isEmpty()) {
			List<MinimalService> minimalValueService = updateMinimalValueService(undeployedService);
			MinimalService minimal = null;
			if (minimalValueService.size() > 1) {
				int minimalCost = Integer.MAX_VALUE;
				for (MinimalService tmpminimal : minimalValueService) {
					int cost = platform.getResource(tmpminimal.getNodeIndex()).getTotalComputationCost();
					if (cost < minimalCost) {
						minimalCost = cost;
						minimal = tmpminimal;
					}
				}
			}
			else if (!minimalValueService.isEmpty()) {
				minimal = minimalValueService.get(0);
			}
			
			if (minimal != null) {
				platform.getResource(minimal.getNodeIndex()).addDeployService(minimal.getService());
//				if (ALauncher.isLogEnable()) {
					System.out.println((minimal.getService().getId()) + " -> " + "Vm." +(platform.getResource(minimal.getNodeIndex()).getId()));
//				}
				undeployedService.remove(minimal.getService());
				for (PeterService service : enabledServicePool) {
					service.clearCostDiffPool();
					service.clearCostInMachinePool();
				}
			}
		}
	}
	
//	protected List<MinimalService> updateMinimalValueService(List<PeterService> undeployedService) {
//		List<MinimalService> minimalValueService = computeMinimalValue(undeployedService);
//		
//		if (minimalValueService.isEmpty()) {
//			int minServiceCost = Integer.MAX_VALUE;
//			PeterService minimalService = null;
//			int minimalMachine = -1;
//			
//			
//			for (PeterService service : undeployedService) {
//				for (int i = 0; i < simulator.getCluster().getResourcelist().size(); i++) {
//					for (PeterService checkService : ((PeterNode)simulator.getCluster().getResourcelist().get(i)).getDeployService()){
//						if (service.getId() == checkService.getId()){
//							continue;
//						}
//					if (service.getCostDiff(i) <= minServiceCost) {
//						minServiceCost = service.getCostDiff(i);
//						minimalMachine = i;
//						minimalService = service;
//					}
//				}
//				}	
//			}
//			
//			minimalValueService.add(new MinimalService(minimalService, minimalMachine));
//		}
//		
//		if (minimalValueService.size() > 1) {
//			int maxN = Integer.MIN_VALUE;
//			
//			for (MinimalService minimal : minimalValueService) {
//				PeterService service = minimal.getService();
//				if ( service.getCostInMachine(minimal.getNodeIndex()) >= maxN ) {
//					maxN = service.getCostInMachine(minimal.getNodeIndex());
//				}
//			}
//			
//			List <MinimalService> remove = new ArrayList<MinimalService>();
//			for (MinimalService minimal : minimalValueService) {
//				PeterService service = minimal.getService();
//				if (service.getCostInMachine(minimal.getNodeIndex()) < maxN) {
//					remove.add(minimal);
//				}
//			}
//			minimalValueService.removeAll(remove);
//		}
//		System.out.println(minimalValueService.size());
//		return minimalValueService;
//	}
	
	
	// original code
	/**
	 * check the minimal service and vm pair is empty of greater 2.
	 * if the result is empty, random select one VM.
	 * if is greater than 2, select the best one by cost in machine.
	 * 
	 * @param undeployedService
	 * 	undeployed service list
	 * @return
	 * 	return service list with minimal service and vm pair
	 */
	protected List<MinimalService> updateMinimalValueService(List<PeterService> undeployedService) {
		List<MinimalService> minimalValueService = computeMinimalValue(undeployedService);
		
		if (minimalValueService.isEmpty()) {
			int minMachineCost = Integer.MAX_VALUE;
			int minServiceCost = Integer.MAX_VALUE;
			PeterService minimalService = null;
			int minimalMachine = -1;
			List<Integer> cantuseVm = new ArrayList<Integer>(); 
			int nodeID=0;
			
			while(minimalMachine==-1){
				System.err.println(cantuseVm);
				for (IRes resnode : simulator.getCluster().getResourcelist()) {
					PeterNode node = (PeterNode) resnode;
					if(!cantuseVm.contains(node.getId())){
						if (node.getTotalComputationCost() <= minMachineCost) {
							minMachineCost = node.getTotalComputationCost();
							nodeID=node.getId();
//							System.out.println(cantuseVm);
						}
					}	
				}
				if(!cantuseVm.contains(nodeID))
					cantuseVm.add(nodeID);
				
				
				for (PeterService service : undeployedService) {
					for (int i = 0; i < simulator.getCluster().getResourcelist().size(); i++) {
						PeterNode node = simulator.getCluster().getResource(i);
						if ( node.getTotalComputationCost() == minMachineCost && service.getCostDiff(i) <= minServiceCost&&!node.getthisVmservicelist().contains(service.getId())) {
							minServiceCost = service.getCostDiff(i);
							minimalMachine = i;
							minimalService = service;
						}
					}
				}
				
				if(minimalMachine==-1){
					minServiceCost=Integer.MAX_VALUE;
					minMachineCost = Integer.MAX_VALUE;
				}
//				System.out.println(cantuseVm);
			}
			
			minimalValueService.add(new MinimalService(minimalService, minimalMachine));
			
			
		}
		else if (minimalValueService.size() > 1) {
			int maxN = Integer.MIN_VALUE;
			
			for (MinimalService minimal : minimalValueService) {
				PeterService service = minimal.getService();
				if ( service.getCostInMachine(minimal.getNodeIndex()) >= maxN ) {
					maxN = service.getCostInMachine(minimal.getNodeIndex());
				}
			}
			
			List <MinimalService> remove = new ArrayList<MinimalService>();
			for (MinimalService minimal : minimalValueService) {
				PeterService service = minimal.getService();
				if (service.getCostInMachine(minimal.getNodeIndex()) < maxN) {
					remove.add(minimal);
				}
			}
			minimalValueService.removeAll(remove);
		}
		return minimalValueService;
	}
	
	/**
	 * calculate every VM cost if service deploy on it.
	 * 
	 * @param undeployedService
	 * 	undeployed service list
	 * @return
	 * 	best service and vm pair according to kcut.
	 */
	protected List<MinimalService> computeMinimalValue(List<PeterService> undeployedService) {
		List<MinimalService> minimalValueService = new ArrayList<MinimalService>();
		int totalLoad = 0;
		int totalVMLoad = 0;
		float averageLoad = 0.0f;
		float averageVMLoad = 0.0f;
		int minimalValue = Integer.MAX_VALUE;
		
		for (PeterService service : enabledServicePool) {
			totalLoad += service.getComputationCostByRate();
//			System.err.println(""+ service.getId() +":" + service.getComputationCostByRate());
		}
		averageLoad = totalLoad / PeterVariable.getInstance().getVmNumber();
		
		for (IRes resnode : platform.getResourcelist()) {
			PeterNode node = (PeterNode) resnode;
			totalVMLoad += node.getTotalComputationCost();
		}
		averageVMLoad = totalVMLoad / PeterVariable.getInstance().getVmNumber();
		
		for (int i = 0; i < platform.getResourcelist().size(); i++) {
			PeterNode node = (PeterNode) platform.getResource(i);
//			if (node.getTotalComputationCost() <= averageVMLoad) {
				for (PeterService service : undeployedService) {
					if(node.getthisVmservicelist().contains(service.getId()))
						continue;
					int Ext = 0;
					for (TaskLink serviceLink : service.getGlobalServicePool()) {
						if ( node.getDeployService().contains(serviceLink.getNextTask()) ) {
							service.setCostInMachine(i, service.getCostInMachine(i) + serviceLink.getWeight());
						}
//						System.err.println(service.getId(1) + "->" + ((PeterService)serviceLink.getNextTask()).getId(1) + ":" + serviceLink.getWeight());
						Ext += serviceLink.getWeight();
//						System.out.println("Ser:" + (service.getId()+1) + "->" + (serviceLink.getNextTask().getId() +1 ) +" link" + serviceLink.getWeight() + ", value:" + Ext);
//						if (service.getId() == 7) {
//							System.err.println(service.getId() + ":" + serviceLink.getNextTask().getId() + " = " + serviceLink.getWeight());
//						}
					}
					/**
					 * print kcut execution progress
					 */
//					
//					System.out.println("Ext:" + Ext + "-" + "N:" +service.getCostInMachine(i) );
					service.setCostDiff(i, Ext - service.getCostInMachine(i) );
//					System.out.println("Diff( " + (service.getId()+1) + " , " + i + " ) = " + service.getCostDiff(i));
					
					/**
					 * end
					 */
					
					if (service.getCostDiff(i) <= minimalValue ) {
						minimalValue = service.getCostDiff(i);
					}
				}
//			}
		}
		
		
		for (PeterService service : undeployedService) {
			for (int i = 0; i < simulator.getCluster().getResourcelist().size(); i++) {
				if (minimalValue == service.getCostDiff(i)) {
					minimalValueService.add(new MinimalService(service, i));
				}
			}
		}
		
		return minimalValueService;
	}

	@Override
	public void generateSCG(int startDeployServiceId) {
		boolean isAllEmpty = true;
		simulator = (PeterSimulator) ASimulator.getInstance();
		platform = simulator.getCluster();
		undeployedService.addAll(enabledServicePool);
		
		for(IRes tnode: platform.getResourcelist()){
			PeterNode node = (PeterNode) tnode;
			if(!node.getDeployService().isEmpty()){
				isAllEmpty = false;
				break;
			}
		}
		for (PeterService service : undeployedService) {
			if (service.getId() == startDeployServiceId-1 && isAllEmpty) {
				((PeterNode) platform.getResource(0)).addDeployService(service);
				undeployedService.remove(service);
				break;
			}
		}
		for (IAttribute workflow : simulator.getWorkflowset()) {
			for (IDepend task : ((IWorkflow) workflow).getTaskList()) {
				PeterService service = ((IPeterAttribute) task).getService();
				for (TaskLink nextTask : task.getChildTaskLink()) {
					PeterService nextService = ((IPeterAttribute) nextTask.getNextTask()).getService();
					int communcationCost = service.getCommunicationCost();
					/**
					 * TODO communication did not use the largest 
					 */
//					if (communcationCost < nextService.getCommunicationCost()) {
//						communcationCost = nextService.getCommunicationCost();
//					}
					boolean isAdded = false;
					for (TaskLink nextServiceLink : service.getNextServicePool()){
						if ( nextServiceLink.getNextTask() == nextService ) {
							isAdded = true;
							nextServiceLink.setWeight(nextServiceLink.getWeight() + communcationCost);
							break;
						}
					}
					if (!isAdded) {
						service.addNextServicePool(new TaskLink(communcationCost, nextService));
						nextService.addPreviousServicePool(new TaskLink(communcationCost, service));
					}
					else {
						for (TaskLink preServiceLink : nextService.getPreviousServicePool()) {
							if (preServiceLink.getNextTask() == service) {
								preServiceLink.setWeight( preServiceLink.getWeight() + communcationCost );
								break;
							}
						}
					}
//					System.err.println("S." + service.getId() + "->S." + nextService.getId() + " Co:" + communcationCost);
				}
			}
		}
		
		
	}

	@Override
	public List<PeterService> getEnabledServicePool() {
		return this.enabledServicePool;
	}

	@Override
	public List<PeterService> getUndeployedService() {
		return undeployedService;
	}
	
}


