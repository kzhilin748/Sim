package com.use.generator;

import java.util.ArrayList;
import java.util.List;

import com.use.config.DAGVariable;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;

public class ��atmulDAGGenerator extends ForkJoinDAGGenerator{
	
//	@Override
	public List<IAttribute> generate() throws Exception {
		
		
		List<IAttribute> workflowSet = new ArrayList<IAttribute>();
		for(int j=0;j<DAGVariable.getInstance().getNumberOfWorkflow();j++) {
				Workflow workflow = new Workflow(j);
			for(int i=0 ;i<8;i++){
				DAGDependTask Task = (DAGDependTask) generateSingleTask(i,workflow);
				workflow.getTaskList().add(Task);
			}
			for(IDepend tmp :workflow.getTaskList()){
				
				DAGDependTask task = (DAGDependTask) tmp;
				
				switch(task.getId()){
				case 0 :
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(1));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(2));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(3));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(4));
					
					break;
				case 1 :
					
	//				task.setComputationTime(computationTime);
					generateEdge(workflow.getTaskList().get(1), workflow.getTaskList().get(5));
	
					break;
				case 2 :
	
					generateEdge(workflow.getTaskList().get(2), workflow.getTaskList().get(5));
	
					break;
				case 3 :
					
					
					generateEdge(workflow.getTaskList().get(3), workflow.getTaskList().get(6));
	
					break;
				case 4 :
	
					generateEdge(workflow.getTaskList().get(4), workflow.getTaskList().get(6));
	
					break;
				case 5 :
	
	
					generateEdge(workflow.getTaskList().get(5), workflow.getTaskList().get(7));
	
					break;	
				case 6 :
	
					generateEdge(workflow.getTaskList().get(6), workflow.getTaskList().get(7));
	
					break;
	
	
				}
			}
			workflowSet.add(workflow);

		
		}
		
		return workflowSet;
		
		
		}



}
