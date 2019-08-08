package com.use.scheduler.deploy;

import java.util.ArrayList;
import java.util.List;

import com.use.workflow.IWorkflow;
import com.use.workflow.service.PeterActionPerform;
import com.use.workflow.service.PeterService;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.PeterTask;
import com.use.workflow.task.TaskLink;

public abstract class ASRG {
	protected List<IDepend> potentialParallelismTask[];
	protected List<IDepend> relatedTask = new ArrayList<IDepend>();
	boolean[][] isDuplicated = null;

	public void developSRG(List<IAttribute> workflowSet) {
		int serviceSize = PeterActionPerform.getInstance().size();
		for (IAttribute workflowTmp : workflowSet) {
			IWorkflow workflow = (IWorkflow) workflowTmp;
			isDuplicated = new boolean[serviceSize][serviceSize];
			
			this.potentialParallelismTask = new List[workflow.getTaskList().size()];
			for (IDepend task : workflow.getTaskList()) {
				this.potentialParallelismTask [task.getId()]= new ArrayList<IDepend>();
				this.relatedTask.clear();
				findRelatedTask(task);
				findProtentialParallelTask(workflow.getTaskList(),task);
			}
			ParallelismMethod(workflow);
			assignEdge(workflow);
		}
	}

	/**
	 * find related task depend on the parent and child task.
	 * 
	 * @param task
	 */
	private void findRelatedTask(IDepend task) {
		addToReleatedTask(task);
		for (int i = 0; i < task.getParentTaskLink().size(); i++) {
			IDepend tasktemp = (IDepend) task.getParentTaskLink().get(i).getNextTask();
			addToReleatedTask(tasktemp);
			while (!tasktemp.getParentTaskLink().isEmpty()) {
				tasktemp = (IDepend) tasktemp.getParentTaskLink().get(0).getNextTask();
				addToReleatedTask(tasktemp);
			}
		}

		for (int i = 0; i < task.getChildTaskLink().size(); i++) {
			IDepend tasktemp = (IDepend) task.getChildTaskLink().get(i).getNextTask();
			addToReleatedTask(tasktemp);
			while (!tasktemp.getChildTaskLink().isEmpty()) {
				tasktemp = (IDepend) tasktemp.getChildTaskLink().get(0).getNextTask();
				addToReleatedTask(tasktemp);
			}
		}
	}
	
	private void addToReleatedTask(IDepend task) {
		if (!relatedTask.contains(task)) {
			relatedTask.add(task);
		}
	}

	/**
	 * find protential parallel task, depend on the releated task.
	 * 
	 * @param workflow
	 */
	private void findProtentialParallelTask(List<IDepend> taskList,IDepend task) {
		for (IDepend tasktemp : taskList) {
			if (!relatedTask.contains(tasktemp) && !potentialParallelismTask[task.getId()].contains(tasktemp)) {
				potentialParallelismTask[task.getId()].add(tasktemp);
			}
		}

	}

	/**
	 * parallel Algorithm.
	 * 
	 * @param task
	 */
	protected abstract void ParallelismMethod(IWorkflow workflow);

	/**
	 * assign service edge depend on the task is overlapping to the potential
	 * parallel task
	 * 
	 * @param task
	 */
	protected void assignEdge(IWorkflow workflow) {
		for (IDepend task : workflow.getTaskList()) {
			for (IDepend parallelTask : potentialParallelismTask[task.getId()]) {
				if (isOverlap(task, parallelTask)) {
					assignServiceEdge(task, parallelTask);
				}
			}
		}
	}

	/**
	 * Overlapping check. need to implement depend on different Algorithm.
	 * 
	 * @param task
	 * @param parallelTask
	 *            parallel Task
	 * @return true, overlapping with parallel task. false, did not overlapping.
	 */
	protected abstract boolean isOverlap(IDepend task, IDepend parallelTask);

	/**
	 * assign service edge
	 */
	protected void assignServiceEdge(IDepend normalTask, IDepend parallelTask) {
		PeterService normalTaskService = ((PeterTask) normalTask).getService();
		PeterService parallelTaskService = ((PeterTask) parallelTask).getService();
		if (normalTaskService != parallelTaskService && !isDuplicated[normalTask.getId()][parallelTask.getId()]) {
			int minimalComunicationCost = normalTaskService.getComputationCost();
			if (normalTaskService.getComputationCost() > parallelTaskService.getComputationCost()) {
				minimalComunicationCost = parallelTaskService.getComputationCost();
			}
			
			isDuplicated[normalTask.getId()][parallelTask.getId()] = true;
			isDuplicated[parallelTask.getId()][normalTask.getId()] = true;

			boolean retn = this.assignSRGEdgeToService(normalTaskService, parallelTaskService, minimalComunicationCost);
			if (retn) {
				this.assignSRGEdgeToService(parallelTaskService, normalTaskService, minimalComunicationCost);
			}
		}
	}

	private boolean assignSRGEdgeToService(PeterService normalTaskService, PeterService parallelTaskService, int minimalComunicationCost) {
		boolean isAdded = false;
		for (TaskLink nextServiceLink : normalTaskService.getNextServicePool()) {
			if (nextServiceLink.getNextTask() == parallelTaskService) {
				isAdded = true;
				nextServiceLink.setWeight(nextServiceLink.getWeight() - minimalComunicationCost);
				break;
			}
		}
		if (!isAdded) {
			for (TaskLink preServiceLink : normalTaskService.getPreviousServicePool()) {
				if (preServiceLink.getNextTask() == parallelTaskService) {
					isAdded = true;
					preServiceLink.setWeight(preServiceLink.getWeight() - minimalComunicationCost);
					break;
				}
			}
		}
		if (!isAdded) {
			normalTaskService.addNextServicePool(new TaskLink(-minimalComunicationCost, parallelTaskService));
			parallelTaskService.addPreviousServicePool(new TaskLink(-minimalComunicationCost, normalTaskService));
		}
		return isAdded;
	}
}
