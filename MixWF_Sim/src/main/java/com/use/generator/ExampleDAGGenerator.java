package com.use.generator;

import java.util.ArrayList;
import java.util.List;

import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;
import com.use.workflow.Workflow;

public class ExampleDAGGenerator extends ForkJoinDAGGenerator {

	@Override
	public List<IAttribute> generate() throws Exception {
		
		List<IAttribute> workflowSet = new ArrayList<IAttribute>();
		Workflow workflow = new Workflow(0);
		for(int i=0 ;i<10;i++){
			IDepend Task = generateSingleTask(i,workflow);
			workflow.getTaskList().add(Task);
		}
//		System.out.println(workflow.getTaskList().size());
		for(IDepend tmp :workflow.getTaskList()){
			
			DAGDependTask task = (DAGDependTask) tmp;
			
			switch(task.getId()){
			case 0 :
				task.setComputationTime(100);
				generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(1));
				generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(2));
				generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(3));
				generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(4));
				
				break;
			case 1 :
				task.setComputationTime(1004);
				generateEdge(workflow.getTaskList().get(1), workflow.getTaskList().get(4));
				generateEdge(workflow.getTaskList().get(1), workflow.getTaskList().get(5));
				generateEdge(workflow.getTaskList().get(1), workflow.getTaskList().get(6));
				generateEdge(workflow.getTaskList().get(1), workflow.getTaskList().get(7));
				generateEdge(workflow.getTaskList().get(1), workflow.getTaskList().get(8));
				break;
			case 2 :
				task.setComputationTime(444);
				generateEdge(workflow.getTaskList().get(2), workflow.getTaskList().get(5));
				generateEdge(workflow.getTaskList().get(2), workflow.getTaskList().get(6));
				generateEdge(workflow.getTaskList().get(2), workflow.getTaskList().get(7));
				generateEdge(workflow.getTaskList().get(2), workflow.getTaskList().get(8));
				break;
			case 3 :
				task.setComputationTime(706);
				generateEdge(workflow.getTaskList().get(3), workflow.getTaskList().get(5));
				generateEdge(workflow.getTaskList().get(3), workflow.getTaskList().get(6));
				generateEdge(workflow.getTaskList().get(3), workflow.getTaskList().get(7));
				generateEdge(workflow.getTaskList().get(3), workflow.getTaskList().get(8));
				break;
			case 4 :
				task.setComputationTime(330);
				generateEdge(workflow.getTaskList().get(4), workflow.getTaskList().get(5));
				generateEdge(workflow.getTaskList().get(4), workflow.getTaskList().get(6));
				generateEdge(workflow.getTaskList().get(4), workflow.getTaskList().get(7));
				generateEdge(workflow.getTaskList().get(4), workflow.getTaskList().get(8));
				break;
			case 5 :
				task.setComputationTime(442);
				generateEdge(workflow.getTaskList().get(5), workflow.getTaskList().get(6));
				generateEdge(workflow.getTaskList().get(5), workflow.getTaskList().get(7));
				generateEdge(workflow.getTaskList().get(5), workflow.getTaskList().get(8));
				generateEdge(workflow.getTaskList().get(5), workflow.getTaskList().get(9));
				break;	
			case 6 :
				task.setComputationTime(200);
				generateEdge(workflow.getTaskList().get(6), workflow.getTaskList().get(7));
				generateEdge(workflow.getTaskList().get(6), workflow.getTaskList().get(8));
				generateEdge(workflow.getTaskList().get(6), workflow.getTaskList().get(9));
				break;
			case 7 :
				task.setComputationTime(58);
				generateEdge(workflow.getTaskList().get(7), workflow.getTaskList().get(9));
				break;
			case 8 :
				task.setComputationTime(1212);
				generateEdge(workflow.getTaskList().get(8), workflow.getTaskList().get(9));
				break;
			case 9 :
				task.setComputationTime(100);
				break;
			}
		}
		workflowSet.add(workflow);

		return workflowSet;
	}
	

}
