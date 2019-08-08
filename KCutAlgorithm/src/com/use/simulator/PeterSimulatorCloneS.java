package com.use.simulator;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.use.ALauncher;
import com.use.queue.QueueType;
import com.use.queue.event.Event;
import com.use.queue.event.EventType;
import com.use.resource.IRes;
import com.use.resource.IResPeter;
import com.use.resource.PeterNode;
import com.use.resource.info.informIResNode;
import com.use.workflow.IWorkflow;
import com.use.workflow.service.PeterService;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.IPeterAttribute;
import com.use.workflow.task.PeterTask;
import com.use.workflow.task.TaskLink;

/**
 * clone service with standalone kcut
 * first kcut original service, then kcut clone service.
 * 
 * @author peter
 *
 */
public class PeterSimulatorCloneS extends PeterSimulator {
	private List<PeterService> needCloneService;
	
	private int clonedVMID;

	public PeterSimulatorCloneS() throws Exception {
		super();
	}

	@Override
	protected void extraVM() throws Exception {
		if (variable.isExtraVM()) {
			List<PeterNode> resourceList = this.getCluster().getResourcelist();
			int index = resourceList.get(resourceList.size() - 1).getId() + 1;
			clonedVMID = index;
			for (int i = 0; i < variable.getExtraVMNumber(); i++) {
				/**
				 * index++ use vmList size to compute
				 */
				informIResNode resource = new informIResNode(index++);
				PeterNode node = new PeterNode(resource);
				this.getCluster().getResourcelist().add(node);
			}
		}
		variable.getDeploy().getEnabledServicePool().clear();
		cloneService();
		variable.getDeploy().generateSCG(13);
		for(PeterService clonedService:clonedService)
			for(TaskLink clonedServiceLink:clonedService.getGlobalServicePool()){
				PeterService nextservice=(PeterService) clonedServiceLink.getNextTask();
				nextservice.addGlobalServicePool(new TaskLink(clonedServiceLink.getWeight(),clonedService));
			}
		variable.getDeploy().deploy();
		
//		for(PeterService service : clonedService){
//			PeterNode node = this.getCluster().getResource(clonedVMID);
//			node.addDeployService(service);
//		}
		 
//		int tmp = clonedVMID;
//		for(PeterService service : clonedService){
//			variable.getDeploy().getEnabledServicePool().add(service);
//			for (int i = clonedVMID; i < resourceList.size(); i++) {
//				PeterNode resource = resourceList.get(tmp++);
//				service.getVmStoreList().add(resource);
//			}
//		}
	
	}
	
