package com.use.simulator;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.use.ALauncher;
import com.use.clone.DeepCopy;
import com.use.config.PeterVariable;
import com.use.queue.MixQueue;
import com.use.queue.QueueType;
import com.use.queue.SubmitQueueSJF;
import com.use.queue.event.Event;
import com.use.queue.event.EventType;
import com.use.resource.IRes;
import com.use.resource.IResPeter;
import com.use.resource.PeterNode;
import com.use.scheduler.deploy.ASRG;
import com.use.workflow.AWorkflow;
import com.use.workflow.IWorkflow;
import com.use.workflow.ranking.PeterBottomAmountRank;
import com.use.workflow.ranking.PeterBottomRank;
import com.use.workflow.service.PeterActionPerform;
import com.use.workflow.service.PeterService;
import com.use.workflow.task.DependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.IPeterAttribute;
import com.use.workflow.task.TaskLink;

public class PeterSimulator extends EventBaseSimulator {
	protected PeterActionPerform servicePool = new PeterActionPerform();
	protected PeterVariable variable;
	// another algorithm
	// private Random selectEndService = new Random();
	protected Random selectService = new Random(20);
	protected List<IWorkflow> requestWorkflow = new ArrayList<IWorkflow>();
	protected  List<PeterService> clonedService = new ArrayList<PeterService>();
	protected AssignService assignService;
	protected CalculateTotalCost calculateTotalCost;
	protected String classIdentify;

	public PeterSimulator() throws Exception {
		instance = this;
		variable = new PeterVariable();
		assignService = new AssignService(servicePool, variable);
		calculateTotalCost=new CalculateTotalCost();
		if (variable.isRankingQueue())
			this.globalWaitingQueue = new SubmitQueueSJF<IAttribute>(); // SJF
	}

	protected void kCutAlgorithm(int startService) throws Exception {
		variable.getDeploy().generateSCG(startService);
//		for(PeterService clonedService:clonedService){
//			for(TaskLink clonedServiceLink:clonedService.getGlobalServicePool()){
//				PeterService nextservice=(PeterService) clonedServiceLink.getNextTask();
//				nextservice.addGlobalServicePool(new TaskLink(clonedServiceLink.getWeight(),clonedService));
//			}
//		}	
//		for(int i=0;i<variable.getDeploy().getEnabledServicePool().size();i++){
//			for(int j=i;j<variable.getDeploy().getEnabledServicePool().size();j++){
//				PeterService firstService =variable.getDeploy().getEnabledServicePool().get(i);
//				PeterService EndService =variable.getDeploy().getEnabledServicePool().get(j);
//				if(firstService.getId()==EndService.getId()&&i!=j){
////					firstService.addGlobalServicePool(new TaskLink(-firstService.getComputationCost(), EndService));
//				}
//			}
//		}		
		variable.getDeploy().deploy();
	}

	@Override
	protected void submitTaskEvent() throws Exception {
		
		
//		cloneService();
		/**
		 * Kcut initial sevice id need -1
		 */
		kCutAlgorithm(13);
		// request number//
		int workflowTime = 0;
		for (IAttribute tmp : fullTaskList) {
			IWorkflow workflow = (IWorkflow) tmp;
			for (IDepend task : workflow.getTaskList()) {
				task.setBelongWorkflow(workflow);
			}
		}
		/**
		 * Ranking method
		 */
		PeterBottomRank bottomRank = new PeterBottomRank();
//		PeterBottomAmountRank bottomAmountRank = new PeterBottomAmountRank();
			extraVM();
		long oldSubmitTime = 0;
		for (int i = 0; i < variable.getRequestNumber(); i++) {
			// Event submitEvent = new Event(EventType.submit, workflowTime,
			// (IWorkflow)DeepCopy.copy(fullTaskList.get(1)));
			Random interarrivalTime = new Random(1);
			long normalDisTime = (long) (oldSubmitTime + variable.getInterArrivalTimeMean() + interarrivalTime.nextGaussian() * variable.getInterArrivalTimeSeed());
			IWorkflow assignWorkflow = (IWorkflow) DeepCopy.copy(selectWorkflowByRandom());
			requestWorkflow.add(assignWorkflow);
			/**
			 * requsetWorkflow : for print information
			 */

			/**
			 * ranking related
			 */
			 bottomRank.ranking((AWorkflow) assignWorkflow);
//			bottomAmountRank.ranking((AWorkflow) assignWorkflow);

			// ((SimpleWorkflow)assignWorkflow).setRankingType(new
			// PeterBottomRank());
			// ((SimpleWorkflow)assignWorkflow).getRankingType();
			Event submitEvent = new Event(EventType.submit, normalDisTime, assignWorkflow);
			this.queue.addToQueue(QueueType.Event, submitEvent);
			assignService.reassignService(assignWorkflow, assignWorkflow.getId());
			// reAssignService(((IAttribute) assignWorkflow),
			// assignWorkflow.getId());
			// workflowTime += 10;
			// randGaussian += randGaussian;
			oldSubmitTime = normalDisTime;
		}
	}

