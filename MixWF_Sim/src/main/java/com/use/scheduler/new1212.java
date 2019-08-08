package com.use.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.use.clone.DeepCopy;
import com.use.config.DAGVariable;
import com.use.speedup.Speedup;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IDepend;

public class new1212 extends IAES{
	@Override
	public void schedule() throws Exception {
		
		Workflow workflow =(Workflow) workflowSet.get(0);
		DAGDependTask firsttask= (DAGDependTask) workflow.getTaskList().get(0);
		int MaxCycle= firsttask.getChildTaskLink().size();
		int Cyclecount=0;
		List<IDepend> Hasbepluslist=new ArrayList<IDepend>();
		workflow.setTaskOneCPURunTime();
		long DAGTime = MLS.MLS(workflow);
		
		
		
	
		boolean found =true;
		while(found==true){
			found=false;
			workflow.ReTopandBottomRank();
			List<IDepend> CPList =findCPListbyPseudoEdgeWF(workflow);
			
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
				if(_task.getNumberOfProcessors()>=(DAGVariable.getInstance().getRESNP()))
					continue;

				if(_task.getComputationTime()>1){
					_task.setNumberOfProcessors(_task.getNumberOfProcessors()+1);
					_task.setComputationTime((int) (_task.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(_task.getNumberOfProcessors())));
					TaskCPUPlus=true;
				}
				Hasbepluslist.add(_task);
				long NewDAGTime=MLS.MLS(workflow);
				
				
					
				
		
				if(NewDAGTime<DAGTime){
					DAGTime=NewDAGTime;
					found=true;
					Cyclecount=0;
					Hasbepluslist.clear();
				}
				else if(NewDAGTime==DAGTime&&Cyclecount<=MaxCycle){
					found=true;
					
					Cyclecount++;
				}
				
				

				else{
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
							System.err.println("FFFFFFFFFFFFFFF");
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

	}
}
