package com.use.resource.platform;

import com.use.config.DAGVariable;
import com.use.resource.IRes;
import com.use.resource.SimpleNode;
import com.use.resource.info.ResInfo;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IDepend;

public class WorkflowPlatform extends APlatform implements IPlatform {

	protected int numberOfResource;
	
	@Override
	public void alloc() throws Exception {
		this.getResourcelist().clear();
		numberOfResource = DAGVariable.getInstance().getNumberOfResource();
		for(int i = 0;i<numberOfResource;i++) {
			ResInfo info = new ResInfo(i);
			info.setSpeed(1);
			SimpleNode resource = new SimpleNode(info);
			add(resource);
		}
	}
	
	public void printTimeLine() {
		for(IRes tmp:list) {
			System.out.print("Resource#"+tmp.getId()+":");
			SimpleNode resource = (SimpleNode)tmp;
			for(IDepend tmp2:resource.getAllocationQueue()) {
				DAGDependTask task = (DAGDependTask)tmp2;
				System.out.print(task.getTrueStartTime()+" "+task.getTrueFinishTime()+" ");
			}
			System.out.println();
		}
	}
	public void printTimeLineEFT() {
		for(IRes tmp:list) {
			System.out.print("Resource#"+tmp.getId()+":");
			SimpleNode resource = (SimpleNode)tmp;
			for(IDepend tmp2:resource.getAllocationQueue()) {
				DAGDependTask task = (DAGDependTask)tmp2;
				System.out.print(task.getEST()+" "+task.getEFT()+" ");
			}
			System.out.println();
		}
	}
	
	public void clonePlatform(WorkflowPlatform cloneBase){
		try {
			alloc();
		} catch (Exception e) {
			System.err.println("clone alloc fail");
		}
		for(int i=0;i<numberOfResource;i++) {
			SimpleNode clone = (SimpleNode)list.get(i);
			SimpleNode baseResource = cloneBase.getResource(i);
			clone.getAllocationQueue().addAll(baseResource.getAllocationQueue());
			clone.getOrderQueue().addAll(baseResource.getOrderQueue());
		}
	}
	
}
