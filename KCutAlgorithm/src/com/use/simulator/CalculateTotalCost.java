package com.use.simulator;




import com.use.config.PeterVariable;
import com.use.resource.IRes;
import com.use.resource.PeterNode;
import com.use.simulator.ASimulator;
import com.use.simulator.PeterSimulator;
import com.use.workflow.service.PeterService;
import com.use.workflow.task.TaskLink;

public class CalculateTotalCost {
	private PeterSimulator simulator;
	private int[] eachServiceCost ;
	private int[] eachServiceCloneAmount ;
	private int SRGTotalCost;
	
	public CalculateTotalCost(){
		 simulator= (PeterSimulator) ASimulator.getInstance();
		 eachServiceCost = new int[PeterVariable.getInstance().getTotalServiceNumber()];
		 eachServiceCloneAmount = new int[PeterVariable.getInstance().getTotalServiceNumber()];
		 SRGTotalCost=0;
	}
	public int calculateCost()  {
		for(IRes resnode : simulator.getCluster().getResourcelist()){
			PeterNode node = (PeterNode) resnode;
			for(PeterService service:node.getDeployService()){
//				System.err.println("serviceID "+service.getId()+"  "+service.getGlobalServicePool().size());
				for(TaskLink serviceLink : service.getGlobalServicePool()){
//					System.err.println(" service "+ (service.getId()) +" to service "+(serviceLink.getNextTask().getId())+" cost "+serviceLink.getWeight());
					if(!node.getDeployService().contains(serviceLink.getNextTask())){
						eachServiceCost[service.getId()]+=serviceLink.getWeight();
					}
				}
				eachServiceCloneAmount[service.getId()]++;
			}
		}
		
		for(int i=0;i<PeterVariable.getInstance().getTotalServiceNumber();i++){
//			if(eachServiceCloneAmount[i]>1)
//				eachServiceCost[i]/=eachServiceCloneAmount[i];
			SRGTotalCost+=eachServiceCost[i];
		}	
		return SRGTotalCost;
	}
	

}
