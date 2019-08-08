package com.use.simulator;

import java.util.ArrayList;
import java.util.List;

import com.use.ALauncher;
import com.use.config.DAGVariable;
import com.use.exception.InvalidTimeLineException;
import com.use.exception.UnknownEventTypeException;
import com.use.queue.MixQueue;
import com.use.queue.QueueType;
import com.use.queue.event.Event;
import com.use.queue.event.EventType;
import com.use.resource.SimpleNode;
import com.use.workflow.IWorkflow;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;

public class mixWFSimulator extends EventBaseSimulator{
	private DAGVariable vars;
	private List<IAttribute> waitingQ = new ArrayList<IAttribute>();
	private int CPUcount=8;
	private int TotalCPU=0;
	private int WFcount=0;
	private int TatalNodeof100DAG;
	private int totalexeTime=0;
	private long startTime =0;
	public mixWFSimulator() throws Exception{
		vars=new DAGVariable();
	}
	
	@Override
	public void simulate() throws Exception {
		startTime = System.currentTimeMillis();
		for(int i=0;i<DAGVariable.getInstance().getNumberOfExperiments();i++){
			WFcount=i;
			
			System.out.println("count :"+WFcount);
			initlize();
			simulateLoop();
			simulateFinish();
			printtaskinfo((Workflow) fullTaskList.get(WFcount));
		}
		System.out.println("Using Time:" + (System.currentTimeMillis() - startTime) + " ms");
	}
	
	protected void initlize() throws Exception {
		CPUcount=DAGVariable.getInstance().getRESNP();
		TotalCPU=DAGVariable.getInstance().getRESNP();
		waitingQ.clear();
		queue = new MixQueue();
		getCluster().alloc();
		fullTaskList = this.generator.generate();
		submitTaskEvent();
	}

	@Override
	protected int submitToQueue(Event event) {
		
		if (event.getTask() instanceof IDepend) {

		}
		else if (event.getTask() instanceof IWorkflow) {
			IAttribute workflow = event.getTask();
			waitingQ.add(workflow);
		}
		return 0;
	}

	@Override
	protected void execution(Event event) throws Exception {
		switch (event.getEventType()) {
			case submit:
				submitToQueue(event);
				scheduler.schedule();
				break;
			case start:


				System.out.println("task ID : "+ event.getId()+" StartTime "+event.getTime()+" used cpu"+event.getNumberOfProcessors()+"\t"+CPUcount);
				CPUcount-=event.getNumberOfProcessors();
				if(CPUcount<0){
					System.err.println("CPU<1");
					System.exit(1);
				}


				break;
			case end:
				if (event.getTask() instanceof IDepend) {


					CPUcount+=event.getNumberOfProcessors();
					if(CPUcount>TotalCPU){
						
						System.err.println("CPU>TotalCPU");
						System.exit(1);
					}
					System.out.println("task ID : "+ event.getId()+" EndTime "+event.getTime()+"\t"+CPUcount);
					break;
				}
		}
	}
	@Override
	protected void submitTaskEvent() throws Exception {
		
			Workflow workflow = (Workflow) fullTaskList.get(WFcount);
			TatalNodeof100DAG+=workflow.getTaskList().size();
//			if(WFcount==DAGVariable.getInstance().getNumberOfExperiments()-1)
//				System.out.println(" Total node "+TatalNodeof100DAG);
			workflow.bottomRanking();
			workflow.topRanking();
			queue.addToQueue( QueueType.Event, new Event(EventType.submit,
					workflow.getId(),
					workflow.getInterArrivalTime(),
					1,
					workflow
					) );
		

	}

	public List<IAttribute> getWaitingQ() {
		return waitingQ;
	}
	protected void printtaskinfo(Workflow workflow) {
		for(IDepend tmp:workflow.getTaskList()) {
			DAGDependTask _task=(DAGDependTask) tmp;
			System.out.println("["+_task.getId()+","+_task.getTrueStartTime()+","+_task.getFinishTime()+","+_task.getNumberOfProcessors()+"],");
			
		}
		totalexeTime+=workflow.getFinishTime();
		System.out.println(" Total node "+TatalNodeof100DAG);	
		System.out.println(" Total exe time "+totalexeTime);
	}
}