	protected IWorkflow randomWorkflowRange(int number) {
		if (number >= 0 && number < 30) {
			return (IWorkflow) fullTaskList.get(0);
		} else if (number >= 30 && number < 150) {
			return (IWorkflow) fullTaskList.get(1);
		} else if (number >= 150 && number < 210) {
			return (IWorkflow) fullTaskList.get(2);
		} else if (number >= 210 && number < 300) {
			return (IWorkflow) fullTaskList.get(3);
		} else if (number >= 300 && number < 388) {
			return (IWorkflow) fullTaskList.get(4);
		} else if (number >= 388 && number < 390) {
			return (IWorkflow) fullTaskList.get(5);
		} else if (number >= 390 && number < 400) {
			return (IWorkflow) fullTaskList.get(6);
		} else if (number >= 400 && number < 420) {
			return (IWorkflow) fullTaskList.get(7);
		} else if (number >= 420 && number < 495) {
			return (IWorkflow) fullTaskList.get(8);
		} else if (number >= 495 && number < 500) {
			return (IWorkflow) fullTaskList.get(9);
		}
		return null;
//		return (IWorkflow) fullTaskList.get(9);
	}

	/**
	 * add extra VM and put clone service on it
	 * 
	 * @throws Exception
	 */
	protected void extraVM() throws Exception {
	}

	protected void cloneService() throws Exception {
	}
	
	protected IWorkflow selectWorkflowByRandom() {
		if (!variable.isBalanceRequest()) {
			return randomWorkflowRange(selectService.nextInt(500));
		}
		return (IWorkflow) fullTaskList.get(selectService.nextInt(fullTaskList.size()));
	}

	@Override
	protected void initlize() throws Exception {
		// get from PeterGenerator
		servicePool.alloc();
		getCluster().alloc();
		fullTaskList = this.generator.generate();
		this.queue = new MixQueue();
		assignService.assignService(fullTaskList);
		// assignService(fullTaskList);
		ASRG srg = variable.getParallemMethod();
		srg.developSRG(fullTaskList);
		submitTaskEvent();

		/**
		 * display DAG
		 */
		if (ALauncher.isLogEnable()) {
			for (int i = 0; i < fullTaskList.size(); i++) {
				System.err.println("== **DAG " + (i) + " ** ==");
				IWorkflow workflow = (IWorkflow) fullTaskList.get(i);
				for (IAttribute task : workflow.getTaskList()) {
					System.err.print((task.getId() + 1) + ": ["); // ID +-
																	// problem
					String msg = "";
					if (!((DependTask) task).getParentTaskLink().isEmpty()) {
						for (TaskLink link : ((DependTask) task).getParentTaskLink()) {
							msg += (link.getNextTask().getId() + 1) + ", ";
						}
					}
					msg += "]";
					System.err.println(msg.replace(", ]", "]"));
				}
				// ID +1 -1
				for (IAttribute tmp : workflow.getTaskList()) {
					IPeterAttribute task = (IPeterAttribute) tmp;
					System.err.print("Service: " + (task.getService().getId()) + " , " + "comptime: " + task.getService().getComputationCost() + " , " + (task.getId()) + ": [");
					String msg = "";
					for (TaskLink link : ((DependTask) task).getChildTaskLink()) {
						msg += (link.getNextTask().getId()) + ", ";
					}
					msg += "]";
					System.err.println(msg.replace(", ]", "]") + " ,commtime: " + task.getService().getCommunicationCost());
				}
			}
		}
		
		/**
		 * simple workflow log
		 */
		if (ALauncher.isLogEnable()) {	
			for (int i = 0; i < fullTaskList.size(); i++) {
//				System.err.println("== **DAG " + (i) + " ** ==");
				IWorkflow workflow = (IWorkflow) fullTaskList.get(i);
				for (IAttribute task : workflow.getTaskList()){
					for (TaskLink link : ((DependTask) task).getChildTaskLink()){
					System.err.println(i + ":" + task.getId() + ":" + link.getNextTask().getId());
					}
				}
			}
		}
		
		classIdentify = this.getClass().getSimpleName() + " + " + scheduler.getClass().getSimpleName() + " + ";
		if (variable.isBalanceRequest()) {
			classIdentify += "BalanceRequest";
		} else {
			classIdentify += "UnbalanceRequest";
		}
		classIdentify += " + ";
		if (variable.isCloneService()) {
			classIdentify += "ClonedService";
		} else {
			classIdentify += "NoClonedService";
		}
		classIdentify += " + ";
		if (variable.isUseRanking()) {
			classIdentify += "SortByRank";
		} else {
			classIdentify += "SortByFIFO";
		}
		classIdentify += " + " + variable.getVmNumber() + ":" + variable.getExtraVMNumber() + " + " + variable.getRequestNumber();
	}

