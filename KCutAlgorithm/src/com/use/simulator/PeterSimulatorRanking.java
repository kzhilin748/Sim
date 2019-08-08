package com.use.simulator;

import java.io.File;
import java.io.FileWriter;

import com.use.ALauncher;
import com.use.queue.QueueType;
import com.use.queue.event.Event;
import com.use.queue.event.EventType;
import com.use.resource.IRes;
import com.use.resource.IResPeter;
import com.use.resource.PeterNode;
import com.use.workflow.IWorkflow;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.IPeterAttribute;


public class PeterSimulatorRanking extends PeterSimulator {
	private FileWriter eventLog;

	public PeterSimulatorRanking() throws Exception {
		super();
	}
	
//	@Override
//	protected void execution(Event event) throws Exception {
//		super.execution(event);
//		switch(event.getEventType()) {
//			case start:96j0 
//				IPeterAttribute task = event.getTask();
//				task.getBelongRes().getWaitingQueue().remove(event.getTask());
//				break;
//		}
//	}
	
	@Override
	protected void initlize() throws Exception {
		super.initlize();
		if (ALauncher.isLogEnable()) {
			File file = new File(classIdentify.replace(" + ", "-") + "-event");
			if (!file.exists()) {
				file.createNewFile();
			}
			eventLog = new FileWriter(file);
		}
	}

	@Override
	protected void submitVMTask(IPeterAttribute finishedTask) {
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
				}
			}
		}
	}
	
//	@Override
//	protected int isTaskcanSubmit(IPeterAttribute childTask) {
//		int retn = super.isTaskcanSubmit(childTask);
//		if (childTask.isInserted()) {
//			return -1;
//		}
//		return retn;
//	}
	
	@Override
	protected void execution(Event event) throws Exception {
		if (event.getTask() instanceof IDepend && ALauncher.isLogEnable()) {
			IPeterAttribute task = event.getTask();
			eventLog.write(event.getEventType() + ":" + task.getId() + ":" + task.getBelongWorkflow().getId() + ":" + 
					Integer.toHexString(System.identityHashCode(task.getBelongWorkflow())) + "\n");
		}
		super.execution(event);
	}
	
	@Override
	protected void simulateFinish() throws Exception {
		super.simulateFinish();
		File file = null;
		FileWriter writer = null;
		if (ALauncher.isLogEnable()) {
			eventLog.close();
			file = new File(classIdentify.replace(" + ", "-") + "-RunningQueue");
			if (!file.exists()) {
				file.createNewFile();
			}
			writer = new FileWriter(file);
		}
		for (IRes node : cluster.getResourcelist()) {
			PeterNode resource = (PeterNode) node;
			if (!resource.getWaitingQueue().isEmpty()) {
				System.err.println("still job in waiting queue: " + resource.getId());
			}
			if (ALauncher.isLogEnable()) {
				for (IAttribute tmp : resource.getRunningQueue()) {
					IPeterAttribute task = (IPeterAttribute) tmp; 
//					System.err.println(task.getBelongRes().getId() + ":" + task.getId() + ":" + task.getBelongWorkflow().getId() +
						writer.write(task.getBelongRes().getId() + ":" + task.getId() + ":" + task.getBelongWorkflow().getId() +
							"," + (task.getStartTime() + ":" + task.getSubmitTime()) + ":" + task.getFinishTime() + ":" + task.getRank() +
							"," + Integer.toHexString(System.identityHashCode(task.getBelongWorkflow())) + "\n" );
				}
			}
		}
		if (ALauncher.isLogEnable()) {
			writer.close();
			file = new File(classIdentify.replace(" + ", "-"));
			if (!file.exists()) {
				file.createNewFile();
			}
			writer = new FileWriter(file);
			for (IWorkflow workflow : requestWorkflow ) {
				for (IDepend tmp : workflow.getTaskList()) {
						IPeterAttribute task = (IPeterAttribute) tmp;
						writer.write(task.getBelongRes().getId() + ":" + task.getId() + ":" + task.getBelongWorkflow().getId() +
							"," + (task.getStartTime() + ":" + task.getSubmitTime()) + ":" + task.getFinishTime() + ":" + task.getRank() +
							"," + Integer.toHexString(System.identityHashCode(task.getBelongWorkflow())) + "\n" );
	//				System.err.println(task.getBelongRes().getId() + ":" + task.getId() + ":" + task.getBelongWorkflow().getId() +
	//						"," + (task.getStartTime() + "," + task.getSubmitTime()) + ":" + task.getRank() +
	////						":" + task.getSubmitTime() + ":" + task.getStartTime() + ":" + task.getFinishTime() + ":" + 
	//						"," + Integer.toHexString(System.identityHashCode(task.getBelongWorkflow())) );
				}
	//			System.err.println();
				writer.write("\n");
			}
			writer.close();
		}
	}
}
