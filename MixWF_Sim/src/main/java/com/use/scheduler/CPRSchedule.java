package com.use.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import com.use.simulator.ASimulator;
import com.use.simulator.mixWFSimulator;
import com.use.speedup.Speedup;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;
import com.use.MLS.Iallocate;
//import com.use.MLS.MLS;
import com.use.MLS.MLSQsortBybottomRK;
import com.use.MLS.newMLS;
import com.use.config.DAGVariable;
import com.use.logwrite.logwrite;
import com.use.queue.MixQueue;
import com.use.queue.QueueType;
import com.use.queue.event.Event;
import com.use.queue.event.EventType;

public class CPRSchedule extends AScheduler {

	protected mixWFSimulator instance = (mixWFSimulator) ASimulator.getInstance();
	protected List<IAttribute> workflowSet = instance.getWaitingQ();
//	protected MixQueue queue = (MixQueue)instance.getQueue();
	Iallocate MLS =new newMLS();
	logwrite FW=new logwrite();


	@Override
	public void schedule() throws Exception {
		double a= DAGVariable.getInstance().getAlpha();
		Workflow workflow = (Workflow) workflowSet.get(0);
		workflow.setTaskOneCPURunTime();
//		workflow.printDAG();
		MixQueue queue = (MixQueue)instance.getQueue();
		workflow.setTaskOneCPURunTime();
		boolean TimeRemainModified=true;
		workflow.topRanking();
		for(IDepend tmp0 :workflow.getTaskList()){
			DAGDependTask _task =  (DAGDependTask) tmp0;
			_task.setOneCPUcomputationTime(_task.getComputationTime());
			_task.setEST(_task.getTopRank());
		} 
		long Time =MLS.MLS(workflow);
		while(TimeRemainModified){
			TimeRemainModified=false;
			List<IDepend> AllTaskList= new ArrayList<IDepend>();

			for (IDepend tmp :workflow.getTaskList()){
				DAGDependTask _task =  (DAGDependTask) tmp;
				if(_task.getNumberOfProcessors()<instance.getCluster().getResource(0).getTotalCpu())
					AllTaskList.add(tmp);
			}
			boolean TimeHasModified=false;
			
			while(!AllTaskList.isEmpty()||TimeHasModified==true){
				workflow.bottomRanking();
				
				workflow.topRanking();
				sortIDependTaskList(AllTaskList);
				
				DAGDependTask task =  (DAGDependTask) AllTaskList.get(0);
//				System.out.println(task.getId()+" :"+(task.getTopRank()+task.getBottomRank()));
				
				
				if(task.getComputationTime()==1){
					AllTaskList.remove(task);
					continue;
				}


				task.setNumberOfProcessors(task.getNumberOfProcessors()+1);
				
				
				
				task.setComputationTime((int)(task.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(task.getNumberOfProcessors())));
				
				
				long NewTime = MLS.MLS(workflow);
				
				if(NewTime<Time){
					Time=NewTime;
					TimeRemainModified=true;
//					TimeHasModified=true;
				}
				else{
					task.setNumberOfProcessors(task.getNumberOfProcessors()-1);
					task.setComputationTime((int)(task.getOneCPUcomputationTime()*Speedup.getAMDlawSpeedup(task.getNumberOfProcessors())));
					
				}
				AllTaskList.remove(task);
			}
			
			
		}
		workflow.setFinishTime(MLS.MLS(workflow));

		
		
		/*
		 * 
		 */
		FW.writelog((int) workflow.getFinishTime());
		
	}
	private List<IDepend> sortIDependTaskList(List<IDepend> taskList){
		
		for(IDepend tmp :taskList){
			DAGDependTask task =  (DAGDependTask) tmp;
			task.setRank(task.getTopRank()+task.getBottomRank());
		}

		Collections.sort( taskList, new Comparator<IDepend>(){
		    public int compare( IDepend task1, IDepend task2 )
		    {
		    	DAGDependTask task =  (DAGDependTask) task1;
		    	DAGDependTask _task =  (DAGDependTask) task2;
				return -(task.getRank()-_task.getRank());
		    }
		});

		return taskList;
		
	}

		

}
