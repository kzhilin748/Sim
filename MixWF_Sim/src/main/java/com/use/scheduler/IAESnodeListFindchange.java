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
import com.use.exception.ProcessorException;;

public class IAESnodeListFindchange extends AScheduler{
	protected mixWFSimulator instance = (mixWFSimulator) ASimulator.getInstance();
	protected List<IAttribute> workflowSet = instance.getWaitingQ();
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
			
			

			
			while(!CPList.isEmpty()){
				
				Workflow BeforeScheduleWorkflow=(Workflow) DeepCopy.copy(workflow);
				boolean TaskCPUPlus=false;
				DAGDependTask _task=(DAGDependTask) findMaxDecreaceTime(CPList);
				DAGDependTask before_task =(DAGDependTask) DeepCopy.copy(_task);
				CPList.remove(_task);
				if(_task.getNumberOfProcessors()>=8)
					continue;

				if(_task.getComputationTime()>1){
					_task.setNumberOfProcessors(_task.getNumberOfProcessors()+1);
					_task.setComputationTime((int) (_task.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(_task.getNumberOfProcessors())));
					System.out.println("CPU+1      TASK ID :"+_task.getId());
					TaskCPUPlus=true;
				}

				long NewDAGTime=MLS.MLS(workflow);
				
				
				
		
				if(NewDAGTime<DAGTime){
					DAGTime=NewDAGTime;
					found=true;
					
				}

				else{
					
					List<IDepend> nodeList=findEXEperiodTaskList(before_task,BeforeScheduleWorkflow);
					List<IDepend> nodeList_clone=(List<IDepend>) DeepCopy.copy(nodeList);
					while(!nodeList.isEmpty()){
						DAGDependTask MinIncreacetimetask=(DAGDependTask) findMinIncreaceTime(nodeList);
						
						List<IDepend> SubList=findSubCPUTask(before_task,nodeList,nodeList_clone);
						nodeList.remove(MinIncreacetimetask);
						
						
						for(IDepend tmp:SubList){
							DAGDependTask tmpTask=(DAGDependTask) tmp;
							tmpTask.setNumberOfProcessors(tmpTask.getNumberOfProcessors()-1);
							tmpTask.setComputationTime((int) (tmpTask.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(tmpTask.getNumberOfProcessors())));
						}
						
				
						long NewDAGTime2=MLS.MLS(workflow);

				
						
						if(NewDAGTime2<DAGTime){
							DAGTime=NewDAGTime2;
							found=true;
							System.out.println("*****found\n");
							
						}
						else{

							for(IDepend tmp:SubList){
								DAGDependTask tmpTask=(DAGDependTask) tmp;
								tmpTask.setNumberOfProcessors(tmpTask.getNumberOfProcessors()+1);
								tmpTask.setComputationTime((int) (tmpTask.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(tmpTask.getNumberOfProcessors())));
							}
							DAGTime = MLS.MLS(workflow);
							
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
		DAGDependTask _task=(DAGDependTask) workflow.getTaskList().get(task.getId());
		long TaskStartTime = _task.getTrueStartTime();
		long TaskFinishTime = _task.getFinishTime();
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
	
	private List<IDepend> findSubCPUTask(DAGDependTask Basetask,List<IDepend> nodeList,List<IDepend> nodeList_clone){
		
		List<IDepend> SubCPUTaskList=new ArrayList<IDepend>();
		List<IDepend> BeforeTaskList=new ArrayList<IDepend>();
		List<IDepend> AfterTaskList=new ArrayList<IDepend>();
		
		DAGDependTask _task=(DAGDependTask) findMinIncreaceTime(nodeList);
		
		if(_task.getTrueStartTime()>Basetask.getTrueStartTime()){
			long taskUPTime=_task.getTrueStartTime();
			
			while(taskUPTime>Basetask.getTrueStartTime()){
				for(IDepend tmp:nodeList_clone){
					if(SubCPUTaskList.contains(tmp))
						continue;
					DAGDependTask tmptask=(DAGDependTask) tmp;
					if(tmptask.getFinishTime()>Basetask.getTrueStartTime()&&tmptask.getFinishTime()<=taskUPTime&&tmptask.getNumberOfProcessors()>1)
						BeforeTaskList.add(tmptask);
				}
				if(!BeforeTaskList.isEmpty()){
					DAGDependTask beforeTask=(DAGDependTask) findMinIncreaceTime(BeforeTaskList);
					SubCPUTaskList.add(beforeTask);
					BeforeTaskList.clear();
					taskUPTime=beforeTask.getTrueStartTime();
				}
				else{
					break;
				}
			}
		}
		
		if(_task.getFinishTime()<Basetask.getFinishTime()){
			
			long taskDownTime =_task.getFinishTime();
			while(taskDownTime<Basetask.getFinishTime()){
				for(IDepend tmp:nodeList_clone){
					DAGDependTask tmptask=(DAGDependTask) tmp;
					if(tmptask.getTrueStartTime()<Basetask.getFinishTime()&&tmptask.getTrueStartTime()>=taskDownTime&&tmptask.getNumberOfProcessors()>1)
						AfterTaskList.add(tmptask);
				}
				if(!AfterTaskList.isEmpty()){
					DAGDependTask afterTask=(DAGDependTask) findMinIncreaceTime(AfterTaskList);
					SubCPUTaskList.add(afterTask);
					AfterTaskList.clear();
					taskDownTime=afterTask.getFinishTime();
				}
				else
					break;
		
			}
		}
		

		return SubCPUTaskList;
		
	}
	
}
