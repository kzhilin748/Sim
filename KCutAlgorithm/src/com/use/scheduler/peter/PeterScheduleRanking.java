package com.use.scheduler.peter;

import com.use.config.PeterVariable;
import com.use.resource.IRes;
import com.use.resource.IResPeter;
import com.use.simulator.ASimulator;
import com.use.workflow.task.IPeterAttribute;

public class PeterScheduleRanking extends PeterSchedule{

	@Override
	public void schedule() throws Exception {
		if (PeterVariable.getInstance().isUseRanking()) {
			instance = ASimulator.getInstance();
			for (IRes tmp : instance.getCluster().getResourcelist()) {
				IResPeter resource = (IResPeter) tmp;
				if (!resource.getWaitingQueue().isEmpty()) {
					if (isResourceIdle(resource)) { 
						IPeterAttribute task = (IPeterAttribute) resource.getWaitingQueue().get(0);
						submitTask(task, findEST(task));
					}
				}
			}
		}
		else {
			super.schedule();
		}
	}
	
//	@Override
//	protected void submitTask(IPeterAttribute singlejob, ResourcePair pair) {
//		if (isResourceIdle((IResPeter) singlejob.getBelongRes())) {
//			super.submitTask(singlejob, pair);
//		}
//	}
	
	@Override
	protected void submitTaskEvent(IPeterAttribute singlejob) {
		if (PeterVariable.getInstance().isUseRanking()) {
	//		super.submitTaskEvent(singlejob);
	//		singlejob.getBelongRes().getWaitingQueue().remove(singlejob);
			IResPeter resource = instance.getCluster().getResource(singlejob.getBelongRes().getId());
			/*if (singlejob.getParentTaskLink().isEmpty()) {
//				singlejob.setInserted(true);
//				super.submitTaskEvent(singlejob);
			}
			else */if (!resource.getWaitingQueue().contains(singlejob)) {
				resource.getWaitingQueue().addToQueue(singlejob);
			}
		}
		else {
			super.submitTaskEvent(singlejob);
		}
	}
	
}