	@Override
	protected void cloneService() throws Exception {
		if (variable.isCloneService()) {
			needCloneService = Arrays.asList(
				/**
				 * 1000 rate
//				 */
//				servicePool.get(4),servicePool.get(4), servicePool.get(6), servicePool.get(10),servicePool.get(11),servicePool.get(12),servicePool.get(12),
//				servicePool.get(13),servicePool.get(14),servicePool.get(14),servicePool.get(15),servicePool.get(17),servicePool.get(18)
				
				/**
				 * formula VM5
				 */
//				servicePool.get(4), servicePool.get(4),servicePool.get(4),servicePool.get(4),servicePool.get(6),
//				servicePool.get(6),servicePool.get(6),servicePool.get(7),servicePool.get(9),servicePool.get(10)
//				,servicePool.get(10),servicePool.get(11),servicePool.get(11)
//				
//				,servicePool.get(12),servicePool.get(12),servicePool.get(12),servicePool.get(12)
//				,servicePool.get(13),servicePool.get(13),servicePool.get(13)
//				,servicePool.get(14),servicePool.get(14),servicePool.get(14),servicePool.get(14)
//				,servicePool.get(15),servicePool.get(15),servicePool.get(15),servicePool.get(16),servicePool.get(17)
//				,servicePool.get(17),servicePool.get(18),servicePool.get(18),servicePool.get(18)
				
				/**
				 * formula VM6
				 */
//				servicePool.get(4), servicePool.get(4),servicePool.get(4),servicePool.get(4),servicePool.get(6),
//				servicePool.get(6),servicePool.get(6),servicePool.get(7),servicePool.get(9),servicePool.get(10)
//				,servicePool.get(10),servicePool.get(11),servicePool.get(11)
//				
//				,servicePool.get(12),servicePool.get(12),servicePool.get(12),servicePool.get(12)
//				,servicePool.get(13),servicePool.get(13),servicePool.get(13)
//				,servicePool.get(14),servicePool.get(14),servicePool.get(14),servicePool.get(14)
//				,servicePool.get(15),servicePool.get(15),servicePool.get(15),servicePool.get(16),servicePool.get(17)
//				,servicePool.get(17),servicePool.get(18),servicePool.get(18),servicePool.get(18)
//				,servicePool.get(4),servicePool.get(6),servicePool.get(7),servicePool.get(10),servicePool.get(11)
//				,servicePool.get(12),servicePool.get(14),servicePool.get(15),servicePool.get(16)
				
				/**
				 * formula VM7
				 * 
				 */
//				servicePool.get(4), servicePool.get(4),servicePool.get(4),servicePool.get(4),servicePool.get(6),
//				servicePool.get(6),servicePool.get(6),servicePool.get(7),servicePool.get(9),servicePool.get(10)
//				,servicePool.get(10),servicePool.get(11),servicePool.get(11)
//				
//				,servicePool.get(12),servicePool.get(12),servicePool.get(12),servicePool.get(12)
//				,servicePool.get(13),servicePool.get(13),servicePool.get(13)
//				,servicePool.get(14),servicePool.get(14),servicePool.get(14),servicePool.get(14)
//				,servicePool.get(15),servicePool.get(15),servicePool.get(15),servicePool.get(16),servicePool.get(17)
//				,servicePool.get(17),servicePool.get(18),servicePool.get(18),servicePool.get(18)
//				,servicePool.get(4),servicePool.get(6),servicePool.get(7),servicePool.get(10),servicePool.get(11)
//				,servicePool.get(12),servicePool.get(14),servicePool.get(15),servicePool.get(16)
//				
//				,servicePool.get(3),servicePool.get(4),servicePool.get(6),servicePool.get(8),servicePool.get(9)
//				,servicePool.get(11),servicePool.get(12),servicePool.get(13),servicePool.get(14),servicePool.get(15)
//				,servicePool.get(17),servicePool.get(18)
				/**
				 * formula VM8
				 */
//				servicePool.get(4), servicePool.get(4),servicePool.get(4),servicePool.get(4),servicePool.get(6),
//				servicePool.get(6),servicePool.get(6),servicePool.get(7),servicePool.get(9),servicePool.get(10)
//				,servicePool.get(10),servicePool.get(11),servicePool.get(11)
//				
//				,servicePool.get(12),servicePool.get(12),servicePool.get(12),servicePool.get(12)
//				,servicePool.get(13),servicePool.get(13),servicePool.get(13)
//				,servicePool.get(14),servicePool.get(14),servicePool.get(14),servicePool.get(14)
//				,servicePool.get(15),servicePool.get(15),servicePool.get(15),servicePool.get(16),servicePool.get(17)
//				,servicePool.get(17),servicePool.get(18),servicePool.get(18),servicePool.get(18)
//				,servicePool.get(4),servicePool.get(6),servicePool.get(7),servicePool.get(10),servicePool.get(11)
//				,servicePool.get(12),servicePool.get(14),servicePool.get(15),servicePool.get(16)
//				
//				,servicePool.get(3),servicePool.get(4),servicePool.get(6),servicePool.get(8),servicePool.get(9)
//				,servicePool.get(11),servicePool.get(12),servicePool.get(13),servicePool.get(14),servicePool.get(15)
//				,servicePool.get(17),servicePool.get(18)
//				
//				,servicePool.get(4),servicePool.get(6),servicePool.get(7),servicePool.get(10)
//				,servicePool.get(12),servicePool.get(13),servicePool.get(14),servicePool.get(15)
				
				/**
				 * formula VM9
				 */
//				servicePool.get(4), servicePool.get(4),servicePool.get(4),servicePool.get(4),servicePool.get(6),
//				servicePool.get(6),servicePool.get(6),servicePool.get(7),servicePool.get(9),servicePool.get(10)
//				,servicePool.get(10),servicePool.get(11),servicePool.get(11)
//				
//				,servicePool.get(12),servicePool.get(12),servicePool.get(12),servicePool.get(12)
//				,servicePool.get(13),servicePool.get(13),servicePool.get(13)
//				,servicePool.get(14),servicePool.get(14),servicePool.get(14),servicePool.get(14)
//				,servicePool.get(15),servicePool.get(15),servicePool.get(15),servicePool.get(16),servicePool.get(17)
//				,servicePool.get(17),servicePool.get(18),servicePool.get(18),servicePool.get(18)
//				,servicePool.get(4),servicePool.get(6),servicePool.get(7),servicePool.get(10),servicePool.get(11)
//				,servicePool.get(12),servicePool.get(14),servicePool.get(15),servicePool.get(16)
//				
//				,servicePool.get(3),servicePool.get(4),servicePool.get(6),servicePool.get(8),servicePool.get(9)
//				,servicePool.get(11),servicePool.get(12),servicePool.get(13),servicePool.get(14),servicePool.get(15)
//				,servicePool.get(17),servicePool.get(18)
//				
//				,servicePool.get(4),servicePool.get(6),servicePool.get(7),servicePool.get(10)
//				,servicePool.get(12),servicePool.get(13),servicePool.get(14),servicePool.get(15)
//				
//				,servicePool.get(4),servicePool.get(6),servicePool.get(11),servicePool.get(12)
//				,servicePool.get(14),servicePool.get(15),servicePool.get(16),servicePool.get(17),servicePool.get(18)
				/**
				 * formula VM10
				 */
//				servicePool.get(4), servicePool.get(4),servicePool.get(4),servicePool.get(4),servicePool.get(6),
//				servicePool.get(6),servicePool.get(6),servicePool.get(7),servicePool.get(9),servicePool.get(10)
//				,servicePool.get(10),servicePool.get(11),servicePool.get(11)
//				
//				,servicePool.get(12),servicePool.get(12),servicePool.get(12),servicePool.get(12)
//				,servicePool.get(13),servicePool.get(13),servicePool.get(13)
//				,servicePool.get(14),servicePool.get(14),servicePool.get(14),servicePool.get(14)
//				,servicePool.get(15),servicePool.get(15),servicePool.get(15),servicePool.get(16),servicePool.get(17)
//				,servicePool.get(17),servicePool.get(18),servicePool.get(18),servicePool.get(18)
//				,servicePool.get(4),servicePool.get(6),servicePool.get(7),servicePool.get(10),servicePool.get(11)
//				,servicePool.get(12),servicePool.get(14),servicePool.get(15),servicePool.get(16)
//				
//				,servicePool.get(3),servicePool.get(4),servicePool.get(6),servicePool.get(8),servicePool.get(9)
//				,servicePool.get(11),servicePool.get(12),servicePool.get(13),servicePool.get(14),servicePool.get(15)
//				,servicePool.get(17),servicePool.get(18)
//				
//				,servicePool.get(4),servicePool.get(6),servicePool.get(7),servicePool.get(10)
//				,servicePool.get(12),servicePool.get(13),servicePool.get(14),servicePool.get(15)
//				
//				,servicePool.get(4),servicePool.get(6),servicePool.get(11),servicePool.get(12)
//				,servicePool.get(14),servicePool.get(15),servicePool.get(16),servicePool.get(17),servicePool.get(18)
//				
//				,servicePool.get(4),servicePool.get(9),servicePool.get(10),servicePool.get(12),servicePool.get(13),servicePool.get(14)
//				,servicePool.get(18)
				
				/**
				 * ILASD and RLASD
				 */
				// ILASD ¡¾
//				servicePool.get(4), servicePool.get(6), servicePool.get(12), servicePool.get(13), servicePool.get(14),servicePool.get(15)
//				servicePool.get(4), servicePool.get(12), servicePool.get(14)   // best original method clone (RLASD)
//				,servicePool.get(9), servicePool.get(16),servicePool.get(18) // join top 3S
//				,servicePool.get(0),servicePool.get(5),servicePool.get(2),servicePool.get(8)
//				,servicePool.get(1), servicePool.get(3),servicePool.get(7),servicePool.get(11),servicePool.get(17)
//				,servicePool.get(7),servicePool.get(11),servicePool.get(17) //fork top 3S
				
				/**
				 * case 5 1300request
				 */
				
//				servicePool.get(4), servicePool.get(6), servicePool.get(12), servicePool.get(14), servicePool.get(15)
				
				/**
				 * case 7 1700 request
				 */
//				servicePool.get(4), servicePool.get(6), servicePool.get(11), servicePool.get(12)
//				, servicePool.get(13), servicePool.get(14),servicePool.get(15), servicePool.get(18)
				
				/**
				 * case 8 1900 request
				 */
//				servicePool.get(4), servicePool.get(6), servicePool.get(7), servicePool.get(10), servicePool.get(11), servicePool.get(12)
//				, servicePool.get(13), servicePool.get(14),servicePool.get(15),servicePool.get(16),servicePool.get(17), servicePool.get(18)
				
				/**
				 * duplicated rate up
				 */
//				,servicePool.get(4),
//				servicePool.get(6),servicePool.get(10),servicePool.get(11),
//				servicePool.get(12),servicePool.get(13),servicePool.get(14),servicePool.get(15),servicePool.get(17),
//				servicePool.get(18)
//				,servicePool.get(4),
//				servicePool.get(6),servicePool.get(10),servicePool.get(11),
//				servicePool.get(12),servicePool.get(13),servicePool.get(14),servicePool.get(15),servicePool.get(17),
//				servicePool.get(18)
					
//				servicePool.get(4), servicePool.get(6), servicePool.get(12), servicePool.get(13), servicePool.get(14),servicePool.get(15)
//				servicePool.get(4),servicePool.get(4),servicePool.get(4),servicePool.get(4),servicePool.get(4)	
					/*
					 * 
					 */
					servicePool.get(1),
					servicePool.get(2),
					servicePool.get(4),
					servicePool.get(4),
					servicePool.get(6),
					servicePool.get(6),
					servicePool.get(10),
					servicePool.get(11),
					servicePool.get(12),
					servicePool.get(12),
					servicePool.get(13),
					servicePool.get(14),
					servicePool.get(14),
					servicePool.get(14),
					servicePool.get(15),
					servicePool.get(15),
					servicePool.get(18)
					/*/
					 * teststart
					 */
//					        servicePool.get(3),
//							servicePool.get(4),
//							servicePool.get(4),
//							servicePool.get(4),
//							servicePool.get(4),
//							servicePool.get(4),
//							servicePool.get(4),
//							servicePool.get(4),
//							servicePool.get(4),
//							servicePool.get(4),
//							servicePool.get(6),
//							servicePool.get(6),
//							servicePool.get(6),
//							servicePool.get(6),
//							servicePool.get(6),
//							servicePool.get(6),
//							servicePool.get(6),
//							servicePool.get(6),
//							servicePool.get(7),
//							servicePool.get(7),
//							servicePool.get(7),
//							servicePool.get(8),
//							servicePool.get(9),
//							servicePool.get(9),
//							servicePool.get(9),
//							servicePool.get(10),
//							servicePool.get(10),
//							servicePool.get(10),
//							servicePool.get(10),
//							servicePool.get(10),
//							servicePool.get(11),
//							servicePool.get(11),
//							servicePool.get(11),
//							servicePool.get(11),
//							servicePool.get(11),
//							servicePool.get(12),
//							servicePool.get(12),
//							servicePool.get(12),
//							servicePool.get(12),
//							servicePool.get(12),
//							servicePool.get(12),
//							servicePool.get(12),
//							servicePool.get(12),
//							servicePool.get(12),
//							servicePool.get(13),
//							servicePool.get(13),
//							servicePool.get(13),
//							servicePool.get(13),
//							servicePool.get(13),
//							servicePool.get(13),
//							servicePool.get(14),
//							servicePool.get(14),
//							servicePool.get(14),
//							servicePool.get(14),
//							servicePool.get(14),
//							servicePool.get(14),
//							servicePool.get(14),
//							servicePool.get(14),
//							servicePool.get(14),
//							servicePool.get(15),
//							servicePool.get(15),
//							servicePool.get(15),
//							servicePool.get(15),
//							servicePool.get(15),
//							servicePool.get(15),
//							servicePool.get(15),
//							servicePool.get(15),
//							servicePool.get(16),
//							servicePool.get(16),
//							servicePool.get(16),
//							servicePool.get(17),
//							servicePool.get(17),
//							servicePool.get(17),
//							servicePool.get(17),
//							servicePool.get(18),
//							servicePool.get(18),
//							servicePool.get(18),
//							servicePool.get(18),
//							servicePool.get(18),
//							servicePool.get(18)
					/*/
					 * tsetend
					 */
				);
			
			/**
			 * cloned service to kcut
			 */
			for (PeterService service : needCloneService) {
				PeterService newService = (PeterService) service.clone();
				// ordinary code part¡õ
//				service.addNextServicePool(new TaskLink(-10000000, newService));
				// edit and test part¡õ
				// newService.addNextServicePool(new TaskLink(-1000000,
				// service));

//				newService.addPreviousServicePool(new TaskLink(-10000000, service));
				variable.getDeploy().getEnabledServicePool().add(newService);
				clonedService.add(newService);
			}
		}
	}
	
