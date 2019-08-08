package com.use.generator;

import java.util.ArrayList;
import java.util.List;

import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;

public class SimpleDAGGenerator extends ForkJoinDAGGenerator{
	
	
	@Override
	public List<IAttribute> generate() throws Exception {
		
		
		
		List<IAttribute> workflowSet = new ArrayList<IAttribute>();
		Workflow workflow = new Workflow(0);
		for(int i=0 ;i<4;i++){
			IDepend Task = generateSingleTask(i,workflow);
			workflow.getTaskList().add(Task);
		}
		for(IDepend tmp :workflow.getTaskList()){
			
			DAGDependTask task = (DAGDependTask) tmp;
			switch(task.getId()){
				case 0 :
					task.setComputationTime(100);
					
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(1));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(2));
					break;
				case 1 :
					task.setComputationTime(80);

					generateEdge(workflow.getTaskList().get(1), workflow.getTaskList().get(3));
					break;
				case 2 :
			
					task.setComputationTime(40);
					generateEdge(workflow.getTaskList().get(2), workflow.getTaskList().get(3));
					break;
				case 3 :
				
					task.setComputationTime(100);
					break;
				
			}
		}
		workflowSet.add(workflow);
		return workflowSet;
		
		
		
	}
	
	

}
