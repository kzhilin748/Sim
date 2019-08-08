package com.use.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.use.config.PeterVariable;
import com.use.workflow.IWorkflow;
import com.use.workflow.service.PeterActionPerform;
import com.use.workflow.service.PeterService;
import com.use.workflow.task.IAttribute;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.IPeterAttribute;
import com.use.workflow.task.PeterTask;
import com.use.workflow.task.TaskLink;

public class AssignService {
	private final PeterActionPerform servicePool;
	private final PeterVariable variable;
	private final boolean[][] isReversedService;
	private final List<PeterService>[][] serviceArrayPool = new ArrayList[4][4];
	private final List<PeterService> startnodePool = new ArrayList<PeterService>();
	private final List<PeterService>[] endnodePool = new ArrayList[4];
	private final List<PeterService> servicetmp = new ArrayList<PeterService>();
	private final Random selectEndService = new Random();
	private final Random selectService = new Random(1);
	private final Random random = new Random(2);
	private final Random rtype = new Random(15);
	private IPeterAttribute preTask;
	private IPeterAttribute prePreTask;
	private final List<PeterService> list[];

	public AssignService(PeterActionPerform servicePool, PeterVariable variable) {
		this.servicePool = servicePool;
		this.variable = variable;
		isReversedService = new boolean[variable.getTotalServiceNumber()][variable.getTotalServiceNumber()];
		list = new List[variable.getTotalDag()];
	}

	public void reassignService(IAttribute workflow, int index) {
		for (int i = 0; i < ((IWorkflow) workflow).getTaskList().size(); i++) {
			PeterTask task = (PeterTask) ((IWorkflow) workflow).getTaskList().get(i);
			PeterService service = list[index].get(i);
			task.setService(service);
			if (!variable.getDeploy().getEnabledServicePool().contains(service)) {
				service.setScheduled(true);
			}
		}
	}
	
	public void assignService(List<IAttribute> workflowSet) {
		this.assignServiceByRandom(workflowSet);
		for (PeterService service : servicePool) {
			if (service.isScheduled()) {
				variable.getDeploy().getEnabledServicePool().add(service);
			}
		}
	}
	
