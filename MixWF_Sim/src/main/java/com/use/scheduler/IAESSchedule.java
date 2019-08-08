package com.use.scheduler;

import java.util.ArrayList;
import java.util.List;

import com.use.simulator.ASimulator;
import com.use.simulator.mixWFSimulator;
import com.use.speedup.Speedup;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;
import com.use.ICASLBTools.BRBS;
import com.use.MLS.Iallocate;
//import com.use.MLS.MLS;
import com.use.MLS.newMLS;
import com.use.clone.DeepCopy;
import com.use.config.DAGVariable;
import com.use.exception.ProcessorException;
import com.use.logwrite.logwrite;
import com.use.queue.MixQueue;;

public class IAESSchedule extends AScheduler{
	protected mixWFSimulator instance = (mixWFSimulator) ASimulator.getInstance();
	protected List<IAttribute> workflowSet = instance.getWaitingQ();
	logwrite FW=new logwrite();
	Iallocate MLS =new newMLS();


	@Override
	public void schedule() throws Exception {
		int cnt=1;
		Workflow workflow =(Workflow) workflowSet.get(0);
//		workflow.printDAG();
		workflow.setTaskOneCPURunTime();
		long DAGTime = MLS.MLS(workflow);

		boolean found =true;
		while(found==true){
			found=false;
			workflow.ReTopandBottomRank();
			List<IDepend> CPList =findCPListbyPseudoEdgeWF(workflow);
//			System.out.println(cnt+"----------------------");
//			for(IDepend tmp:CPList) {
//				DAGDependTask task_ =(DAGDependTask) tmp;
//				System.out.println(task_.getId()+" "+task_.getComputationTime());
//			}
//			cnt++;
//			System.out.println("----------------------");

			
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

				long NewDAGTime=MLS.MLS(workflow);
				
				
				
		
				if(NewDAGTime<DAGTime){
					DAGTime=NewDAGTime;
					found=true;

				}

				else{

					List<IDepend> nodeList=findEXEperiodTaskList(_task,workflow);
					
					boolean TaskhasChange =false;
					
					while(!nodeList.isEmpty()){
						DAGDependTask MinIncreacetimetask=(DAGDependTask) findMinIncreaceTime(nodeList);
						nodeList.remove(MinIncreacetimetask);
						
						
						
						DAGDependTask beforeMinIncreacetimetask=(DAGDependTask) BeforeScheduleWorkflow.getTaskList().get(MinIncreacetimetask.getId());
						if(MinIncreacetimetask.getTrueStartTime()>beforeMinIncreacetimetask.getTrueStartTime()){
							
							
							MinIncreacetimetask.setNumberOfProcessors(MinIncreacetimetask.getNumberOfProcessors()-1);
							MinIncreacetimetask.setComputationTime((int) (MinIncreacetimetask.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(MinIncreacetimetask.getNumberOfProcessors())));
							TaskhasChange=true;
						}
						
						
						
						long NewDAGTime2=MLS.MLS(workflow);
						

						
						if(NewDAGTime2<DAGTime){
							DAGTime=NewDAGTime2;
							found=true;
							System.out.println("*****found"); 
						}
						else if(TaskhasChange){
							MinIncreacetimetask.setNumberOfProcessors(MinIncreacetimetask.getNumberOfProcessors()+1);
							MinIncreacetimetask.setComputationTime((int) (MinIncreacetimetask.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(MinIncreacetimetask.getNumberOfProcessors())));
						}
						
						TaskhasChange=false;
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
		/*
		 * test
		 */
		

		FW.writelog((int) DAGTime);
		/*
		 * test
		 */
	}

	private List<IDepend> findCPListbyPseudoEdgeWF(Workflow workflow) throws ProcessorException{
		BRBS BRBS = new BRBS();
		List<IDepend> CPList =new ArrayList<IDepend>();

		Workflow _workflow=BRBS.SetPseudoEdge(workflow);
		_workflow.topRanking();
		_workflow.bottomRanking();

		List<IDepend> PseudoCPList =_workflow.getCriticalPathTasksList();
		DAGDependTask _task =(DAGDependTask) PseudoCPList.get(0);
		int rank=_task.getBottomRank()+_task.getTopRank();
		for(IDepend tmp:_workflow.getTaskList()) {
			DAGDependTask task =(DAGDependTask) tmp;
			if((task.getBottomRank()+task.getTopRank())==rank&&!PseudoCPList.contains(task)) 
				PseudoCPList.add(task);
				;
		}
		
		for(IDepend tmp :PseudoCPList){
			CPList.add(workflow.getTaskList().get(tmp.getId()));
		}
		return CPList;

	}

	private IDepend findMaxDecreaceTime(List<IDepend> CPList){
		double MAXDec=-1;
		IDepend MaxDecTask =null;
		for(IDepend tmp:CPList){
			DAGDependTask task =(DAGDependTask) tmp;

			if(task.getNumberOfProcessors()==instance.getCluster().getResource(0).getTotalCpu()||task.getComputationTime()==1)
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
	private IDepend findMinIncreaceTime(List<IDepend> nodeList){
		double MinDec=Integer.MAX_VALUE;
		IDepend minDecTask=null;
		for(IDepend tmp :nodeList){
			DAGDependTask task =(DAGDependTask) tmp;

			double IncreaceTime = (task.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(task.getNumberOfProcessors()-1)-task.getComputationTime());

			
			if(IncreaceTime<=MinDec){
				minDecTask=task;
				MinDec=IncreaceTime;
			}
		}

		if(minDecTask==null){
			minDecTask=nodeList.get(0);
		}


		return minDecTask;

	}
	private List<IDepend> findEXEperiodTaskList(DAGDependTask task,Workflow workflow){
		List<IDepend> nodelist =new ArrayList<IDepend>();
		long TaskStartTime = task.getTrueStartTime();
		long TaskFinishTime = task.getFinishTime();
		for(IDepend tmp :workflow.getTaskList()){
			DAGDependTask tmptask = (DAGDependTask) tmp;
			if(tmptask.getFinishTime()>TaskStartTime&&tmptask.getFinishTime()<=TaskFinishTime&&tmptask.getNumberOfProcessors()>1){
				nodelist.add(tmptask);
			}
			else if(tmptask.getNumberOfProcessors()>1&&tmptask.getTrueStartTime()>=TaskStartTime&&tmptask.getTrueStartTime()<TaskFinishTime){
				if(!nodelist.contains(tmptask))
						nodelist.add(tmptask);
			}
		}

		return nodelist;

	}
}
