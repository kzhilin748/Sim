package com.use.queue;

import com.use.config.PeterVariable;
import com.use.workflow.task.IPeterAttribute;

public class SubmitQueueSJF<T> extends Queue<T>{
	
	private void printQueue() {
		System.err.print("\t");
		for (T tmp : this) {
			IPeterAttribute task = (IPeterAttribute) tmp;
//			System.err.print(task.getId() + "," + task.getRank() + "," + Integer.toHexString(System.identityHashCode(task.getBelongWorkflow())) + ":");
			System.err.print(task.getId() + "," + task.getRank() + "," + task.getBelongWorkflow().getId() + ":");
//			System.err.print("Task:" + task.getId() + " R:" + task.getRank() +  ", W:" + task.getBelongWorkflow().getId() + ":submittiime:" + task.getSubmitTime() + " |¡÷| " + task);
		}
		System.err.println();
	}
	
//	private void timeQueue() {
//		System.err.print("\t");
//		for (T tmp:this){
//			IPeterAttribute task = (IPeterAttribute) tmp;
//			System.err.println("Time:" + ASimulator.getInstance().getCurrentTime() + ":TaskId:" + task.getId() + ":Vm:" + task.getBelongRes());
//		}
//		System.err.println();
//	}
	@Override
	public int addToQueue(T task) {
		 
//		timeQueue();
		
		if (!PeterVariable.getInstance().isUseRanking()) {
			return super.addToQueue(task);
		}
		if (task != null) {
//			System.err.println(this.getClass() + "@" + Integer.toHexString(System.identityHashCode(this)));
			if (this.isEmpty()) {
//				System.err.println("Insert empty last: " + ptask.getId() + ":" + ptask.getRank() + ":submittiime:" + ptask.getSubmitTime());
				super.add(task);
//				timeQueue();
				return 0;
			} 
			for (int i = 0; i < this.size(); i++) {
				IPeterAttribute tmp = (IPeterAttribute) this.get(i);
				IPeterAttribute ptask = (IPeterAttribute) task;
				if (tmp.getBelongWorkflow().equals(ptask.getBelongWorkflow()) && tmp.getRank() < ptask.getRank() ) {
//					System.err.println("\tSame: " + ptask.getId() + "," + ptask.getRank() + "," + Integer.toHexString(System.identityHashCode(ptask.getBelongWorkflow())));
//					System.err.println("Same " + tmp.getId() + " -> " + ptask.getId() + "," + tmp.getId() + "=>" + ptask.getId() + "," + 
//							"," + ptask.getRank() + ":" + tmp.getId() + "," + ptask.getRank());
					super.add(i, task);
//					printQueue();
//					timeQueue();
					return i;
				}
				else if (!tmp.getBelongWorkflow().equals(ptask.getBelongWorkflow()) && tmp.getRank() > ptask.getRank()) {
//					System.err.println("\tDifferent: " + ptask.getId() + "," + ptask.getRank() + "," + Integer.toHexString(System.identityHashCode(ptask.getBelongWorkflow())));
//					System.err.println("Different " + tmp.getId() + " -> " + ptask.getId() + "," + tmp.getId() + "=>" + ptask.getId() +  
//							"," + ptask.getRank() + ":" + tmp.getId() + "," + ptask.getRank());
//					printQueue();
					super.add(i, task);
//					printQueue();
//					System.err.println();
//					timeQueue();
					return i;
				} else if (i + 1 >= this.size()) {
//					System.err.println("Insert Last: " + ptask.getId() + ":" + ptask.getRank() + ":submittiime:" + ptask.getSubmitTime());
//					printQueue();
					super.add(task);
//					printQueue();
//					timeQueue();
					return 0;
				}
			}
		}
		return -1;
	}
}