	@Override
	protected void execution(Event event) throws Exception {
		 if (ALauncher.isLogEnable()) {
		if (event.getTask() instanceof IDepend) {
			IPeterAttribute task = event.getTask();
			EventType submitType = event.getEventType().submit;
			EventType submitType2 = event.getEventType().end;
//			if (event.getEventType() != submitType && event.getEventType() != submitType2) {
//				if (event.getEventType() != submitType) { 
				File file = new File("C:\\Users\\peter.FEDERER.001\\Desktop\\logcheck\\log.txt");
				if (!file.isFile()) {
					file.createNewFile();
				}
				FileWriter log = new FileWriter(file, true);
				// if(task.getBelongWorkflow().getId() == 8 ||
				// task.getBelongWorkflow().getId() == 7){
				log.write("        W" + task.getBelongWorkflow().getId() + ":T" + task.getId() + ":S" + task.getService().getId() + " ¡÷ " + event.getEventType() + "-" + event.getTime());
				// log.write("  VM." + task.getBelongRes().getId() +" @:"+
				// System.identityHashCode(task) + "\r\n");
//				log.write("  VM." + task.getBelongRes().getId() + "\r\n");
				//
				// log.write("" + task.getBelongWorkflow().getId() + ":" +
				// task.getId() + ":" + task.getService().getId() + ":" +
				// event.getEventType() + ":" + event.getTime());
				// // log.write("  VM." + task.getBelongRes().getId()
				// +" @:"+
				// System.identityHashCode(task) + "\r\n");
				// log.write(":" + task.getBelongRes().getId() + "\r\n");

				// }
				//
				/**
				 * print each dag rank
				 */
//			if (event.getEventType() == submitType2) {
//				if (task.getBelongWorkflow().getId() == 8 ) {
//					System.err.println( task.getRank());
//				}
//			}
				/**
				 *  end
				 */
//				 System.err.println("" + task.getBelongWorkflow().getId() + ":" +  task.getId() +
//				 ":" + task.getService().getId() + ":" + event.getEventType() + ":" + event.getTime() + ":" +
//					 task.getBelongRes().getId() + ":" + task.getRank());
//				 
//				 System.err.println("" + task.getBelongWorkflow().getId() + ":" +  task.getId() +
//					 ":" + task.getService().getId() + ":" + event.getEventType() + ":" + event.getTime() + ":" +
//						 task.getBelongRes().getId());
				
//				 /**
//				 * task info
//				 */
				System.err.print("W" + task.getBelongWorkflow().getId() + ":T" + task.getId() + ":S" + task.getService().getId() 
					+ " ¡÷ " + event.getEventType() + "-" + event.getTime());
				// System.err.print(":W-" +
				// task.getBelongWorkflow().getId());
				System.err.print(":rank:" + task.getRank());
				log.close();
				if (task.getBelongRes() != null) {
					System.err.println(" ¡±VM." + task.getBelongRes().getId());
				} else {
					System.err.println();
				}
//				/**
//				 * task info end
//				 */
//			}
//			}
		}
	}	
		// else {
		// System.err.println();
		// }
		// }

		switch (event.getEventType()) {

		case submit:
			if (event.getTask() instanceof IWorkflow) {
				event.getTask().setSubmitTime(this.currentTime);
				Event workflowSubmitQueue = new Event(EventType.submit, this.currentTime, ((IWorkflow) event.getTask()).getTaskList().get(0));
				queue.addToQueue(QueueType.Event, workflowSubmitQueue);
			} else if (event.getTask() instanceof IDepend) {
				IPeterAttribute task = event.getTask();
				task.setSubmitTime(this.currentTime);
				getGlobalWaitingQueue().addToQueue(task);
				if (variable.isUseRanking()) {
					task.getService().getVmStoreList().get(0).getWaitingQueue().addToQueue(task);
				}
				scheduler.schedule();
				submitVMTask(task);
			}
			break;
		case start:
			IPeterAttribute task = event.getTask();
			task.setStarted(true);
			task.setStartTime(this.currentTime);
			getGlobalWaitingQueue().removeJobByObject(task);
			((IResPeter) task.getBelongRes()).getRunningQueue().addToQueue(task);
			break;
		case end:
			event.getTask().setScheduled(true); // is finish(submit)
			event.getTask().setFinishTime(currentTime);

			IPeterAttribute task1 = event.getTask();
			if (ALauncher.isLogEnable()) {
				String msg = task1.getBelongWorkflow().getId() + ":" + event.getTask().getId() + ":" + task1.getBelongRes().getId() + ":" + (task1.getService().getComputationCost()) + ":" + task1.getSubmitTime() + ":" + task1.getStartTime() + ":" + task1.getFinishTime();
				System.err.println(msg);
			}
			for (TaskLink forkJoin : ((IDepend) event.getTask()).getChildTaskLink()) {
				IPeterAttribute childTask = (IPeterAttribute) forkJoin.getNextTask();
				if (childTask.isSubmited() || childTask.isScheduled() || childTask.isStarted()) {
					continue;
				}

				// if (((PeterTask) (IDepend) event.getTask()).getService() !=
				// childTask.getService()) {
				// tryCommunicationCost = ((PeterTask) (IDepend)
				// event.getTask()).getService().getCommunicationCost();
				// }

				// for (TaskLink temp : childTask.getParentTaskLink()) {
				// PeterTask parentTask = (PeterTask) temp.getNextTask();
				// if
				// (!childTask.getService().getVmStoreList().contains(parentTask.getBelongRes())
				// && parentTask.getService().getCommunicationCost() >
				// tryCommunicationCost) {
				// tryCommunicationCost = ((PeterTask) (IDepend)
				// event.getTask()).getService().getCommunicationCost();
				// }
				// if (!temp.getNextTask().isScheduled()) {
				// canSubmitFlag = false;
				// }
				// }
				// if (canSubmitFlag) {
				// Event storeEventQueue = new Event(EventType.submit,
				// this.currentTime + tryCommunicationCost, childTask);
				// this.queue.addToQueue(QueueType.Event, storeEventQueue);
				// childTask.setSubmited(true);
				boolean canSubmit = isTaskcanSubmit(childTask);
				if (canSubmit) {
					submitTask((IPeterAttribute) event.getTask(), childTask);
					// Event storeEventQueue = new Event(EventType.submit,
					// this.currentTime + communicationCost, childTask);
					// this.queue.addToQueue(QueueType.Event, storeEventQueue);
					// childTask.setSubmited(true);
				}
				// }
			}
			scheduler.schedule();
			submitVMTask((IPeterAttribute) event.getTask());
			// System.out.println("Time:" + getCurrentTime() + ",eventType:" +
			// event.getEventType() + ",taskID:" + task1.getId() +
			// ",workflow:" + task1.getBelongWorkflow().getId() + ",resource:" +
			// task1.getBelongRes().getId() +
			// ",SubmitTime:" + task1.getSubmitTime() + ",StartTime:" +
			// task1.getStartTime() + ",FinishTime:" + task1.getFinishTime());
			break;

		}

		/**
		 * print Queue information
		 */
		if (ALauncher.isLogEnable()) {
			System.out.println(this.getCurrentTime() + ":" + event.getEventType());
			for (IRes res : getCluster().getResourcelist()) {
				PeterNode node = (PeterNode) res;
				if (!node.getWaitingQueue().isEmpty()) {
					System.out.print("  " + "VM." + node.getId() + "¡÷");
					for (IAttribute tmp : node.getWaitingQueue()) {
						IPeterAttribute ttask = (IPeterAttribute) tmp;
						System.out.print(" W" + ttask.getBelongWorkflow().getId() + "-T" + ttask.getId() + ",SubT:" + ttask.getSubmitTime() + "¡C");
//							",StaT:" + ttask.getStartTime() + ",FinT:" + ttask.getFinishTime() + "¡C");
					}
					System.out.println();
				}
			}
		}
	}

