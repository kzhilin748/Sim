package com.use.simulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.use.ALauncher;
import com.use.utils.USESUtils;
import com.use.workflow.service.PeterActionPerform;
import com.use.workflow.service.PeterService;
import com.use.workflow.task.IDepend;
import com.use.workflow.task.IPeterAttribute;
import com.use.workflow.task.PeterTask;


public class PeterSimulatorRate extends PeterSimulator {

	public PeterSimulatorRate() throws Exception {
		super();
	}

	@Override
	protected void submitTaskEvent() throws Exception {
		if (variable.isUsingRateFactor()) {
			peterServiceRateChang();
		}
		super.submitTaskEvent();
	}

	protected void peterServiceRateChang() throws Exception {
		Scanner file = new Scanner(USESUtils.loadResource("ServiceRate.conf"));
		float[] requestService = new float[servicePool.size()];
		while (file.hasNextLine()) {
			String str = file.nextLine();
			String[] splitStr = str.split(":");
			System.err.println(str);
			int index = Integer.parseInt(splitStr[0]);
			float value = Float.parseFloat(splitStr[1]);
			requestService[index] = value;
		}
		file.close();
		for (PeterService service : PeterActionPerform.getInstance()) {
			service.setRate(requestService[service.getId()]);
			
		}
	}

	protected void OverrideRateFactor() throws IOException {

		/**
		 * Display each ServiceRequsetNumber
		 */
		int Number = 0;
		int[] requestService = new int[servicePool.size()];
		for (int i = 0; i < requestWorkflow.size(); i++) {
	//		 System.err.println("***"+ requestWorkflow.size());
			for (int j = 0; j < requestWorkflow.get(i).getTaskList().size(); j++) {
				Number += requestWorkflow.get(i).getTaskList().size();
	//			 System.err.println("**"+ Number);
				requestService[((PeterTask) requestWorkflow.get(i).getTaskList().get(j)).getService().getId()]++;
				
				if (ALauncher.isLogEnable()) {
					for (IDepend tmp : requestWorkflow.get(i).getTaskList()) {
						IPeterAttribute task = (IPeterAttribute) tmp;
						if (task.getBelongRes().getId() > 4) {
							System.err.println("DEBUG:" + requestWorkflow.get(i).getId() + "," + task.getId());
						}
					}
				}
			
			}
		}
		
		File serviceRate = new File("ServiceRate.conf");
		FileWriter writer = new FileWriter(serviceRate);
		for (int x = 0; x < requestService.length; x++) {
			/**
			 * old rate method 10000
			 */
			writer.write(x + ":" + (((requestService[x] / (float) Number)) / 1.1677778) * variable.getRateFactor() + "\n");
			
//			writer.write(x + ":" + ((requestService[x] / (float) 290) )  + "\n");
			/**
			 * new rate method
			 */
//				writer.write(x + ":" + (float)(((requestService[x] / 11))) * variable.getRateFactor() + "\n");
			
			// if(x == 15 || x == 18 || x == 4 || x == 14 || x==13 || x==12
			// || x==10 || x==5 || x==9 || x==0
			// || x ==1 || x==3 || x==7 || x==11 || x==17 || x==2 || x==8 ||
			// x==16 || x==19){
//			System.err.println("" + x + ":" + requestService[x]);
			// }
		}
		writer.close();
		
	}
	
	@Override
	protected void simulateFinish() throws Exception {
		if (variable.isOverwriteResult()) {
			OverrideRateFactor();
		}
		super.simulateFinish();
	}
}
