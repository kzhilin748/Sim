package com.use.generator;

import java.util.ArrayList;
import java.util.List;

import com.use.config.DAGVariable;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;

public class EqualLigo extends ForkJoinDAGGenerator{
	@Override
	public List<IAttribute> generate() throws Exception {
		
		List<IAttribute> workflowSet = new ArrayList<IAttribute>();
		for(int j=0;j<DAGVariable.getInstance().getNumberOfWorkflow();j++) {
			Workflow workflow = new Workflow(j);
			int computationTime = DAGVariable.getInstance().getMinComputationTime()+randomNumber.nextInt(1+DAGVariable.getInstance().getMaxComputationTime()-DAGVariable.getInstance().getMinComputationTime());
			for(int i=0 ;i<50;i++){
				DAGDependTask Task = (DAGDependTask) generateSingleTask(i,workflow);
				
				Task.setComputationTime(computationTime);
				workflow.getTaskList().add(Task);
			}
			for(IDepend tmp :workflow.getTaskList()){
				
				DAGDependTask task = (DAGDependTask) tmp;
				
				switch(task.getId()){
				case 0 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(12));
					break;
				case 1 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(13));
					break;
				case 2 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(14));
					break;
				case 3 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(15));
					break;
				case 4 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(16));
					break;
				case 5 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(17));
					break;	
				case 6 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(18));
					break;
				case 7 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(19));
					break;	
				case 8 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(20));
					break;	
				case 9 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(21));
					break;	
				case 10 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(22));
					break;	
				case 11 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(23));
					break;		
				case 12 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(24));
					break;		
				case 13 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(24));
					break;	
				case 14 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(24));
					break;
				case 15 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(24));
					break;	
				case 16 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(24));
					break;		
				case 17 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(24));
					break;		
				case 18 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(24));
					break;
				case 19 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(24));
					break;		
				case 20 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(24));
					break;	
				case 21 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(24));
					break;		
				case 22 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(24));
					break;
				case 23 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(24));
					break;	
				case 24 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(25));
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(26));
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(27));
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(28));
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(29));
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(30));
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(31));
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(32));
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(33));
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(34));
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(35));
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(36));
					break;
				case 25 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(37));
					break;	
				case 26 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(38));
					break;
				case 27 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(39));
					break;	
				case 28 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(40));
					break;		
				case 29 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(41));
					break;			
				case 30 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(42));
					break;
				case 31 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(43));
					break;		
				case 32 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(44));
					break;		
				case 33 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(45));
					break;		
				case 34 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(46));
					break;
				case 35 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(47));
					break;
				case 36 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(48));
					break;
				case 37 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(49));
					break;	
				case 38 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(49));
					break;	
				case 39 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(49));
					break;
				case 40 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(49));
					break;
				case 41 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(49));
					break;
				case 42 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(49));
					break;
				case 43 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(49));
					break;
				case 44 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(49));
					break;
				case 45 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(49));
					break;
				case 46 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(49));
					break;
				case 47 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(49));
					break;
				case 48 :
					generateEdge(workflow.getTaskList().get(task.getId()), workflow.getTaskList().get(49));
					break;		
				}
			}
			workflowSet.add(workflow);
		
	}

		
		
		
		return workflowSet;
		
	}

}
