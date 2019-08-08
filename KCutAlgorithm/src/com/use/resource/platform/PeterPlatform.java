package com.use.resource.platform;

import com.use.config.PeterVariable;
import com.use.resource.IResPeter;
import com.use.resource.PeterNode;
import com.use.resource.info.informIResNode;

public class PeterPlatform extends APlatform {
	
	@Override
	public void alloc() throws Exception {
		for (int i = 0; i < PeterVariable.getInstance().getVmNumber(); i++) {
			informIResNode inform = new informIResNode(i);
			IResPeter node = new PeterNode(inform);
			list.add(node);
		}
		
		/**
		 * method 3
		 * 
		 */
		
//		for (int i = 0; i <= 2; i++) {
//			informIResNode inform2 = new informIResNode(i);
//			PeterNode node2 = new PeterNode(inform2);
//			list.add(node2);
//		}
	}
	
	@Override
	public IResPeter getResource(int resourceIndex) {
		return (IResPeter) list.get(resourceIndex);
	}
}