	protected void submitVMTask(IPeterAttribute finishedTask) {

	}

	protected void submitTask(IPeterAttribute finishedTask, IPeterAttribute childTask) {
		Event storeEventQueue = new Event(EventType.submit, this.currentTime, childTask);
		this.queue.addToQueue(QueueType.Event, storeEventQueue);
		childTask.setSubmited(true);
	}

	protected boolean isTaskcanSubmit(IPeterAttribute childTask) {
		boolean canSubmitFlag = true;
//		int tryCommunicationCost = -1;
		for (TaskLink temp : childTask.getParentTaskLink()) {
			IPeterAttribute tmpparentTask = (IPeterAttribute) temp.getNextTask();
			if (!childTask.getService().getVmStoreList().contains(tmpparentTask.getBelongRes())) {
				/**
				 * TODO should be parent or finished task
				 */
//				tryCommunicationCost = tmpparentTask.getService().getCommunicationCost();
			}
			if (!temp.getNextTask().isScheduled()) {
				canSubmitFlag = false;
//				tryCommunicationCost = -1;
				break;
			}
		}
		return canSubmitFlag;
//		if (canSubmitFlag && tryCommunicationCost == -1) {
//			tryCommunicationCost = 0;
//		}
//		return tryCommunicationCost;
		// return canSubmitFlag;
		// if (canSubmitFlag) {
		// Event storeEventQueue = new Event(EventType.submit, this.currentTime
		// + communicationCost, childTask);
		// this.queue.addToQueue(QueueType.Event, storeEventQueue);
		// childTask.setSubmited(true);
		// // return tryCommunicationCost;
		// }
		// return -1;
	}

