package com.use.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.use.config.DAGVariable;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.TaskLink;
import com.use.workflow.task.info.DependTaskInfo;

public class RandomGraphDAGGenerator extends AGenerator {
	private Random randomNumber = new Random();	

	public RandomGraphDAGGenerator() {
		randomNumber.setSeed(DAGVariable.getInstance().getRandomSeed());
	}

	public ArrayList<DAGDependTask> getTasks(Workflow wk){
		ArrayList<DAGDependTask> tasks = new ArrayList<DAGDependTask>();
		for(int i=0; i<10; i++){
			tasks.add(new DAGDependTask(new DependTaskInfo(i, -1)));
		}
	 
		tasks.get(0).getChildTaskLink().add(new TaskLink(17, tasks.get(1)));    

		for(DAGDependTask task : tasks)
				task.setBelongWorkflow(wk);

		return tasks;
	}

	@Override
	public List<IAttribute> generate() throws Exception {
		List<IAttribute> workflowSet = new ArrayList<IAttribute>();
		//loop for generating workflow
		for(int i=0;i<DAGVariable.getInstance().getNumberOfWorkflow();i++) {
			int nodeIndex = 0;
			Workflow workflow = new Workflow(i);
			//random interarrival time for workflow
			workflow.setInterArrivalTime(randomNumber.nextInt(DAGVariable.getInstance().getMaxInterArrivalTime()));
			ArrayList<DAGDependTask> tasks = this.getTasks(workflow);
			for(IDepend task : tasks){
				workflow.getTaskList().add(task);
			}
			
			for(int q=0;q<workflowSet.size();q++) {
				Workflow workflowInSet = (Workflow)workflowSet.get(q);
				if(workflow.getInterArrivalTime()<workflowInSet.getInterArrivalTime()) {
					workflowSet.add(q, workflow);
					break;
				}				
			}

			if(!workflowSet.contains(workflow)){
				workflowSet.add(workflow);
			}
		}
		return workflowSet;
	}

}
	 
