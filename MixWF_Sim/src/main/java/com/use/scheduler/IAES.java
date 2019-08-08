
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
import com.use.MLS.newMLS;
import com.use.logwrite.*;

import com.use.clone.DeepCopy;
import com.use.config.DAGVariable;
import com.use.exception.ProcessorException;
import com.use.queue.MixQueue;;

public class IAES extends AScheduler{
	protected mixWFSimulator instance = (mixWFSimulator) ASimulator.getInstance();
	protected List<IAttribute> workflowSet = instance.getWaitingQ();
	logwrite FW=new logwrite();
	Iallocate MLS =new newMLS();
	


	@Override
	public void schedule() throws Exception {
		
		Workflow workflow =(Workflow) workflowSet.get(0);
		
		
		
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

				long NewDAGTime=MLS.MLS(workflow);
				
				
					
				
		
				if(NewDAGTime<DAGTime){
					DAGTime=NewDAGTime;
					found=true;

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

		/*
		 * test
		 */
		FW.writelog((int) DAGTime);

		/*
		 * test
		 */
	}

	protected List<IDepend> findCPListbyPseudoEdgeWF(Workflow workflow) throws ProcessorException{
		BRBS BRBS = new BRBS();
		List<IDepend> CPList =new ArrayList<IDepend>();

		Workflow _workflow=BRBS.SetPseudoEdge(workflow);
		_workflow.topRanking();
		_workflow.bottomRanking();
		
		List<IDepend> PseudoCPList =_workflow.getCriticalPathTasksList();

		for(IDepend tmp :PseudoCPList){
			CPList.add(workflow.getTaskList().get(tmp.getId()));

		}
		PseudoCPList=null;
		_workflow=null;
		
		return CPList;

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
	protected IDepend findMinIncreaceTime(List<IDepend> nodeList){
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
	protected List<IDepend> findEXEperiodTaskList(DAGDependTask task,Workflow workflow){
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
	protected void Reschedule(Workflow workflow,long DAGTime) throws ProcessorException{
		
		List<IDepend> CPlist =findCPListbyPseudoEdgeWF(workflow);
		List<IDepend> CurTaskList = new ArrayList<IDepend>();
		int curtaskcpu =0;
		long allcurExeTime=0;
		for(IDepend tmp :CPlist){
			DAGDependTask basetask = (DAGDependTask) tmp;
			curtaskcpu+=basetask.getNumberOfProcessors();
			for(IDepend tmp2 :workflow.getTaskList()){
				DAGDependTask cur_task = (DAGDependTask) tmp2;
				
				if(cur_task.getTrueStartTime()==basetask.getTrueStartTime()&&cur_task.getFinishTime()<=basetask.getFinishTime()
						&&cur_task.getId()!=basetask.getId()){
					CurTaskList.add(cur_task);
					curtaskcpu+=cur_task.getNumberOfProcessors();
					if(basetask.getId()==0)
						System.out.println(" ID " +cur_task.getId()+" EXET "+cur_task.getTrueStartTime());
				}
			}
			
//			System.out.println(" task ID :"+basetask.getId()+" size "+CurTaskList.size());
			if(!CurTaskList.isEmpty()){
				CurTaskList.add(basetask);
				List<IDepend> CurTaskList_Copy=(List<IDepend>) DeepCopy.copy(CurTaskList);
				for(IDepend tmp3 :CurTaskList){
					DAGDependTask tmptask = (DAGDependTask) tmp3;
					tmptask.setNumberOfProcessors(curtaskcpu);
					tmptask.setComputationTime((int) (tmptask.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(curtaskcpu)));
				}
				long NT=MLS.MLS(workflow);
				if(NT<DAGTime )
					System.out.println("EEEEEEEEEEEEEEEEEEEEEE");
				else{
					for(int i=0;i<CurTaskList.size();i++){
						DAGDependTask tmp1=(DAGDependTask) CurTaskList.get(i);
						DAGDependTask tmp2=(DAGDependTask) CurTaskList_Copy.get(i);
						tmp1.setNumberOfProcessors(tmp2.getNumberOfProcessors());
						tmp1.setComputationTime(tmp2.getComputationTime());
					}
					NT=MLS.MLS(workflow);
					System.out.println("NOOOOOOOOOOOOOOOOOOOOO");
				}	
			}
			CurTaskList.clear();
			curtaskcpu =0;
			allcurExeTime=0;
		}
		
	}
	
	protected double CaculateUnti(Workflow workflow ,long time ,int cpu){
		
		int TotaltTaskArea=0;
		for(IDepend tmp :workflow.getTaskList()){
			DAGDependTask _task=(DAGDependTask) tmp;
			TotaltTaskArea+=_task.getNumberOfProcessors()*(_task.getComputationTime());
			
		}
		
		int allArea=(int) (time*cpu);

		return (TotaltTaskArea*1000)/allArea;
		
	}
}
