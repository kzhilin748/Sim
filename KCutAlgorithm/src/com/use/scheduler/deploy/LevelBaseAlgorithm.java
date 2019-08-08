package com.use.scheduler.deploy;

import java.util.Arrays;

import com.use.workflow.IWorkflow;
import com.use.workflow.task.IDepend;

public class LevelBaseAlgorithm extends ASRG {
	private int[] level = null;

	@Override
	public void ParallelismMethod(IWorkflow workflow) {
		level = new int[workflow.getTaskList().size()];
		Arrays.fill(level, 0);
		for (IDepend task : workflow.getTaskList()){
			if (!task.getParentTaskLink().isEmpty()) {
				IDepend parent = (IDepend) task.getParentTaskLink().get(0).getNextTask();
				level[task.getId()] = level[parent.getId()] + 1;
			}
			else {
				level[task.getId()] = 1;
			}
		}
		
	}

	@Override
	protected boolean isOverlap(IDepend task, IDepend parallelTask) {
		return level[task.getId()] == level[parallelTask.getId()];
	}
}