	@Override
	protected IWorkflow selectWorkflowByRandom() {
		if (variable.isBalanceRequest()) {
			return super.selectWorkflowByRandom();
		}
		return randomWorkflowRange(selectService.nextInt(500));
	}

	@Override
	protected void simulateFinish() throws Exception {
		/**
		 * Display DAG request Number
		 */
//		if (ALauncher.isLogEnable()) {
			int[] requestWorkflowlist = new int[fullTaskList.size()];
			for (int i = 0; i < requestWorkflow.size(); i++) {
				requestWorkflowlist[requestWorkflow.get(i).getId()]++;
			}
			for (int i = 0; i < requestWorkflowlist.length; i++) {
				System.err.println("Dag:" + i + "  requestNumber:" + requestWorkflowlist[i]);
			}
//		}

		// }
		// } 
			/**
			 * Display each ServiceRequsetNumber
			 */
		int Number = 0;
		int[] requestService = new int[servicePool.size()];
		for (int i = 0; i < requestWorkflow.size(); i++) {
//			 System.err.println("***"+ requestWorkflow.size());
			for (int j = 0; j < requestWorkflow.get(i).getTaskList().size(); j++) {
				Number += requestWorkflow.get(i).getTaskList().size();
//				 System.err.println("**"+ Number);
				requestService[((PeterTask) requestWorkflow.get(i).getTaskList().get(j)).getService().getId()]++;
				
				if (ALauncher.isLogEnable()) {
					for (IDepend tmp : requestWorkflow.get(i).getTaskList()) {
						IPeterAttribute task = (IPeterAttribute) tmp;
						if (task.getBelongRes().getId() > 4) {
							System.err.println("DEBUG:" + requestWorkflow.get(i).getId() + "," + task.getId());
						}
					}
				}
			
			}
		}
		
		
        
        
		List<PeterNode> list = this.getCluster().getResourcelist();
		for (int i = clonedVMID; i < list.size(); i++) {
			System.out.println(i + ":" + list.get(i).getRunningQueue().size());
		}

		File file = new File("log/" + classIdentify.replace(" + ", "-"));
		if (!file.exists()) {
			file.createNewFile();
		}
		if (ALauncher.isLogEnable()){
		FileWriter writer = new FileWriter(file, false);
		for (IWorkflow workflow : requestWorkflow ) {
			for (IDepend tmp : workflow.getTaskList()) {
					IPeterAttribute task = (IPeterAttribute) tmp;
					writer.write("workflowID:" + task.getBelongWorkflow().getId() + ",taskID:" + task.getId() + ",resourceID:" + task.getBelongRes().getId() 
						+ ",SubmitTime:" + task.getSubmitTime() + ",CommunicationCost:" + task.getService().getCommunicationCost() + ",StartTime:" + task.getStartTime() 
						+ ",FinishTime:" + + task.getFinishTime() + ",Rank:" + task.getRank()
						+ ",HexString:" + Integer.toHexString(System.identityHashCode(task.getBelongWorkflow())) + "\n" );
				System.err.println("workflowID:" + task.getBelongWorkflow().getId() + ",taskID:" + task.getId() + ",resourceID:" + task.getBelongRes().getId() 
					+ ",SubmitTime:" + task.getSubmitTime() + ",CommunicationCost:" + task.getService().getCommunicationCost() + ",StartTime:" + task.getStartTime() 
					+ ",FinishTime:" + + task.getFinishTime() + ",Rank:" + task.getRank()
					+ ",HexString:" + Integer.toHexString(System.identityHashCode(task.getBelongWorkflow())) );
			}
			System.err.println();
			writer.write("\n");
		}
		writer.close();
		}
		super.simulateFinish();
		/**
		 * rank below
		 */
		if (variable.isUseRanking()) {
		for (IRes node : cluster.getResourcelist()) {
			PeterNode resource = (PeterNode) node;
			if (!resource.getWaitingQueue().isEmpty()) {
				System.err.println("still job in waiting queue: " + resource.getId());
			}
		}
		}
	}

	@Override
	protected void submitVMTask(IPeterAttribute finishedTask) {
		/**
		 * rank below
		 */
		if (variable.isUseRanking()) {
			IResPeter resource = finishedTask.getBelongRes();
			if (resource != null && !resource.getWaitingQueue().isEmpty()) {
				IPeterAttribute task = (IPeterAttribute) resource.getWaitingQueue().get(0);
				if (isTaskcanSubmit(task)) {
					task.setInserted(true);
					resource.getWaitingQueue().remove(task);
					Event startEvent = new Event(EventType.start, task.getEST(), task);
					Event endEvent = new Event(EventType.end, task.getEFT(), task);
					getQueue().addToQueue(QueueType.Action, startEvent);
					getQueue().addToQueue(QueueType.Action, endEvent);
					task.setBelongRes(resource);
				}
			}
		}
		
		/**
		 * rank end
		 */
	}
}
