package com.use.workflow.task;

import com.use.set.TaskGroup;
import com.use.workflow.task.info.DependTaskInfo;

public class DAGDependTask extends DependTask {

	private int computationTime;
	private int bottomRank;
	private int topRank;
	private int rank;
	private long trueStartTime;
	private long trueFinishTime;
	private TaskGroup belongGroup;
	private int resourceId;
	private int groupId;
	private int Bestofprocess;
	private int OneCPUcomputationTime;
	private int AvgPara;
	private double Sigma;
	private long decreaseEXETime;
	private int level;
	
	
	public DAGDependTask(IDAG taskInfo) {
		super(taskInfo);
		this.bottomRank = 0;
		this.topRank = 0;
		this.rank = 0;
		this.trueFinishTime =0;
		this.trueStartTime = 0;
		this.Bestofprocess=0;
		this.numberOfProcessors=1;
		this.OneCPUcomputationTime=-1;
		this.AvgPara=0;
		this.Sigma=0;
		this.decreaseEXETime=0;
		this.level=-1;
	}

	@Override
	protected void parseTaskInfo(IAttribute taskinfo) {
		DependTaskInfo info = ((DependTaskInfo)taskinfo);
		this.id = taskinfo.getId();
		this.computationTime = info.getComputationTime();
	}
	
	public int getBestofprocess() {
		return Bestofprocess;
	}
	
	public void setBestofprocess(int process ) {
		this.Bestofprocess=process;
	}
	
	public long getdecreaseEXETime() {
		return decreaseEXETime;
	}
	
	public void setdecreaseEXETime(long decreasetime) {
		this.decreaseEXETime=decreasetime;
	}
	
	public double getSigma(){
		return Sigma;	
	}
	
	public void setSigma(double sigma){
		this.Sigma=sigma;	
	}
	
	public int getAvgPara() {
		return AvgPara;
	}
	
	public void setAvgPara(int A) {
		this.AvgPara=A;
	}
	
	public long getTrueStartTime() {
		return trueStartTime;
	}

	public void setTrueStartTime(long currentTime) {
		this.trueStartTime = currentTime;
	}

	public long getTrueFinishTime() {
		return trueFinishTime;
	}

	public void setTrueFinishTime(long trueFinishTime) {
		this.trueFinishTime = trueFinishTime;
	}

	public int getBottomRank() {
		return bottomRank;
	}

	public void setBottomRank(int bottomRank) {
		this.bottomRank = bottomRank;
	}

	public int getTopRank() {
		return topRank;
	}

	public void setTopRank(int topRank) {
		this.topRank = topRank;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getComputationTime() {
		return computationTime;
	}

	public void setComputationTime(int computationTime) {
		this.computationTime = computationTime;
	}
	public void printInformation () {
		System.out.println("Task ID : " + id);
		System.out.println("Computation Time : " + computationTime);
		System.out.println("Bottom Rank : " + bottomRank);
		System.out.println("Top Rank : " + topRank);
		System.out.println("Rank : " + rank);
		System.out.println("EST : " + EST);
		System.out.println("EFT : " + EFT);
		System.out.println("True Start Time : " + trueStartTime);
		System.out.println("True Finish Time : " + trueFinishTime);
		System.out.println("CPU : " + numberOfProcessors);
		if(belongRes!=null)
			System.out.println("Belong Resource : " + belongRes.getId());
		else
			System.out.println("Belong Resource : " + belongRes);
	}

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public TaskGroup getBelongGroup() {
		return belongGroup;
	}

	public void setBelongGroup(TaskGroup belongGroup) {
		this.belongGroup = belongGroup;
	}
	
	public void setOneCPUcomputationTime(int computationTime){
		this.OneCPUcomputationTime = computationTime;
	}
	
	public int getOneCPUcomputationTime(){
		return OneCPUcomputationTime ;
	}
	public int getLevel() {
		return this.level;
	}
	public void setLevel(int setLevel) {
		this.level=setLevel;
	}
	
}
