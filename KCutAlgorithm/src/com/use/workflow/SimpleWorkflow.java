package com.use.workflow;

import com.use.workflow.ranking.IRanking;


public class SimpleWorkflow extends AWorkflow {
	private int id;
	private long submitTime;
	private IRanking rankingType;
	
	public SimpleWorkflow(int interArrivalTime) {
		super(interArrivalTime);
	}

	public SimpleWorkflow() {
		super(0);
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public boolean isScheduled() {
		return false;
	}

	@Override
	public void setScheduled(boolean isScheduled) {
	}

	@Override
	public void setStarted(boolean isStarted) {
		
	}

	@Override
	public void setFinishTime(long finishTime) {
		
	}

	@Override
	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}

	@Override
	public boolean isStarted() {
		return false;
	}

	@Override
	public long getSubmitTime() {
		return this.submitTime;
	}

	@Override
	public long getFinishTime() {
		return 0;
	}

	public void setId(int id) {
		this.id = id;
	}

	public IRanking getRankingType() {
		return rankingType;
	}

	public void setRankingType(IRanking rankingType) {
		this.rankingType = rankingType;
	}

}
