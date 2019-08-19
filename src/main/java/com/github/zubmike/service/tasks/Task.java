package com.github.zubmike.service.tasks;

import com.github.zubmike.service.conf.TaskProperties;

public interface Task extends Runnable {

	void init();

	boolean isRunning();

	long getLastStartMillis();

	long getLastFinishMillis();

	boolean isFail();

	TaskProperties getProperties();

}
