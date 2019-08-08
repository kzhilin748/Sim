package com.use.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.use.ICASLBTools.BRBS;
import com.use.MLS.Iallocate;
import com.use.MLS.newMLS;
import com.use.exception.ProcessorException;
import com.use.simulator.ASimulator;
import com.use.simulator.mixWFSimulator;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;

public class ICASLB extends AScheduler{
	protected mixWFSimulator instance =(mixWFSimulator) ASimulator.getInstance();
	protected List<IAttribute> workflowSet = instance.getWaitingQ();
	protected List<IAttribute> readytasklist = new ArrayList<IAttribute>();
	Iallocate MLS =new newMLS();

	@Override
	public void schedule() throws Exception {
		Workflow workflow=(Workflow) workflowSet.get(0);
		workflow.setTaskOneCPURunTime();
		long DAGTime = MLS.MLS(workflow);
		List<IDepend> cpList=new ArrayList<IDepend>();
		while(true){
			long NewDAGTime = MLS.MLS(workflow);
			if(NewDAGTime<DAGTime)
				DAGTime=NewDAGTime;
			int LookAheadDepth=findMaxNP(workflow);
			int cnt=0;
			cpList.clear();
			cpList.addAll(findCPListbyPseudoEdgeWF(workflow));
			while(cnt<LookAheadDepth){
				
				
			}
		}
		
		
		
	}
	
	protected int findMaxNP(Workflow workflow){
		int MaxNP=-1;
		for(IDepend tmp:workflow.getTaskList()){
			DAGDependTask task =(DAGDependTask) tmp;
			if(task.getNumberOfProcessors()>MaxNP)
				MaxNP=task.getNumberOfProcessors();
		}
		return MaxNP;
		
	}
	private List<IDepend> findCPListbyPseudoEdgeWF(Workflow workflow) throws ProcessorException{
		BRBS BRBS = new BRBS();
		List<IDepend> CPList =new ArrayList<IDepend>();

		Workflow _workflow=BRBS.SetPseudoEdge(workflow);
		_workflow.topRanking();
		_workflow.bottomRanking();

		List<IDepend> PseudoCPList =_workflow.getCriticalPathTasksList();

		for(IDepend tmp :PseudoCPList){
			CPList.add(workflow.getTaskList().get(tmp.getId()));

		}
		return CPList;

	}

}
