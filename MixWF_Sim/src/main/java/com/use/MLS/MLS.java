package com.use.MLS;

import java.util.ArrayList;
import java.util.List;

import com.use.exception.ProcessorException;
import com.use.queue.MixQueue;
import com.use.queue.QueueType;
import com.use.queue.event.Event;
import com.use.queue.event.EventType;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.TaskLink;
import com.use.simulator.ASimulator;
import com.use.simulator.mixWFSimulator;

public class MLS {
	
	public static long MLS(Workflow _workflow) throws ProcessorException{
	
		mixWFSimulator instance = (mixWFSimulator) ASimulator.getInstance();
		MixQueue queue = (MixQueue)instance.getQueue();
		if(!queue.getActionQueue().isEmpty())
			queue.getActionQueue().clear();
		
		long DAGMLSTime=0;	
		Workflow workflow = _workflow;
		workflow.topRanking();
		workflow.bottomRanking();
		List<IDepend> readyQ =new ArrayList<IDepend>();
		List<IDepend> unScheduleQ = new ArrayList<IDepend>(); 
		
		boolean FinshList[]=new boolean[workflow.getTaskList().size()];
		for(IDepend tmp :workflow.getTaskList())
			unScheduleQ.add(tmp);
//		System.out.println(unScheduleQ.size());
		DAGDependTask firstTask= (DAGDependTask) unScheduleQ.get(0);
		firstTask.setTrueStartTime(0);
		firstTask.setFinishTime(0 + firstTask.getComputationTime());
		queue.addToQueue(QueueType.Action, new Event(EventType.start, firstTask.getId(), 0, firstTask.getNumberOfProcessors(), firstTask));
		queue.addToQueue(QueueType.Action, new Event(EventType.end, firstTask.getId(), 0 + firstTask.getComputationTime(),firstTask.getNumberOfProcessors(), firstTask));
		
		unScheduleQ.remove(firstTask);
		FinshList[0]=true;
//		FinshList[firstTask.getId()]=true;
		DAGMLSTime=firstTask.getComputationTime();
		
		while (FinshList[workflow.getTaskList().size()-1]==false){
			for (int i = 0; i < unScheduleQ.size();){
				DAGDependTask  tmp1= (DAGDependTask) unScheduleQ.get(i);
				boolean isAllParentFinish = true;
				for (TaskLink tmp2 : tmp1.getParentTaskLink()){
					DAGDependTask parentTask = (DAGDependTask) tmp2.getNextTask();
					if(FinshList[parentTask.getId()]==false){
						isAllParentFinish=false;
						break;
					}	
				}
				if(isAllParentFinish){
					readyQ.add(tmp1);
					unScheduleQ.remove(tmp1);
					i=0;
				}
				else {
					i++;
					continue;
				}
			}	
			if(!readyQ.isEmpty()){
				DAGDependTask _task =(DAGDependTask) readyQ.get(0);
				readyQ.remove(_task);
				long taskStartTime=findtaskstartTime(_task);
				_task.setTrueStartTime(taskStartTime);
				_task.setFinishTime(taskStartTime + _task.getComputationTime());
				queue.addToQueue(QueueType.Action, new Event(EventType.start, _task.getId(), taskStartTime, _task.getNumberOfProcessors(), _task));
				queue.addToQueue(QueueType.Action, new Event(EventType.end, _task.getId(), taskStartTime + _task.getComputationTime(),_task.getNumberOfProcessors(), _task));
				DAGMLSTime=taskStartTime + _task.getComputationTime();
//				System.out.println(DAGMLSTime);
				FinshList[_task.getId()]=true;
				
			}
			
		}
		_workflow.setFinishTime(DAGMLSTime);
		return DAGMLSTime;
		
	}
	private static long  findtaskstartTime(DAGDependTask task) throws ProcessorException{
		mixWFSimulator instance = (mixWFSimulator) ASimulator.getInstance();
		MixQueue queue = (MixQueue)instance.getQueue();
		List<Integer> CPUrecord =new ArrayList<Integer>();
		List<Long> Timerecord =new ArrayList<Long>();
		int TotalCPU = instance.getCluster().getResource(0).getTotalCpu();
		int Flag=0;
		SetTaskEST(task);
		long last_time=task.getEST();
		for(int i=0;i<queue.getActionQueue().size();i++){
			if(queue.getActionQueue().get(i).getTime()<=last_time){
				Flag=i;
			}else{
				break;
			}
		}
		int currentCPU = instance.getCluster().getResource(0).getTotalCpu();
		for(int i=0;i<=Flag;i++){
			Event event = queue.getActionQueue().get(i);
			switch (event.getEventType()) {
			case start:
				currentCPU -= event.getNumberOfProcessors();
				break;
			case end:
				currentCPU += event.getNumberOfProcessors();
				break;
			default:
				break;
			}
		}
		
		
		
		Timerecord.add(task.getEST()); 										//EST
		CPUrecord.add(currentCPU);//EST's CPU numbers
		
		for(int i=Flag+1;i<queue.getActionQueue().size();i++){
			Event event = queue.getActionQueue().get(i);
			switch (event.getEventType()) {
			case start:
				currentCPU -= event.getNumberOfProcessors();
				Timerecord.add(event.getTime());
				CPUrecord.add(currentCPU);
				break;
			case end:
				currentCPU += event.getNumberOfProcessors();
				Timerecord.add(event.getTime());
				CPUrecord.add(currentCPU);
				break;
			default:
				break;
			}
			if (TotalCPU > instance.getCluster().getResource(0).getTotalCpu()) {
				throw new ProcessorException("Cpu Error > " + instance.getCluster().getResource(0).getTotalCpu()+" : "+TotalCPU);
			}
			else if (TotalCPU < 0) {
				System.out.println("currentFreeCPU"+TotalCPU);
				throw new ProcessorException("Cpu Error < 0");				
			}
		}
		
		long taskcanExeTime=findbestGAP(Timerecord,CPUrecord,task);
		return taskcanExeTime;
		
	}
	private static long findbestGAP(List<Long> Timerecord,List<Integer> cpuRecord,DAGDependTask task){
		long startTime=-1;
		int RecordSize =Timerecord.size();

		for(int i =0;i<RecordSize;i++){
			startTime=Timerecord.get(i);
			long beginTime=Timerecord.get(i);
			int beginTimeCpu=cpuRecord.get(i);
			
			if(beginTimeCpu<task.getNumberOfProcessors()){
				continue;
			}
						
			else if(i==RecordSize-1){

					break;
			}
			
			boolean findgap = false;
			for(int j =i+1;j<RecordSize;j++){
				long EndTime=Timerecord.get(j);
				int thisTimeCpu=cpuRecord.get(j);
				
				
				if(EndTime-beginTime>=task.getComputationTime()){
					findgap = true;
					break;
				}

				if(thisTimeCpu<task.getNumberOfProcessors()){
					findgap=false;

					break;
				}
				if(j==RecordSize-1&&thisTimeCpu>=task.getNumberOfProcessors()){
					findgap = true;
					break;
				}
					
				if(EndTime-beginTime<task.getComputationTime())
					continue;


			}	
			if(findgap)
				break;
		}


		return startTime;
		
		
	}
	
	
	
	
	private static void SetTaskEST(DAGDependTask task)throws ProcessorException{
		long maxFinishTime=-1;
		for(TaskLink tmp :task.getParentTaskLink()){
			DAGDependTask parentTask = (DAGDependTask) tmp.getNextTask();
			if(parentTask.getFinishTime()>=maxFinishTime)
				maxFinishTime=parentTask.getFinishTime();
		}
		if(maxFinishTime!=-1)
			task.setEST(maxFinishTime);
		else if(maxFinishTime==-1&&task.getParentTaskLink().size()==0)
			task.setEST(0);
		else
			throw new ProcessorException("EST=-1");
	}
}
