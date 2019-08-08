package com.use.ICASLBTools;


import java.util.ArrayList;
import java.util.List;

import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.TaskLink;


public class FindConcurrencyRatio {
	public List<IDepend> getConcurrencyTaskList(DAGDependTask task,Workflow workflow){
		List<IDepend>unConcurrencyTaskList=new ArrayList<IDepend>();
		List<IDepend>ConcurrencyTaskList=new ArrayList<IDepend>();
		
		unConcurrencyTaskList=ParentDFS(task,unConcurrencyTaskList);
		unConcurrencyTaskList=ChildDFS(task,unConcurrencyTaskList);
		
		for(IDepend tmp :workflow.getTaskList()){
			if(!unConcurrencyTaskList.contains(tmp))
				ConcurrencyTaskList.add(tmp);
		}	
		
		ConcurrencyTaskList.remove(task);
		
		return ConcurrencyTaskList;
		
	}
	
	private List<IDepend> ParentDFS(DAGDependTask task,List<IDepend> CRTaskList){
		List<IDepend> unAddTaskList = new ArrayList<IDepend>();
		
		for(TaskLink tmp:task.getParentTaskLink()){
			DAGDependTask _task=(DAGDependTask) tmp.getNextTask();
			if(!CRTaskList.contains(_task))
				CRTaskList.add(_task);
			unAddTaskList.add(_task);
		}
		
		while(!unAddTaskList.isEmpty()){
			DAGDependTask _task=(DAGDependTask) unAddTaskList.get(0);
			unAddTaskList.remove(_task);
			CRTaskList=ParentDFS(_task,CRTaskList);
		}	
		return CRTaskList;
		
		
	}
	
	private List<IDepend> ChildDFS(DAGDependTask task,List<IDepend> CRTaskList){
		List<IDepend> unAddTaskList = new ArrayList<IDepend>();
		
		for(TaskLink tmp:task.getChildTaskLink()){
			DAGDependTask _task=(DAGDependTask) tmp.getNextTask();
			if(!CRTaskList.contains(_task))
				CRTaskList.add(_task);
			unAddTaskList.add(_task);
		}
		
		while(!unAddTaskList.isEmpty()){
			DAGDependTask _task=(DAGDependTask) unAddTaskList.get(0);
			unAddTaskList.remove(_task);
			CRTaskList=ChildDFS(_task,CRTaskList);
		}	
		return CRTaskList;
		
		
	}
}
