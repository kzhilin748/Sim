package com.use.simulator;

/**
 * clone service with mix kcut. 
 * 
 * @author peter
 *
 */
public class PeterSimulatorMixS extends PeterSimulatorCloneS {

	public PeterSimulatorMixS() throws Exception {
		super();
	}

	@Override
	protected void extraVM() throws Exception {
		if (!variable.isExtraVM()) {
			return;
		}
		super.extraVM();
	}
	
	@Override
	protected void submitTaskEvent() throws Exception {
		
		if (!variable.isExtraVM()) {
			cloneService();
		}
		super.submitTaskEvent();
	}

}
