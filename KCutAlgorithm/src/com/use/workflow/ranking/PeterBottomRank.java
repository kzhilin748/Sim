package com.use.workflow.ranking;

import java.util.ArrayList;
import java.util.List;

import com.use.workflow.AWorkflow;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.PeterTask;
import com.use.workflow.task.TaskLink;

public class PeterBottomRank implements IRanking {

	@Override
	public void ranking(AWorkflow workflow) {
		boolean[] IsRank = new boolean[workflow.getTaskList().size()];
		List<PeterTask> computeRankQueue = new ArrayList<PeterTask>();
		for (IDepend iDependTask : workflow.getTaskList()) {
			PeterTask task = (PeterTask) iDependTask;
			if (task.getChildTaskLink().size() == 0) {
				computeRankQueue.add(task);
			}
		}
		while (!computeRankQueue.isEmpty()) {
			PeterTask task = computeRankQueue.get(0);
			if (task.getChildTaskLink().size() == 0) {
				task.setRank(task.getService().getComputationCost());
			} else {
				int maxRank = 0;
				for (TaskLink childLink : task.getChildTaskLink()) {
					PeterTask childTask = (PeterTask) childLink.getNextTask();
					int rank = 0;
					if (task.getBelongRes() != null && childTask.getBelongRes() != null && task.getBelongRes().getId() == childTask.getBelongRes().getId()) {
						rank = task.getService().getComputationCost() + childTask.getRank();
					} else {
						rank = task.getService().getComputationCost() + task.getService().getCommunicationCost() + childTask.getRank();
					}
					if (maxRank < rank) {
						maxRank = rank;
					}
					task.setRank(maxRank);
				}
			}
			IsRank[task.getId()] = true;
			computeRankQueue.remove(task);
			for (TaskLink parentLink : task.getParentTaskLink()) {
				PeterTask parentTask = (PeterTask) parentLink.getNextTask();
				boolean allChildRank = true;
				for (TaskLink childLinkOfParentTask : parentTask.getChildTaskLink()) {
					PeterTask childOfParentTask = (PeterTask) childLinkOfParentTask.getNextTask();
					if (IsRank[childOfParentTask.getId()] == false) {
						allChildRank = false;
						break;
					}
				}
				if (!IsRank[parentTask.getId()] && allChildRank) {
					computeRankQueue.add(parentTask);
				}

			}
		}

	}

}