	/**
	 * Simulate Finish
	 */
	@Override
	protected void simulateFinish() throws Exception {
		/*
		 * ­pºâCOST
		 */
		int totalCost =calculateTotalCost.calculateCost();
		
        System.err.println("totalCost*****"+totalCost);
        
		/*
		 * 
		 */
        
		// + variable.isBalanceRequest() +
		// " + " + variable.isCloneService() + " + " + variable.isUseRanking() +
		// " + " + variable.getVmNumber() + " + " + variable.getExtraVMNumber();
		System.out.println(classIdentify);
		for (IRes tmp : getCluster().getResourcelist()) {
			IResPeter node = (IResPeter) tmp;
			print(String.valueOf("VM"+tmp.getId()));
			for (PeterService service : node.getDeployService()) {
				print(":" + ((service.getId()))); // ID+- problem
//				print("compu:" + service.get);
			}
			println();
			if (ALauncher.isLogEnable()) {
				System.out.print("VM" + tmp.getId() + " TimeLine:");
				for (int i = 0; i < node.getRunningQueue().size(); i++) {
					IPeterAttribute checkTask = (IPeterAttribute) node.getRunningQueue().get(i);
					System.out.print(",|" + checkTask.getEST());
					System.out.print(" ¡÷" + checkTask.getEFT() + "!|");
					System.out.print("        W" + checkTask.getBelongWorkflow().getId() + ":T" + checkTask.getId() + ":S" + checkTask.getService().getId() + "-" + checkTask.getFinishTime());
				}
				System.out.println();
			}
			
			System.out.println("node loading:" + node.getTotalComputationCost());
		}
		
		int totalcompu = 0;
		for (IRes tmp : getCluster().getResourcelist()) {
			IResPeter node1 = (IResPeter) this.getCluster().getResource(0);
			IResPeter node = (IResPeter) tmp;
			for (PeterService service : node1.getDeployService()) {
				if (service.getId() == 6) {
					int currentCost = service.getComputationCost();
					if (!node.equals(node1)) {
						for (PeterService service1 : node.getDeployService()) {
							if(service1.getId() != 2 && service1.getId() != 16 &&
								service1.getId() != 1 && service1.getId() != 19 &&
								service1.getId() != 8){
							if (service1.getComputationCost() < currentCost) {
								totalcompu += service1.getCommunicationCost();
							} else {
								totalcompu += currentCost;
							}
						}
						}
					}
				}
//				System.err.println("total:" + totalcompu);
			}
		}
		
		
		long totalResponseTime = 0;

		for (IWorkflow workflow : requestWorkflow) {
			totalResponseTime += (workflow.getTaskList().get(workflow.getTaskList().size() - 1).getFinishTime() - workflow.getSubmitTime());
			/**
			 * print each workflow finish time
			 */
			// if (ALauncher.isLogEnable()) {
			// System.out.println(" W:" + workflow.getId() + " time:" +
			// workflow.getTaskList().get(workflow.getTaskList().size()-1).getFinishTime());
			// }

//			System.out.println(" W:" + workflow.getId() + "True Workflow Time:" + (workflow.getTaskList().get(workflow.getTaskList().size() - 1).getFinishTime() - workflow.getSubmitTime()));
//			System.out.println(" W:" + workflow.getId()+"Time" +workflow.getSubmitTime());
		}

		println("AverageResponseTime:" + (totalResponseTime / requestWorkflow.size()));

		// for (int i = 6; i < 8; i++) {
		// PeterTask checkTask = (PeterTask)requestWorkflow.get(i);
		// System.err.print("Workflow-" + checkTask.getBelongWorkflow().getId()
		// + ":Task-" + checkTask.getId() + ":service-"+
		// checkTask.getService().getId() + " resID: " +
		// checkTask.getBelongRes().getId());
		// System.err.println(" EST:" + checkTask.getEST() + " EFT:" +
		// checkTask.getEFT());
		// }
		
		/**
		 * print Service relationship in
		 */
	if (ALauncher.isLogEnable()) {
		PeterSimulator simulator = (PeterSimulator) ASimulator.getInstance();
		for (IAttribute workflow : fullTaskList) {
			for (IDepend task : ((IWorkflow) workflow).getTaskList()) {
				PeterService tmp = ((IPeterAttribute) task).getService();
				for (TaskLink nextTaskLink : tmp.getNextServicePool()) {
					IAttribute childTask = nextTaskLink.getNextTask();
					System.err.println(" Service" + (tmp.getId()+1)
						+" childLinkWeight:" + nextTaskLink.getWeight()
							+" nextService:" + (childTask.getId()+1));
//					for (TaskLink nextServiceLink : taskService.getNextServicePool()) {
//					}
				}
				for (TaskLink nextTaskLink : tmp.getPreviousServicePool()) {
					IAttribute parentTask = nextTaskLink.getNextTask();
					System.err.println(
//						"Workflow:" + workflow.getId() 
						 " Service" + (tmp.getId()+1)
						+" ParentLinkweight:" + nextTaskLink.getWeight()
							+" nextService:" + (parentTask.getId()+1));
				}
			}
		}
	}
	
	}