	/**
	 * assign Service to each node in workflow
	 * 
	 * @param workflowSet
	 */
	private void assignServiceByRandom(List<IAttribute> workflowSet) {
		servicetmp.addAll(servicePool);
		
		int firstFork = variable.getLevelOneFork();
		int secondFork = variable.getLevelTwoFork();
		int branch = firstFork > secondFork ? firstFork : secondFork;
		for (int i = 0; i <= branch; i++) {
			endnodePool[i] = new ArrayList<PeterService>();
			for (int j = 0; j <= branch; j++) {
				serviceArrayPool[i][j] = new ArrayList<PeterService>();
			}
		}
		
		for (PeterService service : servicePool) {
			int forkJoin = random.nextInt(2);
			int join = rtype.nextInt(firstFork) + 1;
			int fork = rtype.nextInt(secondFork) + 1;
			if (forkJoin == 0) {
				join = 1;
				fork = rtype.nextInt(branch) + 1;
				if (fork == 1) {
					startnodePool.add(service);
				}
			}
			
			if (forkJoin == 1) {
				fork = 1;
				join = rtype.nextInt(branch) + 1;
				startnodePool.add(service);
			}
			
			endnodePool[join].add(service);
			serviceArrayPool[join][fork].add(service);
		}
		
		for (IAttribute tmp : workflowSet) {
			IWorkflow workflow = (IWorkflow)tmp;
			if (list[workflow.getId()] == null) {
				list[workflow.getId()] = new ArrayList<PeterService>();
			}
			for (IDepend taskTmp : workflow.getTaskList()) {
				IPeterAttribute task = (IPeterAttribute) taskTmp;
				int join = task.getParentTaskLink().size();
				int fork = task.getChildTaskLink().size();
				PeterService service = null;
				if (task.getId() == 0) {
					service = getServiceByRandom(startnodePool);
				}
				if (task.getId() != 0 && task.getId() != workflow.getTaskList().size() - 1) {
					service = getServiceByRandom(serviceArrayPool[join][fork]);
				}
				if (task.getId() == workflow.getTaskList().size() - 1) {
					selectEndService.setSeed(task.getId() + 1);
					service = getServiceByRandom(endnodePool[join], selectEndService);
				}

				if (task.getId() > 1) {
					service = checkPreTaskService(task, workflow.getTaskList().size(), service);
					if (preTask != null) {
						int nextTaskLinkSize = preTask.getChildTaskLink().size();
						IPeterAttribute nextTask = (IPeterAttribute) preTask.getChildTaskLink().get(0).getNextTask();
						PeterService nextTaskService = nextTask.getService();
						if ( nextTaskLinkSize == 2 && preTask.getChildTaskLink().get(1).getNextTask().equals(task) ) {
							PeterService preService = preTask.getService();
							PeterService prePreService = prePreTask.getService();
							/**
							 * TODO duplicate while
							 */
							while (service == nextTaskService || service == preService || service == prePreService || isReversedService[service.getId()][preService.getId()]) {
								/**
								 * TODO duplicate code
								 */
								if (task.getId() != workflow.getTaskList().size() - 1) {
									service = getServiceByRandom(serviceArrayPool[join][fork]);
								}
								if (task.getId() == workflow.getTaskList().size() - 1) {
									service = getServiceByRandom(endnodePool[join], selectEndService);
								}
							}
						}
						
						if (nextTaskLinkSize == 3 && preTask.getChildTaskLink().get(2).getNextTask().equals(task)) {
							IPeterAttribute nextTask2 = (IPeterAttribute) preTask.getChildTaskLink().get(1).getNextTask();
							PeterService nextTaskService2 = nextTask2.getService();
							PeterService preService = preTask.getService();
							PeterService prePreService = prePreTask.getService();
							/**
							 * TODO duplicate while
							 */
							while (service == nextTaskService || service == nextTaskService2 || service == preService || service == prePreService || isReversedService[service.getId()][preService.getId()]) {
								/**
								 * TODO duplicate code
								 */
								if (task.getId() != workflow.getTaskList().size() - 1) {
									service = getServiceByRandom(serviceArrayPool[join][fork]);
								}
								if (task.getId() == workflow.getTaskList().size() - 1) {
									service = getServiceByRandom(endnodePool[join], selectEndService);
								}
							}
						}
					}
				}
						
				if (task.getId() == 1) {
					IPeterAttribute preTask = (IPeterAttribute) task.getParentTaskLink().get(0).getNextTask();
					PeterService preService = preTask.getService();
					while (service == preService || isReversedService[service.getId()][preService.getId()]) {
						service = getServiceByRandom(serviceArrayPool[join][fork]);
					}
				}
				
				if (!task.getParentTaskLink().isEmpty()) {
					for (TaskLink preTaskLink : task.getParentTaskLink()) {
						PeterService preService = ((IPeterAttribute) preTaskLink.getNextTask()).getService();
						isReversedService[preService.getId()][service.getId()] = true;
					}
				}
				
				task.setService(service);
				if (!variable.getDeploy().getEnabledServicePool().contains(service)) {
					service.setScheduled(true);
				}
				list[workflow.getId()].add(service);
			}
		}
	}

	private PeterService getServiceByRandom(List<PeterService> list, Random random) {
		int size = list.size();
		int index = random.nextInt(size);
		return list.get( index );
	}
	
	private PeterService getServiceByRandom(List<PeterService> list) {
		return this.getServiceByRandom(list, selectService);
	}
	
	private PeterService checkPreTaskService(IPeterAttribute task, int workflowTaskSize, PeterService selectedService) {
		preTask = null;
		int join = task.getParentTaskLink().size();
		int fork = task.getChildTaskLink().size();
		PeterService service = selectedService;
		for (TaskLink preTaskLink : task.getParentTaskLink()) {
			preTask = (IPeterAttribute) preTaskLink.getNextTask();
			PeterService preService = preTask.getService();
			for (TaskLink prePreTaskLink : preTask.getParentTaskLink()) {
				prePreTask = (IPeterAttribute) prePreTaskLink.getNextTask();
				PeterService prePreService = prePreTask.getService();
				while (service == preService || service == prePreService || isReversedService[service.getId()][preService.getId()]) {
					/**
					 * TODO duplicate code
					 */
					if (task.getId() != workflowTaskSize - 1) {
						service = getServiceByRandom( serviceArrayPool[join][fork] );
					}
					if (task.getId() == workflowTaskSize - 1) {
						service = getServiceByRandom(endnodePool[join], selectEndService);
					}
					PeterService retn = checkPreTaskService(task, workflowTaskSize, service);
					if (retn == null) {
						break;
					}
					else {
						service = retn;
					}
				}
			}
		}
		return service;
	}

}
