package com.use.generator.peter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.use.config.PeterVariable;
import com.use.generator.AWorkflowGenerator;
import com.use.workflow.IWorkflow;
import com.use.workflow.SimpleWorkflow;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.PeterTask;
import com.use.workflow.task.TaskLink;
import com.use.workflow.task.info.ATaskInfo;
import com.use.workflow.task.info.WorkflowTaskInfo;

/**
 * 
 * @author peter
 *
 *         random generate for DAG
 * @TODO two join could merge into one join node function
 */
public class PeterGenerator extends AWorkflowGenerator {
	private int nodeIndex = 0;
	private final Random Rand = new Random(1);
//	private PeterActionPerform service = new PeterActionPerform();
	
	@Override
	public List<IWorkflow> generate() {

		List<IWorkflow> workflowset = new ArrayList<IWorkflow>();

		int workflowindex = 0;
		for (int dagIndex = 0; dagIndex < PeterVariable.getInstance().getTotalDag(); dagIndex++) {
			SimpleWorkflow workflow = new SimpleWorkflow();
			workflow.setId(workflowindex++);
			PeterTask node = null;
			nodeIndex = 0;
			int firstLevelFork = 1;
			int secondLevelFork = 1;
			/**
			 * generate node 0 and node 1
			 */
			for (int i = 0; i < 2; i++) {
				node = generateTask(nodeIndex++);
				workflow.getTaskList().add(node);
			}
			/**
			 * link node 0 and node 1
			 */
			setDoubleLink(0, workflow.getTaskList().get(0), node);

			switch (dagIndex) {
			case 1:
				firstLevelFork = 1;
				secondLevelFork = 2;
				break;
			case 2:
				firstLevelFork = 2;
				secondLevelFork = 1;
				break;
			case 3:
				firstLevelFork = 2;
				secondLevelFork = 2;
				break;
			case 4:
				firstLevelFork = 2;
				secondLevelFork = 1;
				break;
			case 5:
				firstLevelFork = 2;
				secondLevelFork = 2;
				break;
			case 6:
				firstLevelFork = 3;
				secondLevelFork = 1;
				break;
			case 7:
				firstLevelFork = 3;
				secondLevelFork = 2;
				break;
			case 8:
				firstLevelFork = 3;
				secondLevelFork = 1;
				break;
			case 9:
				firstLevelFork = 3;
				secondLevelFork = 2;
				break;

			}

			// turn node to list
			List<PeterTask> forknode = Arrays.asList(node);
			List<List<PeterTask>> forkednodeList = new ArrayList<List<PeterTask>>();
			// means if else
			boolean isFirstFork = true;
			for (int i = 0; i < 2; i++) {
				int forkrange = firstLevelFork;
				if (i > 0) {
					isFirstFork = false;
					forkrange = secondLevelFork;
				}
				// first:list node and forkrange = 3
				forknode = generateForkNode(forknode, forkrange, dagIndex, isFirstFork);
				workflow.getTaskList().addAll(forknode);
				forkednodeList.add(forknode);
			}
			
	
			/**
			 * first join
			 */
			List<PeterTask> joinednodeList = new ArrayList<PeterTask>();
			for (PeterTask joinnode : forkednodeList.get(0)) {
				PeterTask newjoinnode = generateTask(nodeIndex++);
				for (TaskLink link : joinnode.getChildTaskLink()) {
					setDoubleLink(0, link.getNextTask(), newjoinnode);
				}
				joinednodeList.add(newjoinnode);
			}
			workflow.getTaskList().addAll(joinednodeList);
	
			/**
			 * join to final node
			 */
			PeterTask finalnode = generateTask(nodeIndex++);
			workflow.getTaskList().add(finalnode);
			for (PeterTask joinnode : joinednodeList) {
				setDoubleLink(0, joinnode, finalnode);
			}
			workflowset.add(workflow);
		}
		
		return workflowset;

	}

	/**
	 * generate fork node according to forkNodeList.
	 * 
	 * @param forkNodeList
	 *            List that contain fork node list
	 * @param forkNumRange
	 *            fork random range
	 * @return return list contain new fork node
	 */
	private List<PeterTask> generateForkNode(List<PeterTask> forkNodeList, int forkNumRange, int dagIndex, boolean isFirstFork) {
		List<PeterTask> newForkNodeList = new ArrayList<PeterTask>();
		boolean contextswitch = false;
		// all to for loop
		for (int j = 0; j < forkNodeList.size(); j++) {
			PeterTask forknode = forkNodeList.get(j);
			// 3 (0.1.2) turn to 3(1.2.3)
//			int forkNum = Rand.nextInt(forkNumRange) + 1;
			int forkNum = forkNumRange;
			if (!isFirstFork && j > 0) {
				switch (dagIndex) {
				case 3:
					forkNum = 1;
					break;
				case 4:
					forkNum = 2;
					break;
				case 7:
					forkNum = 1;
					break;
				case 8:
					forkNum = 2;
					if (contextswitch == true)
						forkNum = 1;
					contextswitch = true;
					break;
				case 9:
					forkNum = 1;
					if (contextswitch == true)
						forkNum = 2;
					contextswitch = true;
				}

			}
			
			for (int i = 0; i < forkNum; i++) {
				PeterTask node = generateTask(nodeIndex++);
				setDoubleLink(0, forknode, node);
				newForkNodeList.add(node);
			}
		}
		return newForkNodeList;
	}

	/**
	 * generate task
	 * 
	 * @param index
	 *            task id
	 * @return return generated task that task id is index
	 */
	private PeterTask generateTask(int index) {
		IDepend info = new WorkflowTaskInfo(); // for data encapsulate
		((ATaskInfo) info).setId(index); // set nodeIndex to info
		PeterTask node = new PeterTask(info);
//		node.setService(service.getService(Rand.nextInt(service.size())));
//		System.out.println(node.getId() + ":" + node.getService()); //print
		return node;
		
	}

}
