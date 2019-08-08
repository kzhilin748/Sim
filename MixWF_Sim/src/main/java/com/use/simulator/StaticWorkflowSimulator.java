package com.use.simulator;

import java.util.ArrayList;
import java.util.List;

import com.use.ALauncher;
import com.use.config.DAGVariable;
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

public class StaticWorkflowSimulator extends EventBaseSimulator {
	// in this simulator, com.use.simulator.EventBaseSimultor.fullTaskLIst is workflow
	private DAGVariable vars;
	private List<IAttribute> waitingQ = new ArrayList<IAttribute>();
	private float avgMakespansOfworkflows;

	public StaticWorkflowSimulator() throws Exception {
		vars = new DAGVariable();
	}

	@Override
	public void simulate() throws Exception {
		for(int i=0;i<DAGVariable.getInstance().getNumberOfExperiments();i++){
			initlize();
			simulateLoop();
			simulateFinish();
		}

		println("avgMakespansOfExperiments:"+avgMakespansOfworkflows/DAGVariable.getInstance().getNumberOfExperiments());
	}

	@Override
	protected void submitTaskEvent() throws Exception {
		for (int i = 0; i < fullTaskList.size(); i++) {
			Workflow workflow = (Workflow) fullTaskList.get(i);
			workflow.bottomRanking();
			workflow.topRanking();
//			DAGVariable.getInstance().getRanking().ranking(workflow);
//			DAGVariable.getInstance().getGrouping().grouping(workflow);
			queue.addToQueue( QueueType.Event, new Event(EventType.submit,
					workflow.getId(),
					workflow.getInterArrivalTime(),
					1,
					workflow
					) );
		}
	}
	@Override
	protected void initlize() throws Exception {
		waitingQ.clear();
		queue = new MixQueue();
		getCluster().alloc();
		fullTaskList = this.generator.generate();
		submitTaskEvent();
	}

	
		
	
	@Override
	protected void simulateLoop() throws Exception {
			while (!queue.isEmpty()) {
				Event event = queue.deQueue();
				currentTime = event.getTime();
				execution(event);
			}
	}

	@Override
	protected void simulateFinish() throws Exception {
		float sumMakespansOfWorkflow=0;
		for(IAttribute tmp:waitingQ) {
			Workflow workflow = (Workflow)tmp;
			sumMakespansOfWorkflow+=workflow.getFinishTime();
			sumMakespansOfWorkflow-=workflow.getInterArrivalTime();
     
			//workflow.printDAG();
			//workflow.printAllTask();
			//workflow.printAllGroup();
		}
		avgMakespansOfworkflows+=sumMakespansOfWorkflow/waitingQ.size();
		println("avgMakespansOfworkflows:"+sumMakespansOfWorkflow/waitingQ.size());
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
			if (event.getTask() instanceof IDepend) {

			}
			else if (event.getTask() instanceof IWorkflow) {
				submitToQueue(event);
			}
			scheduler.schedule();
			break;
		case start:
			if (event.getTask() instanceof IDepend) {
				DAGDependTask tasktmp = (DAGDependTask) event.getTask();
				tasktmp.setTrueStartTime(currentTime);
				tasktmp.setStarted(true);
				
				SimpleNode resource = (SimpleNode)getCluster().getResource(tasktmp.getResourceId());
				resource.getAllocationQueue().add(tasktmp);
				if (ALauncher.isLogEnable()) {
					println(currentTime+"#Task["+tasktmp.getBelongWorkflow().getId()+"]"+tasktmp.getId()+" allocate on "+tasktmp.getResourceId()+" is start.");
				}
			}
			break;
		case end:
			if (event.getTask() instanceof IDepend) {
				DAGDependTask tasktmp = (DAGDependTask) event.getTask();
				tasktmp.setTrueFinishTime(currentTime);
				tasktmp.setScheduled(true);
				if(tasktmp.getChildTaskLink().size()==0) {
					tasktmp.getBelongWorkflow().setFinishTime(currentTime);
				}
				if (ALauncher.isLogEnable()) {
					println(currentTime+"#Task["+tasktmp.getBelongWorkflow().getId()+"]"+tasktmp.getId()+" allocate on "+tasktmp.getResourceId()+" is end.");
				}
			}
			break;
		default:
			super.execution(event);
		}
	}
	public List<IAttribute> getWaitingQ() {
		return waitingQ;
	}
}
