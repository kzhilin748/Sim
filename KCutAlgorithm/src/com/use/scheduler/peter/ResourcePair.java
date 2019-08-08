package com.use.scheduler.peter;

import com.use.resource.PeterNode;

public class ResourcePair {
	protected long EST;
	protected PeterNode resource;
	
	public ResourcePair(long est, PeterNode res) {
		this.EST = est;
		this.resource = res;
	}
	
	public long getEST() {
		return EST;
	}
	
	public PeterNode getResource() {
		return resource;
	}
	
}