	public List<IAttribute> getWorkflowset() {
		return this.fullTaskList;
	}

	// private void assignService(List<IAttribute> fullTaskList) {
	// List[] list = new List[] { Arrays.asList(1, 14, 7, 5, 16, 13),
	// Arrays.asList(7, 5, 12, 13, 16, 19, 8), Arrays.asList(19, 12, 15, 11, 11,
	// 5, 5, 15, 19), Arrays.asList(16, 18, 12, 13, 14, 11, 5, 10, 15, 17),
	// Arrays.asList(7, 4, 13, 18, 5, 15, 14, 15, 10, 17), Arrays.asList(11, 4,
	// 18, 12, 13, 5, 14, 7, 6, 6, 17), Arrays.asList(6, 2, 16, 13, 7, 13, 5, 5,
	// 11, 16, 16, 3), Arrays.asList(17, 2, 12, 13, 5, 16, 11, 7, 16, 1, 16, 11,
	// 9),
	// Arrays.asList(7, 8, 14, 18, 15, 15, 14, 13, 7, 13, 19, 16, 9),
	// Arrays.asList(13, 8, 12, 11, 18, 11, 16, 5, 15, 5, 19, 14, 19, 20) };
	// int index = 0;
	//
	// for (IAttribute workflow : fullTaskList) {
	// reAssignService(workflow, index);
	// index++;
	// }
	//
	// for (PeterService service : servicePool) {
	// if (service.isScheduled()) {
	// ((KcutAlgorithm)
	// variable.getDeploy()).getEnabledServicePool().add(service);
	// }
	// }
	// }

