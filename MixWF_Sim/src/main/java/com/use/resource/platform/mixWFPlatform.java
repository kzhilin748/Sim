package com.use.resource.platform;

import com.use.config.DAGVariable;
import com.use.resource.SimpleNode;
import com.use.resource.info.ResInfo;


public class mixWFPlatform extends APlatform implements IPlatform{
	int cpu;
	
	@Override
	public void alloc() throws Exception {
		cpu=DAGVariable.getInstance().getRESNP();
		ResInfo info = new ResInfo(0);
		info.setCpu(cpu);
		SimpleNode resource=new SimpleNode(info);
		add(resource);
	}
	
	@Override
	public SimpleNode getResource(int resourceIndex) {
		return (SimpleNode) list.get(0);
	}
}
