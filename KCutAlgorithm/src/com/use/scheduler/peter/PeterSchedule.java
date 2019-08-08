package com.use.scheduler.peter;

import java.util.ArrayList;
import java.util.List;

import com.use.config.PeterVariable;
import com.use.queue.MixQueue;
import com.use.queue.QueueType;
import com.use.queue.event.ActionQueue;
import com.use.queue.event.Event;
import com.use.queue.event.EventType;
import com.use.resource.IResPeter;
import com.use.resource.PeterNode;
import com.use.scheduler.AScheduler;
import com.use.simulator.ASimulator;
import com.use.simulator.EventBaseSimulator;
import com.use.simulator.PeterSimulator;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.IPeterAttribute;
import com.use.workflow.task.PeterTask;
import com.use.workflow.task.TaskLink;

public class PeterSchedule extends AScheduler {
	EventBaseSimulator instance;

	@Override
	public void schedule() throws Exception {
		instance = ASimulator.getInstance();

		// for (int i=0; i < (instance.getGlobalWaitingQueue()).size(); i++){
		if (PeterVariable.getInstance().isRegret()) {
			for (IAttribute task : instance.getGlobalWaitingQueue()) {
				((MixQueue) ((PeterSimulator) ASimulator.getInstance()).getQueue()).cleanAction(task);
			}
		}

		 
		List<IAttribute> removeList = new ArrayList<IAttribute>();
		for (int x = 0; x < instance.getGlobalWaitingQueue().size(); x++) {
//			long bestEST = Long.MAX_VALUE;
//			long compareCommunicationCost = 0;
//			PeterNode bestResourse = null;
//			PeterNode currentBestResource = null;
			PeterTask singlejob = (PeterTask)instance.getGlobalWaitingQueue().getJobByIndex(x);
////			List<PeterNode> Vm = ASimulator.getInstance().getCluster().getResourcelist();
//			List<PeterNode> Vm = singlejob.getService().getVmStoreList();

//			for (int y = 0; y < Vm.size(); y++) {
//				long VmEST = 0;
//				if (!Vm.get(y).getRunningQueue().isEmpty()) {
//					IAttribute finishedJob = Vm.get(y).getRunningQueue().getJobByIndex((Vm.get(y).getRunningQueue().size()) - 1);
//					if (finishedJob.getFinishTime() > VmEST) {
//						VmEST = finishedJob.getFinishTime();
//						/**
//						 * can not update the bestResource here. 
//						 * it will cause the asynchronous when the current VmEST is not the best EST.
//						 */
//						currentBestResource = Vm.get(y);// add to resource
//					}
//				} else {
//					/**
//					 * can not update the bestResource here. 
//					 * it will cause the asynchronous when the current VmEST is not the best EST.
//					 */
//					currentBestResource = Vm.get(y);
//				}
//
//				
//				for (TaskLink link : ((IDepend) singlejob).getParentTaskLink()) {
//					if (link.getNextTask().getFinishTime() > VmEST) {
//						VmEST = link.getNextTask().getFinishTime();
//					}
//					if (Vm.get(y) != ((PeterTask) link.getNextTask()).getBelongRes()) {
//						if (((PeterTask) link.getNextTask()).getService().getCommunicationCost() > compareCommunicationCost) {
//							compareCommunicationCost = ((PeterTask) link.getNextTask()).getService().getCommunicationCost();
//						} 
//					}
//				}
//
//				/**
//				 * true 
//				 */
//				VmEST += compareCommunicationCost;
////				(try Kcut )
//				
//				if (VmEST < instance.getCurrentTime()) {
//					VmEST = instance.getCurrentTime();
//				} 
//
//				ActionQueue actionQueue = ((MixQueue) ((PeterSimulator) ASimulator.getInstance()).getQueue()).getActionQueue();
//
//				for (int j = 0; j < actionQueue.size(); j++) {
//					Event getQ = actionQueue.get(j);
//					IDepend task = getQ.getTask();
//					if (getQ.getEventType() == EventType.end &&  ((PeterTask) getQ.getTask()).getBelongRes() == Vm.get(y) && 
//						((PeterTask) getQ.getTask()).getEFT() > VmEST) {
//						VmEST = ((PeterTask) getQ.getTask()).getEFT();
//					}
//				}
//
//				
//				// decide which job should be deploy on VM
//				
//				/**
//				 * add back currentBestResource when the VmEST is not zero.
//				 * it means there are an initialized EST. it must has a current Best Resource
//				 */
//				if (VmEST > 0) {
//					currentBestResource = Vm.get(y);
//				}
//				if (bestEST > VmEST) {
//					bestEST = VmEST;
//					bestResourse = currentBestResource;
//				}
//				
//				
//		}
			
			
			
//			for (int i=0; i < instance.getQueue().getEventQueue().size(); i ++) {
//				Event checkQ = instance.getQueue().getEventQueue().get(i);
//					PeterTask eventJob = checkQ.getTask();
//					eventJob.getService().getVmStoreList().
////							if(eventJob.isStarted()){
////								singlejob.setEFT(eventJob.getService().getComputationCost() + bestEST);
//////						} 
//					}
			
			ResourcePair pair = findEST(singlejob);
			boolean isSubmitted = submitTask(singlejob, pair);
//			long bestEST = pair.getEST();
//			PeterNode bestResourse = pair.getResource();
//			
//			/**
//			 * actionQ { have start event and end event }
//			 * event Q {has submit event}
//			 */
//			 				
//			singlejob.setEST(bestEST);
//			singlejob.setEFT(singlejob.getService().getComputationCost() + singlejob.getEST());
//			singlejob.setBelongRes(bestResourse);
//			
//			submitTaskEvent(singlejob);
			
			if (!PeterVariable.getInstance().isRegret() && isSubmitted) {
				removeList.add(singlejob);
			}
		}
		/**
		 * 111
		 */
		if (!PeterVariable.getInstance().isRegret()) {
			instance.getGlobalWaitingQueue().removeJobByList(removeList);
		}
	}

