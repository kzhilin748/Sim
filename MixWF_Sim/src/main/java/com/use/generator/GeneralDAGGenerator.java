package com.use.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.use.config.DAGVariable;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;

public class GeneralDAGGenerator extends ForkJoinDAGGenerator {

	private Random randomNumber = new Random();	
	
	public GeneralDAGGenerator() {
		randomNumber.setSeed(DAGVariable.getInstance().getRandomSeed());
	}
	
	@Override
	public List<IAttribute> generate() throws Exception {
		List<IAttribute> workflowSet = new ArrayList<IAttribute>();
		for(int i=0;i<DAGVariable.getInstance().getNumberOfWorkflow();i++) {
			Workflow workflow = new Workflow(i);
			workflow.setInterArrivalTime(randomNumber.nextInt(DAGVariable.getInstance().getMaxInterArrivalTime()));
			int taskIndex = 0;
			List<ArrayList<DAGDependTask>> taskLevel = new ArrayList<ArrayList<DAGDependTask>>();
			
			for (int j = 0; j < DAGVariable.getInstance().getNumberOfLevel(); j++) {
				taskLevel.add(new ArrayList<DAGDependTask>());
				int taskNumberOfThisLevel = 1 + randomNumber.nextInt(DAGVariable.getInstance().getNumberOfNodesPerLevel());
				for (int k = 0; k < taskNumberOfThisLevel; k++) {
					DAGDependTask task = (DAGDependTask)generateSingleTask(taskIndex++, workflow);
					taskLevel.get(j).add(task);
					workflow.getTaskList().add(task);
				}
			}
			for (int m = 0; m < taskLevel.size(); m++) {
				if (m < taskLevel.size() - 1) {
					List<DAGDependTask> level = taskLevel.get(m);
					// from level
					for (int n = 0; n < level.size(); n++) {
						DAGDependTask task = level.get(n);
						// next level
						while (task.getChildTaskLink().isEmpty()) {
							List<DAGDependTask> nextLevel = taskLevel.get(m+1);
							for (int p = 0; p < nextLevel.size(); p++) {
								DAGDependTask nextLevelTask = nextLevel.get(p);
								int isLink = randomNumber.nextInt();
								if (isLink % 2 == 1) {
									generateEdge(task, nextLevelTask);
								}
							}
						}
						if (m > 0) {
							if (task.getParentTaskLink().isEmpty()) {
								List<DAGDependTask> upperLevel = taskLevel.get(m-1);
								DAGDependTask taskOfMinChild = upperLevel.get(0);
								int minChildNumber = taskOfMinChild.getChildTaskLink().size();
								for (int q = 0; q < upperLevel.size(); q++) {
									DAGDependTask upperLevelTask = upperLevel.get(q);
									if (upperLevelTask.getChildTaskLink().size() < minChildNumber) {
										minChildNumber = upperLevelTask.getChildTaskLink().size();
										taskOfMinChild = upperLevelTask;
									}
								}
								generateEdge(taskOfMinChild, task);
							}
						}
					}

				} else if (m == taskLevel.size() - 1) {
					List<DAGDependTask> level = taskLevel.get(m);
					for (int r = 0; r < level.size(); r++) {
						DAGDependTask task = level.get(r);
						if (task.getParentTaskLink().isEmpty()) {
							List<DAGDependTask> upperLevel = taskLevel.get(m-1);
							DAGDependTask taskOfMinChild = upperLevel.get(0);
							int minChildNumber = taskOfMinChild.getChildTaskLink().size();
							for (int q = 0; q < upperLevel.size(); q++) {
								DAGDependTask upperLevelTask = upperLevel.get(q);
								if (upperLevelTask.getChildTaskLink().size() < minChildNumber) {
									minChildNumber = upperLevelTask.getChildTaskLink().size();
									taskOfMinChild = upperLevelTask;
								}
							}
							generateEdge(taskOfMinChild, task);
						}
					}
				}
			}
			
			for(int q=0;q<workflowSet.size();q++) {
				Workflow workflowInSet = (Workflow)workflowSet.get(q);
				if(workflow.getInterArrivalTime()<workflowInSet.getInterArrivalTime()) {
					workflowSet.add(q, workflow);
					break;
				}				
			}
			if(!workflowSet.contains(workflow))
				workflowSet.add(workflow);
		}
		return workflowSet;
	}

}
