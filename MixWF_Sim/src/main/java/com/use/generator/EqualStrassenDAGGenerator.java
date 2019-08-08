package com.use.generator;

import java.util.ArrayList;
import java.util.List;

import com.use.config.DAGVariable;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;

public class EqualStrassenDAGGenerator extends ForkJoinDAGGenerator{
	
	@Override
	public List<IAttribute> generate() throws Exception {
		
		List<IAttribute> workflowSet = new ArrayList<IAttribute>();
		for(int j=0;j<DAGVariable.getInstance().getNumberOfWorkflow();j++) {
			Workflow workflow = new Workflow(j);
			int computationTime = DAGVariable.getInstance().getMinComputationTime()+randomNumber.nextInt(1+DAGVariable.getInstance().getMaxComputationTime()-DAGVariable.getInstance().getMinComputationTime());
			for(int i=0 ;i<23;i++){
				DAGDependTask Task = (DAGDependTask) generateSingleTask(i,workflow);
				Task.setComputationTime(computationTime);
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
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(5));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(6));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(7));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(8));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(9));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(10));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(12));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(13));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(14));
					generateEdge(workflow.getTaskList().get(0), workflow.getTaskList().get(15));
	
					break;
				case 1 :
					
	//				task.setComputationTime(computationTime);
					generateEdge(workflow.getTaskList().get(1), workflow.getTaskList().get(11));
	
					break;
				case 2 :
	
					generateEdge(workflow.getTaskList().get(2), workflow.getTaskList().get(11));
	
					break;
				case 3 :
					
					
					generateEdge(workflow.getTaskList().get(3), workflow.getTaskList().get(12));
	
					break;
				case 4 :
	
					generateEdge(workflow.getTaskList().get(4), workflow.getTaskList().get(13));
	
					break;
				case 5 :
	
	
					generateEdge(workflow.getTaskList().get(5), workflow.getTaskList().get(14));
	
					break;	
				case 6 :
	
					generateEdge(workflow.getTaskList().get(6), workflow.getTaskList().get(15));
	
					break;
				case 7 :
	
					generateEdge(workflow.getTaskList().get(7), workflow.getTaskList().get(16));
	
					break;
				case 8 :
	
					generateEdge(workflow.getTaskList().get(8), workflow.getTaskList().get(16));
	
					break;
				case 9 :
	
					generateEdge(workflow.getTaskList().get(9), workflow.getTaskList().get(17));
	
					break;	
				case 10 :
	
					generateEdge(workflow.getTaskList().get(10), workflow.getTaskList().get(17));
	
					break;	
				case 11 :
	
					generateEdge(workflow.getTaskList().get(11), workflow.getTaskList().get(18));
					generateEdge(workflow.getTaskList().get(11), workflow.getTaskList().get(21));
					
					break;	
				case 12 :
	
					generateEdge(workflow.getTaskList().get(12), workflow.getTaskList().get(20));
					generateEdge(workflow.getTaskList().get(12), workflow.getTaskList().get(21));
					
					break;	
				case 13 :
	
					generateEdge(workflow.getTaskList().get(13), workflow.getTaskList().get(19));
					generateEdge(workflow.getTaskList().get(13), workflow.getTaskList().get(21));
					
					break;
					
				case 14 :
	
					generateEdge(workflow.getTaskList().get(14), workflow.getTaskList().get(18));
					generateEdge(workflow.getTaskList().get(14), workflow.getTaskList().get(20));
					
					break;	
					
				case 15 :
	
					generateEdge(workflow.getTaskList().get(15), workflow.getTaskList().get(18));
					generateEdge(workflow.getTaskList().get(15), workflow.getTaskList().get(19));
					
					break;
				case 16 :
	
					generateEdge(workflow.getTaskList().get(16), workflow.getTaskList().get(21));
	
					break;	
				case 17 :
	
					generateEdge(workflow.getTaskList().get(17), workflow.getTaskList().get(18));
	
					break;	
				case 18 :
	
					generateEdge(workflow.getTaskList().get(18), workflow.getTaskList().get(22));
	
					break;	
				case 19 :
	
					generateEdge(workflow.getTaskList().get(19), workflow.getTaskList().get(22));
	
					break;	
				case 20 :
	
					generateEdge(workflow.getTaskList().get(20), workflow.getTaskList().get(22));
	
					break;	
				case 21 :
	
					generateEdge(workflow.getTaskList().get(21), workflow.getTaskList().get(22));
	
					break;	
				}
			}
			workflowSet.add(workflow);
		
	}

		
		
		
		return workflowSet;
		
		
	}

	

}
