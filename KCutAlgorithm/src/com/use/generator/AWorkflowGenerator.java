package com.use.generator;

import com.use.workflow.task.DependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.TaskLink;

public abstract class AWorkflowGenerator extends AGenerator {

	/**
	 * generate task link for task.
	 * 
	 * @param communication
	 * @param task
	 * @return
	 */
	private TaskLink generateTaskLink(int communication, IAttribute task) {
		TaskLink link = new TaskLink(communication, task);
		return link;
	}

	/**
	 * set link for parent node and child node.
	 * 
	 * @param communication
	 *            communication time
	 * @param parent
	 *            parent task
	 * @param child
	 *            child task
	 */
	protected void setDoubleLink(int communication, IAttribute parent,
			IAttribute child) {
		((DependTask) parent).getChildTaskLink().add(generateTaskLink(communication, child));
		((DependTask) child).getParentTaskLink().add(generateTaskLink(communication, parent));
	}

}
