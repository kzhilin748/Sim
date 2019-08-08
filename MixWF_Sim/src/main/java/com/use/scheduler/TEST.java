package com.use.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.use.ICASLBTools.BRBS;
import com.use.ICASLBTools.DowneySpeedup;
import com.use.ICASLBTools.FindConcurrencyRatio;
import com.use.queue.MixQueue;
import com.use.simulator.ASimulator;
import com.use.simulator.mixWFSimulator;
import com.use.speedup.Speedup;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;

public class TEST extends AScheduler{
	protected mixWFSimulator instance = (mixWFSimulator) ASimulator.getInstance();
	protected List<IAttribute> workflowSet = instance.getWaitingQ();
	DowneySpeedup DowneySpeedup=new DowneySpeedup();
	BRBS BRBS=new BRBS();
	@Override
	public void schedule() throws Exception {
		
		Workflow _wf =(Workflow) workflowSet.get(0);
		_wf.ReTopandBottomRank();
		DAGDependTask task1 =(DAGDependTask) _wf.getTaskList().get(1);
		DAGDependTask task3 =(DAGDependTask) _wf.getTaskList().get(3);
		System.err.println(task3.getTopRank());
		task1.setComputationTime(2);
		_wf.topRanking();
		System.err.println(task3.getTopRank());
	}

}
