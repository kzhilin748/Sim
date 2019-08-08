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

public class fixIAES  extends IAES{
	
	public void schedule() throws Exception {
		
		Workflow workflow =(Workflow) workflowSet.get(0);
		DAGDependTask firsttask= (DAGDependTask) workflow.getTaskList().get(0);
//		int MaxCycle= firsttask.getChildTaskLink().size();
		List<IDepend> Hasbepluslist=new ArrayList<IDepend>();
		workflow.setTaskOneCPURunTime();
		long DAGTime = MLS.MLS(workflow);
		List<IDepend> Hasbeminuslist=new ArrayList<IDepend>();
//		workflow.printAllTask();
		
		
		int cnt=0;
		boolean found =true;
		while(found==true){ 
			found=false;
			workflow.ReTopandBottomRank();
			List<IDepend> CPList =findCPListbyPseudoEdgeWF(workflow);
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
				if(_task.getNumberOfProcessors()>=(DAGVariable.getInstance().getRESNP())||Hasbeminuslist.contains(_task))
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
				
				
					
				
		
				if(NewDAGTime<DAGTime){
					Hasbeminuslist.clear();
					DAGTime=NewDAGTime;
					found=true;
					cnt=0;
					Hasbepluslist.clear();
				}
				else if(NewDAGTime==DAGTime&&chkCP&&cnt<firsttask.getChildTaskLink().size()) {
					
					found=true;
					cnt++;
//					System.out.println("WORK!!");
					
				}

				
				

				else{
					
					Hasbeminuslist.clear();
					List<IDepend> nodeList=findEXEperiodTaskList(_task,workflow);
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
							Hasbeminuslist.addAll(nodeList);
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
//		boolean chk=Chkcanbeallocate();
//		while(chk!=false) {
//			List<IDepend> CPList =findCPListbyPseudoEdgeWF(workflow);
//			while(!CPList.isEmpty()) {
//				DAGDependTask _task=(DAGDependTask) findMaxDecreaceTime(CPList);
//				CPList.remove(_task);
//				if(_task.getNumberOfProcessors()>=(DAGVariable.getInstance().getRESNP()))
//					continue;
//
//				if(_task.getComputationTime()>1){
//					_task.setNumberOfProcessors(_task.getNumberOfProcessors()+1);
//					_task.setComputationTime((int) (_task.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(_task.getNumberOfProcessors())));
//				}
//			}
//			DAGTime = MLS.MLS(workflow);
//			chk=Chkcanbeallocate();
//		}
//		DAGTime = MLS.MLS(workflow);
		
		/*
		 * test
		 */
		MLS.MLS(workflow);
		FW.writelog((int) DAGTime);

		/*
		 * test
		 */

	}
	
	protected boolean Chkcanbeallocate() throws InvalidTimeLineException {
		boolean chk=true;
		mixWFSimulator instance = (mixWFSimulator) ASimulator.getInstance();
		MixQueue queue = (MixQueue)instance.getQueue();
		int TotalCPU=DAGVariable.getInstance().getRESNP();
		while ( !queue.isEmpty() ) {
//			System.out.println(TotalCPU);
			Event event = queue.deQueue();
			switch (event.getEventType()) {
			case start:
				
				TotalCPU-=event.getNumberOfProcessors();


				break;
			case end:
				if (event.getTask() instanceof IDepend) {
					TotalCPU+=event.getNumberOfProcessors();
				}
			}
			if(TotalCPU==0) {
				chk=false;
				break;
				
			}
		}
		return chk;
		
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

}