	protected boolean isResourceIdle( IResPeter resource ) {
		if (!resource.getRunningQueue().isEmpty()) {
			IPeterAttribute tmp = (IPeterAttribute) resource.getRunningQueue().get(resource.getRunningQueue().size() - 1);
			if (tmp.isStarted() && !tmp.isScheduled()) {
				return false;
			}
		}
//		else {
			for ( Event event: ((MixQueue)instance.getQueue()).getActionQueue() ) {
				IPeterAttribute t = event.getTask();
				if (event.getEventType().equals(EventType.end) && t.getBelongRes().equals(resource)) {
					return false;
				}
			}
//		}
		return true;
	}
	
	protected boolean submitTask(IPeterAttribute singlejob, ResourcePair pair) {
		long bestEST = pair.getEST();
		PeterNode bestResourse = pair.getResource();
		if (bestResourse == null || bestEST == Long.MAX_VALUE) {
			return false;
		}
		
		/**
		 * actionQ { have start event and end event }
		 * event Q {has submit event}
		 */
		 				
		singlejob.setEST(bestEST);
		singlejob.setEFT(singlejob.getService().getComputationCost() + singlejob.getEST());
		singlejob.setBelongRes(bestResourse);
		
		submitTaskEvent(singlejob);
		
		return true;
	}
	
