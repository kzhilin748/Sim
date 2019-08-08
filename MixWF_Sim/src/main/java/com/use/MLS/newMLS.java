package com.use.MLS;

import java.util.ArrayList;
import java.util.List;

import com.use.exception.ProcessorException;
import com.use.queue.MixQueue;
import com.use.queue.QueueType;
import com.use.queue.event.Event;
import com.use.queue.event.EventType;
import com.use.simulator.ASimulator;
import com.use.simulator.mixWFSimulator;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.TaskLink;

public class newMLS extends Iallocate{

	@Override
	public long MLS(Workflow _workflow) throws ProcessorException {
		mixWFSimulator instance = (mixWFSimulator) ASimulator.getInstance();
		MixQueue queue = (MixQueue)instance.getQueue();
		if(!queue.getActionQueue().isEmpty())
			queue.getActionQueue().clear();
		
		long DAGMLSTime=0;	
		Workflow workflow = _workflow;
		workflow.topRanking();
		workflow.bottomRanking();
		List<IDepend> UnStartTaskList =new ArrayList<IDepend>();
		UnStartTaskList.addAll(_workflow.getTaskList());
		List<IDepend> StartTaskList =new ArrayList<IDepend>();
		List<IDepend> readyQ =new ArrayList<IDepend>();
		while(StartTaskList.size()<_workflow.getTaskList().size()){
			
			if(!UnStartTaskList.isEmpty())
				findReadTask(readyQ,UnStartTaskList,StartTaskList);
			
			
//			System.out.println(readyQ.size());
//			
//			for(int i=0;i<readyQ.size();i++){
//				System.out.print(readyQ.get(i).getId()+"  ");
//			}
//			
//			System.out.println();
			
			if(queue.getActionQueue().isEmpty()){
				DAGDependTask _task =(DAGDependTask) readyQ.get(0);
				readyQ.remove(_task);
			
				_task.setTrueStartTime(0);
				_task.setFinishTime(0 + _task.getComputationTime());
				queue.addToQueue(QueueType.Action, new Event(EventType.start, _task.getId(), 0, _task.getNumberOfProcessors(), _task));
				queue.addToQueue(QueueType.Action, new Event(EventType.end, _task.getId(), 0 + _task.getComputationTime(),_task.getNumberOfProcessors(), _task));
				
				DAGMLSTime=0 + _task.getComputationTime();
				
				StartTaskList.add(_task);
			}
			
			if(!readyQ.isEmpty()){
				
				DAGDependTask _task =(DAGDependTask) readyQ.get(0);
				readyQ.remove(_task);
				long taskStartTime=findtaskStartTime(_task);
				_task.setTrueStartTime(taskStartTime);
				_task.setFinishTime(taskStartTime + _task.getComputationTime());
				queue.addToQueue(QueueType.Action, new Event(EventType.start, _task.getId(), taskStartTime, _task.getNumberOfProcessors(), _task));
				queue.addToQueue(QueueType.Action, new Event(EventType.end, _task.getId(), taskStartTime + _task.getComputationTime(),_task.getNumberOfProcessors(), _task));
				
				if(DAGMLSTime<taskStartTime + _task.getComputationTime())
					DAGMLSTime=taskStartTime + _task.getComputationTime();
				StartTaskList.add(_task);
			}
		}
		_workflow.setFinishTime(DAGMLSTime);
		return DAGMLSTime;
	}
	
	private void findReadTask(List<IDepend> readyQ,List<IDepend> UnStartTaskList,List<IDepend> StartTaskList){
//		System.out.println(readyQ.size());
		
		for(int i=0;i<UnStartTaskList.size();i++){
			DAGDependTask tmp = (DAGDependTask) UnStartTaskList.get(i);
			if(tmp.getParentTaskLink().size()==0){
				tmp.setEST(0);
				readyQ.add(tmp);
				
			}	
			else{
				boolean CanBeReady=true;
				long estTime=0;
				for(TaskLink tmp2:tmp.getParentTaskLink()){
					
					if(!StartTaskList.contains(tmp2.getNextTask())){
						CanBeReady=false;
						break;
					}
					DAGDependTask tmp3=(DAGDependTask) tmp2.getNextTask();
					if(tmp3.getFinishTime()>estTime)
						estTime=tmp3.getFinishTime();
				}
				if(CanBeReady){
					readyQ.add(tmp);
					tmp.setEST(estTime);
				}

			}
		}
		
		UnStartTaskList.removeAll(readyQ);
	}
	private long findtaskStartTime(DAGDependTask task) throws ProcessorException{
		mixWFSimulator instance = (mixWFSimulator) ASimulator.getInstance();
		MixQueue queue = (MixQueue)instance.getQueue();
		List<Integer> CPUrecord =new ArrayList<Integer>();
		List<Long> Timerecord =new ArrayList<Long>();
		int TotalCPU = instance.getCluster().getResource(0).getTotalCpu();
		int Flag=0;
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
	private long findbestGAP(List<Long> Timerecord,List<Integer> cpuRecord,DAGDependTask task){
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
}