	// private void reAssignService(IAttribute fullTaskList, int index) {
	// List[] list = new List[] { Arrays.asList(1, 14, 7, 5, 16, 13),
	// Arrays.asList(7, 5, 12, 13, 16, 19, 8), Arrays.asList(19, 12, 15, 11, 11,
	// 5, 5, 15, 19), Arrays.asList(16, 18, 12, 13, 14, 11, 5, 10, 15, 17),
	// Arrays.asList(7, 4, 13, 18, 5, 15, 14, 15, 10, 17), Arrays.asList(11, 4,
	// 18, 12, 13, 5, 14, 7, 6, 6, 17), Arrays.asList(6, 2, 16, 13, 7, 13, 5, 5,
	// 11, 16, 16, 3), Arrays.asList(17, 2, 12, 13, 5, 16, 11, 7, 16, 1, 16, 11,
	// 9),
	// Arrays.asList(7, 8, 14, 18, 15, 15, 14, 13, 7, 13, 19, 16, 9),
	// Arrays.asList(13, 8, 12, 11, 18, 11, 16, 5, 15, 5, 19, 14, 19, 20) };
	//
	// for (int i = 0; i < ((IWorkflow) fullTaskList).getTaskList().size(); i++)
	// {
	// IPeterAttribute task = (IPeterAttribute) ((IWorkflow)
	// fullTaskList).getTaskList().get(i);
	// PeterService service = servicePool.get((int) list[index].get(i) - 1);
	// task.setService(service);
	// if (!((KcutAlgorithm)
	// variable.getDeploy()).getEnabledServicePool().contains(service)) {
	// service.setScheduled(true);
	// }
	//
	// }
	//
	// }

	
	public int calculateCost()  {
	    PeterSimulator simulator= (PeterSimulator) ASimulator.getInstance();
	    int[] eachServiceCost = new int[PeterVariable.getInstance().getTotalServiceNumber()];
		int[] eachServiceCloneAmount = new int[PeterVariable.getInstance().getTotalServiceNumber()];
		int SRGTotalCost=0;
		for(IRes resnode : simulator.getCluster().getResourcelist()){
			PeterNode node = (PeterNode) resnode;
			for(PeterService service:node.getDeployService()){
				for(TaskLink serviceLink : service.getNextServicePool()){
					if(!node.getDeployService().contains(serviceLink.getNextTask())){
						eachServiceCost[service.getId()]+=serviceLink.getWeight();
					}
				}
				eachServiceCloneAmount[service.getId()]++;
			}
		}
		
		for(int i=0;i<PeterVariable.getInstance().getTotalServiceNumber();i++){
			eachServiceCost[i]/=eachServiceCloneAmount[i];
			SRGTotalCost+=eachServiceCost[i];
		}	
		return SRGTotalCost;
	}
}