	protected ResourcePair findEST(IPeterAttribute singlejob) {
		long bestEST = Long.MAX_VALUE;
		long compareCommunicationCost = 0;
		PeterNode bestResourse = null;
		PeterNode currentBestResource = null;
//		PeterTask singlejob = (PeterTask)instance.getGlobalWaitingQueue().getJobByIndex(x);
//		List<PeterNode> Vm = ASimulator.getInstance().getCluster().getResourcelist();
		List<PeterNode> Vm = singlejob.getService().getVmStoreList();

		for (int y = 0; y < Vm.size(); y++) {
			long VmEST = 0;
			if (!isResourceIdle(Vm.get(y))) {
				continue;
			}
			if (!Vm.get(y).getRunningQueue().isEmpty()) {
				IAttribute finishedJob = Vm.get(y).getRunningQueue().getJobByIndex((Vm.get(y).getRunningQueue().size()) - 1);
				if (finishedJob.getFinishTime() > VmEST) {
					VmEST = finishedJob.getFinishTime();
					/**
					 * can not update the bestResource here. 
					 * it will cause the asynchronous when the current VmEST is not the best EST.
					 */
					currentBestResource = Vm.get(y);// add to resource
				}
			} else {
				/**
				 * can not update the bestResource here. 
				 * it will cause the asynchronous when the current VmEST is not the best EST.
				 */
				currentBestResource = Vm.get(y);
			}

			
			for (TaskLink link : ((IDepend) singlejob).getParentTaskLink()) {
				if (link.getNextTask().getFinishTime() > VmEST) {
					VmEST = link.getNextTask().getFinishTime();
				}
				if (Vm.get(y) != ((PeterTask) link.getNextTask()).getBelongRes()) {
					if (((PeterTask) link.getNextTask()).getService().getCommunicationCost() > compareCommunicationCost) {
						compareCommunicationCost = ((PeterTask) link.getNextTask()).getService().getCommunicationCost();
					} 
				}
			}

			/**
			 * true 
			 */
			VmEST += compareCommunicationCost;
//			(try Kcut )
			
			if (VmEST < instance.getCurrentTime()) {
				VmEST = instance.getCurrentTime();
			} 

			ActionQueue actionQueue = ((MixQueue) ((PeterSimulator) ASimulator.getInstance()).getQueue()).getActionQueue();

			for (int j = 0; j < actionQueue.size(); j++) {
				Event getQ = actionQueue.get(j);
				IDepend task = getQ.getTask();
				if (getQ.getEventType() == EventType.end &&  ((PeterTask) getQ.getTask()).getBelongRes() == Vm.get(y) && 
					((PeterTask) getQ.getTask()).getEFT() > VmEST) {
					VmEST = ((PeterTask) getQ.getTask()).getEFT();
				}
			}

			
			// decide which job should be deploy on VM
			
			/**
			 * add back currentBestResource when the VmEST is not zero.
			 * it means there are an initialized EST. it must has a current Best Resource
			 */
			if (VmEST > 0) {
				currentBestResource = Vm.get(y);
			}
			if (bestEST > VmEST) {
				bestEST = VmEST;
				bestResourse = currentBestResource;
			}
		}
		return new ResourcePair(bestEST, bestResourse);
	}
	
	protected void submitTaskEvent(IPeterAttribute task) {
		long communicationCost = 0, parentFinishTime = 0;
		for (TaskLink tmp : task.getParentTaskLink()){
			IPeterAttribute parenTask = (IPeterAttribute) tmp.getNextTask();
			if (task.getFinishTime() > parentFinishTime && !(task.getBelongRes().equals(parenTask.getBelongRes()))) {
				parentFinishTime = task.getFinishTime();
				communicationCost = parenTask.getService().getCommunicationCost();
			}
			else if (task.getFinishTime() == parentFinishTime && parenTask.getService().getCommunicationCost() > communicationCost
				&& !(task.getBelongRes().equals(parenTask.getBelongRes()))) {
				parentFinishTime = parenTask.getFinishTime();
				communicationCost = parenTask.getService().getCommunicationCost();
			}
			else if (task.getBelongRes().equals(parenTask.getBelongRes())){
				communicationCost = 0;
			}
		}
		if (parentFinishTime + communicationCost <= instance.getCurrentTime()) {
			task.setEST(instance.getCurrentTime());
		}
		else {
			task.setEST(parentFinishTime + communicationCost);
		}
		task.setEFT(task.getEST() + task.getService().getComputationCost());
		Event startEvent = new Event(EventType.start, task.getEST(), task);
		Event endEvent = new Event(EventType.end, task.getEFT(), task);
		
		instance.getQueue().addToQueue(QueueType.Action, startEvent);
		instance.getQueue().addToQueue(QueueType.Action, endEvent);
		
		}
}

