package com.use.MLS;

import com.use.exception.ProcessorException;
import com.use.workflow.Workflow;

public abstract class Iallocate {
	
	public abstract long MLS(Workflow _workflow) throws ProcessorException;

}