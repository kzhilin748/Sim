package com.use.workflow.task;

import java.util.List;

import com.use.workflow.service.PeterService;

public interface IPeterAttribute extends IDepend {
	PeterService getService();
	void setService(PeterService service);
	List<TaskLink> getGlobalServicePool();
	long getStartTime();
	void setStartTime(long startTime);
	boolean isSubmited();
	void setSubmited(boolean isSubmited);
	int getRank();
	void setRank(int rank);
	boolean isInserted();
	void setInserted(boolean bool);
}
