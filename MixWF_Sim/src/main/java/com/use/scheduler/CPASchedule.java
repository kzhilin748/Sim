package com.use.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.use.MLS.Iallocate;
import com.use.MLS.newMLS;
import com.use.config.DAGVariable;
import com.use.logwrite.logwrite;
import com.use.simulator.ASimulator;
import com.use.simulator.StaticWorkflowSimulator;
import com.use.simulator.mixWFSimulator;
import com.use.speedup.Speedup;
import com.use.workflow.Workflow;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.TaskLink;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;

public class CPASchedule extends AScheduler{
	protected mixWFSimulator instance =(mixWFSimulator) ASimulator.getInstance();
	protected List<IAttribute> workflowSet = instance.getWaitingQ();
	protected List<IAttribute> readytasklist = new ArrayList<IAttribute>();
	Iallocate MLS =new newMLS();
	logwrite FW=new logwrite();


	@Override
	public  void schedule() throws Exception{
		Workflow workflow =(Workflow) workflowSet.get(0);
		workflow.setTaskOneCPURunTime();
		int DAGTime=Integer.MAX_VALUE;
		List<IDepend> CPList =new ArrayList<IDepend>();
		CPList.addAll(workflow.getCriticalPathTasksList());
		int Tcp=getTcpTime(CPList);
		int Ta=getTaTime(workflow,DAGVariable.getInstance().getRESNP());
		
		while(Tcp>Ta&&!CPList.isEmpty()){
			DAGDependTask _task=(DAGDependTask) findMaxDecreaceTime(CPList);

			
			if(_task.getNumberOfProcessors()>DAGVariable.getInstance().getRESNP()){
				
				System.err.println("Task has too many CPU");
				System.exit(0);
			}
			else if(_task.getNumberOfProcessors()==DAGVariable.getInstance().getRESNP()){
				CPList.remove(_task);
				continue;
			}
			else if(((int)(_task.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(_task.getNumberOfProcessors()+1)))-_task.getComputationTime()==0) {
				CPList.remove(_task);
				continue;
			}
			else if(_task.getNumberOfProcessors()<DAGVariable.getInstance().getRESNP()){
				System.out.println(_task.getId()+" "+_task.getNumberOfProcessors());
				_task.setNumberOfProcessors(_task.getNumberOfProcessors()+1);
				_task.setComputationTime((int) (_task.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(_task.getNumberOfProcessors())));
				
			}
			int DAGTime2=(int) MLS.MLS(workflow);
//			if(DAGTime2>DAGTime) {
//				
////				System.out.println("----------------------------");
////				printtaskinfo(workflow);
////				System.out.println("----------------------------");
//			}
//			else{
//				printtaskinfo(workflow);
//			}
			
			DAGTime=DAGTime2;	
			CPList.clear();
			workflow.ReTopandBottomRank();
			CPList.addAll(workflow.getCriticalPathTasksList());
			Tcp=getTcpTime(CPList);
			Ta=getTaTime(workflow,DAGVariable.getInstance().getRESNP());
//			System.out.println(Ta+" "+Tcp);
			
//			for(IDepend tmp:CPList){
//				System.out.print(tmp.getId());
//			}
//			System.out.println();

		}
		/*
		 * 
		 */
		MLS.MLS(workflow);
		FW.writelog(DAGTime);

	}
	protected int getTcpTime(List<IDepend> CPList){
		int Tcp=0;
		
		for(IDepend tmp:CPList){
			DAGDependTask _task = (DAGDependTask) tmp;
//			System.out.print(_task.getId()+":"+_task.getComputationTime()+" ");
			Tcp+=_task.getComputationTime();
		}
//		System.out.println();
		
		return Tcp;
	}
	protected int getTaTime(Workflow workflow,int TotalCPU){
		int Ta=0;
		int TatalArea=0;
		List<IDepend> AllTaskList=workflow.getTaskList();
		for(IDepend tmp:AllTaskList){
			DAGDependTask _task = (DAGDependTask) tmp;
			TatalArea+=(_task.getNumberOfProcessors()*_task.getComputationTime());
		}
		Ta=TatalArea/TotalCPU;
		return Ta;
	}
	protected IDepend findMaxDecreaceTime(List<IDepend> CPList){
		double MAXDec=-1;
		IDepend MaxDecTask =null;
		for(IDepend tmp:CPList){
			DAGDependTask task =(DAGDependTask) tmp;

			if(task.getNumberOfProcessors()==(DAGVariable.getInstance().getRESNP())||task.getComputationTime()==1)
				continue;

			double decreaseTime = (task.getComputationTime()-task.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(task.getNumberOfProcessors()+1));

			if(decreaseTime>MAXDec){
				MaxDecTask=task;
				MAXDec=decreaseTime;
			}
		}
		if(MaxDecTask==null){
			MaxDecTask=CPList.get(0);
		}
		
		return MaxDecTask;

	}
	protected void printtaskinfo(Workflow workflow) {
		for(IDepend tmp:workflow.getTaskList()) {
			DAGDependTask _task=(DAGDependTask) tmp;
			System.out.println("["+_task.getId()+","+_task.getTrueStartTime()+","+_task.getFinishTime()+","+_task.getNumberOfProcessors()+"],");
			
		}
		
	}
}
				
	
				
	
	
	