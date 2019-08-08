package com.use.ICASLBTools;

import com.use.clone.DeepCopy;
import com.use.workflow.Workflow;
import com.use.workflow.task.DAGDependTask;
import com.use.workflow.task.IDepend;

public class DowneySpeedup {
		
	public void setSpeedUpTime(IDepend task, int CPU){
		double SpeedUp =0;
		int n =CPU;
		DAGDependTask _task =(DAGDependTask) task;
		int AvgPara =_task.getAvgPara();
		double Sigma =_task.getSigma();
		
		if(Sigma<=1){
			if(n>=1&&n<AvgPara){
				SpeedUp=(AvgPara*n)/(AvgPara+(Sigma*(n-1)/2));
			}
			else if(n>=AvgPara&&n<=(2*AvgPara-1)){
				SpeedUp=(AvgPara*n)/((Sigma*(AvgPara-1)/2)+n*(1-Sigma/2));
			}
			else if(n>=Sigma&&n>=(2*AvgPara)-1)
				SpeedUp=AvgPara;
		}
		else if(Sigma>1){
			if(n>=1&&n<=(AvgPara+AvgPara*Sigma-Sigma)){
				SpeedUp =(n*AvgPara*(Sigma+1))/(Sigma*(n+AvgPara-1)+AvgPara);
			}
			else if(n>(AvgPara+AvgPara*Sigma-Sigma)){
				SpeedUp=AvgPara;
			}
		}
		
		if(SpeedUp!=0){
			System.out.println(" SpeedUp "+SpeedUp);
			long newtime = (long) (_task.getOneCPUcomputationTime()/SpeedUp);
			_task.setdecreaseEXETime(newtime-_task.getComputationTime());
		}
	}
	
	public void setoptimalCPU(IDepend task){
		DAGDependTask _task =(DAGDependTask) task;
		int AvgPara =_task.getAvgPara();
		double Sigma =_task.getSigma();
		int optimalCPU=(int) ((AvgPara*(Sigma+1)-1)/Sigma);
		_task.setBestofprocess(optimalCPU);
		
	}
	
			
		
}
