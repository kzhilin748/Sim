//package com.use.simulator;
//
//import com.use.workflow.task.IPeterAttribute;
//
//
//public class PeterSimulatorNonRanking extends PeterSimulator {
//
//	public PeterSimulatorNonRanking() throws Exception {
//		super();
//	}
//	
//	@Override
//	protected void submitVMTask(IPeterAttribute finishedTask) {
//	}
//
////	@Override
////	protected void submitTask(Event event) {
////		for (TaskLink forkJoin : ((IDepend) event.getTask()).getChildTaskLink()) {
////			IPeterAttribute childTask = (IPeterAttribute) forkJoin.getNextTask();
////			if (childTask.isSubmited() || childTask.isScheduled() || childTask.isStarted()) {
////				continue;
////			}
////			isTaskcanSubmit((IPeterAttribute) event.getTask(), childTask);
////		}
////	}
//	
////	protected void isTaskcanSubmit(PeterTask finishedTask, PeterTask childTask) {
////		boolean canSubmitFlag = true;
////		int tryCommunicationCost = 0;
////		for (TaskLink temp : childTask.getParentTaskLink()) {
////			PeterTask tmpparentTask = (PeterTask) temp.getNextTask();
////			if (!childTask.getService().getVmStoreList().contains(tmpparentTask.getBelongRes()) && tmpparentTask.getService().getCommunicationCost() > tryCommunicationCost) {
////				tryCommunicationCost = ((PeterTask) finishedTask).getService().getCommunicationCost();
////			}
////			if (!temp.getNextTask().isScheduled()) {
////				canSubmitFlag = false;
////			}
////		}
////		if (canSubmitFlag) {
////			Event storeEventQueue = new Event(EventType.submit, this.currentTime + tryCommunicationCost, childTask);
////			this.queue.addToQueue(QueueType.Event, storeEventQueue);
////			childTask.setSubmited(true);
////		}
////	}
//}
