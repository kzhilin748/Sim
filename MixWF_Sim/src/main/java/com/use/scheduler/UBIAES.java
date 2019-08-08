package com.use.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.use.clone.DeepCopy;
import com.use.config.DAGVariable;
import com.use.exception.InvalidTimeLineException;
import com.use.queue.MixQueue;
import com.use.queue.event.Event;
import com.use.simulator.ASimulator;
import com.use.simulator.mixWFSimulator;
import com.use.speedup.Speedup;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IDepend;

public class UBIAES  extends IAES{
	
	public void schedule() throws Exception {
		
		Workflow workflow =(Workflow) workflowSet.get(0);
//		workflow.printDAG();
		DAGDependTask firsttask= (DAGDependTask) workflow.getTaskList().get(0);
//		workflow.printAllTask();
//		int MaxCycle= firsttask.getChildTaskLink().size();
		List<IDepend> Hasbepluslist=new ArrayList<IDepend>();
		List<IDepend> badselectionnode=new ArrayList<IDepend>();
		workflow.setTaskOneCPURunTime();
		long DAGTime = MLS.MLS(workflow);
		double beforeUnt=CaculateUnti(workflow, DAGTime, DAGVariable.getInstance().getRESNP());
		int cnt=0,cntnum;
		
		
		

		boolean found =true;
		while(found==true){ 
			cntnum=chkexpandcnt(workflow,DAGVariable.getInstance().getRESNP());
			found=false;
			workflow.ReTopandBottomRank();
			List<IDepend> CPList = new ArrayList<IDepend>();
			CPList.addAll(findCPListbyPseudoEdgeWF(workflow));
			boolean chkCP =chkpluralCP(CPList,workflow);
//			System.out.print("CPList : ");
//			for(IDepend tmp:CPList){
//				DAGDependTask _task=(DAGDependTask) tmp;
//				System.out.print(_task.getId()+"  ");
//			}
//			System.out.println("");
			
			while(!CPList.isEmpty()){
				boolean TaskCPUPlus=false;
				DAGDependTask _task=(DAGDependTask) findMaxDecreaceTime(CPList);
				Workflow BeforeScheduleWorkflow=(Workflow) DeepCopy.copy(workflow);
				
				CPList.remove(_task);
				if(_task.getNumberOfProcessors()>=(DAGVariable.getInstance().getRESNP())||badselectionnode.contains(_task))
					continue;
				else if(((int)(_task.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(_task.getNumberOfProcessors()+1)))==_task.getComputationTime()) {
					continue;
				}
				else if(_task.getComputationTime()>1){
					_task.setNumberOfProcessors(_task.getNumberOfProcessors()+1);
					_task.setComputationTime((int) (_task.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(_task.getNumberOfProcessors())));
					TaskCPUPlus=true;
				}
				Hasbepluslist.add(_task);
				long NewDAGTime=MLS.MLS(workflow);
				double newUnt=CaculateUnti(workflow, NewDAGTime, DAGVariable.getInstance().getRESNP());
				
					
				
		
				if(NewDAGTime<DAGTime){
				//	System.out.println(DAGTime+"  "+NewDAGTime);
					DAGTime=NewDAGTime;
					found=true;

				}
				else if(NewDAGTime==DAGTime&&newUnt>beforeUnt&&!chktaskdelay(BeforeScheduleWorkflow,workflow)) {
				//	System.out.println(" * "+DAGTime+"  "+NewDAGTime+" T ID" +_task.getId());
					beforeUnt=newUnt;
					found=true;


				}

				
				

				else {
					List<IDepend> nodeList=findEXEperiodTaskList(_task,workflow);
					
					nodeList.remove(Hasbepluslist.get(Hasbepluslist.size()-1));
					List<IDepend> frontoftaskList=new ArrayList<IDepend>();
					
					for(IDepend tmp:workflow.getTaskList()){
						DAGDependTask tmptask=(DAGDependTask) tmp;
						if(tmptask.getTrueStartTime()==_task.getFinishTime()){
							frontoftaskList.add(tmptask);
						}
//						if(tmptask.getTrueStartTime()>=_task.getTrueStartTime()&&tmptask.getFinishTime()<=_task.getFinishTime()&&!(frontoftaskList.contains(tmptask))){
//							frontoftaskList.add(tmptask);
//						}
					}
//					System.err.println("Size :"+frontoftaskList.size());
					for(int i=0;i<frontoftaskList.size();){
						DAGDependTask Basetmptask=(DAGDependTask)frontoftaskList.get(i);
						DAGDependTask tmptask=(DAGDependTask) BeforeScheduleWorkflow.getTaskList().get(frontoftaskList.get(i).getId());
						if(tmptask.getTrueStartTime()>Basetmptask.getTrueStartTime()){
							i++;
						}
						else{
							frontoftaskList.remove(frontoftaskList.get(i));
						}	
					}
					if(frontoftaskList.size()!=0){
						
						for(IDepend tmp:nodeList){
							DAGDependTask tmptask=(DAGDependTask)tmp;

							
								tmptask.setNumberOfProcessors(tmptask.getNumberOfProcessors()-1);
								tmptask.setComputationTime((int) (tmptask.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(tmptask.getNumberOfProcessors())));
								
							


						}
						long NT=MLS.MLS(workflow);
						if(NT<DAGTime){
//							System.err.println("FFFFFFFFFFFFFFF");
							DAGTime=NT;
							found=true;
						}
						else{
							for(IDepend tmp:nodeList){
								DAGDependTask tmptask=(DAGDependTask)tmp;
								
									tmptask.setNumberOfProcessors(tmptask.getNumberOfProcessors()+1);
									tmptask.setComputationTime((int) (tmptask.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(tmptask.getNumberOfProcessors())));
							
								

							}
							DAGTime=MLS.MLS(workflow);
						}
					}

				}
				if(found==true){
					Hasbepluslist.clear();
					badselectionnode.clear();
					cnt=0;
					break;
				}
				else if(found==false){
					if(TaskCPUPlus==true){
						_task.setNumberOfProcessors(_task.getNumberOfProcessors()-1);
						_task.setComputationTime((int) (_task.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(_task.getNumberOfProcessors())));
						TaskCPUPlus=false;
						DAGTime = MLS.MLS(workflow);
					}
					
				}
				

			}

		}

		
		/*
		 * test
		 */
		MLS.MLS(workflow);
		FW.writelog((int) DAGTime);

		/*
		 * test
		 */

	}
	

	
	boolean chkpluralCP(List<IDepend> CPList,Workflow workflow) {
		boolean chk=false;
		List<IDepend> workflowalltask = workflow.getTaskList();
		int maxrank=((DAGDependTask)CPList.get(0)).getTopRank()+((DAGDependTask)CPList.get(0)).getBottomRank();
		for(IDepend tmp:workflowalltask) {
			DAGDependTask _task=(DAGDependTask) tmp;
			if((_task.getTopRank()+_task.getBottomRank())==maxrank&&!CPList.contains(_task)) {
				chk=true;
			}
		}
		
		return chk;
		
	}
	int chkexpandcnt(Workflow workflow,int totalCPU) {
		int maxCPU=1;
		for(IDepend tmp:workflow.getTaskList()) {
			DAGDependTask _task=(DAGDependTask) tmp;
			if(_task.getNumberOfProcessors()>maxCPU)
				maxCPU=_task.getNumberOfProcessors();
		}
		if(totalCPU<maxCPU) {
			System.out.println("CPU too much (UBIAES)");
			System.exit(0);
		}
		
		return (totalCPU-maxCPU);
	}
	
	boolean chktaskdelay(Workflow bw,Workflow aw) {
		boolean chk=false;
		int wfsize=bw.getTaskList().size();
		for(int i= 0;i<wfsize;i++) {
			DAGDependTask btmptask=(DAGDependTask) bw.getTaskList().get(i);
			DAGDependTask atmptask=(DAGDependTask) aw.getTaskList().get(i);
			if(btmptask.getFinishTime()<atmptask.getFinishTime()) {
				chk=true;
				break;
			}
				
		}
		
		
		return chk;
		
	}
	


}
