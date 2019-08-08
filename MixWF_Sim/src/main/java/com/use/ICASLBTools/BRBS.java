package com.use.ICASLBTools;

import java.util.ArrayList;
import java.util.List;

import com.use.MLS.Iallocate;
//import com.use.MLS.MLS;
import com.use.MLS.MLSQsortBybottomRK;
import com.use.MLS.newMLS;
import com.use.clone.DeepCopy;
import com.use.config.DAGVariable;
import com.use.exception.ProcessorException;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.TaskLink;

public class BRBS {
	Iallocate MLS =new newMLS();
		
	public Workflow SetPseudoEdge(Workflow workflow) throws ProcessorException{
		
		Workflow _workflow=(Workflow) DeepCopy.copy(workflow);
		
		_workflow.topRanking();
		_workflow.setFinishTime(MLS.MLS(_workflow));
		for(IDepend tmp:_workflow.getTaskList()){
			DAGDependTask task =(DAGDependTask) tmp;
			if(task.getTopRank()<task.getTrueStartTime()){
				createPseudoEdge(task,_workflow);

			}
		}

		return _workflow;
		
	}
	private void createPseudoEdge(DAGDependTask task ,Workflow workflow){
		List<IDepend> AddPseudoEdgeList =new ArrayList<IDepend>();
		for(IDepend tmp :workflow.getTaskList()){
			DAGDependTask _task =(DAGDependTask) tmp;
			
			if(_task.getFinishTime()==task.getTrueStartTime()){
				AddPseudoEdgeList.add(_task);
			}
		}
		
		while(!AddPseudoEdgeList.isEmpty()){
			IDepend tmp =AddPseudoEdgeList.get(0);
			AddPseudoEdgeList.remove(tmp);
			if(!chkIsDependTask(tmp,task)){
				generateEdge(tmp,task);
			}
		}
	}
	
	protected boolean chkIsDependTask(IDepend Ftask,IDepend Stask){
		List<IDepend> DependTaskList =new ArrayList<IDepend>();
		DAGDependTask task1 =(DAGDependTask) Ftask;
		DAGDependTask task2 =(DAGDependTask) Stask;
		for(TaskLink tmp : task1.getParentTaskLink()){
			DAGDependTask _task =(DAGDependTask) tmp.getNextTask();
			DependTaskList.add(_task);
		}
		for(TaskLink tmp : task1.getChildTaskLink()){
			DAGDependTask _task =(DAGDependTask) tmp.getNextTask();
			DependTaskList.add(_task);
		}
		
		if(DependTaskList.contains(task2))
			return true;
		else
			return false;
	}
	
 	protected void generateEdge(IDepend parent, IDepend child) {
		TaskLink parentToChildLink = new TaskLink(0, child);
		parent.getChildTaskLink().add(parentToChildLink);
		TaskLink childToParentLink = new TaskLink(0, parent);
		child.getParentTaskLink().add(childToParentLink);
	}
	
	
}
