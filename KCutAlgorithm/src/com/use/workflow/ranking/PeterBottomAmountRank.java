package com.use.workflow.ranking;

import java.util.ArrayList;
import java.util.List;

import com.use.workflow.AWorkflow;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.PeterTask;
import com.use.workflow.task.TaskLink;

public class PeterBottomAmountRank implements IRanking {

	@Override
	public void ranking(AWorkflow workflow) {
		List<IDepend> taskList = workflow.getTaskList();
		boolean[] IsRank = new boolean[taskList.size()];
		List<PeterTask> computeRankQ = new ArrayList<PeterTask>();
		// if tasks have no child, add in computeRankQ
		for (IDepend iDependtask : taskList) {
			PeterTask task = (PeterTask) iDependtask;
			if (task.getChildTaskLink().size() == 0)
				computeRankQ.add(task);
		}
		while (!computeRankQ.isEmpty()) {
			PeterTask task = computeRankQ.get(0);
			if (task.getChildTaskLink().size() == 0)
				task.setRank(task.getService().getComputationCost());
			else {
				int maxRank = 0;
				for (TaskLink childLink : task.getChildTaskLink()) {
					PeterTask childTask = (PeterTask) childLink.getNextTask();
					int rank = 0;
					if (task.getBelongRes() != null && childTask.getBelongRes() != null && task.getBelongRes().getId() == childTask.getBelongRes().getId())
						rank = childTask.getRank();
					else
						rank = task.getService().getCommunicationCost() + childTask.getRank();
					maxRank += rank;
				}
				maxRank += task.getService().getComputationCost();
				task.setRank(maxRank);
			}
			IsRank[task.getId()] = true;
			computeRankQ.remove(task);
			for (TaskLink parentLink : task.getParentTaskLink()) {
				PeterTask parentTask = (PeterTask) parentLink.getNextTask();
				boolean allChildRank = true;
				for (TaskLink childLinkOfparentTask : parentTask.getChildTaskLink()) {
					PeterTask childOfparentTask = (PeterTask) childLinkOfparentTask.getNextTask();
					if (IsRank[childOfparentTask.getId()] == false) {
						allChildRank = false;
						break;
					}
				}
				if (!IsRank[parentTask.getId()] && allChildRank)
					computeRankQ.add(parentTask);
			}
		}
	}

}
